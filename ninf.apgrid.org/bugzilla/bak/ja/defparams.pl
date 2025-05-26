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
#                 Dawn Endico <endico@mozilla.org>
#                 Dan Mosedale <dmose@mozilla.org>
#                 Joe Robins <jmrobins@tgix.com>
#                 Jake <jake@acutex.net>
#

# This file defines all the parameters that we have a GUI to edit within
# Bugzilla.

# ATTENTION!!!!   THIS FILE ONLY CONTAINS THE DEFAULTS.
# You cannot change your live settings by editing this file.
# Only adding new parameters is done here.  Once the parameter exists, you 
# must use %baseurl%/editparams.cgi from the web to edit the settings.

use diagnostics;
use strict;

# Shut up misguided -w warnings about "used only once".  For some reason,
# "use vars" chokes on me when I try it here.

sub defparams_pl_sillyness {
    my $zz;
    $zz = %::param_checker;
    $zz = %::param_desc;
    $zz = %::param_type;
}

sub WriteParams {
    foreach my $i (@::param_list) {
        if (!defined $::param{$i}) {
            $::param{$i} = $::param_default{$i};
            if (!defined $::param{$i}) {
                die "No default parameter ever specified for $i";
            }
        }
    }

    require File::Temp;
    my ($fh, $tmpname) = File::Temp::tempfile("params.XXXXX",
                                              DIR=>'data');
    my $v = $::param{'version'};
    delete $::param{'version'};  # Don't write the version number out to
                                # the params file.
    print $fh (GenerateCode('%::param'));
    $::param{'version'} = $v;
    print $fh "1;\n";
    close $fh;
    rename $tmpname, "data/params" || die "Can't rename $tmpname to data/params";
    ChmodDataFile('data/params', 0666);
}
    

sub DefParam {
    my ($id, $desc, $type, $default, $checker) = (@_);
    push @::param_list, $id;
    $::param_desc{$id} = $desc;
    $::param_type{$id} = $type;
    $::param_default{$id} = $default;
    if (defined $checker) {
        $::param_checker{$id} = $checker;
    }
}


sub check_numeric {
    my ($value) = (@_);
    if ($value !~ /^[0-9]+$/) {
        return "must be a numeric value";
    }
    return "";
}
    
sub check_shadowdb {
    my ($value) = (@_);
    $value = trim($value);
    if ($value eq "") {
        return "";
    }
    SendSQL("SHOW DATABASES");
    while (MoreSQLData()) {
        my $n = FetchOneColumn();
        if (lc($n) eq lc($value)) {
            return "�ǡ����١���$n �ϴ���¸�ߤ��ޤ����⤷�����ˤ���̾����Хå����å��Ѥ˻��Ѥ������ʤ�С�����Ĥ��ƺ�����ǡ����١�����ɤ����ذܤ��Ƥ�����ľ���Ƥ���������";
        }
    }
    SendSQL("CREATE DATABASE $value");
    SendSQL("INSERT INTO shadowlog (command) VALUES ('SYNCUP')", 1);
    return "";
}

@::param_list = ();



# OK, here are the definitions themselves.
#
# The type of parameters (the third parameter to DefParam) can be one
# of the following:
#
# t -- A short text entry field (suitable for a single line)
# l -- A long text field (suitable for many lines)
# b -- A boolean value (either 1 or 0)

DefParam("maintainer",
         "���� Bugzilla �δ����ԤΥ᡼�륢�ɥ쥹��",
         "t",
         'THE MAINTAINER HAS NOT YET BEEN SET');

DefParam("urlbase",
         "Bugzilla �� URL �����˲ä���URL��",
         "t",
         "http://cvs-mirror.mozilla.org/webtools/bugzilla/",
         \&check_urlbase);

sub check_urlbase {
    my ($url) = (@_);
    if ($url !~ m:^http.*/$:) {
        return "http �ǻϤޤꥹ��å���(/)�ǽ���롢������ URL ɽ���ǽ񤤤Ƥ���������";
    }
    return "";
}

DefParam("cookiepath", 
  "Bugzilla �����֤��� document root �ʲ��ˤ���ǥ��쥯�ȥ�ѥ�������å��� (/) �ǻϤޤ뤳�Ȥ��ǧ���Ƥ���������<!--
Directory path under your document root that holds your Bugzilla installation. Make sure to begin with a /. -->",
  "t",
  "/");

DefParam("usequip",
        "����ˤ���ȡ�Bugzilla �ϸ�����̥ꥹ�Ȥξ����˥إåɥ饤�� (quip) ��ɽ�����ޤ����إåɥ饤��ϥ桼�����ɲä��뤳�Ȥ��Ǥ��ޤ���",
        "b",
        1);

# Added parameter - JMR, 2/16/00
DefParam("usebuggroups",
         "����ˤ���ȡ�Bugzilla �ϳƥץ�����Ȥ˴�Ϣ�����Х����롼�פ��������褦�ˤʤꡢ�������˻��Ѥ��ޤ���<!--
If this is on, Bugzilla will associate a bug group with each product in the database, and use it for querying bugs.-->",
         "b",
         0); 

# Added parameter - JMR, 2/16/00
DefParam("usebuggroupsentry",
         "����ˤ���ȡ��ץ�����ȥХ����롼�פ���Ѥ��ơ��Х�����Ͽ�Ǥ���桼�������¤��ޤ���usebuggroups �⥪��ˤ��Ƥ���������",
         "b",
         0); 

DefParam("shadowdb",
         "�ɤ߽Ф����ѤΥ��ԡ��Ȥʤ롢�̤Υǡ����١�����̾������ꤷ�ޤ������Τ��Ȥˤ�ꡢĹ���٤��ɤ߽Ф������Ϥ���DB���Ф��ƹԤ���褦�ˤʤꡢ���Τۤ���DB�������å����ʤ��Ƥ���褦�ˤʤ�ޤ���<br>ʸ��������Ϥ���ȡ�����̾���ǥǡ����١�����������ޤ������߻��Ѥ���Ƥ���ǡ����١���̾�ϻ��ꤷ�ʤ��Ǥ�������!",
         "t",
         "",
         \&check_shadowdb);

DefParam("queryagainstshadowdb",
         "����򥪥�ˤ��ơ������ shadowdb �����åȤ��Ƥ���ȡ������ϥ���ɥ�DB�˹Ԥ��ޤ���",
         "b",
         0);
         

# Adding in four parameters for LDAP authentication.  -JMR, 7/28/00
DefParam("useLDAP",
         "����ˤ���ȡ��桼����ǧ�ڤˡ�Bugzilla �ǡ����١���������� LDAP �ǥ��쥯�ȥ����Ѥ��ޤ���(�桼�����ץ�ե�����ϥǡ����١����Τۤ��˳�Ǽ����ޤ������᡼�륢�ɥ쥹�ˤ�ä� LDAP �桼�����Ⱦȹ礵��ޤ�)��",
         "b",
         0);


DefParam("LDAPserver",
         "LDAP�����ФΥ��ɥ쥹(:�ݡ���)��(��: ldap.company.com �ޤ��� ldap.company.com:�ݡ����ֹ�)",
         "t",
         "");


DefParam("LDAPBaseDN",
         "�桼����ǧ�ڤ��Ф���١���DN(����̾)��(��: \"ou=People,o=Company\")",
         "t",
         "");


DefParam("LDAPmailattribute",
         "LDAP�ǥ��쥯�ȥ����ǡ��᡼�륢�ɥ쥹��ޤ�桼������°��̾��",
         "t",
         "mail");
#End of LDAP parameters


DefParam("mostfreqthreshold",
         "<A HREF=\"duplicates.cgi\">�Ǥ����ˤ���𤵤��Х��Υڡ���</a>�˷Ǻܤ���뤿��˺����ɬ�פʥХ��ν�ʣ�����⤷�礭�ʥǡ����١��������ꡢ���Υڡ������ɤ߹���Τ˻��֤�������ΤǤ���С����ο������䤷�ƤߤƤ���������",
         "t",
         "2");


DefParam("mybugstemplate",
         "�ֻ�ΥХ��פΥꥹ�Ȥ�ƤӽФ������ URL��%userid% �ϥ桼���Υ�����̾���֤��������ޤ���",
         "t",
         "buglist.cgi?bug_status=NEW&amp;bug_status=ASSIGNED&amp;bug_status=REOPENED&amp;email1=%userid%&amp;emailtype1=exact&amp;emailassigned_to1=1&amp;emailreporter1=1");
    

DefParam("shutdownhtml",
         "���ι��ܤ����Ǥʤ��Ȥ��ϡ�Bugzilla �Ϥޤä������ѤǤ��ʤ��ʤꡢ��������ˤ��Υƥ����Ȥ� Bugzilla �ΤɤΥڡ����Ǥ�ɽ������ޤ���",
         "l",
         "");

DefParam("sendmailnow",
         "����ˤ���ȡ�Bugzilla �� sendmail �ˤɤΥ᡼��⤹�������դ���褦�ؼ����ޤ����⤷¿���Υ桼�����ˤ�뤿������Υ᡼��Υȥ�ե��å�������Ȥ�����ˤ���С�Bugzilla��ư����٤��ʤ�Ǥ��礦��Bugzilla�ΰ������Ϥ��������Ȥ��ˤ����ᤷ�ޤ���",
         "b",
         1);

DefParam("passwordmail",
q{�ѥ�������ΤΥ᡼����ʸ�Ǥ������Υƥ�������� %mailaddress% �Ϥ��οͤΥ᡼�륢�ɥ쥹���Ѵ�����ޤ���%login% �Ϥ��οͤΥ�����̾���Ѵ�����ޤ�(�����Ƥ��ϥ᡼�륢�ɥ쥹��Ʊ���Ǥ���) %<i>����ʳ�</i>% �ϡ����Υڡ�����������줿�ѥ�᡼�����Ѵ�����ޤ���},
         "l",
         q{From: bugzilla-daemon
To: %mailaddress%
Subject: Your Bugzilla password.

Bugzilla �����Ѥ���ˤϡ��ʲ����Ѥ��Ƥ�������:

 �᡼�륢�ɥ쥹: %login%
     �ѥ����: %password%

�ѥ���ɤ��ѹ��ϰʲ��عԤäƤ�������:

 %urlbase%userprefs.cgi

-------------------------------------------------------------------

To use the wonders of Bugzilla, you can use the following:

 E-mail address: %login%
       Password: %password%

 To change your password, go to:
 %urlbase%userprefs.cgi
});


DefParam("newchangedmail",
q{�Х����Ѳ������ä��Ȥ���������᡼�����ʸ�Ǥ�������ʸ����Ρ�%to% �ϥ᡼���������ͤ��֤��������ޤ���%bugid% �ϥХ��ֹ�ˤʤ�ޤ���%diffs% �ϥХ���ݡ��Ȥ��ѹ����Ǥ���%neworchanged$ �Ͽ����Х��ξ��� "New" �ˡ���¸�Х����ѹ��ξ��϶�ʸ����ˤʤ�ޤ���%summary% �Ϥ��ΥХ�������Ǥ���%reasonsheader% �ϲ��Τ��οͤ��᡼��������ä��Τ��Ȥ�����ͳ���ʷ�˽񤫤줿�ꥹ�Ȥˤʤ�ޤ�������ϥ᡼��إå� (���Ȥ��� X-Bugzilla-Reason) �˸����Ƥ��ޤ���%reasonsbody% �ϥ桼�������Υ᡼��������ä���ͳ�� %reasonsheader% ���ʬ����פ��񤫤�Ƥ��ޤ���%<i>����ʳ�</i>% �ϡ����Υڡ�����������줿�ѥ�᡼�����Ѵ�����ޤ���},
         "l",
"From: bugzilla-daemon
To: %to%
Subject: [Bug %bugid%] %neworchanged%%summary%
X-Bugzilla-Reason: %reasonsheader%

%urlbase%show_bug.cgi?id=%bugid%

%diffs%



%reasonsbody%");



DefParam("whinedays",
         "�Х���ݡ��Ȥ� NEW �Υ��ơ������Ǽ��դ����Τޤ޲������֤��Ƥ����������δ��¤�᤮���顢cronjob ��ô���Ԥ˷ٹ�᡼�������ޤ���",
         "t",
         7,
         \&check_numeric);


DefParam("whinemail",
         "���Ĥ����ʤ��ޤ� <b>whinedays</b>��ۤ��� NEW �Х����������ͤ�����᡼�����ʸ�����Υƥ������桢%email% ��ô���ԤΥ᡼�륢�ɥ쥹���֤��������ޤ���%userid% ��ô���ԤΥ�����̾(���̤ϥ᡼�륢�ɥ쥹��Ʊ��)���֤��������ޤ���%<i>����ʳ�</i>% �����Ϥ��Υڡ�������������褦��������֤������ޤ���<p> ���Υ�å������������� From: ���ɥ쥹��ܤ��Ƥ����ΤϤ褤�ͤ��Ǥ����⤷�᡼�뤬������������äƤ���С�ô���ԤΥ��ɥ쥹���������ȵ��դ����Ȥ��Ǥ��뤫��Ǥ���",
         "l",
         q{From: %maintainer%
To: %email%
Subject: Your Bugzilla buglist needs attention.

  [���Υ᡼��ϼ�ưŪ����������Ƥ��ޤ�]

  Bugzilla�Х������ƥ� (%urlbase%) �ˡ����ʤ���ô������Х���
  ��𤵤�Ƥ��ޤ���
  
  ������ NEW ���֤Τޤޡ�%whinedays% ���вᤷ�ޤ�����
  ���Τ��Ȥ��ǧ���ƹ�ư��ȤäƤ���������

  ����ϰ���Ū�ˤϼ���3�ĤΤ����Τ����줫�ˤʤ�ޤ�:

  (1) ���ΥХ��ˤ����н褹�뤳�Ȥˤ����ʤ� (�Х��� INVALID �Ȥ���
      �ʤ�)�������˽������Ƥ���������

  (2) ��ʬ���н褹��Х��ǤϤʤ��ȹͤ����ʤ顢¾�οͤ˳������ľ��
      �Ƥ���������
      (�ҥ��: ï��ô�����뤫ʬ����ʤ��ä��顢Ŭ�ڤʥ���ݡ��ͥ��
      �����ӡ��֥���ݡ��ͥ�Ȥν��ô���Ԥ˺Ƴ�����Ƥ���פ�
      ���򤷤Ƥ���������)

  (3) ���ΥХ��ϼ�ʬ��ô���ǤϤ���Ȼפ�����ɡ����������Ǥ��ʤ�
      �Ȥ��ϡ��֥Х������������ץ��ޥ�ɤ�ȤäƤ���������

  ���٤Ƥ� NEW�Х��Υꥹ�Ȥ�����ˤϡ��ʲ��� URL ����Ѥ��Ƥ���������

    %urlbase%buglist.cgi?bug_status=NEW&assigned_to=%userid%

  ���뤤�ϡ�����Ū�ʸ����ڡ�������Ѥ��뤳�Ȥ�Ǥ��ޤ���

    %urlbase%query.cgi

  ����ź�դ���Ƥ����Τϡ��콵�ְʾ��Ĥ�����
  ���ʤ���ô�����Ƥ��� NEW �Х��� URL �Ǥ���
  �����ΥХ����н褹��ޤǡ�������󤳤Υ�å�����������ޤ�!

});



DefParam("defaultquery",
          "�Х������ڡ����Ǥκǽ�Υǥե���ȸ������Ǥ���URL�����ǥѥ�᡼��������ꤷ�Ƥ���Τ��ɤߤˤ����Ǥ���Sorry!",
         "t",
         "bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailqa_contact2=1&order=%22Importance%22");


DefParam("letsubmitterchoosepriority",
         "����򥪥�ˤ���ȡ��Х�����𤹤�ͤ��������ͥ���̤�����Ǥ���褦�ˤʤ�ޤ������դˤ���ȡ����ǻ��ꤵ�줿ͥ���̤ˤʤ�ޤ���",
         "b",
         1);


sub check_priority {
    my ($value) = (@_);
    GetVersionTable();
    if (lsearch(\@::legal_priority, $value) < 0) {
        return "������ͥ���̤���ꤷ�Ƥ������������Τ����������٤ޤ�: " .
            join(", ", @::legal_priority);
    }
    return "";
}

DefParam("defaultpriority",
         "������Ͽ�Х��˥��åȤ����ͥ���̤Ǥ���",
         "t",
         "P2",
         \&check_priority);


DefParam("usetargetmilestone",
         "�������åȥޥ��륹�ȡ���ι��ܤ����Ѥ��ޤ���?",
         "b",
         0);

DefParam("nummilestones",
         "�������åȥޥ��륹�ȡ������Ѥ�����ˡ������ĤΥޥ��륹�ȡ����ɽ�����뤫���ꤷ�ޤ���",
         "t",
         10,
         \&check_numeric);

DefParam("curmilestone",
         "�������åȥޥ��륹�ȡ������Ѥ�����ˡ����ߺ���оݤȤʤäƤ���ޥ��륹�ȡ�������ꤷ�ޤ���",
         "t",
         1,
         \&check_numeric);

DefParam("musthavemilestoneonaccept",
         "�������åȥޥ��륹�ȡ������Ѥ�����ˡ��֥Х������������ץ��ޥ�ɤ����֤Ȥ��ϥޥ��륹�ȡ���ɬ�����åȤ���Ƥ���٤��Ǥ���?",
         "b",
         0);

DefParam("useqacontact",
         "QA ���󥿥��Ȥι��ܤ���Ѥ��ޤ���?",
         "b",
         0);

DefParam("usestatuswhiteboard",
         "�ۥ磻�ȥܡ��ɤ���Ѥ��ޤ���?",
         "b",
         0);

DefParam("usebrowserinfo",
         "�Х���ݡ��Ȥ� OS�ȥץ�åȥե�������ˡ��桼���������Ѥ��Ƥ��� Web�֥饦������ξ�������Ϥ��ޤ���?",
         "b",
         1);

DefParam("usedependencies",
         "��¸�ط� (����Х���¾�ΥХ��˰�¸���Ƥ���Ȥ����ޡ����դ����Ǥ���褦�ˤʤ�) ����Ѥ��ޤ���?",
         "b",
         1);

DefParam("usevotes",
         "�Х����Ф�����ɼ�Ǥ���褦�ˤ��ޤ���? ���ι��ܤ����̤�ȯ�����뤿��ˤϡ�<a href=\"editproducts.cgi\">�ץ�������Խ��ڡ���</a> �ǡ��ץ�����Ȥ��Ȥκ�����ɼ���� 0 �ʳ����ѹ�����ɬ�פ�����ޤ���",
         "b",
         1);

DefParam("webdotbase",
         "��¸�ط��ˤ���Х��򥰥�ղ����뤳�Ȥ��Ǥ��ޤ������Υѥ�᡼����ʲ��Τ����ɤ줫�˥��åȤ��ޤ�:
<ul>
<li>\'dot\' ���ޥ�ɤؤδ����ʥե�����ѥ� (<a href=\"http://www.graphviz.org\">GraphViz</a> �˴ޤޤ�륳�ޥ�ɤǡ�������ǥ����������Ԥʤ��ޤ�)��
</li>
<li> ��⡼�Ȥǥ���դ��������� <a href=\"http://www.research.att.com/~north/cgi-bin/webdot.cgi\">webdot �ѥå�����</a>�����֤��줿���ؤ� URL �ץ�ե�������
</li>
<li>����ϰ�¸�ط�����դ�����ԲĤˤ��롣</li>
</ul>
�ǥե�����ͤ�ï�Ǥ⥢�������Ǥ��� webdot �����ФˤʤäƤ��ޤ��������ͤ��ѹ�����Ȥ��ϡ�webdot �����Ф� data/webdot �ǥ��쥯�ȥ꤫��ե�������ɤ߹��ळ�ȤΤǤ���褦�ˤ��Ƥ����Ƥ���������Apache �ˤ����Ƥϡ�.htaccess �ե�������Խ�����в�ǽ�ˤʤ�ޤ����̤Υ����ƥ�Ǥ���ˡ�ϰۤʤ�ޤ���.htaccess �ե����뤬�ʤ��ʤä��Ȥ��ϡ�checksetup.pl ��¹Ԥ��ƺ��������Ƥ���������",
         "t",
         "http://www.research.att.com/~north/cgi-bin/webdot.cgi/%urlbase%",
         \&check_webdotbase);

sub check_webdotbase {
    my ($value) = (@_);
    $value = trim($value);
    if ($value eq "") {
        return "";
    }
    if($value !~ /^https?:/) {
        if(! -x $value) {
            return "�ե�����ѥ� \"$value\" ���������¹ԥե�����ǤϤ���ޤ��󡣥�����ǥ���դ���������Ĥ��ʤ顢'dot' �ؤδ����ʥե�����ѥ�����ꤷ�Ƥ���������";
        }
        # Check .htaccess allows access to generated images
        if(-e "data/webdot/.htaccess") {
            open HTACCESS, "data/webdot/.htaccess";
            if(! grep(/ \\\.png\$/,<HTACCESS>)) {
                print "��¸�ط�����դβ����˥��������Ǥ��ʤ����֤ˤʤäƤ��ޤ������Υե�������ѹ����Ƥ��ʤ��ΤǤ���С�data/webdot/.htaccess �������ơ�checksetup.pl ��Ƽ¹Ԥ��ƺ��ľ���Ƥ���������\n";
            }
            close HTACCESS;
        }
    }
    return "";
}

DefParam("expectbigqueries",
         "����򥪥�ˤ���ȡ�mysql ���Ф��ƥХ������򤹤����� <tt>set option SQL_BIG_TABLES=1</tt> ���������ޤ�����������Ⱦ����٤��ʤ�ޤ���������ʰ���ơ��֥��ɬ�פȤ���褦���׵���Ф��Ƥ� <tt>The table ### is full</tt> �Υ��顼���Фʤ��ʤ�ޤ���",
         "b",
         0);

DefParam("emailregexp",
         '�������᡼�륢�ɥ쥹��ɾ���˻��Ѥ�������ɽ����������ޤ����ǥե���ȤǤϡ������ʥ᡼�륢�ɥ쥹�˰��פ��ޤ���¾�ˤ⡢@ ��̵��������桼��̾ ("local usernames, no @ allowed.") �� <tt>^[^@, ]*$</tt> �Ϥ褯�Ȥ��ޤ���',
         "t",
         q:^[^@]+@[^@]+\\.[^@]+$:);

DefParam("emailregexpdesc",
         "�����ˤϡ�<tt>emailregexp</tt> �ǵ��Ĥ�����������᡼�륢�ɥ쥹�ˤĤ��Ƥ�������Ѹ�ǽ񤭤ޤ���",
         "l",
         "A legal address must contain exactly one '\@', and at least one '.' after the \@.");

DefParam("emailsuffix",
         "�᡼���ºݤ���������Ȥ��ˡ����ɥ쥹���ɲä���ʸ����Ǥ���<tt>emailregexp</tt> �������桼����̾�Τߵ����褦��������ѹ���������ɤ⡢username\@my.local.hostname ���ۿ�����������ͭ�ѤǤ���",
         "t",
         "");


DefParam("voteremovedmail",
q{�Х�������ɼ���������줿������������᡼�����ʸ�Ǥ���%to% �Ϥ��ΥХ�����ɼ���Ƥ����ͤ��֤��������ޤ���%bugid% �ϥХ��ֹ���֤��������ޤ���%reason% �ϡ��ʤ����ΥХ��������줿����û������ͳ���֤������ޤ���%votesremoved%, %votesold% ������ %votesnew% �Ϥ��줾�졢������줿��ɼ���ȡ����ΥХ��κ��������ɼ����������ɼ���ˤʤ�ޤ���%votesremovedtext%, %votesoldtext% and %votesnewtext% �ϡ�������Ʊ���Ǥ�����ʸ�Ϥˤʤ�ޤ������Ȥ��� "You had 2 votes on this bug." %count% �ϸ����ߴ����Τ���˥��ݡ��Ȥ���ޤ���%<i>����ʳ�</i>% �Ϥ��Υڡ������������뤽�줾���������֤������ޤ���},
         "l",
"From: bugzilla-daemon
To: %to%
Subject: [Bug %bugid%] Some or all of your votes have been removed.

bug %bugid% ���顢���ʤ�����ɼ�������Ĥ���������ޤ�����

%votesoldtext%

%votesnewtext%

Reason: %reason%

%urlbase%show_bug.cgi?id=%bugid%
");
         
DefParam("allowbugdeletion",
         q{�ץ�����Ȥȥ���ݡ��ͥ�ȡ��С��������Խ�����ڡ����ǡ��ץ������(�ޤ��ϥ���ݡ��ͥ�ȡ��С������) ���������Ȥ���Ϣ����Х����٤Ƥ����Ǥ���褦�ˤ��ޤ�������Ϸ빽�ݤ�������ʤΤǡ�����ˤ������ˤɤ��ǲ���������Τ����İ����Ƥ����Ƥ���������},
         "b",
         0);


DefParam("allowemailchange",
         q{�桼������ʬ���ȤΥ᡼�륢�ɥ쥹��ִĶ�����ץڡ������ѹ��Ǥ���褦�ˤʤ�ޤ�����­����ȡ������ѹ��Ϥɤ���Υ��ɥ쥹�ˤ�᡼�뤹�뤳�Ȥ�ͭ���ˤʤ뤿�ᡢ����ˤ��Ƥ�桼�����������ʤ����ɥ쥹��Ȥ����Ȥ������ΤǤϤ���ޤ���},
         "b",
         0);


DefParam("allowuserdeletion",
         q{�桼�����Խ�����ڡ����ǡ��桼���������뤳�Ȥ��Ǥ���褦�ˤʤ�ޤ������������ǡ����١������¾�Υơ��֥�ˤ��뤽�Υ桼���ؤλ��Ȥ򤦤ޤ��õ�륳���ɤϤ���ޤ��󡣤Ǥ��Τǡ����Τ褦�ʺ���ϴ��Ǥ�������ˤ������ˤɤ��ǲ���������Τ����İ����Ƥ����Ƥ���������},
         "b",
         0);

DefParam("browserbugmessage",
         "Bugzilla ���֥饦������ͽ�����ʤ��ǡ����������ä��Ȥ�������θ�����ɽ������Τ˲ä��ơ����� HTML ����Ϥ��ޤ���",
         "l",
         "this may indicate a bug in your browser.\n");

#
# Parameters to force users to comment their changes for different actions.
DefParam("commentonaccept", 
         "�Х�������������Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonclearresolution", 
         "�Х��ν�����ˡ�����ꥢ���줿�Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonconfirm", 
         "��ǧ����Ƥ��ʤ��Х� (UNCONFIRMED) ��ǧ����Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonresolve", 
         "�Х����褷���Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonreassign", 
         "�Ƴ�����Ƥ����Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonreassignbycomponent", 
         "����ݡ��ͥ�Ȥν��ô���Ԥ˺Ƴ�����Ƥ����Ȥ��������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonreopen", 
         "�Х���Ƴ� (REOPENED) ����Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonverify", 
         "�Х��� VERIFIED �ˤ���Ȥ��������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonclose", 
         "�Х��� CLOSED �ˤ���Ȥ��������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("commentonduplicate",
         "�Х��� �ۤ��ΥХ��ν�ʣ�Ǥ���ȥޡ�������Ȥ��ˡ������Ȥ�����ʤ���Фʤ�ʤ��褦�ˤ��ޤ���",
         "b", 0 );
DefParam("supportwatchers",
         "�桼������¾�οͤ�������᡼��Υ��ԡ���ʬ�ˤ��������Ƥ�館��褦�ˤ��ޤ����ٲˤ��äƤ���ͤκ����Ԥ䡢����γ�ȯ�Ԥ˴�Ϣ�����Х��� QA �򤷤Ƥ���ͤ������Ǥ���",
         "b", 0 );


DefParam("move-enabled",
         "����ˤ���ȡ�����οͤϡ�����줿�ǡ����١����˥Х���ܹԤ����뤳�Ȥ��Ǥ���褦�ˤʤ�ޤ���",
         "b",
         0);
DefParam("move-button-text",
         "Move �ܥ����ɽ�������ʸ���󡣥Х����ܹԤ��뤳�Ȥˤʤ�����������Ƥ���������",
         "t",
         'Move To Bugscape');
DefParam("move-to-url",
         "Bugzilla ����Х���ܹԤ����뤳�Ȥ���Ĥ����ǡ����١����� URL �Ǥ���",
         "t",
         '');
DefParam("move-to-address",
         "�Х���ܹԤ���ȡ��ܹ���Υǡ����١����˥᡼����������ޤ������Υǡ����١������Х���Ͽ������դ��Ƥ���᡼�륢�ɥ쥹�������Ƥ���������",
         "t",
         'bugzilla-import');
DefParam("moved-from-address",
         "�Х���ܹԤ���ȡ��ܹ���Υǡ����١����˥᡼����������ޤ������Υ᡼����������᡼�륢�ɥ쥹�������Ƥ������������顼��å������Ϥ�������������ޤ���",
         "t",
         'bugzilla-admin');
DefParam("movers",
         "�Х���ܹԤ����뤳�Ȥ��Ǥ��� �ܹԤ������Х��� (�ܹԤ˼��Ԥ����Ȥ�) �Ƴ��Ǥ��븢�¤���ĿͤΥꥹ�ȡ�",
         "t",
         '');
DefParam("moved-default-product",
         "�ۤ��Υǡ����١������餳���˰ܹԤ����Х��˳�����Ƥ���ץ�����ȡ�",
         "t",
         '');
DefParam("moved-default-component",
         "�ۤ��Υǡ����١������餳���˰ܹԤ����Х��˳�����Ƥ��륳��ݡ��ͥ�ȡ�",
         "t",
         '');

# The maximum size (in bytes) for patches and non-patch attachments.
# The default limit is 1000KB, which is 24KB less than mysql's default
# maximum packet size (which determines how much data can be sent in a
# single mysql packet and thus how much data can be inserted into the
# database) to provide breathing space for the data in other fields of
# the attachment record as well as any mysql packet overhead (I don't
# know of any, but I suspect there may be some.)

DefParam("maxpatchsize",
         "�ѥå��κ��祵���� (����Х���)��Bugzilla �Ϥ��Υ������ʾ�Υѥå��ϼ������ޤ��󡣤ɤ�ʥ������Υѥå��Ǥ�����դ��� (Web�����Ф����¤˽���) �ʤ顢�����ͤ� 0 �ˤ��Ƥ���������" ,
         "t",
         '1000');

DefParam("maxattachmentsize" , 
         "�ѥå��ʳ���ź�եե�����κ��祵���� (����Х���)��Bugzilla �Ϥ��Υ������ʾ��ź�եե�����ϼ������ޤ��󡣤ɤ�ʥ������Υե�����Ǥ�����դ��� (Web�����Ф����¤˽���) �ʤ顢�����ͤ� 0 �ˤ��Ƥ���������" , 
         "t" , 
         '1000');

1;
