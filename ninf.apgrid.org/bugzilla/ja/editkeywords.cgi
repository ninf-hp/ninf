#!/usr/local/bin/perl -w
# -*- Mode: perl; indent-tabs-mode: nil -*-
#
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# The Original Code is the Bugzilla Bug Tracking System.
#
# The Initial Developer of the Original Code is Terry Weissman.
# Portions created by Terry Weissman are
# Copyright (C) 2000 Terry Weissman. All
# Rights Reserved.
#
# Contributor(s): Terry Weissman <terry@mozilla.org>

use diagnostics;
use strict;
use lib ".";

require "CGI.pl";

my $localtrailer = "<A HREF=\"editkeywords.cgi\">edit</A> more keywords";


#
# Displays a text like "a.", "a or b.", "a, b or c.", "a, b, c or d."
#

sub PutTrailer (@)
{
    my (@links) = ("<A HREF=\"query.cgi\">検索ページ</A> に戻る", @_);

    my $count = $#links;
    my $num = 0;
    print "<P>\n";
    foreach (@links) {
        print $_;
        if ($num == $count) {
            print "\n";
        }
        elsif ($num == $count-1) {
            print "／";
        }
        else {
            print "、";
        }
        $num++;
    }
    PutFooter();
}


#
# Displays the form to edit a keyword's parameters
#

sub EmitFormElements ($$$)
{
    my ($id, $name, $description) = @_;

    $name = value_quote($name);
    $description = value_quote($description);

    print qq{<INPUT TYPE="HIDDEN" NAME=id VALUE=$id>};

    print "  <TR><TH ALIGN=\"right\">キーワード:</TH>\n";
    print "  <TD><INPUT SIZE=64 MAXLENGTH=64 NAME=\"name\" VALUE=\"$name\"></TD>\n";
    print "</TR><TR>\n";

    print "  <TH ALIGN=\"right\">説明:</TH>\n";
    print "  <TD><TEXTAREA ROWS=4 COLS=64 WRAP=VIRTUAL NAME=\"description\">$description</TEXTAREA></TD>\n";
    print "</TR>\n";

}


sub Validate ($$) {
    my ($name, $description) = @_;
    if ($name eq "") {
        print "キーワードとして空白以外の名前を入力してください。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
    if ($name =~ /[\s,]/) {
        print "キーワードにはコンマと空白は使えません。\n";
        print "<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }    
    if ($description eq "") {
        print "キーワードの説明を入力してください。\n<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
}


#
# Preliminary checks:
#

ConnectToDatabase();
confirm_login();

print "Content-type: text/html; charset=EUC-JP\n\n";

unless (UserInGroup("editkeywords")) {
    PutHeader("許可されていません");
    print "申しわけありません。あなたは 'editkeywords' グループのメンバーではありません。\n";
    print "そのため、キーワードを追加、編集、削除することはできません。\n";
    PutTrailer();
    exit;
}


my $action  = trim($::FORM{action}  || '');
detaint_natural($::FORM{id});


if ($action eq "") {
    PutHeader("キーワード選択");
    my $tableheader = qq{
<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>
<TR BGCOLOR="#6666FF">
<TH ALIGN="left">キーワードの編集 ...</TH>
<TH ALIGN="left">説明</TH>
<TH ALIGN="left">バグ数</TH>
<TH ALIGN="left">Action</TH>
</TR>
};
    print $tableheader;
    my $line_count = 0;
    my $max_table_size = 50;

    SendSQL("SELECT keyworddefs.id, keyworddefs.name, keyworddefs.description,
                    COUNT(keywords.bug_id), keywords.bug_id
             FROM keyworddefs LEFT JOIN keywords ON keyworddefs.id = keywords.keywordid
             GROUP BY keyworddefs.id
             ORDER BY keyworddefs.name");
    while (MoreSQLData()) {
        my ($id, $name, $description, $bugs, $onebug) = FetchSQLData();
        $description ||= "<FONT COLOR=\"red\">missing</FONT>";
        $bugs ||= 'none';
        if (!$onebug) {
            # This is silly hackery for old versions of MySQL that seem to
            # return a count() of 1 even if there are no matching.  So, we 
            # ask for an actual bug number.  If it can't find any bugs that
            # match the keyword, then we set the count to be zero, ignoring
            # what it had responded.
            $bugs = 'none';
        }
        if ($line_count == $max_table_size) {
            print "</table>\n$tableheader";
            $line_count = 0;
        }
        $line_count++;
            
        print qq{
<TR>
<TH VALIGN="top"><A HREF="editkeywords.cgi?action=edit&id=$id">$name</TH>
<TD VALIGN="top">$description</TD>
<TD VALIGN="top" ALIGN="right">$bugs</TD>
<TH VALIGN="top"><A HREF="editkeywords.cgi?action=delete&id=$id">削除</TH>
</TR>
};
    }
    print qq{
<TR>
<TD VALIGN="top" COLSPAN=3>新規キーワード追加</TD><TD><A HREF="editkeywords.cgi?action=add">追加</TD>
</TR>
</TABLE>
};
    PutTrailer();
    exit;
}
    

if ($action eq 'add') {
    PutHeader("キーワード追加");
    print "<FORM METHOD=POST ACTION=editkeywords.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0>\n";

    EmitFormElements(-1, '', '');

    print "</TABLE>\n<HR>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"追加\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"new\">\n";
    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/さらに/別の/;
    PutTrailer($other);
    exit;
}

#
# action='new' -> add keyword entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("キーワードを追加中");

    # Cleanups and valididy checks

    my $name = trim($::FORM{name} || '');
    my $description  = trim($::FORM{description}  || '');

    Validate($name, $description);
    
    SendSQL("SELECT id FROM keyworddefs WHERE name = " . SqlQuote($name));

    if (FetchOneColumn()) {
        print "'$name' というキーワードは既に存在します。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }


    # Pick an unused number.  Be sure to recycle numbers that may have been
    # deleted in the past.  This code is potentially slow, but it happens
    # rarely enough, and there really aren't ever going to be that many
    # keywords anyway.

    SendSQL("SELECT id FROM keyworddefs ORDER BY id");

    my $newid = 1;

    while (MoreSQLData()) {
        my $oldid = FetchOneColumn();
        if ($oldid > $newid) {
            last;
        }
        $newid = $oldid + 1;
    }

    # Add the new keyword.
    SendSQL("INSERT INTO keyworddefs (id, name, description) VALUES ($newid, " .
            SqlQuote($name) . "," .
            SqlQuote($description) . ")");

    # Make versioncache flush
    unlink "data/versioncache";

    print "OK、完了です。<p>\n";
    PutTrailer("さらにキーワードを<A HREF=\"editkeywords.cgi\">編集</A>／別のキーワードを<A HREF=\"editkeywords.cgi?action=add\">追加</a>");
    exit;
}

    

#
# action='edit' -> present the edit keywords from
#
# (next action would be 'update')
#

if ($action eq 'edit') {
    PutHeader("キーワードを編集");

    my $id  = trim($::FORM{id} || 0);
    # get data of keyword
    SendSQL("SELECT name,description
             FROM keyworddefs
             WHERE id=$id");
    my ($name, $description) = FetchSQLData();
    if (!$name) {
        print "おかしなことが起きました。やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
    print "<FORM METHOD=POST ACTION=editkeywords.cgi>\n";
    print "<TABLE  BORDER=0 CELLPADDING=4 CELLSPACING=0>\n";

    EmitFormElements($id, $name, $description);
    
    print "<TR>\n";
    print "  <TH ALIGN=\"right\">バグ数:</TH>\n";
    print "  <TD>";
    SendSQL("SELECT count(*)
             FROM keywords
             WHERE keywordid = $id");
    my $bugs = '';
    $bugs = FetchOneColumn() if MoreSQLData();
    print $bugs || 'none';

    print "</TD>\n</TR></TABLE>\n";

    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"update\">\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"更新\">\n";

    print "</FORM>";

    my $x = $localtrailer;
    $x =~ s/さらに/別の/;
    PutTrailer($x);
    exit;
}


#
# action='update' -> update the keyword
#

if ($action eq 'update') {
    PutHeader("キーワードの更新");

    my $id = $::FORM{id};
    my $name  = trim($::FORM{name} || '');
    my $description  = trim($::FORM{description}  || '');

    Validate($name, $description);

    SendSQL("SELECT id FROM keyworddefs WHERE name = " . SqlQuote($name));

    my $tmp = FetchOneColumn();

    if ($tmp && $tmp != $id) {
        print "キーワード '$name' は既に存在します。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }

    SendSQL("UPDATE keyworddefs SET name = " . SqlQuote($name) .
            ", description = " . SqlQuote($description) .
            " WHERE id = $id");

    print "キーワードを更新しました。<BR>\n";

    &RebuildCacheWarning;
    # Make versioncache flush
    unlink "data/versioncache";

    PutTrailer($localtrailer);
    exit;
}


if ($action eq 'delete') {
    PutHeader("キーワード削除");
    my $id = $::FORM{id};

    SendSQL("SELECT name FROM keyworddefs WHERE id=$id");
    my $name = FetchOneColumn();

    if (!$::FORM{reallydelete}) {

        SendSQL("SELECT count(*)
                 FROM keywords
                 WHERE keywordid = $id");
        
        my $bugs = FetchOneColumn();
        
        if ($bugs) {
            
            
            print qq{
このキーワードがセットされたバグレポートが $bugs つ存在します。<b>本当に</b> キーワード <code>$name</code> を削除しますか?

<FORM METHOD=POST ACTION=editkeywords.cgi>
<INPUT TYPE=HIDDEN NAME="id" VALUE="$id">
<INPUT TYPE=HIDDEN NAME="action" VALUE="delete">
<INPUT TYPE=HIDDEN NAME="reallydelete" VALUE="1">
<INPUT TYPE=SUBMIT VALUE="はい、本当にこのキーワードを削除します">
</FORM>
};

            PutTrailer($localtrailer);
            exit;
        }
    }

    SendSQL("DELETE FROM keywords WHERE keywordid = $id");
    SendSQL("DELETE FROM keyworddefs WHERE id = $id");

    print "キーワード $name は削除されました。\n";

    &RebuildCacheWarning;
    # Make versioncache flush
    unlink "data/versioncache";

    PutTrailer($localtrailer);
    exit;
}

PutHeader("エラー");
print "あなたが何をしたかったかの手がかりもつかめませんでした。<BR>\n";

foreach ( sort keys %::FORM) {
    print "$_: $::FORM{$_}<BR>\n";
}



sub RebuildCacheWarning {

    print "<BR><BR><B>
キーワードが削除または更新されました。キーワードキャッシュを再構築する必要があります。</B>";
    print "キャッシュは sanitycheck.cgi で再構築できます。Bugzilla の規模が大きいときは、数分かかることもあります。<BR><BR><B><A HREF=\"sanitycheck.cgi?rebuildkeywordcache=1\">キャッシュ再構築</A><BR></B>";

}


