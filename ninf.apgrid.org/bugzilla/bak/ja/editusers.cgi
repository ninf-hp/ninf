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
# The Original Code is mozilla.org code.
#
# The Initial Developer of the Original Code is Holger
# Schurig. Portions created by Holger Schurig are
# Copyright (C) 1999 Holger Schurig. All
# Rights Reserved.
#
# Contributor(s): Holger Schurig <holgerschurig@nikocity.de>
#                 Dave Miller <justdave@syndicomm.com>
#                 Joe Robins <jmrobins@tgix.com>
#                 Dan Mosedale <dmose@mozilla.org>
#
# Direct any questions on this source code to
#
# Holger Schurig <holgerschurig@nikocity.de>

use diagnostics;
use strict;
use lib ".";

require "CGI.pl";
require "globals.pl";

# Shut up misguided -w warnings about "used only once".  "use vars" just
# doesn't work for me.

sub sillyness {
    my $zz;
    $zz = $::userid;
    $zz = $::superusergroupset;
}

my $editall;
my $opblessgroupset = '9223372036854775807'; # This is all 64 bits.



# TestUser:  just returns if the specified user does exists
# CheckUser: same check, optionally  emit an error text

sub TestUser ($)
{
    my $user = shift;

    # does the product exist?
    SendSQL("SELECT login_name
             FROM profiles
             WHERE login_name=" . SqlQuote($user));
    return FetchOneColumn();
}

sub CheckUser ($)
{
    my $user = shift;

    # do we have a product?
    unless ($user) {
        print "すみません、ユーザが指定されていません。";
        PutTrailer();
        exit;
    }

    unless (TestUser $user) {
        print "すみません、ユーザ '$user' は存在しません";
        PutTrailer();
        exit;
    }
}



sub EmitElement ($$)
{
    my ($name, $value) = (@_);
    $value = value_quote($value);
    if ($editall) {
        print qq{<TD><INPUT SIZE=64 MAXLENGTH=255 NAME="$name" VALUE="$value"></TD>\n};
    } else {
        print qq{<TD>$value</TD>\n};
    }
}


#
# Displays the form to edit a user parameters
#

sub EmitFormElements ($$$$$)
{
    my ($user, $realname, $groupset, $blessgroupset, $disabledtext) = @_;

    print "  <TH ALIGN=\"right\">ログイン名 (メールアドレス):</TH>\n";
    EmitElement("user", $user);

    print "</TR><TR>\n";
    print "  <TH ALIGN=\"right\">名前:</TH>\n";
    EmitElement("realname", $realname);

    if ($editall) {
        print "</TR><TR>\n";
        print "  <TH ALIGN=\"right\">パスワード:</TH>\n";
        if(Param('useLDAP')) {
          print "  <TD><FONT COLOR=RED>このサイトでは認証に LDAP を使用しています!</FONT></TD>\n";
        } else {
          print qq|
            <TD><INPUT TYPE="PASSWORD" SIZE="16" MAXLENGTH="16" NAME="password" VALUE=""><br>
                (変更する場合は、新しいパスワードを入力します)
            </TD>
          |;
        }
        print "</TR><TR>\n";

        print "  <TH ALIGN=\"right\">使用不可のメッセージ:</TH>\n";
        print "  <TD ROWSPAN=2><TEXTAREA NAME=\"disabledtext\" ROWS=10 COLS=60>" .
            value_quote($disabledtext) . "</TEXTAREA>\n";
        print "  </TD>\n";
        print "</TR><TR>\n";
        print "  <TD VALIGN=\"top\">指定すると、アカウントが使用不可になります。このメッセージで、なぜ使用不可になったかを説明しなくてはなりません。</TD>\n";
    }
        
    
    if($user ne "") {
        print "</TR><TR><TH VALIGN=TOP ALIGN=RIGHT>グループ:</TH><TD><TABLE><TR>";
        SendSQL("SELECT bit,name,description,bit & $groupset != 0, " .
                "       bit & $blessgroupset " .
                "FROM groups " .
                "WHERE bit & $opblessgroupset != 0 AND isbuggroup " .
                "ORDER BY name");
        if (MoreSQLData()) {
            if ($editall) {
                print "<TD COLSPAN=3 ALIGN=LEFT><B>別のユーザのこのグループを変更可能</B></TD>\n";
                print "</TR><TR>\n<TD ALIGN=CENTER><B>|</B></TD>\n";
            }
            print "<TD COLSPAN=2 ALIGN=LEFT><B>このユーザが、このグループに所属している</B></TD>\n";
            while (MoreSQLData()) {
                my ($bit,$name,$description,$checked,$blchecked) = FetchSQLData();
                print "</TR><TR>\n";
                if ($editall) {
                    $blchecked = ($blchecked) ? "CHECKED" : "";
                    print "<TD ALIGN=CENTER><INPUT TYPE=CHECKBOX NAME=\"blbit_$name\" $blchecked VALUE=\"$bit\"></TD>";
                }
                $checked = ($checked) ? "CHECKED" : "";
                print "<TD ALIGN=CENTER><INPUT TYPE=CHECKBOX NAME=\"bit_$name\" $checked VALUE=\"$bit\"></TD>";
                print "<TD><B>" . ucfirst($name) . "</B>: $description</TD>\n";
            }
        }
        print "</TR></TABLE></TD>\n";
    
        print "</TR><TR><TH VALIGN=TOP ALIGN=RIGHT>権限:</TH><TD><TABLE><TR>";
        SendSQL("SELECT bit,name,description,bit & $groupset != 0, " .
                "       bit & $blessgroupset " .
                "FROM groups " .
                "WHERE bit & $opblessgroupset != 0 AND !isbuggroup " .
                "ORDER BY name");
        if (MoreSQLData()) {
            if ($editall) {
                print "<TD COLSPAN=3 ALIGN=LEFT><B>別のユーザのこの権限を変更可能</B></TD>\n";
                print "</TR><TR>\n<TD ALIGN=CENTER><B>|</B></TD>\n";
            }
            print "<TD COLSPAN=2 ALIGN=LEFT><B>このユーザが、この権限を持っている</B></TD>\n";
            while (MoreSQLData()) {
                my ($bit,$name,$description,$checked,$blchecked) = FetchSQLData();
                print "</TR><TR>\n";
                if ($editall) {
                    $blchecked = ($blchecked) ? "CHECKED" : "";
                    print "<TD ALIGN=CENTER><INPUT TYPE=CHECKBOX NAME=\"blbit_$name\" $blchecked VALUE=\"$bit\"></TD>";
                }
                $checked = ($checked) ? "CHECKED" : "";
                print "<TD ALIGN=CENTER><INPUT TYPE=CHECKBOX NAME=\"bit_$name\" $checked VALUE=\"$bit\"></TD>";
                print "<TD><B>" . ucfirst($name) . "</B>: $description</TD>\n";
            }
        }
    } else {
        print "</TR><TR><TH ALIGN=RIGHT>グループと<br>権限:</TH><TD><TABLE><TR>";        
        print "<TD COLSPAN=3>新規ユーザは userregexp を元にグループに所属させられます。このユーザのグループ／権限を変更するには、アカウントを編集してからグループを設定してください。</TD>\n";
    }
    print "</TR></TABLE></TD>\n";

}



#
# Displays a text like "a.", "a or b.", "a, b or c.", "a, b, c or d."
#

sub PutTrailer (@)
{
    my (@links) = ("<A HREF=\"./\">メインページ</A>に戻る",
        "<A HREF=\"editusers.cgi?action=add\">新規ユーザを追加</A>", @_);

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
            print "／";
        }
        $num++;
    }
    PutFooter();
}



#
# Preliminary checks:
#

ConnectToDatabase();
confirm_login();

print "Content-type: text/html; charset=EUC-JP\n\n";

$editall = UserInGroup("editusers");

if (!$editall) {
    SendSQL("SELECT blessgroupset FROM profiles WHERE userid = $::userid");
    $opblessgroupset = FetchOneColumn();
    if (!$opblessgroupset) {
        PutHeader("許可されていません");
        print "申しわけありません。あなたは 'editusers' グループのメンバーではありません。そしてまた、別のユーザをグループに加入させたり離脱させたりする権限もありません。そのため、ユーザを追加、編集、削除することはできません。\n";
        PutTrailer();
        exit;
    }
}



#
# often used variables
#
my $user    = trim($::FORM{user}   || '');
my $action  = trim($::FORM{action} || '');
my $localtrailer = "さらにユーザを<A HREF=\"editusers.cgi\">編集</A>";
my $candelete = Param('allowuserdeletion');



#
# action='' -> Ask for match string for users.
#

unless ($action) {
    PutHeader("ユーザ名検索");
    print qq{
<FORM METHOD=GET ACTION="editusers.cgi">
<INPUT TYPE=HIDDEN NAME="action" VALUE="list">
ログイン名で検索する: 
<INPUT SIZE=32 NAME="matchstr">
<SELECT NAME="matchtype">
<OPTION VALUE="substr" SELECTED>文字列を含む (大文字小文字を区別しない)
<OPTION VALUE="regexp">正規表現
<OPTION VALUE="notregexp">正規表現に一致しないもの
</SELECT>
<BR>
<INPUT TYPE=SUBMIT VALUE="送信">
</FORM>
};
    PutTrailer();
    exit;
}


#
# action='list' -> Show nice list of matching users
#

if ($action eq 'list') {
    PutHeader("ユーザ選択");
    my $query = "";
    my $matchstr = $::FORM{'matchstr'};
    if (exists $::FORM{'matchtype'}) {
      $query = "SELECT login_name,realname,disabledtext " .
          "FROM profiles WHERE login_name ";
      if ($::FORM{'matchtype'} eq 'substr') {
          $query .= "like";
          $matchstr = '%' . $matchstr . '%';
      } elsif ($::FORM{'matchtype'} eq 'regexp') {
          $query .= "regexp";
          $matchstr = '.'
                unless $matchstr;
      } elsif ($::FORM{'matchtype'} eq 'notregexp') {
          $query .= "not regexp";
          $matchstr = '.'
                unless $matchstr;
      } else {
          die "不明な検索形式です";
      }
      $query .= SqlQuote($matchstr) . " ORDER BY login_name";
    } elsif (exists $::FORM{'query'}) {
      $query = "SELECT login_name,realname,disabledtext " .
          "FROM profiles WHERE " . $::FORM{'query'} . " ORDER BY login_name";
    } else {
      die "Missing parameters";
    }

    SendSQL($query);
    my $count = 0;
    my $header = "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">
<TH ALIGN=\"left\">ユーザ編集 ...</TH>
<TH ALIGN=\"left\">名前</TH>
";
    if ($candelete) {
        $header .= "<TH ALIGN=\"left\">Action</TH>\n";
    }
    $header .= "</TR>\n";
    print $header;
    while ( MoreSQLData() ) {
        $count++;
        if ($count % 100 == 0) {
            print "</table>$header";
        }
        my ($user, $realname, $disabledtext) = FetchSQLData();
        my $s = "";
        my $e = "";
        if ($disabledtext) {
            $s = "<STRIKE>";
            $e = "</STRIKE>";
        }
        $realname = ($realname ? html_quote($realname) : "<FONT COLOR=\"red\">missing</FONT>");
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editusers.cgi?action=edit&user=", url_quote($user), "\"><B>$s$user$e</B></A></TD>\n";
        print "  <TD VALIGN=\"top\">$s$realname$e</TD>\n";
        if ($candelete) {
            print "  <TD VALIGN=\"top\"><A HREF=\"editusers.cgi?action=del&user=", url_quote($user), "\">削除</A></TD>\n";
        }
        print "</TR>";
    }
    if ($editall && !Param('useLDAP')) {
        print "<TR>\n";
        my $span = $candelete ? 3 : 2;
        print qq{
<TD VALIGN="top" COLSPAN=$span ALIGN="right">
    <A HREF=\"editusers.cgi?action=add\">新規ユーザを追加</A>
</TD>
};
        print "</TR>";
    }
    print "</TABLE>\n";
    print "$count 人のユーザが見つかりました。\n";

    PutTrailer($localtrailer);
    exit;
}




#
# action='add' -> present form for parameters for new user
#
# (next action will be 'new')
#

if ($action eq 'add') {
    PutHeader("ユーザ追加");
    if (!$editall) {
        print "すみません、新規ユーザを追加する権限がありません。";
        PutTrailer();
        exit;
    }

    if(Param('useLDAP')) {
      print "このサイトは認証に LDAP を使用しています。新規ユーザを追加するには、LDAP管理者に問い合わせてください。";
      PutTrailer();
      exit;
    }

    print "<FORM METHOD=POST ACTION=editusers.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements('', '', 0, 0, '');

    print "</TR></TABLE>\n<HR>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"追加\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"new\">\n";
    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/さらに/別の/;
    PutTrailer($other);
    exit;
}



#
# action='new' -> add user entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("新規ユーザを追加中");

    if (!$editall) {
        print "すみません、新規ユーザを追加する権限がありません。";
        PutTrailer();
        exit;
    }

    if(Param('useLDAP')) {
      print "このサイトは認証に LDAP を使用しています。新規ユーザを追加するには、LDAP管理者に問い合わせてください。";
      PutTrailer();
      exit;
    }

    # Cleanups and valididy checks
    my $realname = trim($::FORM{realname} || '');
    # We don't trim the password since that could falsely lead the user
    # to believe a password with a space was accepted even though a space 
    # is an illegal character in a Bugzilla password.
    my $password = $::FORM{'password'};
    my $disabledtext = trim($::FORM{disabledtext} || '');
    my $emailregexp = Param("emailregexp");

    unless ($user) {
        print "新規ユーザの名前を入力してください。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
    unless ($user =~ m/$emailregexp/) {
        print "ユーザ名は正しいメールアドレスでなくてはなりません。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
    if (!ValidateNewUser($user)) {
        print "'$user' ユーザは既に存在しています。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
    my $passworderror = ValidatePassword($password);
    if ( $passworderror ) {
        print $passworderror;
        PutTrailer($localtrailer);
        exit;
    }

    # For new users, we use the regexps from the groups table to determine
    # their initial group membership.
    # We also keep a list of groups the user was added to for display on the
    # confirmation page.
    my $bits = "0";
    my @grouplist = ();
    SendSQL("select bit, name, userregexp from groups where userregexp != ''");
    while (MoreSQLData()) {
        my @row = FetchSQLData();
        if ($user =~ m/$row[2]/i) {
            $bits .= "+ $row[0]"; # Silly hack to let MySQL do the math,
                                  # not Perl, since we're dealing with 64
                                  # bit ints here, and I don't *think* Perl
                                  # does that.
            push(@grouplist, $row[1]);
        }
    }

    # Add the new user
    SendSQL("INSERT INTO profiles ( " .
            "login_name, cryptpassword, realname, groupset, " .
            "disabledtext" .
            " ) VALUES ( " .
            SqlQuote($user) . "," .
            SqlQuote(Crypt($password)) . "," .
            SqlQuote($realname) . "," .
            $bits . "," .
            SqlQuote($disabledtext) . ")" );

    #+++ send e-mail away

    print "OK、完了です。<br>\n";
    if($#grouplist > -1) {
        print "新規ユーザは group regexp に基づいて、以下のグループに追加されました。:\n";
        print "<ul>\n";
        foreach (@grouplist) {
            print "<li>$_</li>\n";
        }
        print "</ul>\n";
    } else {
        print "新規ユーザはどのグループにも追加されませんでした。<br><br>\n";
    }
    print "${user} の権限を変更するには、<a href=\"editusers.cgi?action=edit&user=" . url_quote($user)."\">このユーザを編集</A> に戻ってください。";
    print "<p>\n";
    PutTrailer($localtrailer,
        "別のユーザを<a href=\"editusers.cgi?action=add\">追加</a>");
    exit;

}



#
# action='del' -> ask if user really wants to delete
#
# (next action would be 'delete')
#

if ($action eq 'del') {
    PutHeader("ユーザ削除");
    if (!$candelete) {
        print "すみません、ユーザ削除は許可されていません。";
        PutTrailer();
        exit;
    }
    if (!$editall) {
        print "すみません、ユーザを削除する権限がありません。";
        PutTrailer();
        exit;
    }
    CheckUser($user);

    # display some data about the user
    SendSQL("SELECT realname, groupset FROM profiles
             WHERE login_name=" . SqlQuote($user));
    my ($realname, $groupset) = 
      FetchSQLData();
    $realname = ($realname ? html_quote($realname) : "<FONT COLOR=\"red\">missing</FONT>");
    
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>\n";
    print "<TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Part</TH>\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Value</TH>\n";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">ログイン名:</TD>\n";
    print "  <TD VALIGN=\"top\">$user</TD>\n";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">名前:</TD>\n";
    print "  <TD VALIGN=\"top\">$realname</TD>\n";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">グループ:</TD>\n";
    print "  <TD VALIGN=\"top\">";
    SendSQL("SELECT name
             FROM groups
             WHERE bit & $groupset = bit
             ORDER BY isbuggroup, name");
    my $found = 0;
    while ( MoreSQLData() ) {
        my ($name) = FetchSQLData();
        print "<br>\n" if $found;
        print ucfirst $name;
        $found = 1;
    }
    print "none" unless $found;
    print "</TD>\n</TR>";


    # Check if the user is an initialowner
    my $nodelete = '';

    SendSQL("SELECT program, value
             FROM components
             WHERE initialowner=" . DBname_to_id($user));
    $found = 0;
    while (MoreSQLData()) {
        if ($found) {
            print "<BR>\n";
        } else {
            print "<TR>\n";
            print "  <TD VALIGN=\"top\">初期担当者:</TD>\n";
            print "  <TD VALIGN=\"top\">";
        }
        my ($product, $component) = FetchSQLData();
        print "<a href=\"editcomponents.cgi?product=", url_quote($product),
                "&component=", url_quote($component),
                "&action=edit\">$product: $component</a>";
        $found    = 1;
        $nodelete = '初期バグ担当者';
    }
    print "</TD>\n</TR>" if $found;


    # Check if the user is an initialqacontact

    SendSQL("SELECT program, value
             FROM components
             WHERE initialqacontact=" . DBname_to_id($user));
    $found = 0;
    while (MoreSQLData()) {
        if ($found) {
            print "<BR>\n";
        } else {
            print "<TR>\n";
            print "  <TD VALIGN=\"top\">初期 QA コンタクト:</TD>\n";
            print "  <TD VALIGN=\"top\">";
        }
        my ($product, $component) = FetchSQLData();
        print "<a href=\"editcomponents.cgi?product=", url_quote($product),
                "&component=", url_quote($component),
                "&action=edit\">$product: $component</a>";
        $found    = 1;
        $nodelete = '初期 QA コンタクト';
    }
    print "</TD>\n</TR>" if $found;

    print "</TABLE>\n";


    if ($nodelete) {
        print "<P>このユーザを削除できません。なぜなら、'$user' は少なくとも一つのプロダクトの $nodelete だからです。";
        PutTrailer($localtrailer);
        exit;
    }


    print "<H2>確認</H2>\n";
    print "<P>本当にこのユーザを削除しますか?<P>\n";

    print "<FORM METHOD=POST ACTION=editusers.cgi>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"はい、削除します\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"delete\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"user\" VALUE=\"$user\">\n";
    print "</FORM>";

    PutTrailer($localtrailer);
    exit;
}



#
# action='delete' -> really delete the user
#

if ($action eq 'delete') {
    PutHeader("ユーザ削除中");
    if (!$candelete) {
        print "すみません、ユーザ削除は許可されていません。";
        PutTrailer();
        exit;
    }
    if (!$editall) {
        print "すみません、ユーザ削除の権限がありません。";
        PutTrailer();
        exit;
    }
    CheckUser($user);

    SendSQL("SELECT userid
             FROM profiles
             WHERE login_name=" . SqlQuote($user));
    my $userid = FetchOneColumn();

    SendSQL("DELETE FROM profiles
             WHERE login_name=" . SqlQuote($user));
    SendSQL("DELETE FROM logincookies
             WHERE userid=" . $userid);
    print "ユーザは削除されました。<BR>\n";

    PutTrailer($localtrailer);
    exit;
}



#
# action='edit' -> present the user edit from
#
# (next action would be 'update')
#

if ($action eq 'edit') {
    PutHeader("ユーザ編集");
    CheckUser($user);

    # get data of user
    SendSQL("SELECT realname, groupset, blessgroupset, disabledtext
             FROM profiles
             WHERE login_name=" . SqlQuote($user));
    my ($realname, $groupset, $blessgroupset,
        $disabledtext) = FetchSQLData();

    print "<FORM METHOD=POST ACTION=editusers.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements($user, $realname, $groupset, $blessgroupset, $disabledtext);
    
    print "</TR></TABLE>\n";

    print "<INPUT TYPE=HIDDEN NAME=\"userold\" VALUE=\"$user\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"realnameold\" VALUE=\"$realname\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"groupsetold\" VALUE=\"$groupset\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"blessgroupsetold\" VALUE=\"$blessgroupset\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"disabledtextold\" VALUE=\"" .
        value_quote($disabledtext) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"update\">\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"更新\">\n";

    print "</FORM>";

    my $x = $localtrailer;
    $x =~ s/さらに/別の/;
    PutTrailer($x);
    exit;
}

#
# action='update' -> update the user
#

if ($action eq 'update') {
    PutHeader("ユーザを更新しました");

    my $userold               = trim($::FORM{userold}              || '');
    my $realname              = trim($::FORM{realname}             || '');
    my $realnameold           = trim($::FORM{realnameold}          || '');
    my $password              = $::FORM{password}                  || '';
    my $disabledtext          = trim($::FORM{disabledtext}         || '');
    my $disabledtextold       = trim($::FORM{disabledtextold}      || '');
    my $groupsetold           = trim($::FORM{groupsetold}          || '0');
    my $blessgroupsetold      = trim($::FORM{blessgroupsetold}     || '0');

    my $groupset = "0";
    foreach (keys %::FORM) {
        next unless /^bit_/;
        #print "$_=$::FORM{$_}<br>\n";
        detaint_natural($::FORM{$_}) || die "Groupset field tampered with";
        $groupset .= " + $::FORM{$_}";
    }
    my $blessgroupset = "0";
    foreach (keys %::FORM) {
        next unless /^blbit_/;
        #print "$_=$::FORM{$_}<br>\n";
        detaint_natural($::FORM{$_}) || die "Blessgroupset field tampered with";
        $blessgroupset .= " + $::FORM{$_}";
    }

    CheckUser($userold);

    # Note that the order of this tests is important. If you change
    # them, be sure to test for WHERE='$product' or WHERE='$productold'

    if ($groupset ne $groupsetold) {
        SendSQL("SELECT groupset FROM profiles WHERE login_name=" .
                SqlQuote($userold));
        $groupsetold = FetchOneColumn();
        # Updated, 5/7/00, Joe Robins
        # We don't want to change the groupset of a superuser.
        if($groupsetold eq $::superusergroupset) {
          print "スーパーユーザの権限は変更できません。\n";
        } else {
           SendSQL("UPDATE profiles
                    SET groupset =
                         groupset - (groupset & $opblessgroupset) + 
                         (($groupset) & $opblessgroupset)
                    WHERE login_name=" . SqlQuote($userold));

           # I'm paranoid that someone who I give the ability to bless people
           # will start misusing it.  Let's log who blesses who (even though
           # nothing actually uses this log right now).
           my $fieldid = GetFieldID("groupset");
           SendSQL("SELECT userid, groupset FROM profiles WHERE login_name=" .
                   SqlQuote($userold));
           my $u;
           ($u, $groupset) = (FetchSQLData());
           if ($groupset ne $groupsetold) {
               SendSQL("INSERT INTO profiles_activity " .
                       "(userid,who,profiles_when,fieldid,oldvalue,newvalue) " .
                       "VALUES " .
                       "($u, $::userid, now(), $fieldid, " .
                       " $groupsetold, $groupset)");
           }
           print "権限を変更しました。\n";
       }
    }

    if ($editall && $blessgroupset ne $blessgroupsetold) {
        SendSQL("UPDATE profiles
                 SET blessgroupset=" . $blessgroupset . "
                 WHERE login_name=" . SqlQuote($userold));
        print "別のユーザの権限を変更することができる資格を更新しました。\n";
    }

    # Update the database with the user's new password if they changed it.
    if ( !Param('useLDAP') && $editall && $password ) {
        my $passworderror = ValidatePassword($password);
        if ( !$passworderror ) {
            my $cryptpassword = SqlQuote(Crypt($password));
            my $loginname = SqlQuote($userold);
            SendSQL("UPDATE  profiles
                     SET     cryptpassword = $cryptpassword
                     WHERE   login_name = $loginname");
            SendSQL("SELECT userid
                     FROM profiles
                     WHERE login_name=" . SqlQuote($userold));
            my $userid = FetchOneColumn();
            InvalidateLogins($userid);
            print "Updated password.<BR>\n";
        } else {
            print "パスワードを更新しませんでした: $passworderror<br>\n";
        }
    }
    if ($editall && $realname ne $realnameold) {
        SendSQL("UPDATE profiles
                 SET realname=" . SqlQuote($realname) . "
                 WHERE login_name=" . SqlQuote($userold));
        print "名前を更新しました。<BR>\n";
    }
    if ($editall && $disabledtext ne $disabledtextold) {
        SendSQL("UPDATE profiles
                 SET disabledtext=" . SqlQuote($disabledtext) . "
                 WHERE login_name=" . SqlQuote($userold));
        SendSQL("SELECT userid
                 FROM profiles
                 WHERE login_name=" . SqlQuote($userold));
        my $userid = FetchOneColumn();
        InvalidateLogins($userid);
        print "使用不可のメッセージを更新しました。<BR>\n";
    }
    if ($editall && $user ne $userold) {
        unless ($user) {
            print "すみません、ユーザ名を削除できません。";
            PutTrailer($localtrailer);
            exit;
        }
        if (TestUser($user)) {
            print "ユーザ名の '$user' は既に使用されています。";
            PutTrailer($localtrailer);
            exit;
        }

        SendSQL("UPDATE profiles
                 SET login_name=" . SqlQuote($user) . "
                 WHERE login_name=" . SqlQuote($userold));

        print "ユーザ名を更新しました。<BR>\n";
    }

    PutTrailer($localtrailer);
    exit;
}



#
# No valid action found
#

PutHeader("エラー");
print "あなたが何をしたかったかの手がかりもつかめませんでした。<BR>\n";

foreach ( sort keys %::FORM) {
    print "$_: $::FORM{$_}<BR>\n";
}
