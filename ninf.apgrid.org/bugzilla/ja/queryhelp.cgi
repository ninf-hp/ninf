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
# Contributor(s):         Brian Bober <boberb@rpi.edu>
#			  Terry Weissman <terry@mozilla.org>
#			  Tara Hernandez <tara@tequilarista.org>

use vars %::FORM;

use diagnostics;
use strict;

use lib qw(.);

require "CGI.pl";

ConnectToDatabase();
quietly_check_login();

GetVersionTable();

print "Content-type: text/html; charset=EUC-JP\n\n";

my $product = $::FORM{'product'};

PutHeader("Bugzilla �����ڡ����إ��","�إ��", "�����ե�����λȤ�����ؤ֥ڡ����Ǥ�");





print qq{

<br>

<form action="none"> <!-- Cause NS4.x is stupid. Die NS4.x you eeeevil eeeevil program! -->

<a name="top"></a>

<p><center><b><font size="+2">Bugzilla �θ����ե��������Ѥ��뤿��Υإ��</font></b><br>2001ǯ1��20�� - 
<a href="mailto:netdemonz\@yahoo.com">Brian Bober (netdemon)</a>.  
<BR><I>Further heavy mutiliations by <a href="mailto:tara\@tequilarista.org">Tara Heranandez</A>, April 20, 2001.</I></CENTER>

<br><center><img width="329" height="220" src="ant.jpg" border="2" alt="Da Ant"></center>

<p><br><center><h3>��Ω��</h3></center>

<p>�����ڡ����ϼ��Τ褦�ʥ���������ʬ������:

<p><a href="#bugsettings">�Х�������(Bug Settings)</a> 
<br><a href="#peopleinvolved">�ط���(People Involved)</a> 
<br><a href="#textsearch">�ƥ����ȸ����Υ��ץ����(Text Search Options)</a>
<br><a href="#moduleoptions">�⥸�塼�륪�ץ����(Module Options)</a> 
<br><a href="#advancedquerying">���٤ʸ���(Advanced Querying)</a>
<br><a href="#therest">�ե�����β�����ʬ(The Bottom Of The Form)</a>

<p>�֤錄���Ϥ⤦ <a href="http://www.mozilla.org/bugs/">Bugzilla</a> �λȤ������ΤäƤ��롣������ Bugzilla �Ȥ��Υɥ�����Ȥ�ɮ�Ԥ˴ؤ���<a href="#info">����</a>���Τꤿ����<br>
��OK���ɤ����䤬���Ĥ����Х��� Bugzilla ��̵���褦����
�Ǥϡ��ɤΤ褦�˥Х��� <a href="enter_bug.cgi">���</a>
����٤���?�� - <a href= "docs/html/Bugzilla-Guide.html#BUG_WRITING">
�ޤ��ϥ����ɥ饤����ɤ⤦</a>!

<p><br><center><h3>����(Tips)</h3></center> �����ڡ����Τ��٤Ƥ�
�ե�����ɤ�����ɬ�פϤʤ����ե�����ɤ˵������뤳�ȤǤ��ʤ��θ�������
���¤��롣�㤨�� Status �Τ褦�ʥꥹ�ȥܥå����Ǥϡ�Ctrl�򲡤��ʤ���
����å����뤳�Ȥǡ�����������뤳�Ȥ�����롣���ʤ�������
�ʤ�ޤǤϡ�����ñ��ʸ���������ꤷ��Ĺ���Х��ꥹ�Ȥ����ʬ��õ����롢
"�Ϥޤ���" ����ˡ��Ȥ����Ȥ����롣¾�οͤθ���®�٤��㲼�����ʤ�����ˡ�
������ˡ��Ȥ������ʤ��褦�ˤ��Ƥۤ������⤷������ȡ�
¿���οͤ�Ʊ���˸�����ԤäƤ��뤫�⤷��ʤ���������ǽ�Ū�ˤϡ�
��Ϥ����� Boolean Chart �ˤĤ��Ƴؤ֤��Ȥ򤪤����᤹�롣����ϤȤƤ⶯�Ϥ��������
�ޤ����ۤȤ�ɤ� Bugzilla �Υڡ�����<a href="#bottom">�ǲ���</a>�ˤϡ�
�ʥӥ��������С������ꡢ�����<a href="./">�ե��ȥڡ���</a>��
��������פʥ�󥯤�ޤ�Ǥ��롣

<p><a href="query.cgi">����</a>�������
�⤷���˥ե�����ˤʤˤ��񤭹���Ǥ���Τʤ顢
�֥饦���ǥХå��ܥ���򲡤������ȹͤ��Ƥ��뤫���Τ�ʤ���
�⤷�����ɤ߽������Ȥ��ˤϡ�<a href="#samplequery">����ץ�θ���</a>��
�¹Ԥ��Ƥߤ��ߤ���!

};





print qq{

<a name="bugsettings"></a>

<p><br><center><h3>�Х�������(Bug Settings)</h3></center>

<center>

<table width="700" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center" height="200">
<table cellspacing="0">
<tr>
<th align="left"><A HREF="queryhelp.cgi#status">Status</a>:</th>
<th align="left"><A HREF="queryhelp.cgi#resolution">Resolution</a>:</th>
<th align="left"><A HREF="queryhelp.cgi#platform">Platform</a>:</th>
<th align="left"><A HREF="queryhelp.cgi#opsys">OpSys</a>:</th>
<th align="left"><A HREF="queryhelp.cgi#priority">Priority</a>:</th>
<th align="left"><A HREF="queryhelp.cgi#severity">Severity</a>:</th>
</tr>
<tr>
<td align="left" valign="top">

<SELECT NAME="bug_status" MULTIPLE SIZE="7">
<OPTION VALUE="UNCONFIRMED">UNCONFIRMED<OPTION VALUE="NEW">NEW<OPTION VALUE="ASSIGNED">ASSIGNED<OPTION VALUE="REOPENED">REOPENED<OPTION VALUE="RESOLVED">RESOLVED<OPTION VALUE="VERIFIED">VERIFIED<OPTION VALUE="CLOSED">CLOSED</SELECT>

</td>
<td align="left" valign="top">
<SELECT NAME="resolution" MULTIPLE SIZE="7">
<OPTION VALUE="FIXED">FIXED<OPTION VALUE="INVALID">INVALID<OPTION VALUE="WONTFIX">WONTFIX<OPTION VALUE="LATER">LATER<OPTION VALUE="REMIND">REMIND<OPTION VALUE="DUPLICATE">DUPLICATE<OPTION VALUE="WORKSFORME">WORKSFORME<OPTION VALUE="MOVED">MOVED<OPTION VALUE="---">---</SELECT>

</td>
<td align="left" valign="top">
<SELECT NAME="rep_platform" MULTIPLE SIZE="7">
<OPTION VALUE="All">All<OPTION VALUE="DEC">DEC<OPTION VALUE="HP">HP<OPTION VALUE="Macintosh">Macintosh<OPTION VALUE="PC">PC<OPTION VALUE="SGI">SGI<OPTION VALUE="Sun">Sun<OPTION VALUE="Other">Other</SELECT>

</td>
<td align="left" valign="top">
<SELECT NAME="op_sys" MULTIPLE SIZE="7">
<OPTION VALUE="All">All<OPTION VALUE="Windows 3.1">Windows 3.1<OPTION VALUE="Windows 95">Windows 95<OPTION VALUE="Windows 98">Windows 98<OPTION VALUE="Windows ME">Windows ME<OPTION VALUE="Windows 2000">Windows 2000<OPTION VALUE="Windows NT">Windows NT<OPTION VALUE="Mac System 7">Mac System 7<OPTION VALUE="Mac System 7.5">Mac System 7.5<OPTION VALUE="Mac System 7.6.1">Mac System 7.6.1<OPTION VALUE="Mac System 8.0">Mac System 8.0<OPTION VALUE="Mac System 8.5">Mac System 8.5<OPTION VALUE="Mac System 8.6">Mac System 8.6<OPTION VALUE="Mac System 9.0">Mac System 9.0<OPTION VALUE="Linux">Linux<OPTION VALUE="BSDI">BSDI<OPTION VALUE="FreeBSD">FreeBSD<OPTION VALUE="NetBSD">NetBSD<OPTION VALUE="OpenBSD">OpenBSD<OPTION VALUE="AIX">AIX<OPTION VALUE="BeOS">BeOS<OPTION VALUE="HP-UX">HP-UX<OPTION VALUE="IRIX">IRIX<OPTION VALUE="Neutrino">Neutrino<OPTION VALUE="OpenVMS">OpenVMS<OPTION VALUE="OS/2">OS/2<OPTION VALUE="OSF/1">OSF/1<OPTION VALUE="Solaris">Solaris<OPTION VALUE="SunOS">SunOS<OPTION VALUE="other">other</SELECT>

</td>
<td align="left" valign="top">
<SELECT NAME="priority" MULTIPLE SIZE="7">
<OPTION VALUE="P1">P1<OPTION VALUE="P2">P2<OPTION VALUE="P3">P3<OPTION VALUE="P4">P4<OPTION VALUE="P5">P5</SELECT>

</td>
<td align="left" valign="top">
<SELECT NAME="bug_severity" MULTIPLE SIZE="7">
<OPTION VALUE="blocker">blocker<OPTION VALUE="critical">critical<OPTION VALUE="major">major<OPTION VALUE="normal">normal<OPTION VALUE="minor">minor<OPTION VALUE="trivial">trivial<OPTION VALUE="enhancement">enhancement</SELECT>
</td>
</tr>
</table>
</td>
</tr>
</table>
</center>

<br>

<b>���ơ�����</b> �� <b>������ˡ</b> �Υե�����ɤϡ��Х��Υ饤�ե�����
�������������פ��롣<b>�ץ�åȥե�����</b> �� <b>OS</b> �ϡ�
�Х���¸�ߤ���Ķ��򤢤�魯��<b>ͥ����</b> �� <b>������</b>
�����פΤ���Τ�Τ���

<a name="status"></a>
<p><b>���ơ�����</b> 

<ul>

<li><b>UNCONFIRMED</b> - ���ΥХ��������ʤ���Фʤ�ʤ�����ï���ǧ���Ƥ��ʤ����桼�������¤��äƤ���С����ΥХ���ǧ (confirm) ���� ���ơ������� NEW �ˤ��뤳�Ȥ��Ǥ��롣<a href="userprefs.cgi?tab=permissions">���ʤ��θ���</a> �Ϥ����Ǹ��뤳�Ȥ��Ǥ��롣�Х��ϡ������ʤ��褵�� RESOLVED �ˤʤ뤳�Ȥ⤢�뤱��ɡ������Ƥ��Ϥ��ΥХ���ô���Ԥˤ�äƾ�ǧ����롣UNCONFIRMED �Х��ϡ����Ԥ���Ƥ����Х����ºݤ˺Ƹ����뤳�Ȥ�ï�����Τ����ޤǤϡ�̤��ǧ (unconfirmed) �ΤޤޤǤ��롣

<li><b>NEW</b> - ���ΥХ��ϺǶ�ô���ԤΥХ��ꥹ�Ȥ��ɲä��줿��Τǡ�
����Ͻ��������ɬ�פ����롣���ξ��֤ΥХ��ϰ���������� ASSIGNED �Ȥʤ뤫��
¾�οͤ˳�����Ƥ��뤫��NEW�ΤޤޤǤ��뤫����褵��� 
RESOLVED �˥ޡ�������롣

<li><b>ASSIGNED</b> - ���ΥХ��Ϥޤ���褵��Ƥ��ʤ�������������Ǥ���ȹͤ���ï���˳�����Ƥ��Ƥ��롣�Х��Ϥ��ξ��֤���¾�οͤ˺Ƴ�����Ƥ��졢NEW �ˤʤ뤳�Ȥ����ꡢ���뤤�ϲ�褵��� RESOLVED �˥ޡ�������롣

<li><b>REOPENED</b> - ���ΥХ��ϰ��ٲ�� (resolved) ���줿�������ν�����ˡ (resolution) ����äƤ����褦�˻פ��롣���Ȥ��� WORKSFORME �Х��ϡ����󤬽��ޤꤽ�ΥХ���Ƹ��Ǥ���褦�ˤʤä��Ȥ� REOPENED �ˤʤ롣��������Х��� ASSIGNED �� RESOLVED �Τɤ��餫�ˤʤ롣

<li><b>RESOLVED</b> - ��褵�졢QA (�ʼ�ɾ��ô����) �ˤ�븡�ڤ��ԤäƤ�
�롣��������Х��Ϻ���̤�����֤� REOPENED �ˤʤ뤫��VERIFIED �ˤʤ뤫�����뤤�Ϥ��Τޤ� CLOSED �ˤ��ƽ�λ���롣

<li><b>VERIFIED</b>- QA ���Х��Ƚ�����ˡ�򸫤���ǡ�Ŭ�ڤʽ������ʤ��줿��Ʊ�դ�����

<li><b>CLOSED</b> - ���ΥХ��ϻ�����Τȸ��ʤ��졢������ˡ��Ŭ�ڤǤ��롣�Х�����𤵤�Ƥ������ʤϽ��뤷�����в٤��줿��
����ӤȤ����ɤ뤳�Ȥ�������Х��Ϥɤ�⡢REOPENED �ˤʤ롣���Υ��ơ������Ϻ��ޤǤۤȤ�ɻȤ��Ƥ��ʤ���
</ul>

<a name="resolution"></a>
<p><b>������ˡ</b> 

<p><b>������ˡ</b> �Υե�����ɤϡ��Х��˲��������ä����򼨤���ΤǤ��롣

<p>�ޤ����ν�����ˡ��¸�ߤ��ʤ�: ̤���Υ��ơ����� (NEW, ASSIGNED, REOPENED) �Τɤ줫�ˤ�����Х��Ϥ��٤ơ�������ˡ���������ꤵ��Ƥ��롣
����ʳ��ΥХ��Ϥ��٤ơ���Ҥ��������ˡ�Τɤ줫�˥ޡ�������Ƥ��롣

<ul>
<li><b>FIXED</b> - �Х��Ͻ������졢�ĥ꡼�˥����å����󤵤����줿��
<li><b>INVALID</b> - �Х��ǤϤʤ��Ȥ��줿���ꡣ
<li><b>WONTFIX</b> - ����ˤ錄�äƽ�������뤳�ȤϤʤ��Ȥ��줿���ꡣ
<li><b>LATER</b> - ���ʤθ��С������ǤϽ�������뤳�Ȥ��ʤ��Ȥ��줿���ꡣ
<li><b>REMIND</b> - ���ΥС������ǤϽ�������븫���ߤ��ʤ��������β�ǽ����ޤ��ĤäƤ������ꡣ
<li><b>DUPLICATE</b> - ����¸�ߤ��Ƥ���Х��Ƚ�ʣ���Ƥ������ꡣduplicate �ȥޡ������뤿��ˤϡ���ʣ���Ƥ���Х��ֹ椬ɬ�פǤ��ꡢ�����ֹ�ϥХ��Υ����Ȥ˽񤭹��ޤ�롣
<li><b>WORKSFORME</b> - ���ΥХ���Ƹ����뤹�٤Ƥλ�ߤ�̵�̤˽���ꡢ�����ɤ��ɤ�Ǥ⤳�Τ褦�ʤ��Ȥ����ε�����Τ������Ĥ���ʤ���硣�⤷��äȾ��󤬤��ȤǸ��줿�顢���٥Х���������ľ�����Ȥˤ������ߤ�ñ�˵�Ͽ���Ƥ�����
</ul>

<a name="platform"></a>
<p><b>�ץ�åȥե�����</b>
<p><b>�ץ�åȥե�����</b> �ե�����ɤϡ��Х�����𤵤줿�ϡ��ɥ������ץ�åȥե�����Ǥ��롣�ʲ��Υץ�åȥե������ޤ�:

<ul>
<li>All (���٤ƤΥץ�åȥե�����ǵ�����; �����ץ�åȥե�����ʥХ�)<br>
<li>Macintosh
<li>PC
<li>Sun
<li>HP
</ul>
<p><b>Note:</b> "All" ���ץ��������򤹤뤳�Ȥϡ����٤ƤΥץ�åȥե�����˳�����Ƥ�줿�Х������򤹤뤳�ȤˤϤʤ�ʤ�������Ϥ��٤ƤΥץ�åȥե�����Ǥ��ΥХ��� <b>������</b> �Ȥ����Ȥ������򤵤�롣

<a name="opsys"></a>
<p><b>OS</b>
<p><b>OS</b>�Υե�����ɤϡ��Х�����𤵤줿���ڥ졼�ƥ��󥰥����ƥ�Ǥ��롣�ʲ��Υ��ڥ졼�ƥ��󥰥����ƥ��ޤ�:

<ul>
<li>All (���٤ƤΥץ�åȥե�����ǵ�����; �����ץ�åȥե�����ΥХ�)
<li>Windows 95
<li>Windows 2000
<li>Mac System 8.0
<li>Linux
<li>Other (������OS�Τɤ�ˤ�°���ʤ�OS)<br>
</ul>

<p>���ڥ졼�ƥ��󥰥����ƥ�ϥץ�åȥե������Ť˼���������ˤ����ǤϤʤ����㤨�С�Linux �� PC �� Mac ���Τۤ��Υץ�åȥե�����Ǥ�ư��롣

<a name="priority"></a>
<p><b>ͥ����</b>

<p><b>ͥ����</b>�Ͻ������Ƚ�������뤿��ν��֤�ɽ�魯�����Υե�����ɤϥץ���ޤ䥨�󥸥˥�����ʬ�λŻ���ͥ���̤�Ĥ��뤿��˻Ȥ��Ƥ��롣ͥ���٤ˤ� P1 (��ͥ��) ���顢 P5(�Ǥ��㤤ͥ����)�ޤǤ��롣

<a name="severity"></a>
<p><b>������</b>

<p><b>������</b> �ϡ��Х��ο����٤�ɽ�魯��

<ul>
<li><b>Blocker</b> - ��ȯ�ȥƥ��Ȥΰ����ޤ���ξ����˸�����Ρ�<br>
<li><b>Critical</b> - ����å��塢�ǡ�����»��������ʥ���꡼����<br>
<li><b>Major</b> - ���פʵ�ǽ�η�ǡ��<br>
<li><b>Normal</b> - ���̤ΥХ���<br>
<li><b>Minor</b> - �ޥ��ʡ��ʵ�ǽ�η�ǡ�����뤤�ϸ��ߴ�ñ�����ؼ��ʤ�����¾�����ꡣ<br>
<li><b>Trivial</b> - ñ����Ǥ��ְ㤤��ƥ����Ȥ����֥ߥ��ʤɤΡ�ɽ�ؾ�����ꡣ<br>
<li><b>Enhancement</b> - ��ǽ��ĥ�Υꥯ�����ȡ�<br>
</ul>

};












print qq{
<a name="peopleinvolved"></a>
<p><br><center><h3>�ط���</h3></center>
<center>


<table width="390" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td height="180" align="center">

<table>
<tr>
<td valign="middle">Email:
<input name="email1" size="25" value="">&nbsp;</td><td valign="top">matching as:<br>
<SELECT NAME="emailtype1"><OPTION VALUE="regexp">regexp
<OPTION VALUE="notregexp">not regexp
<OPTION VALUE="substring">substring
<OPTION VALUE="exact">exact
</SELECT>
</td>
</tr>
<tr>
<td colspan="2" align="center">Will match any of the following selected fields:</td>
</tr>
<tr>
<td colspan=2>
<center>
<input type="checkbox" name="emailassigned_to1" value="1">Assigned To
<input type="checkbox" name="emailreporter1" value="1">Reporter

<input type="checkbox" name="emailqa_contact1" value="1">QA Contact
</center>
</td>
</tr>
<tr>
<td colspan=2 align="center">
<input type="checkbox" name="emailcc1" value="1">CC
<input type="checkbox" name="emaillongdesc1" value="1">Added comment
</td>
</tr>
</table>

</td>
</tr>
</table>
</center>
<br>

������ʬ�ϡ��������Ϥˤ��뤿��ʣ���ˤʤꡢ��ǰ�ʤ���ʬ����ˤ�����ΤȤʤäƤ��롣
�����Ǥϡ��᡼�륢�ɥ쥹�˴ط�����Х��򸡺��Ǥ��롣

<p>

�ҤȤĤΥ᡼�륢�ɥ쥹�˴ط������Х���õ������ˤ�:

<ul>
  <li> �᡼�륢�ɥ쥹�ΰ�����ƥ����ȥե�����ɤ˥����פ��롣
  <li> ���Υ��ɥ쥹���ޤޤ�Ƥ���Ȼפ���ե�����ɤΥ����å��ܥå����򥯥�å����롣
</ul>

<p>

��Ĥΰۤʤä��᡼�륢�ɥ쥹��õ�����Ȥ����롣����ξ��������Ǥ��Ƥ���Τʤ顢����ξ���Υ᡼�륢�ɥ쥹�˥ޥå�����Х�������ɽ������롣������㤨�С�Ralph �ˤ�äƺ������졢Fred �˳�����Ƥ�줿�Х��򸡺�����Ȥ���ͭ�ѤǤ��롣

<p>
�ɥ�åץ������˥塼��Ȥäơ��ɤθ�����ˡ��Ȥ��������Ǥ��롣�᡼�륢�ɥ쥹ʸ����ΰ����ˤ����ס�<a href="http://jt.mozilla.gr.jp/bugs/text-searching.html">����ɽ�� (Regular Expressions)</a>�λ��ѡ��ޤ��ϥ᡼�륢�ɥ쥹�����Τ����Ϥ��Ƥδ������׸�����
};











print qq{
<a name="textsearch"></a>
<p><br><center><h3>�ƥ����ȸ���</h3></center>
<center>



<table width="610" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center" height="210" >

<table>
<tr>
<td align="right"><a href="queryhelp.cgi#summaries">Bug summary</a>:</td>
<td><input name="short_desc" size="30" value=""></td>
<td><SELECT NAME="short_desc_type">
<OPTION VALUE="substring">case-insensitive substring
<OPTION VALUE="casesubstring">case-sensitive substring
<OPTION VALUE="allwords">all words
<OPTION VALUE="anywords">any words
<OPTION VALUE="regexp">regular expression
<OPTION VALUE="notregexp">not ( regular expression )
</SELECT></TD>
</tr>
<tr>
<td align="right"><a href="queryhelp.cgi#descriptions">A description entry</a>:</td>
<td><input name="long_desc" size="30" value=""></td>
<td><SELECT NAME="long_desc_type">
<OPTION VALUE="substring">case-insensitive substring
<OPTION VALUE="casesubstring">case-sensitive substring
<OPTION VALUE="allwords">all words
<OPTION VALUE="anywords">any words
<OPTION VALUE="regexp">regular expression
<OPTION VALUE="notregexp">not ( regular expression )
</SELECT></TD>
</tr>
<tr>
<td align="right"><a href="queryhelp.cgi#url">Associated URL</a>:</td>
<td><input name="bug_file_loc" size="30" value=""></td>
<td><SELECT NAME="bug_file_loc_type">
<OPTION VALUE="substring">case-insensitive substring
<OPTION VALUE="casesubstring">case-sensitive substring
<OPTION VALUE="allwords">all words
<OPTION VALUE="anywords">any words
<OPTION VALUE="regexp">regular expression
<OPTION VALUE="notregexp">not ( regular expression )
</SELECT></TD>
</tr>
<tr>
<td align="right"><a href="queryhelp.cgi#statuswhiteboard">Status whiteboard</a>:</td>
<td><input name="status_whiteboard" size="30" value=""></td>
<td><SELECT NAME="status_whiteboard_type">
<OPTION VALUE="substring">case-insensitive substring
<OPTION VALUE="casesubstring">case-sensitive substring
<OPTION VALUE="allwords">all words
<OPTION VALUE="anywords">any words
<OPTION VALUE="regexp">regular expression
<OPTION VALUE="notregexp">not ( regular expression )
</SELECT></TD>
</tr>

<TR>
<TD ALIGN="right"><A HREF="queryhelp.cgi#keywords">Keywords</A>:</TD>
<TD><INPUT NAME="keywords" SIZE="30" VALUE=""></TD>
<TD>
<SELECT NAME="keywords_type"><OPTION VALUE="anywords">Any of the listed keywords set
<OPTION VALUE="allwords">All of the listed keywords set
<OPTION VALUE="nowords">None of the listed keywords set
</SELECT></TD></TR>
</table>
</td></tr>
</table>
</center>
<br>


<p>���ξϤǤϡ��ͤ����Ϥ����٤ƤΥХ����椫�鸡����Ԥ����Ȥ������ (���뤤��¾�Υե�����ɤ˵������뤳�Ȥˤ�äơ���������Х���ʤꤳ�ळ�Ȥ������)�� 
����ɽ����ƥ����ȸ����ˤĤ����Τꤿ���ȹͤ���Τʤ�С�<a href="http://jt.mozilla.gr.jp/bugs/text-searching.html">Bugzilla �ƥ����ȸ���</a>
�򸫤�Ȥ������⤷��ʤ��������Υե�����ɤ�³�������ˤ�äơ��ɤθ�����ˡ��Ȥ�������ޤ롣<br>

<a name="summaries"></a>
<h4>����</h4>
<p>���ι��ܤǡ�����򸡺������������롣����ϥХ����Ԥ�ɽ��������ΤǤ��롣

<a name="descriptions"></a>
<h4>������</h4>

<p>���ι��ܤǡ������Ȥ򸡺����뤳�Ȥ��Ǥ��롣�����ȤϤɤ�ʿͤ�
�ɲä��������ǽ�����ۤȤ�ɤΥХ��ǥ����ȤϤ�äȤ��礭�ʸ�����ǽ���ꥢ����äƤ��롣
¿���θ�����̤�˾��Ȥ��ϡ������Ȥ򸡺�����Ȥ褤��
<BR><B>Note:</B>�Х�����ǡ������ȤϤҤɤ��礭���ʤ뤳�Ȥ����ꡢ���Υ����פθ����򤪤��ʤ���Ĺ�����֤������뤳�Ȥ����롣

<a name="url"></a>
<h4>URL</h4>
<p>���ι��ܤǡ�URL �Υե�����ɤ򸡺����뤳�Ȥ�����롣���ι��ܤϥХ��˴�Ϣ���륦���֥ڡ�����URL��ޤࡣ

<a name="statuswhiteboard"></a>
<h4>���ơ����� �ۥ磻�ȥܡ���</h4>
<p>���ι��ܤǡ��Х��Υۥ磻�ȥܡ��ɤ򸡺����뤳�Ȥ�����롣�ۥ磻�ȥܡ��ɤ��ޤ�Ǥ���Τϡ����󥸥˥��ν񤭹��������Ū�ʾ������
};


print qq{
<a name="keywords"></a>
<h4>�������</h4>
<br><br>���줾��ΥХ�������Υ�����ɤ���Ĥ��Ȥ�����롣�Х������Ԥ��桼����Ŭ�ڤʸ��¤���äƤ���С������Υ�����ɤ��Խ����뤳�Ȥ�����롣�ʲ��ˡ����� Bugzilla ���ݻ����Ƥ��륭����ɤΥꥹ�Ȥ�󤲤�:
};

ConnectToDatabase();

my $tableheader = qq{
<p><table border="1" cellpadding="4" cellspacing="0">
<tr bgcolor="#6666FF">
<th align="left">Name</th>
<th align="left">Description</th>
<th align="left">Bugs</th>
</tr> 
};

print $tableheader;

my $line_count = 0;
my $max_table_size = 50;

SendSQL("SELECT keyworddefs.name, keyworddefs.description, 
                COUNT(keywords.bug_id), keywords.bug_id
         FROM keyworddefs LEFT JOIN keywords ON keyworddefs.id=keywords.keywordid
         GROUP BY keyworddefs.id
         ORDER BY keyworddefs.name");

while (MoreSQLData()) {
    my ($name, $description, $bugs, $onebug) = FetchSQLData();
    if ($bugs && $onebug) {
        # This 'onebug' stuff is silly hackery for old versions of
        # MySQL that seem to return a count() of 1 even if there are
        # no matching.  So, we ask for an actual bug number.  If it
        # can't find any bugs that match the keyword, then we set the
        # count to be zero, ignoring what it had responded.
        my $q = url_quote($name);
        $bugs = qq{<A HREF="buglist.cgi?keywords=$q">$bugs</A>};
    } else {
        $bugs = "none";
    }
    if ($line_count == $max_table_size) {
        print "</table>\n$tableheader";
        $line_count = 0;
    }
    $line_count++;
    print qq{
<tr>
<th>$name</th>
<td>$description</td>
<td align="right">$bugs</td>
</tr>
};
}

print "</table><p>\n";


if (UserInGroup("editkeywords")) {
    print qq{<p><a href="editkeywords.cgi">Edit keywords</a>\n};
}












my %default;
my %type;

print qq{
<a name="moduleoptions"></a>
<p><br><center><h3>�⥸�塼�륪�ץ����</h3></center>

<br>

<p>�⥸�塼�륪�ץ����ϡ�õ�������Х����ɤΥץ�����ȡ��⥸�塼�롦�С������ʤΤ������򤹤롣��İʾ�Υץ�����ȡ��С�����󡦥���ݡ��ͥ�ȡ����뤤�ϥޥ��륹�ȡ������ꤹ�뤳�Ȥǡ�������ʤꤳ������Ǥ��롣

<p><a name="product"></a>
<h4>�ץ������</h4> 


<p>Mozilla �ץ������������Υ��֥ץ������Ȥϻ��̤ä���ΤǤ��뤬����ȯ��ξ��ʤ���ˤ��ڤ�Υ���줿�ץ�����Ȥ⤤���Ĥ����롣���줾��Υץ�����ȤϤ����ͭ�Υ���ݡ��ͥ�Ȥ��äƤ��롣

};




$line_count = 0;
$max_table_size = 50;
my @products;

$tableheader = 	qq{ <p><table border=0><tr><td>
	<table border="1" width="100%" cellpadding="4" cellspacing="0">
	<tr bgcolor="#6666FF">
	<th align="left">Product</th>
	<th align="left">Description</th></tr> };


print qq{
	$tableheader
};


SendSQL("SELECT product,description FROM products ORDER BY product");
	while (MoreSQLData()) {

	my ($product, $productdesc) = FetchSQLData();
	push (@products, $product);

	$line_count++;
	if ($line_count > $max_table_size) {
			print qq{
			</table>
			$tableheader
		};
	  	$line_count=1;
	}

	print qq{ <tr><th>$product</th><td>$productdesc</td></tr> };


}


print qq{ 	

</table></td></tr></table> };

if (UserInGroup("editcomponents")) {
    print qq{<p><a href="editproducts.cgi">Edit products</a><p>};
}

print qq{
<p><a name="version"></a>
<h4>�С������</h4>

<p>���ι��ܤ�ñ�˸����������Х��˵�Ͽ����Ƥ���С������Ǥ��롣¿���ΥХ���   Other �Ȥ����С������˥ޡ������졢����˥ޥ��륹�ȡ��󤬻��ꤵ��� (�ޥ��륹�ȡ���ˤĤ��Ƥϡ���������)��

};




 
$line_count = 0;
$tableheader = qq{ 
	<p>
	<table border="1" width="100%" cellpadding="4" cellspacing="0">
	<tr bgcolor="#6666FF">
	<th align="left">Component</th>
	<th align="left">Product</th>
	<th align="left">Description</th></tr>
};

print qq{ 	
<p><a name="component"></a>
<h4>����ݡ��ͥ��</h4>
<p>���줾��Υץ�����Ȥϥ���ݡ��ͥ�Ȥ���äƤ��ơ����줾��ˤĤ��ƤΥХ�����Ͽ�Ǥ���褦�ˤʤäƤ��롣����ݡ��ͥ�Ȥϥץ�����Ȥΰ����Ǥ��ꡢ�⥸�塼�륪���ʡ��˳�����Ƥ��롣���Υꥹ�Ȥϡ�����ݡ��ͥ�ȤȤ���˴�Ϣ����ץ�����ȤǤ��롣:
		$tableheader
};
foreach $product (@products)
{

	SendSQL("SELECT value,description FROM components WHERE program=" . SqlQuote($product) . " ORDER BY value");

	while (MoreSQLData()) {

		my ($component, $compdesc) = FetchSQLData();

		$line_count++;
		if ($line_count > $max_table_size) {
				print qq{
				</table>
				$tableheader
			};
			$line_count=0;
		}
		print qq{<tr><th>$component</th><td>$product</td><td>$compdesc</td></tr>};
	}

}

print qq{</table>};
if (UserInGroup("editcomponents")) {
    print qq{<p><a href="editcomponents.cgi">Edit components</a><p>};
}

print qq{
<p><a name="targetmilestone"></a>
<h4>�ޥ��륹�ȡ���</h4>

<p>���Υ������������򤹤뤳�Ȥˤ�ꡢ�������åȥޥ��륹�ȡ�����ͤ����åȤ��Ƥ���Х��򸡺��Ǥ��롣�ޥ��륹�ȡ���ϥС�������Ʊ�ͤΤ�ΤǤ��롣����ϻŪ�ʤ������դǡ��������絬�ϤʥХ��θ����������ꡢ���Ū���ꤷ����꡼�������߽Ф���롣�㤨�С�Mozilla.prg �� "M10" ���� "M18" �Ȥ��������Υޥ��륹�ȡ������äƤ����������ߤ� "Mozilla0.9" �Ȥ��������ˤʤäƤ��롣Bigzilla �ޥ��륹�ȡ���� "Bugzilla 2.12"��"Bugzilla 2.14" �ʤɤη����Ǥ��롣


};














print qq{
<a name="incexcoptions"></a>
<p><br><center><h3>���/����(Inclusion/Exclusion) ���ץ����</h3></center>

<center>


<table width="480" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center"  height="260" >
<table>

<tr>
<td>
<SELECT NAME="bugidtype">
<OPTION VALUE="include">Only
<OPTION VALUE="exclude" >Exclude
</SELECT>
bugs numbered: 
<INPUT TYPE="text" NAME="bug_id" VALUE="" SIZE=15>
</td>
</tr>
<tr>
<td>
Changed in the last <INPUT NAME=changedin SIZE=3 VALUE=""> days
</td>
</tr>
<tr>
<td>
Containing at least <INPUT NAME=votes SIZE=3 VALUE=""> votes
</td>
</tr>
<tr>
<td>
Where the field(s)
<SELECT NAME="chfield" MULTIPLE SIZE=4>
<OPTION VALUE="[Bug creation]">[Bug creation]<OPTION VALUE="assigned_to">assigned_to<OPTION VALUE="bug_file_loc">bug_file_loc<OPTION VALUE="bug_severity">bug_severity<OPTION VALUE="bug_status">bug_status<OPTION VALUE="component">component<OPTION VALUE="everconfirmed">everconfirmed<OPTION VALUE="groupset">groupset<OPTION VALUE="keywords">keywords<OPTION VALUE="op_sys">op_sys<OPTION VALUE="priority">priority<OPTION VALUE="product">product<OPTION VALUE="qa_contact">qa_contact<OPTION VALUE="rep_platform">rep_platform<OPTION VALUE="reporter">reporter<OPTION VALUE="resolution">resolution<OPTION VALUE="short_desc">short_desc<OPTION VALUE="status_whiteboard">status_whiteboard<OPTION VALUE="target_milestone">target_milestone<OPTION VALUE="version">version<OPTION VALUE="votes">votes
</SELECT> changed to <INPUT NAME="chfieldvalue" SIZE="10">
</td>
</tr>
<tr>
<td colspan=2>
During dates <INPUT NAME="chfieldfrom" SIZE="10" VALUE="">
to <INPUT NAME="chfieldto" SIZE="10" VALUE="Now">
</td>
</tr>
<tr>
<td>

</td>
</tr>
</table>

</td>
</tr>
</table>

</center>
<br>

<p>���/����(Inclusion/Exclusion) ���ץ����ϡ�
���Ϥ����ͤ˴�Ť�����ޤ����ͤ��Ǥ��붯�Ϥʥ��������Ǥ��롣

<P><b>�Х��ֹ�: [text] [�Τߤ�ޤࡿ���������]</b>

<p>����޶��ڤ�ΥХ��ֹ�Υꥹ�Ȥ����Ϥ��ơ�������̤ˤ����ޤࡢ�ޤ��ϸ�����̤���������뤳�Ȥ��Ǥ��롣
����Ū�ˤ��ϰϻ�������뤳�ȤȤ����Τ������㤨�� [1-1000] �Ȥ��뤳�Ȥǡ� 
1����1000�Ȥ����褦�ˡˡ���ǰ�ʤ��麣�Ϥޤ�����ʤ���

<p><b>���δ��֤˹������줿�Х��Τ�: ��� [text] ������</b>

<p>���ꤷ����������˾��֤��Ѳ������Х�

<p><b>[text] ɼ�ʾ����ɼ������Х��Τ�</b>

<p>���ꤷ���ʾ����ɼ������Х�

<p><b>���ι��ܤ� [fields]</b> �ѹ������: (���ץ����) [text]

<p>
 �����Ǥϡ��Х���ݡ��Ȥˤ�����ܤ��ͤ���ꤹ�롣�⤷�ҤȤĤ�����ʾ�ι��ܤ��������硢��¦�Υܥå����ˤ��ι��ܤΤ����ҤȤĤ����Ϥ��ʤ��ƤϤʤ�ʤ��������鿴�ԤˤϤ��ι��ܤ������̣����Τ������򤷤���������������ϥХ���ݡ��Ⱦ���Τ��ޤ��ޤʹ��ܤ˥ޥå����롣
���ץ����ϡ����ܤ��Ѳ������ͤ����Ϥ��뤳�Ȥ�Ǥ��롣���Ȥ��С��Х���ݡ��Ȥ� jon\@netscape.com ���� brian\@netscape.com �˳�����Ƥ�줿�ʤ顢assigned_to ������ �ѹ�����ͤˡ�brian\@netscape.com�פ����Ϥ��뤳�Ȥ��Ǥ��롣

<p><b>���δ�������ѹ����줿�Х��Τ�: [text] ���� [text] �ޤ�</b>

<p>����ǡ��ե�����ɤ��ѹ����줿�ͤ��ѹ����뤳�Ȥ�����롣"Now" �ϥ���ȥ꡼�Ȥ��ƻ��Ѳ�ǽ�Ǥ��롣¾�Υ���ȥ꡼��mm/dd/yyyy ���뤤�� yyyy-mm-dd �Ȥ����񼰤ǻ���Ǥ��롣


};












print qq{
<a name="advancedquerying"></a>
<p><br><center><h3>"Boolean Charts" ����Ѥ������٤ʸ���</h3></center>

<p>Bugzilla �θ����ڡ����ϡ����ʤ�Ĥ����䤹���褦���߷פ���Ƥ��롣
�����������Τ褦�ʻȤ��䤹���ϡ������ˤ�������­��⤿�餹�����ι��٤�
�����Υ��������ϡ��ȤƤ⶯�Ϥʸ������Ǥ���褦�߷פ���Ƥ��롣
������������ϳؤ֤Τ���������Τ��ñ�ʤ��ȤǤϤʤ���<br> <p> <center>

<table width="780" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center"  height="140" >
<table>
<tr><td>
<table><tr><td>&nbsp;</td><td><SELECT NAME="field0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value0-0-0" VALUE=""><INPUT TYPE="button" VALUE="Or" ><INPUT TYPE="button" VALUE="And" 
	NAME="cmd-add0-1-0"></td></tr>
    
		<tr><td>&nbsp;</td><td align="center">
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <INPUT TYPE="button" VALUE="Add another boolean chart" NAME="cmd-add1-0-0">
       
        
</td></tr>
</table>
</td></tr>
</table>
</td>
</tr>
</table>

</center> <br> <p>���٤ʸ��� (�ޤ��� �֡��ꥢ����㡼��) �ϰ�Ĥ� "term(��)" ��
��Ϥ��ޤ롣�ҤȤĤ� term ����ĤΥץ�������˥塼�ȥƥ����ȥե�����ɤ��Ȥ߹�碌�Ǥ��롣
��˥塼�������֤��Ȥˤ�ꡢ���Τ��Ȥ����ꤹ��:
<p>�ե������ 1: �Ѹ��õ�����<br>
�ե������ 2: �����ޥå�����Τ�����ꤹ��<br>
�ե������ 3: ���������<br> <br> <center>

<table width="790" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center" height="160" >
<table>
<tr><td>
<table><tr><td>&nbsp;</td><td><SELECT NAME="field0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value0-0-0" VALUE=""></td></tr><tr><td><b>OR</b></td><td><SELECT NAME="field0-0-1"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type0-0-1"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value0-0-1" VALUE=""><INPUT TYPE="button" VALUE="Or" NAME="cmd-add0-0-2" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"><INPUT TYPE="button" VALUE="And" 
	NAME="cmd-add0-1-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"></td></tr>
    
		<tr><td>&nbsp;</td><td align="center">
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <INPUT TYPE="button" VALUE="Add another boolean chart" NAME="cmd-add1-0-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;">
       
        
</td></tr>
</table>
</td></tr>
</table>
</td>
</tr>
</table>

</center>

<br> <p>���������򤤤��Ȥ��Ϥޤ�Τϡ�"Or" �ޤ��� "And" �ܥ���򲡤���
�Ȥ��Ǥ��롣�⤷ "Or" �ܥ���򲡤����ʤ顢�ǽ�Τ�Τβ��ˡ�����ܤ� 
term �����뤳�Ȥˤʤ롣���� term �ˤ⸡�����ꤹ�뤳�Ȥ��Ǥ���������̤Ϥɤ��餫�� term �ˤ�ޥå�������Τ��٤Ƥˤʤ롣<br> <p> <center>

<table width="790" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center" height="180" >

<table>
<tr><td>

<table><tr><td>&nbsp;</td><td><SELECT NAME="field0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value0-0-0" VALUE=""><INPUT TYPE="button" VALUE="Or" NAME="cmd-add0-0-1" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"></td></tr><tr><td>&nbsp;</td><td align="center" valign="middle"><b>AND</b></td></tr><tr><td>&nbsp;</td><td><SELECT NAME="field0-1-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type0-1-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value0-1-0" VALUE=""><INPUT TYPE="button" VALUE="Or" NAME="cmd-add0-1-1" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"><INPUT TYPE="button" VALUE="And" 
	NAME="cmd-add0-2-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"></td></tr>
    
		<tr><td>&nbsp;</td><td align="center">
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <INPUT TYPE="button" VALUE="Add another boolean chart" NAME="cmd-add1-0-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;">
       
        
</td></tr>
</table>
</td></tr>
</table>
</td>
</tr>
</table>

</center> <br> <p>"And" �ܥ���򲡤����Ȥ��ˤϡ����ꥸ�ʥ�� term �β��˿�����
term �����뤳�Ȥ������ - ����� "AND" �Ȥ���ñ��Ƕ��ڤ��Ƥ��ꡢ
������̤Ϥ���ξ���� term �˰��פ����ΤȤʤ롣

<p>"And" �� "Or" �򤵤�˥���å����뤳�Ȥ�Ǥ��롣�����ƥڡ�����ʣ����
term ����Ĥ��Ȥˤʤ롣"Or" �� "And" ����⤤ͥ�踢����äƤ��롣
"Or" �Ϥ��μ����()�������Ƥ���ȹͤ��뤳�Ȥ��Ǥ��롣

<br><p>
<center>

<table width="790" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center" height="170" >

<table>
<tr><td>
<table><tr><td>&nbsp;</td><td><SELECT NAME="field0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type0-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value0-0-0" VALUE=""><INPUT TYPE="button" VALUE="Or" NAME="cmd-add0-0-1" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"><INPUT TYPE="button" VALUE="And" 
	NAME="cmd-add0-1-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"></td></tr>
    
		<tr>
		<td colspan="2"><hr></td>
		</tr><tr><td>&nbsp;</td><td>
		<SELECT NAME="field1-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="groupset">groupset
<OPTION VALUE="bug_id">Bug #
<OPTION VALUE="short_desc">Summary
<OPTION VALUE="product">Product
<OPTION VALUE="version">Version
<OPTION VALUE="rep_platform">Platform
<OPTION VALUE="bug_file_loc">URL
<OPTION VALUE="op_sys">OS/Version
<OPTION VALUE="bug_status">Status
<OPTION VALUE="status_whiteboard">Status Whiteboard
<OPTION VALUE="keywords">Keywords
<OPTION VALUE="resolution">Resolution
<OPTION VALUE="bug_severity">Severity
<OPTION VALUE="priority">Priority
<OPTION VALUE="component">Component
<OPTION VALUE="assigned_to">AssignedTo
<OPTION VALUE="reporter">ReportedBy
<OPTION VALUE="votes">Votes
<OPTION VALUE="qa_contact">QAContact
<OPTION VALUE="cc">CC
<OPTION VALUE="dependson">BugsThisDependsOn
<OPTION VALUE="blocked">OtherBugsDependingOnThis
<OPTION VALUE="attachments.description">Attachment description
<OPTION VALUE="attachments.thedata">Attachment data
<OPTION VALUE="attachments.mimetype">Attachment mime type
<OPTION VALUE="attachments.ispatch">Attachment is patch
<OPTION VALUE="target_milestone">Target Milestone
<OPTION VALUE="delta_ts">Last changed date
<OPTION VALUE="(to_days(now()) - to_days(bugs.delta_ts))">Days since bug changed
<OPTION VALUE="longdesc">Comment
</SELECT><SELECT NAME="type1-0-0"><OPTION SELECTED VALUE="noop">---
<OPTION VALUE="equals">equal to
<OPTION VALUE="notequals">not equal to
<OPTION VALUE="casesubstring">contains (case-sensitive) substring
<OPTION VALUE="substring">contains (case-insensitive) substring
<OPTION VALUE="notsubstring">does not contain (case-insensitive) substring
<OPTION VALUE="regexp">contains regexp
<OPTION VALUE="notregexp">does not contain regexp
<OPTION VALUE="lessthan">less than
<OPTION VALUE="greaterthan">greater than
<OPTION VALUE="anywords">any words
<OPTION VALUE="allwords">all words
<OPTION VALUE="nowords">none of the words
<OPTION VALUE="changedbefore">changed before
<OPTION VALUE="changedafter">changed after
<OPTION VALUE="changedto">changed to
<OPTION VALUE="changedby">changed by
</SELECT><INPUT NAME="value1-0-0" VALUE=""><INPUT TYPE="button" VALUE="Or" NAME="cmd-add1-0-1" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"><INPUT TYPE="button" VALUE="And" 
	NAME="cmd-add1-1-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;"></td></tr>
    
		<tr><td>&nbsp;</td><td align="center">
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <INPUT TYPE="button" VALUE="Add another boolean chart" NAME="cmd-add2-0-0" ONCLICK="document.forms[0].action='query.cgi#chart' ; document.forms[0].method='POST' ; return 1;">
       
        
</td></tr>
</table>
</td></tr>
</table>
</td>
</tr>
</table>


</center>
<br>

<p>���֤狼��ˤ����Τϡ�"�̤Υ֡��ꥢ����㡼�Ȥ��ɲä���" �ܥ���Ǥ��롣
����ϤۤȤ�� "And" �ܥ����Ʊ���Ǥ��롣
����ϡ��ҤȤĤΥХ���ʣ���ι��ܤ��ط��Ť����Ƥ���ե�����ɤ�
��� ("Comments", "CC", �����Ƥ��٤Ƥ� "changed [something]" �Υ���ȥ꡼��ޤ�) ��Ȥ������Ȥ���ɬ�פˤʤ������
�⤷��ʣ���� term �����ä���硢
���줬�����Υե�����ɤΰ�� (�����Ȥʤ�) �ˤĤ��Ƥ��٤�
�Ҥ٤Ƥ����ΤǤ��ä��Ȥ���ȡ�
���Υե�����ɤΰۤʤä�ʣ���Υ��󥹥��󥹤˽����Τ���
�ҤȤĤΥ��󥹥��󥹤˽����Τ��Ϥä��ꤷ�ʤ��������ǡ�
ξ������ˡ��Ϳ����term ���ۤʤä� ���㡼�Ȥ˸���ʤ��¤��
���Ʊ�����󥹥��󥹤�ɽ���褦�ˤ��롣

<p>�㤨��: �⤷ "ͥ���� �� P5 ���ѹ��ˤʤä����" �� "ͥ���̤� 
person\@addr �ˤ�ä��ѹ������ä����", �����Ŭ�� ͥ���� �� P5 �ˤ�
�ѹ������ͤ򸡺���������Ǥ��롣���������⤷�����ˤ���Ȥ��ˤ��ο�ʪ��
��ä� �ޥ��륹�ȡ����ѹ��ˤʤꡢ����Ȥ��˥ޥ��륹�ȡ����ͥ���̤� 
P5 �ˤ��줫(�ۤ���ï���β�ǽ��������) �ˤ�ä��ѹ����줿�Х��򤹤٤Ƹ�
���������Ȥ��ˡ���Ĥ� term ��ۤʤä����㡼�Ȥ��֤����Ȥˤʤ������
};


print qq{
<a name="therest"></a>
<center><h3>�ե�����λĤ����ʬ</h3></center>
<center>


<table width="650" bgcolor="#00afff" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center" height="190" >
<table cellspacing="0" cellpadding="0">
<tr>
<td align="left">
<INPUT TYPE="radio" NAME="cmdtype" VALUE="editnamed"> Load the remembered query:
<select name="namedcmd"><OPTION VALUE="Assigned to me">Assigned to me</select><br>
<INPUT TYPE="radio" NAME="cmdtype" VALUE="runnamed"> Run the remembered query:<br>
<INPUT TYPE="radio" NAME="cmdtype" VALUE="forgetnamed"> Forget the remembered query:<br>
<INPUT TYPE="radio" NAME="cmdtype" VALUE="asdefault"> Remember this as the default query<br>
<INPUT TYPE="radio" NAME="cmdtype" VALUE="asnamed"> Remember this query, and name it:
<INPUT TYPE="text" NAME="newqueryname"><br>
<B>Sort By:</B>
<SELECT NAME="order">
<OPTION VALUE="Reuse same sort as last time">Reuse same sort as last time<OPTION VALUE="Bug Number">Bug Number<OPTION VALUE="'Importance'">'Importance'<OPTION VALUE="Assignee">Assignee</SELECT>
</td>
</tr>
</table>
<table>
<tr>
<td>
<INPUT TYPE="button" VALUE="Reset back to the default query">
</td>
<td>
<INPUT TYPE="button" VALUE="Submit query">
</td>
</tr>
</table>
</td>
</tr>
</table>



</center>
<br>
<p>���ʤ��Ϥ����ޤǤ��٤Ƥ򸫽��������������֤��Υե�����β��ˤ��륬�饯���ϲ�������
���ʤ��������󤷤Ƥ���Ȥ��Ϥ��ĤǤ⡢���ߤθ������򡢥ǥե���ȸ������Ȥ��Ƶ��������Ƥ������Ȥ��Ǥ��롣 �ޤ���������̤�ɽ���Υ�������ˡ�����֤��Ȥ�Ǥ��롣 ����ä��顢'�������¹�' �򥯥�å����褦�� 
};




print qq{

<a name="info"></a>

<br><center><h3>���Υɥ�����ȤˤĤ���</h3></center>

<p>�����Ĥ��θŤ��С������� Bugzilla �Υɥ������ (Terry Weissman, Tara Hernandez �ۤ��ο͡��ˤ��) ����Ѥ��� <a href="mailto:netdemonz\@yahoo.com">Brian Bober</a> ���񤤤���
irc.mozilla.org - #mozilla, #mozwebtools, #mozillazine �ʤɤǡ�����ä������뤳�Ȥ�����롣��� netdemon �Ȥ���̾�����̤äƤ��롣

<P>Bugzilla �λ���ˡ�ˤĤ��ƤΥɥ�����Ȥϡ�Mozilla.org ����¾�Υ����Ȥ����Ѳ�ǽ�Ǥ���:
<br><a href="http://www.mozilla.org/quality/help/beginning-duplicate-finding.html\"> 
���ޤǤ˸������줿�Х���ɤΤ褦�˸������뤫</a><br>
<a href="http://jt.mozilla.gr.jp/bugs/">Bugzilla �ΰ���Ū�ʾ���</a><br>
<a href="http://jt.mozilla.gr.jp/quality/help/bugzilla-helper.jp.html">Mozilla �Х���ݡ��ȥե�����</a><br>
<a href="http://jt.mozilla.gr.jp/bugs/text-searching.html">Bugzilla �Υƥ����ȸ���</a><br>
<a href="http://jt.mozilla.gr.jp/quality/bug-writing-guidelines.html">�Х���ݡ��ȤΥ����ɥ饤��</a><br>

<p>��μ�ɮư���Ϥ����餷�� Bugzilla �Υ桼�����ɤΤ褦�� Bugzilla 
�θ����ե���������Ѥ��뤫�ȸ������Ȥ�ؤ���ˡ��Ϳ���뤳�Ȥˤ�äƥ��󥸥˥�������뤳�ȤǤ��롣
��� query.cgi ��񤭽����ơ����Τ褦�˸���������"What the heck, I'll write this too".

<p><br><center><h3>�ʤ������Ȥ��Τ�?</h3></center>

<p>���ʤ��ϸ����ڡ����򸫤ơ������������Ȥ������֤��Υڡ����Ϥ���ä��񤷤��褦���������פ���������̤˸�������ɬ�פʤ�Ƥʤ���͡�
�Х�����Ƥ������˽�ʣ������Τ򸫤Ĥ��뤳�Ȥ����˽��פǤ��롣
����� <a href="http://jt.mozilla.gr.jp/quality/bug-writing-guidelines.html">�Х���ݡ��ȤΥ����ɥ饤��</a>
����������Ƥ��롣�Х���ݡ��Ȥ��ɤ�͡���˻��������˥Х��˰��ݤ���Ƥ��롣
�����顢���ʤ��Ͻ�ʣ��õ���ͤ��٤ƤˤȤäƤȤƤ���ڤʤ��Ȥ򤷤Ƥ���Τ���

};








print qq{
<a name="samplequery"></a>
<p><br><center><h3>Sample Query</h3></center>

<p>�Ǥϼºݤ�<b>�Х��򸡺����褦!</b> �����˻Ȥ���֤⤸���ȡפΥǡ������١������Ҽڤ��뤳�Ȥˤ��롣
<BR>�ޤ��������ڡ�����<a target="_blank" href="http://bugzilla.mozilla.gr.jp/query.cgi">
������������ɥ�</a>�ǳ����Ƥ��Υɥ�����Ȥ򸡺��ڡ�����ξ�������䤹���褦�ˤ��褦��
<p>�����Ƽ��Τ褦�ˤ���:
<ul>
<li>"���ơ�����"�ե�����ɤΤ��٤ƤΥե�����ɤ�����ʤޤ����������ˤ���
<li>�ƥ����ȸ������ǡ�����ˡֲ��ԡס�����ʸ�ˡ֥᡼��פ����Ϥ���
�ʡ�����ʸ�ˡ֥᡼��פ����ꡢ���ġ�����ˡֲ��ԡפ��ޤޤ��פȤ�����̣��
</ul>

<p>��̤ΤҤȤĤȤ��Ƽ��Τ褦�ʥХ���ɽ�������Ϥ���:
<a href="http://bugzilla.mozilla.gr.jp/show_bug.cgi?id=551">bug 551- �᡼��ɽ��������ɥ��ǲ��Ԥ����֤�</a>
};

print qq{
<hr>

<a name="bottom"></a>

</form>

};



PutFooter();
