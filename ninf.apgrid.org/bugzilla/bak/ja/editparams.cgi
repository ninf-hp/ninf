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
# Contributor(s): Terry Weissman <terry@mozilla.org>


use diagnostics;
use strict;
use lib ".";

require "CGI.pl";
require "defparams.pl";

# Shut up misguided -w warnings about "used only once":
use vars @::param_desc,
    @::param_list;

ConnectToDatabase();
confirm_login();

print "Content-type: text/html; charset=EUC-JP\n\n";

if (!UserInGroup("tweakparams")) {
    print "<H1>���ߤޤ��󡢤��ʤ��� 'tweakparams' ���롼�פΥ��С��ǤϤ���ޤ���</H1>\n";
    print "���Τ��ᡢ�ѥ�᡼�����Խ����뤳�ȤϤǤ��ޤ���\n";
    PutFooter();
    exit;
}



PutHeader("�ѥ�᡼���Խ�");

print "Bugzilla �δ���Ū�ʱ��ѥѥ�᡼�����Խ��Ǥ��ޤ���\n";
print "��դ��Ƥ�������!\n";
print "<p>\n";
print "Reset �˰���Ĥ������٤Ƥι��ܤϡ��ǥե�����ͤ����ޤ���\n";

print "<form method=post action=doeditparams.cgi><table>\n";

my $rowbreak = "<tr><td colspan=2><hr></td></tr>";
print $rowbreak;

foreach my $i (@::param_list) {
    print "<tr><th align=right valign=top>$i:</th><td>$::param_desc{$i}</td></tr>\n";
    print "<tr><td valign=top><input type=checkbox name=reset-$i>Reset</td><td>\n";
    my $value = Param($i);
    SWITCH: for ($::param_type{$i}) {
        /^t$/ && do {
            print "<input size=80 name=$i value=\"" .
                value_quote($value) . "\">\n";
            last SWITCH;
        };
        /^l$/ && do {
            print "<textarea wrap=hard name=$i rows=10 cols=80>" .
                value_quote($value) . "</textarea>\n";
            last SWITCH;
        };
        /^b$/ && do {
            my $on;
            my $off;
            if ($value) {
                $on = "checked";
                $off = "";
            } else {
                $on = "";
                $off = "checked";
            }
            print "<input type=radio name=$i value=1 $on>On\n";
            print "<input type=radio name=$i value=0 $off>Off\n";
            last SWITCH;
        };
        # DEFAULT
        print "<font color=red><blink>�����ʥѥ�᡼���η��� $::param_type{$i}!!!</blink></font>\n";
    }
    print "</td></tr>\n";
    print $rowbreak;
}

print "<tr><th align=right valign=top>version:</th><td>
���� Bugzilla �ΥС�����󡣤����Ǥ��ѹ��Ǥ��ޤ��󡣤�������
<tt>%version%</tt> �򤳤Τ褦�ʥѥ�᡼��������Ǥ���Ȥ����
���Ѥ��뤳�Ȥ��Ǥ��ޤ���</td></tr>
<tr><td></td><td>" . Param('version') . "</td></tr>";

print "</table>\n";

print "<input type=reset value=\"�ե�����ν����\"><br>\n";
print "<input type=submit value=\"�ѹ�������\">\n";

print "</form>\n";

print "<p><a href=query.cgi>��Ȥ��˴����Ƹ����ڡ��������</a>\n";
PutFooter();
