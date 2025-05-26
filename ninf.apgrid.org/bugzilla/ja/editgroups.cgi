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
# The Initial Developer of the Original Code is Netscape Communications
# Corporation. Portions created by Netscape are
# Copyright (C) 1998 Netscape Communications Corporation. All
# Rights Reserved.
#
# Contributor(s): Dave Miller <justdave@syndicomm.com>
#                 Jake Steenhagen <jake@acutexx.net>

# Code derived from editowners.cgi and editusers.cgi

use diagnostics;
use strict;
use lib ".";

require "CGI.pl";

ConnectToDatabase();
confirm_login();

print "Content-type: text/html; charset=EUC-JP\n\n";

if (!UserInGroup("creategroups")) {
    PutHeader("権限がありません","グループの編集","","この機能を使用する権限がありません!");
    print "<H1>すみません、あなたは 'creategroups' グループのメンバーではありません。</H1>\n";
    print "そのため、グループを編集することはできません。\n";
    print "<p>\n";
    PutFooter();
    exit;
}

my $action  = trim($::FORM{action} || '');

# TestGroup: check if the group name exists
sub TestGroup ($)
{
    my $group = shift;

    # does the group exist?
    SendSQL("SELECT name
             FROM groups
             WHERE name=" . SqlQuote($group));
    return FetchOneColumn();
}

sub ShowError ($)
{
    my $msgtext = shift;
    print "<TABLE BGCOLOR=\"#FF0000\" CELLPADDING=15><TR><TD>";
    print "<B>$msgtext</B>";
    print "</TD></TR></TABLE><P>";
    return 1;
}

#
# Displays a text like "a.", "a or b.", "a, b or c.", "a, b, c or d."
#

sub PutTrailer (@)
{
    my (@links) = ("<a href=\"./\">Bugzilla メインページに戻る</a>", @_);

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
# action='' -> No action specified, get a list.
#

unless ($action) {
    PutHeader("グループの編集","グループの編集","グループを編集して、ユーザを所属させることができます。");

    print "<form method=post action=editgroups.cgi>\n";
    print "<table border=1>\n";
    print "<tr>";
    print "<th>Bit</th>";
    print "<th>Name</th>";
    print "<th>説明</th>";
    print "<th>User RegExp</th>";
    print "<th>使用中</th>";
    print "<th>Action</th>";
    print "</tr>\n";

    SendSQL("SELECT bit,name,description,userregexp,isactive " .
            "FROM groups " .
            "WHERE isbuggroup != 0 " .
            "ORDER BY bit");

    while (MoreSQLData()) {
        my ($bit, $name, $desc, $regexp, $isactive) = FetchSQLData();
        print "<tr>\n";
        print "<td valign=middle>$bit</td>\n";
        print "<td><input size=20 name=\"name-$bit\" value=\"$name\">\n";
        print "<input type=hidden name=\"oldname-$bit\" value=\"$name\"></td>\n";
        print "<td><input size=40 name=\"desc-$bit\" value=\"$desc\">\n";
        print "<input type=hidden name=\"olddesc-$bit\" value=\"$desc\"></td>\n";
        print "<td><input size=30 name=\"regexp-$bit\" value=\"$regexp\">\n";
        print "<input type=hidden name=\"oldregexp-$bit\" value=\"$regexp\"></td>\n";
        print "<td><input type=\"checkbox\" name=\"isactive-$bit\" value=\"1\"" . ($isactive ? " checked" : "") . ">\n";
        print "<input type=hidden name=\"oldisactive-$bit\" value=\"$isactive\"></td>\n";
        print "<td align=center valign=middle><a href=\"editgroups.cgi?action=del&group=$bit\">削除</a></td>\n";
        print "</tr>\n";
    }

    print "<tr>\n";
    print "<td colspan=5></td>\n";
    print "<td><a href=\"editgroups.cgi?action=add\">グループ追加</a></td>\n";
    print "</tr>\n";
    print "</table>\n";
    print "<input type=hidden name=\"action\" value=\"update\">";
    print "<input type=submit value=\"変更を送信\">\n";

    print "<p>";
    print "<b>Name</b> は、cgi ファイルの中の UserInGroup() 関数で使用されています。ほかにも、メールでバグ登録をする人が、バグレポートを特定のグループだけ閲覧できるように制限するためにも使用されます。<p>";
    print "<b>説明</b> は、バグレポートに表示されるものです。バグレポートを同じグループのメンバーだけ閲覧できるように制限するかしないかの選択肢として表示されます。<p>";
    print "<b>User RegExp</b> はオプションです。もしこの欄が入力されていると、この正規表現にマッチするメールアドレスで新しく作成されたユーザには、自動的にこのグループに所属／権限を与えられます。<p>";
    print "<b>使用中</b>フラグはそのグループが使用中かどうかを決めます。使用を中止した場合、新規バグレポートをそのグループに所属させることができなくなります。しかし中止になる以前に登録されたレポートは、そのグループに所属し続けます。
グループに所属するバグを増やさない方法としてグループを使用中止にすることは、グループを削除するよりは穏当な処置です。
<p>それに加えて、以下のグループが既に存在します。これらはユーザ権限を決めているグループです。このグループにおいては User RegExp を編集することしかできません。グループ名にこれらの名前を使用してしまわないよう気をつけてください。
<p>補足するとこのページに二つある「変更を送信」ボタンはどちらも、上と下のテーブルの変更をまとめて送信してしまいます。二つあるのは単に便利だからです。<p>";

    print "<table border=1>\n";
    print "<tr>";
    print "<th>Bit</th>";
    print "<th>Name</th>";
    print "<th>説明</th>";
    print "<th>User RegExp</th>";
    print "</tr>\n";

    SendSQL("SELECT bit,name,description,userregexp " .
            "FROM groups " .
            "WHERE isbuggroup = 0 " .
            "ORDER BY bit");

    while (MoreSQLData()) {
        my ($bit, $name, $desc, $regexp) = FetchSQLData();
        print "<tr>\n";
        print "<td>$bit</td>\n";
        print "<td>$name</td>\n";
        print "<input type=hidden name=\"name-$bit\" value=\"$name\">\n";
        print "<input type=hidden name=\"oldname-$bit\" value=\"$name\">\n";
        print "<td>$desc</td>\n";
        print "<td><input type=text size=30 name=\"regexp-$bit\" value=\"$regexp\"></td>\n";
        print "<input type=hidden name=\"oldregexp-$bit\" value=\"$regexp\">\n";
        print "</tr>\n";
    }

    print "</table><p>\n";
    print "<input type=submit value=\"変更を送信\">\n";
    print "</form>\n";

    PutFooter();
    exit;
}

#
# action='add' -> present form for parameters for new group
#
# (next action will be 'new')
#

if ($action eq 'add') {
    PutHeader("グループ追加");

    print "<FORM METHOD=POST ACTION=editgroups.cgi>\n";
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR>\n";
    print "<th>New Name</th>";
    print "<th>説明</th>";
    print "<th>New User RegExp</th>";
    print "<th>使用中</th>";
    print "</tr><tr>";
    print "<td><input size=20 name=\"name\"></td>\n";
    print "<td><input size=40 name=\"desc\"></td>\n";
    print "<td><input size=30 name=\"regexp\"></td>\n";
    print "<td><input type=\"checkbox\" name=\"isactive\" value=\"1\" checked></td>\n";
    print "</TR></TABLE>\n<HR>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"追加\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"new\">\n";
    print "</FORM>";

    print "<p>";
    print "<b>Name</b> は、cgi ファイルの中の UserInGroup() 関数で使用されています。ほかにも、メールでバグ登録をする人が、バグレポートを特定のグループだけ閲覧できるように制限するためにも使用されます。空白を含めることはできません。<p>";
    print "<b>説明</b> は、バグレポートに表示されるものです。バグレポートを同じグループのメンバーだけ閲覧できるように制限するかしないかの選択肢として表示されます。<p>";
    print "<b>使用中</b>フラグはそのグループが使用中かどうかを決めます。使用を中止した場合、新規バグレポートをそのグループに所属させることができなくなります。しかし中止になる以前に登録されたレポートは、そのグループに所属し続けます。
グループに所属するバグを増やさない方法としてグループを使用中止にすることは、グループを削除するよりは穏当な処置です。<b>補足: グループを作ると、おそらくそれを使用中にしたくなります。その場合、この欄に印をつけたままにしておく必要があります。</b><p>";
    print "<b>User RegExp</b> はオプションです。もしこの欄が入力されていると、この正規表現にマッチするメールアドレスで新しく作成されたユーザには、自動的にこのグループに所属／権限を与えられます。<p>";

    PutTrailer("<a href=editgroups.cgi>グループ一覧に戻る</a>");
    exit;
}



#
# action='new' -> add group entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("新規グループを追加中");

    # Cleanups and valididy checks
    my $name = trim($::FORM{name} || '');
    my $desc = trim($::FORM{desc} || '');
    my $regexp = trim($::FORM{regexp} || '');
    # convert an undefined value in the inactive field to zero
    # (this occurs when the inactive checkbox is not checked
    # and the browser does not send the field to the server)
    my $isactive = $::FORM{isactive} || 0;

    unless ($name) {
        ShowError("新規グループの名前を入力してください。<BR><b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }
    unless ($desc) {
        ShowError("新規グループの説明を入力してください。<BR><b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }
    if (TestGroup($name)) {
        ShowError("'" . $name . "' というグループは既に存在します。<BR>" .
                  "<b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }

    if ($isactive != 0 && $isactive != 1) {
        ShowError("使用中 フラグが適切にセットされていません。Bugzilla か ブラウザの問題だと思われます。<br><b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }

    # Major hack for bit values...  perl can't handle 64-bit ints, so I can't
    # just do the math to get the next available bit number, gotta handle
    # them as strings...  also, we're actually only going to allow 63 bits
    # because that's all that opblessgroupset masks for (the high bit is off
    # to avoid signing issues).

    my @bitvals = ('1','2','4','8','16','32','64','128','256','512','1024',
                   '2048','4096','8192','16384','32768',

                   '65536','131072','262144','524288','1048576','2097152',
                   '4194304','8388608','16777216','33554432','67108864',
                   '134217728','268435456','536870912','1073741824',
                   '2147483648',

                   '4294967296','8589934592','17179869184','34359738368',
                   '68719476736','137438953472','274877906944',
                   '549755813888','1099511627776','2199023255552',
                   '4398046511104','8796093022208','17592186044416',
                   '35184372088832','70368744177664','140737488355328',

                   '281474976710656','562949953421312','1125899906842624',
                   '2251799813685248','4503599627370496','9007199254740992',
                   '18014398509481984','36028797018963968','72057594037927936',
                   '144115188075855872','288230376151711744',
                   '576460752303423488','1152921504606846976',
                   '2305843009213693952','4611686018427387904');

    # First the next available bit
    my $bit = "";
    foreach (@bitvals) {
        if ($bit eq "") {
            SendSQL("SELECT bit FROM groups WHERE bit=" . SqlQuote($_));
            if (!FetchOneColumn()) { $bit = $_; }
        }
    }
    if ($bit eq "") {
        ShowError("申しわけありません。作成できるグループの最大まで既に作られています。<BR><B>グループを追加する前に、グループを削除する必要があります。</B>");
        PutTrailer("<a href=editgroups.cgi>グループ一覧に戻る</a>");
        exit;
    }

    # Add the new group
    SendSQL("INSERT INTO groups ( " .
            "bit, name, description, isbuggroup, userregexp, isactive" .
            " ) VALUES ( " .
            $bit . "," .
            SqlQuote($name) . "," .
            SqlQuote($desc) . "," .
            "1," .
            SqlQuote($regexp) . "," . 
            $isactive . ")" );

    print "OK、完了です。<p>\n";
    print "新規グループは bit #$bit が割り当てられました。<p>";
    PutTrailer("<a href=\"editgroups.cgi?action=add\">別のグループを追加</a>",
               "<a href=\"editgroups.cgi\">グループ一覧に戻る</a>");
    exit;
}

#
# action='del' -> ask if user really wants to delete
#
# (next action would be 'delete')
#

if ($action eq 'del') {
    PutHeader("グループ削除");
    my $bit = trim($::FORM{group} || '');
    unless ($bit) {
        ShowError("グループが指定されていません。<BR><b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }
    SendSQL("SELECT bit FROM groups WHERE bit=" . SqlQuote($bit));
    if (!FetchOneColumn()) {
        ShowError("そのグループは存在しません。<BR><b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }
    SendSQL("SELECT name,description " .
            "FROM groups " .
            "WHERE bit = " . SqlQuote($bit));

    my ($name, $desc) = FetchSQLData();
    print "<table border=1>\n";
    print "<tr>";
    print "<th>Bit</th>";
    print "<th>Name</th>";
    print "<th>説明</th>";
    print "</tr>\n";
    print "<tr>\n";
    print "<td>$bit</td>\n";
    print "<td>$name</td>\n";
    print "<td>$desc</td>\n";
    print "</tr>\n";
    print "</table>\n";

    print "<FORM METHOD=POST ACTION=editgroups.cgi>\n";
    my $cantdelete = 0;
    SendSQL("SELECT login_name FROM profiles WHERE " .
            "(groupset & $bit) OR (blessgroupset & $bit)");
    if (!FetchOneColumn()) {} else {
       $cantdelete = 1;
       print "
<B>このグループに所属しているユーザがいます。ユーザがいる限りこのグループを削除できません。</B><BR>
<A HREF=\"editusers.cgi?action=list&query=" .
url_quote("(groupset & $bit) OR (blessgroupset & $bit)") . "\">そのユーザを見る</A> - <INPUT TYPE=CHECKBOX NAME=\"removeusers\">このグループからすべてのユーザを取り除く<P>
";
    }
    SendSQL("SELECT bug_id FROM bugs WHERE (groupset & $bit)");
    if (MoreSQLData()) {
       $cantdelete = 1;
       my $buglist = "0";
       while (MoreSQLData()) {
         my ($bug) = FetchSQLData();
         $buglist .= "," . $bug;
       }
       print "
<B>このグループの人だけが閲覧できるバグレポートがあります。バグレポートがこのグループを使っている限り、このグループを削除できません。</B><BR>
<A HREF=\"buglist.cgi?bug_id=$buglist\">そのレポートを見る</A> -
<INPUT TYPE=CHECKBOX NAME=\"removebugs\">すべてのバグレポートからこのグループによる制限を取り除く<BR>
<B>補足:</B>このボックスに印をつけることで、部外秘のバグレポートをグループ外の人にも公開することができます。このボックスに印を付ける前に、バグレポートの中身を確認しておくことを<B>強く</B>お勧めします。<P>
";
    }
    SendSQL("SELECT product FROM products WHERE product=" . SqlQuote($name));
    if (MoreSQLData()) {
       $cantdelete = 1;
       print "
<B>このグループは <U>$name</U> プロダクトと結び付いています。
プロダクトと結び付いている限り、このグループを削除できません。</B><BR>
<INPUT TYPE=CHECKBOX NAME=\"unbind\">このグループを削除し、<U>$name</U> プロダクトをグループ外の人にも見ることができるようにする<BR>
";
    }

    print "<H2>確認</H2>\n";
    print "<P>本当にこのグループを削除しますか?\n";
    if ($cantdelete) {
      print "<BR><B>進める前に、上記のボックスにすべて印を付けるか、指示された問題を解決しなくてはなりません。</B>";
    }
    print "<P><INPUT TYPE=SUBMIT VALUE=\"はい、削除します\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"delete\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"group\" VALUE=\"$bit\">\n";
    print "</FORM>";

    PutTrailer("<a href=editgroups.cgi>いいえ、グループ一覧に戻ります</a>");
    exit;
}

#
# action='delete' -> really delete the group
#

if ($action eq 'delete') {
    PutHeader("グループを削除中");
    my $bit = trim($::FORM{group} || '');
    unless ($bit) {
        ShowError("グループが指定されていません。<BR><b>Back</b> ボタンを押して、やり直してください。");
        PutFooter();
        exit;
    }
    SendSQL("SELECT name " .
            "FROM groups " .
            "WHERE bit = " . SqlQuote($bit));
    my ($name) = FetchSQLData();

    my $cantdelete = 0;
    my $opblessgroupset = '9223372036854775807'; # This is all 64 bits.

    SendSQL("SELECT userid FROM profiles " .
            "WHERE (groupset & $opblessgroupset)=$opblessgroupset");
    my @opusers = ();
    while (MoreSQLData()) {
      my ($userid) = FetchSQLData();
      push @opusers, $userid; # cache a list of the users with admin powers
    }
    SendSQL("SELECT login_name FROM profiles WHERE " .
            "(groupset & $bit)=$bit OR (blessgroupset & $bit)=$bit");
    if (FetchOneColumn()) {
      if (!defined $::FORM{'removeusers'}) {
        $cantdelete = 1;
      }
    }
    SendSQL("SELECT bug_id FROM bugs WHERE (groupset & $bit)=$bit");
    if (FetchOneColumn()) {
      if (!defined $::FORM{'removebugs'}) {
        $cantdelete = 1;
      }
    }
    SendSQL("SELECT product FROM products WHERE product=" . SqlQuote($name));
    if (FetchOneColumn()) {
      if (!defined $::FORM{'unbind'}) {
        $cantdelete = 1;
      }
    }

    if ($cantdelete == 1) {
      ShowError("このグループは削除できません。なぜなら、このグループを参照しているデータベース上のレコード(記録) があるからです。すべてのレコードを削除するかこのグループへの参照を変更しないと、このグループは削除できません。");
      print "<A HREF=\"editgroups.cgi?action=del&group=$bit\">" .
            "影響を受けるレコード一覧を見る</A><BR>";
      PutTrailer("<a href=editgroups.cgi>グループ一覧に戻る</a>");
      exit;
    }

    SendSQL("SELECT login_name,groupset,blessgroupset FROM profiles WHERE " .
            "(groupset & $bit) OR (blessgroupset & $bit)");
    if (FetchOneColumn()) {
      SendSQL("UPDATE profiles SET groupset=(groupset-$bit) " .
              "WHERE (groupset & $bit)");
      print "すべてのユーザは group $bit から取り除かれました.<BR>";
      SendSQL("UPDATE profiles SET blessgroupset=(blessgroupset-$bit) " .
              "WHERE (blessgroupset & $bit)");
      print "group $bit を与えることのできる権限を持ったユーザから、その権限が剥奪されました。<BR>";
    }
    SendSQL("SELECT bug_id FROM bugs WHERE (groupset & $bit)");
    if (FetchOneColumn()) {
      SendSQL("UPDATE bugs SET groupset=(groupset-$bit), delta_ts=delta_ts " .
              "WHERE (groupset & $bit)");
      print "すべてのバグレポートから group bit $bit が消去されました。ほかのグループに属していないレポートは、誰からも閲覧できるようになりました。<BR>";
    }
    SendSQL("DELETE FROM groups WHERE bit=$bit");
    print "<B>Group $bit は削除されました。</B><BR>";

    foreach my $userid (@opusers) {
      SendSQL("UPDATE profiles SET groupset=$opblessgroupset " .
              "WHERE userid=$userid");
      print DBID_to_name($userid) .
            " (管理者) の group bits を復元しました<BR>\n";
    }

    PutTrailer("<a href=editgroups.cgi>グループ一覧に戻る</a>");
    exit;
}

#
# action='update' -> update the groups
#

if ($action eq 'update') {
    PutHeader("グループ情報を更新中");

    my $chgs = 0;

    foreach my $b (grep(/^name-\d*$/, keys %::FORM)) {
        if ($::FORM{$b}) {
            my $v = substr($b, 5);

# print "Old: '" . $::FORM{"oldname-$v"} . "', '" . $::FORM{"olddesc-$v"} .
#      "', '" . $::FORM{"oldregexp-$v"} . "'<br>";
# print "New: '" . $::FORM{"name-$v"} . "', '" . $::FORM{"desc-$v"} .
#      "', '" . $::FORM{"regexp-$v"} . "'<br>";

            if ($::FORM{"oldname-$v"} ne $::FORM{"name-$v"}) {
                $chgs = 1;
                SendSQL("SELECT name FROM groups WHERE name=" .
                         SqlQuote($::FORM{"name-$v"}));
                if (!FetchOneColumn()) {
                    SendSQL("SELECT name FROM groups WHERE name=" .
                             SqlQuote($::FORM{"oldname-$v"}) .
                             " && isbuggroup = 0");
                    if (FetchOneColumn()) {
                        ShowError("システムグループの名前は更新できません。$v をスキップしました。");
                    } else {
                        SendSQL("UPDATE groups SET name=" .
                                SqlQuote($::FORM{"name-$v"}) .
                                " WHERE bit=" . SqlQuote($v));
                        print "Group $v の名前を更新しました。<br>\n";
                    }
                } else {
                    ShowError("group $v に指定された名前の '" . $::FORM{"name-$v"} . 
                              "' は重複しています。<BR>" .
                              "group $v の名前の更新はスキップされました。");
                }
            }
            if ($::FORM{"olddesc-$v"} ne $::FORM{"desc-$v"}) {
                $chgs = 1;
                SendSQL("SELECT description FROM groups WHERE description=" .
                         SqlQuote($::FORM{"desc-$v"}));
                if (!FetchOneColumn()) {
                    SendSQL("UPDATE groups SET description=" .
                            SqlQuote($::FORM{"desc-$v"}) .
                            " WHERE bit=" . SqlQuote($v));
                    print "Group $v の説明を更新しました。<br>\n";
                } else {
                    ShowError("group $v に指定された名前の '" . $::FORM{"desc-$v"} .
                              "' は重複しています。<BR>" .
                              "group $v の説明の更新はスキップされました。");
                }
            }
            if ($::FORM{"oldregexp-$v"} ne $::FORM{"regexp-$v"}) {
                $chgs = 1;
                SendSQL("UPDATE groups SET userregexp=" .
                        SqlQuote($::FORM{"regexp-$v"}) .
                        " WHERE bit=" . SqlQuote($v));
                print "Group $v の user regexp は更新されました。<br>\n";
            }
            # convert an undefined value in the inactive field to zero
            # (this occurs when the inactive checkbox is not checked 
            # and the browser does not send the field to the server)
            my $isactive = $::FORM{"isactive-$v"} || 0;
            if ($::FORM{"oldisactive-$v"} != $isactive) {
                $chgs = 1;
                if ($isactive == 0 || $isactive == 1) {
                    SendSQL("UPDATE groups SET isactive=$isactive" .
                            " WHERE bit=" . SqlQuote($v));
                    print "Group $v の使用中フラグは更新されました。<br>\n";
                } else {
                    ShowError("'" . $isactive .
                              "' という値は、使用中フラグには適切ではありません。<BR>" .
                              "Bugzilla か ブラウザの問題だと思われます。<br>" . 
                              "group $v の使用中フラグの更新はスキップされました。");
                }
            }
        }
    }
    if (!$chgs) {
        print "何も変更されていません!<BR>\n";
        print "それでよいのなら、<b>Back</b> ボタンを押して、やり直してください。<p>\n";
    } else {
        print "完了です。<p>\n";
    }
    PutTrailer("<a href=editgroups.cgi>グループ一覧を見る</a>");
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

PutTrailer("<a href=editgroups.cgi>グループ一覧に戻る</a>");
