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
#                 Terry Weissman <terry@mozilla.org>
#
# Direct any questions on this source code to
#
# Holger Schurig <holgerschurig@nikocity.de>

use diagnostics;
use strict;
use lib ".";

require "CGI.pl";
require "globals.pl";

# Shut up misguided -w warnings about "used only once".  For some reason,
# "use vars" chokes on me when I try it here.

sub sillyness {
    my $zz;
    $zz = $::buffer;
}


my $dobugcounts = (defined $::FORM{'dobugcounts'});



# TestProduct:    just returns if the specified product does exists
# CheckProduct:   same check, optionally  emit an error text
# TestComponent:  just returns if the specified product/component combination exists
# CheckComponent: same check, optionally emit an error text

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
        print "���ߤޤ��󡢥ץ�����Ȥ����ꤵ��Ƥ��ޤ���";
        PutTrailer();
        exit;
    }

    unless (TestProduct $prod) {
        print "���ߤޤ��󡢥ץ������ '$prod' ��¸�ߤ��ޤ���";
        PutTrailer();
        exit;
    }
}

sub TestComponent ($$)
{
    my ($prod,$comp) = @_;

    # does the product exist?
    SendSQL("SELECT program,value
             FROM components
             WHERE program=" . SqlQuote($prod) . " and value=" . SqlQuote($comp));
    return FetchOneColumn();
}

sub CheckComponent ($$)
{
    my ($prod,$comp) = @_;

    # do we have the component?
    unless ($comp) {
        print "���ߤޤ��󡢥���ݡ��ͥ�Ȥ����ꤵ��Ƥ��ޤ���";
        PutTrailer();
        exit;
    }

    CheckProduct($prod);

    unless (TestComponent $prod,$comp) {
        print "���ߤޤ��󡢥ץ������ '$prod' ����Υ���ݡ��ͥ�� '$comp' ��¸�ߤ��ޤ���";
        PutTrailer();
        exit;
    }
}


#
# Displays the form to edit component parameters
#

sub EmitFormElements ($$$$$)
{
    my ($product, $component, $initialownerid, $initialqacontactid, $description) = @_;

    my ($initialowner, $initialqacontact) = ($initialownerid ? DBID_to_name ($initialownerid) : '',
                                             $initialqacontactid ? DBID_to_name ($initialqacontactid) : '');

    print "  <TH ALIGN=\"right\">����ݡ��ͥ��:</TH>\n";
    print "  <TD><INPUT SIZE=64 MAXLENGTH=50 NAME=\"component\" VALUE=\"" .
        value_quote($component) . "\">\n";
    print "      <INPUT TYPE=HIDDEN NAME=\"product\" VALUE=\"" .
        value_quote($product) . "\"></TD>\n";

    print "</TR><TR>\n";
    print "  <TH ALIGN=\"right\">����:</TH>\n";
    print "  <TD><TEXTAREA ROWS=4 COLS=64 WRAP=VIRTUAL NAME=\"description\">" .
        value_quote($description) . "</TEXTAREA></TD>\n";

    print "</TR><TR>\n";
    print "  <TH ALIGN=\"right\">���ô����:</TH>\n";
    print "  <TD><INPUT TYPE=TEXT SIZE=64 MAXLENGTH=255 NAME=\"initialowner\" VALUE=\"" .
        value_quote($initialowner) . "\"></TD>\n";

    if (Param('useqacontact')) {
        print "</TR><TR>\n";
        print "  <TH ALIGN=\"right\">��� QA ���󥿥���:</TH>\n";
        print "  <TD><INPUT TYPE=TEXT SIZE=64 MAXLENGTH=255 NAME=\"initialqacontact\" VALUE=\"" .
            value_quote($initialqacontact) . "\"></TD>\n";
    }
}


#
# Displays a text like "a.", "a or b.", "a, b or c.", "a, b, c or d."
#

sub PutTrailer (@)
{
    my (@links) = ("<A HREF=\"query.cgi\">�����ڡ���</A> �����", @_);

    my $count = $#links;
    my $num = 0;
    print "<P>\n";
    if (!$dobugcounts) {
        print qq{<a href="editcomponents.cgi?dobugcounts=1&$::buffer">};
        print qq{�Х��ο���ɽ������ (�٤��ʤ�ޤ�)</a><p>\n};
    }
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
# Preliminary checks:
#

ConnectToDatabase();
confirm_login();

print "Content-type: text/html; charset=EUC-JP\n\n";

unless (UserInGroup("editcomponents")) {
    PutHeader("���Ĥ���Ƥ��ޤ���");
    print "���ߤޤ��󡢤��ʤ��� 'editcomponents' �Υ��С��ǤϤ���ޤ���\n�Ǥ����顢����ݡ��ͥ�Ȥ��ɲá��ѹ���������뤳�ȤϤǤ��ޤ���\n";
    PutTrailer();
    exit;
}


#
# often used variables
#
my $product   = trim($::FORM{product}   || '');
my $component = trim($::FORM{component} || '');
my $action    = trim($::FORM{action}    || '');
my $localtrailer;
if ($product) {
    $localtrailer = "����˥���ݡ��ͥ�Ȥ�<A HREF=\"editcomponents.cgi?product=" . url_quote($product) . "\">�Խ�</A>����";
} else {
    $localtrailer = "����˥���ݡ��ͥ�Ȥ�<A HREF=\"editcomponents.cgi\">�Խ�</A>����";
}



#
# product = '' -> Show nice list of products
#

unless ($product) {
    PutHeader("�ץ����������");

    if ($dobugcounts){
        SendSQL("SELECT products.product,products.description,COUNT(bug_id)
             FROM products LEFT JOIN bugs
               ON products.product=bugs.product
             GROUP BY products.product
             ORDER BY products.product");
    } else {
        SendSQL("SELECT products.product,products.description
             FROM products 
             ORDER BY products.product");
    }
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH ALIGN=\"left\">����ݡ��ͥ�Ȥ��Խ� ...</TH>\n";
    print "  <TH ALIGN=\"left\">����</TH>\n";
    if ($dobugcounts) {
        print "  <TH ALIGN=\"left\">�Х���</TH>\n";
    }
    #print "  <TH ALIGN=\"left\">�Խ�</TH>\n";
    print "</TR>";
    while ( MoreSQLData() ) {
        my ($product, $description, $bugs) = FetchSQLData();
        $description ||= "<FONT COLOR=\"red\">̵��</FONT>";
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editcomponents.cgi?product=", url_quote($product), "\"><B>$product</B></A></TD>\n";
        print "  <TD VALIGN=\"top\">$description</TD>\n";
        if ($dobugcounts) {
            $bugs ||= "̵��";
            print "  <TD VALIGN=\"top\">$bugs</TD>\n";
        }
        #print "  <TD VALIGN=\"top\"><A HREF=\"editproducts.cgi?action=edit&product=", url_quote($product), "\">�Խ�</A></TD>\n";
    }
    print "</TR></TABLE>\n";

    PutTrailer();
    exit;
}



#
# action='' -> Show nice list of components
#

unless ($action) {
    PutHeader("$product �Υ���ݡ��ͥ�Ȥ�����");
    CheckProduct($product);

    if ($dobugcounts) {
        SendSQL("SELECT value,description,initialowner,initialqacontact,COUNT(bug_id)
             FROM components LEFT JOIN bugs
               ON components.program=bugs.product AND components.value=bugs.component
             WHERE program=" . SqlQuote($product) . "
             GROUP BY value");
    } else {
        SendSQL("SELECT value,description,initialowner,initialqacontact
             FROM components 
             WHERE program=" . SqlQuote($product) . "
             GROUP BY value");
    }        
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH ALIGN=\"left\">����ݡ��ͥ�Ȥ��Խ� ...</TH>\n";
    print "  <TH ALIGN=\"left\">����</TH>\n";
    print "  <TH ALIGN=\"left\">���ô����</TH>\n";
    print "  <TH ALIGN=\"left\">��� QA ���󥿥���</TH>\n"
        if Param('useqacontact');
    print "  <TH ALIGN=\"left\">�Х���</TH>\n"
        if $dobugcounts;
    print "  <TH ALIGN=\"left\">���</TH>\n";
    print "</TR>";
    my @data;
    while (MoreSQLData()) {
        push @data, [FetchSQLData()];
    }
    foreach (@data) {
        my ($component,$desc,$initialownerid,$initialqacontactid, $bugs) = @$_;

        $desc             ||= "<FONT COLOR=\"red\">̵��</FONT>";
        my $initialowner = $initialownerid ? DBID_to_name ($initialownerid) : "<FONT COLOR=\"red\">̵��</FONT>";
        my $initialqacontact = $initialqacontactid ? DBID_to_name ($initialqacontactid) : "<FONT COLOR=\"red\">̵��</FONT>";
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editcomponents.cgi?product=", url_quote($product), "&component=", url_quote($component), "&action=edit\"><B>$component</B></A></TD>\n";
        print "  <TD VALIGN=\"top\">$desc</TD>\n";
        print "  <TD VALIGN=\"top\">$initialowner</TD>\n";
        print "  <TD VALIGN=\"top\">$initialqacontact</TD>\n"
                if Param('useqacontact');
        if ($dobugcounts) {
            $bugs ||= '̵��';
            print "  <TD VALIGN=\"top\">$bugs</TD>\n";
        }
        print "  <TD VALIGN=\"top\"><A HREF=\"editcomponents.cgi?product=", url_quote($product), "&component=", url_quote($component), "&action=del\"><B>���</B></A></TD>\n";
        print "</TR>";
    }
    print "<TR>\n";
    my $span = 3;
    $span++ if Param('useqacontact');
    $span++ if $dobugcounts;
    print "  <TD VALIGN=\"top\" COLSPAN=$span>��������ݡ��ͥ�Ȥ��ɲ�</TD>\n";
    print "  <TD VALIGN=\"top\" ALIGN=\"middle\"><A HREF=\"editcomponents.cgi?product=", url_quote($product) . "&action=add\">�ɲ�</A></TD>\n";
    print "</TR></TABLE>\n";

    PutTrailer();
    exit;
}


$dobugcounts = 1;               # Stupid hack to force further PutTrailer()
                                # calls to not offer a "bug count" option.


#
# action='add' -> present form for parameters for new component
#
# (next action will be 'new')
#

if ($action eq 'add') {
    PutHeader("$product �Υ���ݡ��ͥ�Ȥ��ɲ�");
    CheckProduct($product);

    #print "This page lets you add a new product to bugzilla.\n";

    print "<FORM METHOD=POST ACTION=editcomponents.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements($product, '', 0, 0, '');

    print "</TR></TABLE>\n<HR>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"�ɲ�\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"new\">\n";
    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/�����/�̤�/;
    PutTrailer($other);
    exit;
}



#
# action='new' -> add component entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("$product �ο�������ݡ��ͥ�Ȥ��ɲ�");
    CheckProduct($product);

    # Cleanups and valididy checks

    unless ($component) {
        print "��������ݡ��ͥ��̾�����Ϥ��Ƥ���������<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
    }
    if (TestComponent($product,$component)) {
        print "'$component' �Ȥ�������ݡ��ͥ�Ȥϴ���¸�ߤ��ޤ���<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
    }
    if (length($component) > 50) {
        print "Sorry, the name of a component is limited to 50 characters.";
        PutTrailer($localtrailer);
        exit;
    }

    my $description = trim($::FORM{description} || '');

    if ($description eq '') {
        print "����ݡ��ͥ�Ȥ����������Ϥ��Ƥ���������<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
    }

    my $initialowner = trim($::FORM{initialowner} || '');

    if ($initialowner eq '') {
        print "'$component' ����ݡ��ͥ�Ȥν��ô���Ԥ����Ϥ��Ƥ���������<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
    }

    my $initialownerid = DBname_to_id ($initialowner);
    if (!$initialownerid) {
        print "'$component' ����ݡ��ͥ�Ȥν��ô���Ԥϡ�Bugzilla �������桼���Ǥʤ���Фʤ�ޤ���<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
      }

    my $initialqacontact = trim($::FORM{initialqacontact} || '');
    my $initialqacontactid = DBname_to_id ($initialqacontact);
    if (Param('useqacontact')) {
        if (!$initialqacontactid && $initialqacontact ne '') {
            print "'$component' ����ݡ��ͥ�Ȥν�� QA ���󥿥��Ȥϡ�Bugzilla �������桼���Ǥʤ���Фʤ�ޤ���<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
            PutTrailer($localtrailer);
            exit;
        }
    }

    # Add the new component
    SendSQL("INSERT INTO components ( " .
          "program, value, description, initialowner, initialqacontact " .
          " ) VALUES ( " .
          SqlQuote($product) . "," .
          SqlQuote($component) . "," .
          SqlQuote($description) . "," .
          SqlQuote($initialownerid) . "," .
          SqlQuote($initialqacontactid) . ")");

    # Make versioncache flush
    unlink "data/versioncache";

    print "OK����λ��<p>\n";
    if ($product) {
        PutTrailer("����˥���ݡ��ͥ�Ȥ�<A HREF=\"editcomponents.cgi?product=" . url_quote($product) . "\">�Խ�</A>���롿�̤Υ���ݡ��ͥ�Ȥ�<A HREF=\"editcomponents.cgi?product=". url_quote($product) . "&action=add\">�ɲ�</A>����");
    } else {
        PutTrailer("����˥���ݡ��ͥ�Ȥ�<A HREF=\"editcomponents.cgi\">�Խ�</A>���롿�̤Υ���ݡ��ͥ�Ȥ�<A HREF=\"editcomponents.cgi?action=add\">�ɲ�</A>����");
    }
    exit;
}



#
# action='del' -> ask if user really wants to delete
#
# (next action would be 'delete')
#

if ($action eq 'del') {
    PutHeader("$product �Υ���ݡ��ͥ�Ȥ���");
    CheckComponent($product, $component);

    # display some data about the component
    SendSQL("SELECT products.product,products.description,
                products.milestoneurl,products.disallownew,
                components.program,components.value,components.initialowner,
                components.initialqacontact,components.description
             FROM products
             LEFT JOIN components on product=program
             WHERE product=" . SqlQuote($product) . "
               AND   value=" . SqlQuote($component) );


    my ($product,$pdesc,$milestoneurl,$disallownew,
        $dummy,$component,$initialownerid,$initialqacontactid,$cdesc) = FetchSQLData();

    my $initialowner = $initialownerid ? DBID_to_name ($initialownerid) : "<FONT COLOR=\"red\">̵��</FONT>";
    my $initialqacontact = $initialqacontactid ? DBID_to_name ($initialqacontactid) : "<FONT COLOR=\"red\">̵��</FONT>";
    my $milestonelink = $milestoneurl ? "<A HREF=\"$milestoneurl\">$milestoneurl</A>"
                                      : "<FONT COLOR=\"red\">̵��</FONT>";
    $pdesc            ||= "<FONT COLOR=\"red\">̵��</FONT>";
    $disallownew        = $disallownew ? 'closed' : 'open';
    $cdesc            ||= "<FONT COLOR=\"red\">̵��</FONT>";
    
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Part</TH>\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Value</TH>\n";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">����ݡ��ͥ��:</TD>\n";
    print "  <TD VALIGN=\"top\">$component</TD>";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">����ݡ��ͥ�Ȥ�����:</TD>\n";
    print "  <TD VALIGN=\"top\">$cdesc</TD>";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">���ô����:</TD>\n";
    print "  <TD VALIGN=\"top\">$initialowner</TD>";

    if (Param('useqacontact')) {
        print "</TR><TR>\n";
        print "  <TD VALIGN=\"top\">��� QA ���󥿥���:</TD>\n";
        print "  <TD VALIGN=\"top\">$initialqacontact</TD>";
    }
    SendSQL("SELECT count(bug_id)
             FROM bugs
             WHERE product=" . SqlQuote($product) . "
                AND component=" . SqlQuote($component));

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">����ݡ��ͥ�Ȥν�°����ץ������:</TD>\n";
    print "  <TD VALIGN=\"top\">$product</TD>\n";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">�ץ�����Ȥ�����:</TD>\n";
    print "  <TD VALIGN=\"top\">$pdesc</TD>\n";

    if (Param('usetargetmilestone')) {
         print "</TR><TR>\n";
         print "  <TD VALIGN=\"top\">�ޥ��륹�ȡ���� URL:</TD>\n";
         print "  <TD VALIGN=\"top\">$milestonelink</TD>\n";
    }

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">�Х���Ͽ�Ǥ��ڤ�:</TD>\n";
    print "  <TD VALIGN=\"top\">$disallownew</TD>\n";

    print "</TR><TR>\n";
    print "  <TD VALIGN=\"top\">��Ͽ�Х���</TD>\n";
    print "  <TD VALIGN=\"top\">";
    my $bugs = FetchOneColumn();
    print $bugs || '̵��';


    print "</TD>\n</TR></TABLE>";

    print "<H2>��ǧ</H2>\n";

    if ($bugs) {
        if (!Param("allowbugdeletion")) {
            print "���ߤޤ��󡢤��Υ���ݡ��ͥ�Ȥˤ� $bugs �ĤΥХ�������ޤ��������ΥХ���ۤ��Υ���ݡ��ͥ�Ȥ˳������ľ���ȡ���������Ǥ���褦�ˤʤ�ޤ���";
            PutTrailer($localtrailer);
            exit;
        }
        print "<TABLE BORDER=0 CELLPADDING=20 WIDTH=\"70%\" BGCOLOR=\"red\"><TR><TD>\n",
              "���Υ���ݡ��ͥ�Ȥˤϴ��˥Х�����Ͽ����Ƥ��ޤ������Υ���ݡ��ͥ�Ȥ�������ȡ���Ͽ����Ƥ���<B><BLINK>���٤Ƥ�</BLINK></B>�Х���������ޤ������Υ���ݡ��ͥ�Ȥ˴ؤ���Х�����⸫�뤳�Ȥ��Ǥ��ʤ��ʤ�ޤ�!\n",
              "</TD></TR></TABLE>\n";
    }

    print "<P>�����ˤ��Υ���ݡ��ͥ�Ȥ������ޤ���?<P>\n";

    print "<FORM METHOD=POST ACTION=editcomponents.cgi>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"�Ϥ���������ޤ�\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"delete\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"product\" VALUE=\"" .
        value_quote($product) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"component\" VALUE=\"" .
        value_quote($component) . "\">\n";
    print "</FORM>";

    PutTrailer($localtrailer);
    exit;
}



#
# action='delete' -> really delete the component
#

if ($action eq 'delete') {
    PutHeader("$product �Υ���ݡ��ͥ�Ȥ���");
    CheckComponent($product,$component);

    # lock the tables before we start to change everything:

    SendSQL("LOCK TABLES attachments WRITE,
                         bugs WRITE,
                         bugs_activity WRITE,
                         components WRITE,
                         dependencies WRITE");

    # According to MySQL doc I cannot do a DELETE x.* FROM x JOIN Y,
    # so I have to iterate over bugs and delete all the indivial entries
    # in bugs_activies and attachments.

    if (Param("allowbugdeletion")) {
        SendSQL("SELECT bug_id
             FROM bugs
             WHERE product=" . SqlQuote($product) . "
               AND component=" . SqlQuote($component));
        while (MoreSQLData()) {
            my $bugid = FetchOneColumn();

            PushGlobalSQLState();
            SendSQL("DELETE FROM attachments WHERE bug_id=$bugid");
            SendSQL("DELETE FROM bugs_activity WHERE bug_id=$bugid");
            SendSQL("DELETE FROM dependencies WHERE blocked=$bugid");
            PopGlobalSQLState();
        }
        print "ź�եե����롢�Х���ư���Ȱ�¸�ط����������ޤ�����<BR>\n";


        # Deleting the rest is easier:

        SendSQL("DELETE FROM bugs
             WHERE product=" . SqlQuote($product) . "
               AND component=" . SqlQuote($component));
        print "�Х���ݡ��Ȥ������ޤ�����<BR>\n";
    }

    SendSQL("DELETE FROM components
             WHERE program=" . SqlQuote($product) . "
               AND value=" . SqlQuote($component));
    print "����ݡ��ͥ�Ȥ������ޤ�����<P>\n";
    SendSQL("UNLOCK TABLES");

    unlink "data/versioncache";
    PutTrailer($localtrailer);
    exit;
}



#
# action='edit' -> present the edit component form
#
# (next action would be 'update')
#

if ($action eq 'edit') {
    PutHeader("$product �Υ���ݡ��ͥ�Ȥ��Խ�");
    CheckComponent($product,$component);

    # get data of component
    SendSQL("SELECT products.product,products.description,
                products.milestoneurl,products.disallownew,
                components.program,components.value,components.initialowner,
                components.initialqacontact,components.description
             FROM products
             LEFT JOIN components on product=program
             WHERE product=" . SqlQuote($product) . "
               AND   value=" . SqlQuote($component) );

    my ($product,$pdesc,$milestoneurl,$disallownew,
        $dummy,$component,$initialownerid,$initialqacontactid,$cdesc) = FetchSQLData();

    my $initialowner = $initialownerid ? DBID_to_name ($initialownerid) : '';
    my $initialqacontact = $initialqacontactid ? DBID_to_name ($initialqacontactid) : '';

    print "<FORM METHOD=POST ACTION=editcomponents.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    #+++ display product/product description

    EmitFormElements($product, $component, $initialownerid, $initialqacontactid, $cdesc);

    print "</TR><TR>\n";
    print "  <TH ALIGN=\"right\">�Х���:</TH>\n";
    print "  <TD>";
    SendSQL("SELECT count(*)
             FROM bugs
             WHERE product=" . SqlQuote($product) .
            " and component=" . SqlQuote($component));
    my $bugs = '';
    $bugs = FetchOneColumn() if MoreSQLData();
    print $bugs || '̵��';

    print "</TD>\n</TR></TABLE>\n";

    print "<INPUT TYPE=HIDDEN NAME=\"componentold\" VALUE=\"" .
        value_quote($component) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"descriptionold\" VALUE=\"" .
        value_quote($cdesc) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"initialownerold\" VALUE=\"" .
        value_quote($initialowner) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"initialqacontactold\" VALUE=\"" .
        value_quote($initialqacontact) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"update\">\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"����\">\n";

    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/�����/�̤�/;
    PutTrailer($other);
    exit;
}



#
# action='update' -> update the component
#

if ($action eq 'update') {
    PutHeader("$product �Υ���ݡ��ͥ�Ȥ򹹿�");

    my $componentold        = trim($::FORM{componentold}        || '');
    my $description         = trim($::FORM{description}         || '');
    my $descriptionold      = trim($::FORM{descriptionold}      || '');
    my $initialowner        = trim($::FORM{initialowner}        || '');
    my $initialownerold     = trim($::FORM{initialownerold}     || '');
    my $initialqacontact    = trim($::FORM{initialqacontact}    || '');
    my $initialqacontactold = trim($::FORM{initialqacontactold} || '');

    CheckComponent($product,$componentold);

    if (length($component) > 50) {
        print "Sorry, the name of a component is limited to 50 characters.";
        PutTrailer($localtrailer);
        exit;
    }

    # Note that the order of this tests is important. If you change
    # them, be sure to test for WHERE='$component' or WHERE='$componentold'

    SendSQL("LOCK TABLES bugs WRITE,
                         components WRITE, profiles READ");

    if ($description ne $descriptionold) {
        unless ($description) {
            print "�����櫓����ޤ��������������뤳�ȤϤǤ��ޤ���";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }
        SendSQL("UPDATE components
                 SET description=" . SqlQuote($description) . "
                 WHERE program=" . SqlQuote($product) . "
                   AND value=" . SqlQuote($componentold));
        print "�����򹹿����ޤ�����<BR>\n";
    }


    if ($initialowner ne $initialownerold) {
        unless ($initialowner) {
            print "���ߤޤ��󡢽��ô���Ԥ������뤳�Ȥ��Ǥ��ޤ���";
            SendSQL("UNLOCK TABLES");
            PutTrailer($localtrailer);
            exit;
        }

        my $initialownerid = DBname_to_id($initialowner);
        unless ($initialownerid) {
            print "���ߤޤ��󡢽��ô���Ԥϡ�Bugzilla �������桼���Ǥʤ���Фʤ�ޤ���";
            SendSQL("UNLOCK TABLES");
            PutTrailer($localtrailer);
            exit;
        }

        SendSQL("UPDATE components
                 SET initialowner=" . SqlQuote($initialownerid) . "
                 WHERE program=" . SqlQuote($product) . "
                   AND value=" . SqlQuote($componentold));
        print "���ô���Ԥ򹹿����ޤ�����<BR>\n";
    }

    if (Param('useqacontact') && $initialqacontact ne $initialqacontactold) {
        my $initialqacontactid = DBname_to_id($initialqacontact);
        if (!$initialqacontactid && $initialqacontact ne '') {
            print "�����櫓����ޤ��󡣽�� QA ���󥿥��Ȥϡ�Bugzilla �������桼���Ǥʤ���Фʤ�ޤ���";
            SendSQL("UNLOCK TABLES");
            PutTrailer($localtrailer);
            exit;
        }

        SendSQL("UPDATE components
                 SET initialqacontact=" . SqlQuote($initialqacontactid) . "
                 WHERE program=" . SqlQuote($product) . "
                   AND value=" . SqlQuote($componentold));
        print "��� QA ���󥿥��Ȥ򹹿����ޤ���<BR>\n";
    }


    if ($component ne $componentold) {
        unless ($component) {
            print "���ߤޤ��󡢥ץ������̾�������뤳�Ȥ��Ǥ��ޤ���";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }
        if (TestComponent($product,$component)) {
            print "���ߤޤ��󡢥ץ������̾ '$component' �ϴ��˻��Ѥ���Ƥ��ޤ���";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }

        SendSQL("UPDATE bugs
                 SET component=" . SqlQuote($component) . ",
                 delta_ts = delta_ts
                 WHERE component=" . SqlQuote($componentold) . "
                   AND product=" . SqlQuote($product));
        SendSQL("UPDATE components
                 SET value=" . SqlQuote($component) . "
                 WHERE value=" . SqlQuote($componentold) . "
                   AND program=" . SqlQuote($product));

        unlink "data/versioncache";
        print "�ץ������̾�򹹿����ޤ���<BR>\n";
    }
    SendSQL("UNLOCK TABLES");

    PutTrailer($localtrailer);
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
