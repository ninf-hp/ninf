#!/usr/local/bin/perl -wT
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

use lib qw(.);

require "CGI.pl";
require "defparams.pl";

# Shut up misguided -w warnings about "used only once":
use vars %::param,
    %::param_default,
    @::param_list;

ConnectToDatabase();
confirm_login();

print "Content-type: text/html; charset=EUC-JP\n\n";

if (!UserInGroup("tweakparams")) {
    print "<H1>���ߤޤ��󡢤��ʤ��� 'tweakparams' ���롼�פΥ��С��ǤϤ���ޤ���</H1>\n";
    print "�Ǥ����顢�ѥ�᡼�����Խ����뤳�ȤϤǤ��ޤ���\n";
    PutFooter();
    exit;
}


PutHeader("�������ѥ�᡼������¸����");

foreach my $i (@::param_list) {
#    print "Processing $i...<BR>\n";
    if (exists $::FORM{"reset-$i"}) {
        $::FORM{$i} = $::param_default{$i};
    }
    $::FORM{$i} =~ s/\r\n?/\n/g;   # Get rid of windows/mac-style line endings.
    $::FORM{$i} =~ s/^\n$//;      # assume single linefeed is an empty string
    if ($::FORM{$i} ne Param($i)) {
        if (defined $::param_checker{$i}) {
            my $ref = $::param_checker{$i};
            my $ok = &$ref($::FORM{$i});
            if ($ok ne "") {
                print "$i �ο������ͤ�����������ޤ���: $ok<p>\n";
                print "<b>Back</b> �򲡤��ơ��⤦���٤��ᤷ�ƤߤƤ���������\n";
                PutFooter();
                exit;
            }
        }
        print "$i ���ѹ����ޤ�����<br>\n";
#      print "Old: '" . url_quote(Param($i)) . "'<BR>\n";
#      print "New: '" . url_quote($::FORM{$i}) . "'<BR>\n";
        $::param{$i} = $::FORM{$i};
    }
}


WriteParams();

unlink "data/versioncache";
print "<PRE>";
system("./syncshadowdb", "-v");
print "</PRE>";

print "OK����λ�Ǥ���<p>\n";
print "<a href=editparams.cgi>�ѥ�᡼���򤵤���Խ�����</a><p>\n";
print "<a href=query.cgi>�����ڡ��������</a>\n";
    
PutFooter();
