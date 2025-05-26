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
#               Terry Weissman <terry@mozilla.org>
#
#
# Direct any questions on this source code to
#
# Holger Schurig <holgerschurig@nikocity.de>

use diagnostics;
use strict;
use lib ".";

require "CGI.pl";
require "globals.pl";




# TestProduct:  just returns if the specified product does exists
# CheckProduct: same check, optionally  emit an error text
# TestVersion:  just returns if the specified product/version combination exists
# CheckVersion: same check, optionally emit an error text

sub TestProduct ($)
{
    my $prod = shift;

    # does the product exist?
    SendSQL("SELECT product
             FROM products
             WHERE product=" . SqlQuote($prod));
    return FetchOneColumn();
}

sub CheckProduct ($)
{
    my $prod = shift;

    # do we have a product?
    unless ($prod) {
        print "すみません、プロダクトが指定されていません。";
        PutTrailer();
        exit;
    }

    unless (TestProduct $prod) {
        print "すみません、プロダクト '$prod' は存在しません。";
        PutTrailer();
        exit;
    }
}

sub TestVersion ($$)
{
    my ($prod,$ver) = @_;

    # does the product exist?
    SendSQL("SELECT program,value
             FROM versions
             WHERE program=" . SqlQuote($prod) . " and value=" . SqlQuote($ver));
    return FetchOneColumn();
}

sub CheckVersion ($$)
{
    my ($prod,$ver) = @_;

    # do we have the version?
    unless ($ver) {
        print "すみません、バージョンが指定されていません。";
        PutTrailer();
        exit;
    }

    CheckProduct($prod);

    unless (TestVersion $prod,$ver) {
        print "すみません、プロダクト '$prod' のバージョン '$ver' は存在しません。";
        PutTrailer();
        exit;
    }
}


#
# Displays the form to edit a version
#

sub EmitFormElements ($$)
{
    my ($product, $version) = @_;

    print "  <TH ALIGN=\"right\">Version:</TH>\n";
    print "  <TD><INPUT SIZE=64 MAXLENGTH=64 NAME=\"version\" VALUE=\"" .
        value_quote($version) . "\">\n";
    print "      <INPUT TYPE=HIDDEN NAME=\"product\" VALUE=\"" .
        value_quote($product) . "\"></TD>\n";
}


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

unless (UserInGroup("editcomponents")) {
    PutHeader("許可されていません");
    print "すみません、あなたは 'editcomponents' グループのメンバーではありません。そのため、バージョンを追加、編集、削除することはできません。\n";
    PutTrailer();
    exit;
}


#
# often used variables
#
my $product = trim($::FORM{product} || '');
my $version = trim($::FORM{version} || '');
my $action  = trim($::FORM{action}  || '');
my $localtrailer;
if ($version) {
    $localtrailer = "さらにバージョンを<A HREF=\"editversions.cgi?product=" . url_quote($product) . "\">編集</A>";
} else {
    $localtrailer = "さらにバージョンを<A HREF=\"editversions.cgi\">編集</A>";
}



#
# product = '' -> Show nice list of versions
#

unless ($product) {
    PutHeader("プロダクト選択");

    SendSQL("SELECT products.product,products.description,'xyzzy'
             FROM products 
             GROUP BY products.product
             ORDER BY products.product");
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH ALIGN=\"left\">バージョン編集 ...</TH>\n";
    print "  <TH ALIGN=\"left\">説明</TH>\n";
    print "  <TH ALIGN=\"left\">バグ数</TH>\n";
    #print "  <TH ALIGN=\"left\">Edit</TH>\n";
    print "</TR>";
    while ( MoreSQLData() ) {
        my ($product, $description, $bugs) = FetchSQLData();
        $description ||= "<FONT COLOR=\"red\">missing</FONT>";
        $bugs ||= "none";
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editversions.cgi?product=", url_quote($product), "\"><B>$product</B></A></TD>\n";
        print "  <TD VALIGN=\"top\">$description</TD>\n";
        print "  <TD VALIGN=\"top\">$bugs</TD>\n";
        #print "  <TD VALIGN=\"top\"><A HREF=\"editversions.cgi?action=edit&product=", url_quote($product), "\">Edit</A></TD>\n";
    }
    print "</TR></TABLE>\n";

    PutTrailer();
    exit;
}



#
# action='' -> Show nice list of versions
#

unless ($action) {
    PutHeader("$product のバージョン選択");
    CheckProduct($product);

=for me

    # Das geht nicht wie vermutet. Ich bekomme nicht alle Versionen
    # angezeigt!  Schade. Ich w?rde gerne sehen, wieviel Bugs pro
    # Version angegeben sind ...

    SendSQL("SELECT value,program,COUNT(bug_id)
             FROM versions LEFT JOIN bugs
               ON program=product AND value=version
             WHERE program=" . SqlQuote($product) . "
             GROUP BY value");

=cut

    SendSQL("SELECT value,program
             FROM versions 
             WHERE program=" . SqlQuote($product) . "
             ORDER BY value");

    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH ALIGN=\"left\">バージョン編集 ...</TH>\n";
    #print "  <TH ALIGN=\"left\">バグ数</TH>\n";
    print "  <TH ALIGN=\"left\">Action</TH>\n";
    print "</TR>";
    while ( MoreSQLData() ) {
        my ($version,$dummy,$bugs) = FetchSQLData();
        $bugs ||= 'none';
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editversions.cgi?product=", url_quote($product), "&version=", url_quote($version), "&action=edit\"><B>$version</B></A></TD>\n";
        #print "  <TD VALIGN=\"top\">$bugs</TD>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editversions.cgi?product=", url_quote($product), "&version=", url_quote($version), "&action=del\"><B>Delete</B></A></TD>\n";
        print "</TR>";
    }
    print "<TR>\n";
    print "  <TD VALIGN=\"top\">新規バージョンを追加</TD>\n";
    print "  <TD VALIGN=\"top\" ALIGN=\"middle\"><A HREF=\"editversions.cgi?product=", url_quote($product) . "&action=add\">追加</A></TD>\n";
    print "</TR></TABLE>\n";

    PutTrailer();
    exit;
}




#
# action='add' -> present form for parameters for new version
#
# (next action will be 'new')
#

if ($action eq 'add') {
    PutHeader("$product のバージョンを追加");
    CheckProduct($product);

    #print "This page lets you add a new version to a bugzilla-tracked product.\n";

    print "<FORM METHOD=POST ACTION=editversions.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements($product, $version);

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
# action='new' -> add version entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("新規バージョンを追加中");
    CheckProduct($product);

    # Cleanups and valididy checks

    unless ($version) {
        print "新規バージョンを入力してください。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }
    if (TestVersion($product,$version)) {
        print "バージョン '$version' は既に存在します。<b>Back</b> ボタンを押して、やり直してください。\n";
        PutTrailer($localtrailer);
        exit;
    }

    # Add the new version
    SendSQL("INSERT INTO versions ( " .
          "value, program" .
          " ) VALUES ( " .
          SqlQuote($version) . "," .
          SqlQuote($product) . ")");

    # Make versioncache flush
    unlink "data/versioncache";

    print "OK、完了です。<p>\n";
    PutTrailer($localtrailer);
    exit;
}




#
# action='del' -> ask if user really wants to delete
#
# (next action would be 'delete')
#

if ($action eq 'del') {
    PutHeader("$product のバージョンを削除");
    CheckVersion($product, $version);

    SendSQL("SELECT count(bug_id),product,version
             FROM bugs
             GROUP BY product,version
             HAVING product=" . SqlQuote($product) . "
                AND version=" . SqlQuote($version));
    my $bugs = FetchOneColumn();

    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>\n";
    print "<TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Part</TH>\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Value</TH>\n";

    print "</TR><TR>\n";
    print "  <TH ALIGN=\"left\" VALIGN=\"top\">プロダクト:</TH>\n";
    print "  <TD VALIGN=\"top\">$product</TD>\n";
    print "</TR><TR>\n";
    print "  <TH ALIGN=\"left\" VALIGN=\"top\">バージョン:</TH>\n";
    print "  <TD VALIGN=\"top\">$version</TD>\n";
    print "</TR><TR>\n";
    print "  <TH ALIGN=\"left\" VALIGN=\"top\">バグ数:</TH>\n";
    print "  <TD VALIGN=\"top\">", $bugs || 'none' , "</TD>\n";
    print "</TR></TABLE>\n";

    print "<H2>確認</H2>\n";

    if ($bugs) {
        if (!Param("allowbugdeletion")) {
            print "すみません、このバージョンにはバグが $bugs つ存在します。これらのバグを別のバージョンに割り当てないと、このバージョンを削除できません。";
            PutTrailer($localtrailer);
            exit;
        }
        print "<TABLE BORDER=0 CELLPADDING=20 WIDTH=\"70%\" BGCOLOR=\"red\"><TR><TD>\n",
              "このバージョンには既にバグが登録されています! このバージョンを削除すると、記録されている<B><BLINK>すべての</BLINK></B>バグも削除されます。このバージョンのバグ履歴も見ることができなくなります!\n",
              "</TD></TR></TABLE>\n";
    }

    print "<P>本当にこのバージョンを削除しますか?<P>\n";
    print "<FORM METHOD=POST ACTION=editversions.cgi>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"はい、削除します\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"delete\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"product\" VALUE=\"" .
        value_quote($product) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"version\" VALUE=\"" .
        value_quote($version) . "\">\n";
    print "</FORM>";

    PutTrailer($localtrailer);
    exit;
}



#
# action='delete' -> really delete the version
#

if ($action eq 'delete') {
    PutHeader("$product のバージョンを削除中");
    CheckVersion($product,$version);

    # lock the tables before we start to change everything:

    SendSQL("LOCK TABLES attachments WRITE,
                         bugs WRITE,
                         bugs_activity WRITE,
                         versions WRITE,
                         dependencies WRITE");

    # According to MySQL doc I cannot do a DELETE x.* FROM x JOIN Y,
    # so I have to iterate over bugs and delete all the indivial entries
    # in bugs_activies and attachments.

    if (Param("allowbugdeletion")) {

        SendSQL("SELECT bug_id
             FROM bugs
             WHERE product=" . SqlQuote($product) . "
               AND version=" . SqlQuote($version));
        while (MoreSQLData()) {
            my $bugid = FetchOneColumn();

            PushGlobalSQLState();
            SendSQL("DELETE FROM attachments WHERE bug_id=$bugid");
            SendSQL("DELETE FROM bugs_activity WHERE bug_id=$bugid");
            SendSQL("DELETE FROM dependencies WHERE blocked=$bugid");
            PopGlobalSQLState();
        }
        print "添付ファイルとバグの動き、依存関係が削除されました。<BR>\n";


        # Deleting the rest is easier:

        SendSQL("DELETE FROM bugs
             WHERE product=" . SqlQuote($product) . "
               AND version=" . SqlQuote($version));
        print "バグレポートが削除されました。<BR>\n";
    }

    SendSQL("DELETE FROM versions
             WHERE program=" . SqlQuote($product) . "
               AND value=" . SqlQuote($version));
    print "バージョンが削除されました。<P>\n";
    SendSQL("UNLOCK TABLES");

    unlink "data/versioncache";
    PutTrailer($localtrailer);
    exit;
}



#
# action='edit' -> present the edit version form
#
# (next action would be 'update')
#

if ($action eq 'edit') {
    PutHeader("$product のバージョンを編集");
    CheckVersion($product,$version);

    print "<FORM METHOD=POST ACTION=editversions.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements($product, $version);

    print "</TR></TABLE>\n";

    print "<INPUT TYPE=HIDDEN NAME=\"versionold\" VALUE=\"" .
        value_quote($version) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"update\">\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"更新\">\n";

    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/さらに/別の/;
    PutTrailer($other);
    exit;
}



#
# action='update' -> update the version
#

if ($action eq 'update') {
    PutHeader("$product のバージョンを更新");

    my $versionold = trim($::FORM{versionold} || '');

    CheckVersion($product,$versionold);

    # Note that the order of this tests is important. If you change
    # them, be sure to test for WHERE='$version' or WHERE='$versionold'

    SendSQL("LOCK TABLES bugs WRITE,
                         versions WRITE");

    if ($version ne $versionold) {
        unless ($version) {
            print "すみません、バージョンを削除できません。";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }
        if (TestVersion($product,$version)) {
            print "すみません、バージョン '$version' は既に使用されています。";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }
        SendSQL("UPDATE bugs
                 SET version=" . SqlQuote($version) . ",
                 delta_ts = delta_ts
                 WHERE version=" . SqlQuote($versionold) . "
                   AND product=" . SqlQuote($product));
        SendSQL("UPDATE versions
                 SET value=" . SqlQuote($version) . "
                 WHERE program=" . SqlQuote($product) . "
                   AND value=" . SqlQuote($versionold));
        unlink "data/versioncache";
        print "バージョンを更新しました。<BR>\n";
    }
    SendSQL("UNLOCK TABLES");

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
