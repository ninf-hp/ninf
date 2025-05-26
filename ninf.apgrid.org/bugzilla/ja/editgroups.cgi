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
    PutHeader("���¤�����ޤ���","���롼�פ��Խ�","","���ε�ǽ����Ѥ��븢�¤�����ޤ���!");
    print "<H1>���ߤޤ��󡢤��ʤ��� 'creategroups' ���롼�פΥ��С��ǤϤ���ޤ���</H1>\n";
    print "���Τ��ᡢ���롼�פ��Խ����뤳�ȤϤǤ��ޤ���\n";
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
    my (@links) = ("<a href=\"./\">Bugzilla �ᥤ��ڡ��������</a>", @_);

    my $count = $#links;
    my $num = 0;
    print "<P>\n";
    foreach (@links) {
        print $_;
        if ($num == $count) {
            print "\n";
        }
        elsif ($num == $count-1) {
            print "��";
        }
        else {
            print "��";
        }
        $num++;
    }
    PutFooter();
}

#
# action='' -> No action specified, get a list.
#

unless ($action) {
    PutHeader("���롼�פ��Խ�","���롼�פ��Խ�","���롼�פ��Խ����ơ��桼�����°�����뤳�Ȥ��Ǥ��ޤ���");

    print "<form method=post action=editgroups.cgi>\n";
    print "<table border=1>\n";
    print "<tr>";
    print "<th>Bit</th>";
    print "<th>Name</th>";
    print "<th>����</th>";
    print "<th>User RegExp</th>";
    print "<th>������</th>";
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
        print "<td align=center valign=middle><a href=\"editgroups.cgi?action=del&group=$bit\">���</a></td>\n";
        print "</tr>\n";
    }

    print "<tr>\n";
    print "<td colspan=5></td>\n";
    print "<td><a href=\"editgroups.cgi?action=add\">���롼���ɲ�</a></td>\n";
    print "</tr>\n";
    print "</table>\n";
    print "<input type=hidden name=\"action\" value=\"update\">";
    print "<input type=submit value=\"�ѹ�������\">\n";

    print "<p>";
    print "<b>Name</b> �ϡ�cgi �ե��������� UserInGroup() �ؿ��ǻ��Ѥ���Ƥ��ޤ����ۤ��ˤ⡢�᡼��ǥХ���Ͽ�򤹤�ͤ����Х���ݡ��Ȥ�����Υ��롼�פ��������Ǥ���褦�����¤��뤿��ˤ���Ѥ���ޤ���<p>";
    print "<b>����</b> �ϡ��Х���ݡ��Ȥ�ɽ��������ΤǤ����Х���ݡ��Ȥ�Ʊ�����롼�פΥ��С����������Ǥ���褦�����¤��뤫���ʤ����������Ȥ���ɽ������ޤ���<p>";
    print "<b>User RegExp</b> �ϥ��ץ����Ǥ����⤷���������Ϥ���Ƥ���ȡ���������ɽ���˥ޥå�����᡼�륢�ɥ쥹�ǿ������������줿�桼���ˤϡ���ưŪ�ˤ��Υ��롼�פ˽�°�����¤�Ϳ�����ޤ���<p>";
    print "<b>������</b>�ե饰�Ϥ��Υ��롼�פ������椫�ɤ�������ޤ������Ѥ���ߤ�����硢�����Х���ݡ��Ȥ򤽤Υ��롼�פ˽�°�����뤳�Ȥ��Ǥ��ʤ��ʤ�ޤ�����������ߤˤʤ��������Ͽ���줿��ݡ��Ȥϡ����Υ��롼�פ˽�°��³���ޤ���
���롼�פ˽�°����Х������䤵�ʤ���ˡ�Ȥ��ƥ��롼�פ������ߤˤ��뤳�Ȥϡ����롼�פ���������ϲ����ʽ��֤Ǥ���
<p>����˲ä��ơ��ʲ��Υ��롼�פ�����¸�ߤ��ޤ��������ϥ桼�����¤���Ƥ��륰�롼�פǤ������Υ��롼�פˤ����Ƥ� User RegExp ���Խ����뤳�Ȥ����Ǥ��ޤ��󡣥��롼��̾�ˤ�����̾������Ѥ��Ƥ��ޤ�ʤ��褦����Ĥ��Ƥ���������
<p>��­����Ȥ��Υڡ�������Ĥ�����ѹ��������ץܥ���Ϥɤ���⡢��Ȳ��Υơ��֥���ѹ���ޤȤ���������Ƥ��ޤ��ޤ�����Ĥ���Τ�ñ������������Ǥ���<p>";

    print "<table border=1>\n";
    print "<tr>";
    print "<th>Bit</th>";
    print "<th>Name</th>";
    print "<th>����</th>";
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
    print "<input type=submit value=\"�ѹ�������\">\n";
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
    PutHeader("���롼���ɲ�");

    print "<FORM METHOD=POST ACTION=editgroups.cgi>\n";
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR>\n";
    print "<th>New Name</th>";
    print "<th>����</th>";
    print "<th>New User RegExp</th>";
    print "<th>������</th>";
    print "</tr><tr>";
    print "<td><input size=20 name=\"name\"></td>\n";
    print "<td><input size=40 name=\"desc\"></td>\n";
    print "<td><input size=30 name=\"regexp\"></td>\n";
    print "<td><input type=\"checkbox\" name=\"isactive\" value=\"1\" checked></td>\n";
    print "</TR></TABLE>\n<HR>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"�ɲ�\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"new\">\n";
    print "</FORM>";

    print "<p>";
    print "<b>Name</b> �ϡ�cgi �ե��������� UserInGroup() �ؿ��ǻ��Ѥ���Ƥ��ޤ����ۤ��ˤ⡢�᡼��ǥХ���Ͽ�򤹤�ͤ����Х���ݡ��Ȥ�����Υ��롼�פ��������Ǥ���褦�����¤��뤿��ˤ���Ѥ���ޤ��������ޤ�뤳�ȤϤǤ��ޤ���<p>";
    print "<b>����</b> �ϡ��Х���ݡ��Ȥ�ɽ��������ΤǤ����Х���ݡ��Ȥ�Ʊ�����롼�פΥ��С����������Ǥ���褦�����¤��뤫���ʤ����������Ȥ���ɽ������ޤ���<p>";
    print "<b>������</b>�ե饰�Ϥ��Υ��롼�פ������椫�ɤ�������ޤ������Ѥ���ߤ�����硢�����Х���ݡ��Ȥ򤽤Υ��롼�פ˽�°�����뤳�Ȥ��Ǥ��ʤ��ʤ�ޤ�����������ߤˤʤ��������Ͽ���줿��ݡ��Ȥϡ����Υ��롼�פ˽�°��³���ޤ���
���롼�פ˽�°����Х������䤵�ʤ���ˡ�Ȥ��ƥ��롼�פ������ߤˤ��뤳�Ȥϡ����롼�פ���������ϲ����ʽ��֤Ǥ���<b>��­: ���롼�פ���ȡ������餯����������ˤ������ʤ�ޤ������ξ�硢������˰���Ĥ����ޤޤˤ��Ƥ���ɬ�פ�����ޤ���</b><p>";
    print "<b>User RegExp</b> �ϥ��ץ����Ǥ����⤷���������Ϥ���Ƥ���ȡ���������ɽ���˥ޥå�����᡼�륢�ɥ쥹�ǿ������������줿�桼���ˤϡ���ưŪ�ˤ��Υ��롼�פ˽�°�����¤�Ϳ�����ޤ���<p>";

    PutTrailer("<a href=editgroups.cgi>���롼�װ��������</a>");
    exit;
}



#
# action='new' -> add group entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("�������롼�פ��ɲ���");

    # Cleanups and valididy checks
    my $name = trim($::FORM{name} || '');
    my $desc = trim($::FORM{desc} || '');
    my $regexp = trim($::FORM{regexp} || '');
    # convert an undefined value in the inactive field to zero
    # (this occurs when the inactive checkbox is not checked
    # and the browser does not send the field to the server)
    my $isactive = $::FORM{isactive} || 0;

    unless ($name) {
        ShowError("�������롼�פ�̾�������Ϥ��Ƥ���������<BR><b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
        PutFooter();
        exit;
    }
    unless ($desc) {
        ShowError("�������롼�פ����������Ϥ��Ƥ���������<BR><b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
        PutFooter();
        exit;
    }
    if (TestGroup($name)) {
        ShowError("'" . $name . "' �Ȥ������롼�פϴ���¸�ߤ��ޤ���<BR>" .
                  "<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
        PutFooter();
        exit;
    }

    if ($isactive != 0 && $isactive != 1) {
        ShowError("������ �ե饰��Ŭ�ڤ˥��åȤ���Ƥ��ޤ���Bugzilla �� �֥饦����������Ȼפ��ޤ���<br><b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
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
        ShowError("�����櫓����ޤ��󡣺����Ǥ��륰�롼�פκ���ޤǴ��˺���Ƥ��ޤ���<BR><B>���롼�פ��ɲä������ˡ����롼�פ�������ɬ�פ�����ޤ���</B>");
        PutTrailer("<a href=editgroups.cgi>���롼�װ��������</a>");
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

    print "OK����λ�Ǥ���<p>\n";
    print "�������롼�פ� bit #$bit ��������Ƥ��ޤ�����<p>";
    PutTrailer("<a href=\"editgroups.cgi?action=add\">�̤Υ��롼�פ��ɲ�</a>",
               "<a href=\"editgroups.cgi\">���롼�װ��������</a>");
    exit;
}

#
# action='del' -> ask if user really wants to delete
#
# (next action would be 'delete')
#

if ($action eq 'del') {
    PutHeader("���롼�׺��");
    my $bit = trim($::FORM{group} || '');
    unless ($bit) {
        ShowError("���롼�פ����ꤵ��Ƥ��ޤ���<BR><b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
        PutFooter();
        exit;
    }
    SendSQL("SELECT bit FROM groups WHERE bit=" . SqlQuote($bit));
    if (!FetchOneColumn()) {
        ShowError("���Υ��롼�פ�¸�ߤ��ޤ���<BR><b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
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
    print "<th>����</th>";
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
<B>���Υ��롼�פ˽�°���Ƥ���桼�������ޤ����桼��������¤ꤳ�Υ��롼�פ����Ǥ��ޤ���</B><BR>
<A HREF=\"editusers.cgi?action=list&query=" .
url_quote("(groupset & $bit) OR (blessgroupset & $bit)") . "\">���Υ桼���򸫤�</A> - <INPUT TYPE=CHECKBOX NAME=\"removeusers\">���Υ��롼�פ��餹�٤ƤΥ桼���������<P>
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
<B>���Υ��롼�פοͤ����������Ǥ���Х���ݡ��Ȥ�����ޤ����Х���ݡ��Ȥ����Υ��롼�פ�ȤäƤ���¤ꡢ���Υ��롼�פ����Ǥ��ޤ���</B><BR>
<A HREF=\"buglist.cgi?bug_id=$buglist\">���Υ�ݡ��Ȥ򸫤�</A> -
<INPUT TYPE=CHECKBOX NAME=\"removebugs\">���٤ƤΥХ���ݡ��Ȥ��餳�Υ��롼�פˤ�����¤������<BR>
<B>��­:</B>���Υܥå����˰���Ĥ��뤳�Ȥǡ�������ΥХ���ݡ��Ȥ򥰥롼�׳��οͤˤ�������뤳�Ȥ��Ǥ��ޤ������Υܥå����˰����դ������ˡ��Х���ݡ��Ȥ���Ȥ��ǧ���Ƥ������Ȥ�<B>����</B>�����ᤷ�ޤ���<P>
";
    }
    SendSQL("SELECT product FROM products WHERE product=" . SqlQuote($name));
    if (MoreSQLData()) {
       $cantdelete = 1;
       print "
<B>���Υ��롼�פ� <U>$name</U> �ץ�����Ȥȷ���դ��Ƥ��ޤ���
�ץ�����Ȥȷ���դ��Ƥ���¤ꡢ���Υ��롼�פ����Ǥ��ޤ���</B><BR>
<INPUT TYPE=CHECKBOX NAME=\"unbind\">���Υ��롼�פ�������<U>$name</U> �ץ�����Ȥ򥰥롼�׳��οͤˤ⸫�뤳�Ȥ��Ǥ���褦�ˤ���<BR>
";
    }

    print "<H2>��ǧ</H2>\n";
    print "<P>�����ˤ��Υ��롼�פ������ޤ���?\n";
    if ($cantdelete) {
      print "<BR><B>�ʤ�����ˡ��嵭�Υܥå����ˤ��٤ư����դ��뤫���ؼ����줿������褷�ʤ��ƤϤʤ�ޤ���</B>";
    }
    print "<P><INPUT TYPE=SUBMIT VALUE=\"�Ϥ���������ޤ�\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"delete\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"group\" VALUE=\"$bit\">\n";
    print "</FORM>";

    PutTrailer("<a href=editgroups.cgi>�����������롼�װ��������ޤ�</a>");
    exit;
}

#
# action='delete' -> really delete the group
#

if ($action eq 'delete') {
    PutHeader("���롼�פ�����");
    my $bit = trim($::FORM{group} || '');
    unless ($bit) {
        ShowError("���롼�פ����ꤵ��Ƥ��ޤ���<BR><b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������");
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
      ShowError("���Υ��롼�פϺ���Ǥ��ޤ��󡣤ʤ��ʤ顢���Υ��롼�פ򻲾Ȥ��Ƥ���ǡ����١�����Υ쥳����(��Ͽ) �����뤫��Ǥ������٤ƤΥ쥳���ɤ������뤫���Υ��롼�פؤλ��Ȥ��ѹ����ʤ��ȡ����Υ��롼�פϺ���Ǥ��ޤ���");
      print "<A HREF=\"editgroups.cgi?action=del&group=$bit\">" .
            "�ƶ��������쥳���ɰ����򸫤�</A><BR>";
      PutTrailer("<a href=editgroups.cgi>���롼�װ��������</a>");
      exit;
    }

    SendSQL("SELECT login_name,groupset,blessgroupset FROM profiles WHERE " .
            "(groupset & $bit) OR (blessgroupset & $bit)");
    if (FetchOneColumn()) {
      SendSQL("UPDATE profiles SET groupset=(groupset-$bit) " .
              "WHERE (groupset & $bit)");
      print "���٤ƤΥ桼���� group $bit �����������ޤ���.<BR>";
      SendSQL("UPDATE profiles SET blessgroupset=(blessgroupset-$bit) " .
              "WHERE (blessgroupset & $bit)");
      print "group $bit ��Ϳ���뤳�ȤΤǤ��븢�¤���ä��桼�����顢���θ��¤���å����ޤ�����<BR>";
    }
    SendSQL("SELECT bug_id FROM bugs WHERE (groupset & $bit)");
    if (FetchOneColumn()) {
      SendSQL("UPDATE bugs SET groupset=(groupset-$bit), delta_ts=delta_ts " .
              "WHERE (groupset & $bit)");
      print "���٤ƤΥХ���ݡ��Ȥ��� group bit $bit ���õ��ޤ������ۤ��Υ��롼�פ�°���Ƥ��ʤ���ݡ��Ȥϡ�ï���������Ǥ���褦�ˤʤ�ޤ�����<BR>";
    }
    SendSQL("DELETE FROM groups WHERE bit=$bit");
    print "<B>Group $bit �Ϻ������ޤ�����</B><BR>";

    foreach my $userid (@opusers) {
      SendSQL("UPDATE profiles SET groupset=$opblessgroupset " .
              "WHERE userid=$userid");
      print DBID_to_name($userid) .
            " (������) �� group bits ���������ޤ���<BR>\n";
    }

    PutTrailer("<a href=editgroups.cgi>���롼�װ��������</a>");
    exit;
}

#
# action='update' -> update the groups
#

if ($action eq 'update') {
    PutHeader("���롼�׾���򹹿���");

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
                        ShowError("�����ƥ॰�롼�פ�̾���Ϲ����Ǥ��ޤ���$v �򥹥��åפ��ޤ�����");
                    } else {
                        SendSQL("UPDATE groups SET name=" .
                                SqlQuote($::FORM{"name-$v"}) .
                                " WHERE bit=" . SqlQuote($v));
                        print "Group $v ��̾���򹹿����ޤ�����<br>\n";
                    }
                } else {
                    ShowError("group $v �˻��ꤵ�줿̾���� '" . $::FORM{"name-$v"} . 
                              "' �Ͻ�ʣ���Ƥ��ޤ���<BR>" .
                              "group $v ��̾���ι����ϥ����åפ���ޤ�����");
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
                    print "Group $v �������򹹿����ޤ�����<br>\n";
                } else {
                    ShowError("group $v �˻��ꤵ�줿̾���� '" . $::FORM{"desc-$v"} .
                              "' �Ͻ�ʣ���Ƥ��ޤ���<BR>" .
                              "group $v �������ι����ϥ����åפ���ޤ�����");
                }
            }
            if ($::FORM{"oldregexp-$v"} ne $::FORM{"regexp-$v"}) {
                $chgs = 1;
                SendSQL("UPDATE groups SET userregexp=" .
                        SqlQuote($::FORM{"regexp-$v"}) .
                        " WHERE bit=" . SqlQuote($v));
                print "Group $v �� user regexp �Ϲ�������ޤ�����<br>\n";
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
                    print "Group $v �λ�����ե饰�Ϲ�������ޤ�����<br>\n";
                } else {
                    ShowError("'" . $isactive .
                              "' �Ȥ����ͤϡ�������ե饰�ˤ�Ŭ�ڤǤϤ���ޤ���<BR>" .
                              "Bugzilla �� �֥饦����������Ȼפ��ޤ���<br>" . 
                              "group $v �λ�����ե饰�ι����ϥ����åפ���ޤ�����");
                }
            }
        }
    }
    if (!$chgs) {
        print "�����ѹ�����Ƥ��ޤ���!<BR>\n";
        print "����Ǥ褤�Τʤ顢<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������<p>\n";
    } else {
        print "��λ�Ǥ���<p>\n";
    }
    PutTrailer("<a href=editgroups.cgi>���롼�װ����򸫤�</a>");
    exit;
}

#
# No valid action found
#

PutHeader("���顼");
print "���ʤ������򤷤����ä����μ꤬�����Ĥ���ޤ���Ǥ�����<BR>\n";

foreach ( sort keys %::FORM) {
    print "$_: $::FORM{$_}<BR>\n";
}

PutTrailer("<a href=editgroups.cgi>���롼�װ��������</a>");
