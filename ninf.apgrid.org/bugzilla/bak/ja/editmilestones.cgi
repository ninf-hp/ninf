#!/usr/local/bin/perl -w
# -*- Mode: perl; indent-tabs-mode: nil -*-

#
# This is a script to edit the target milestones. It is largely a copy of
# the editversions.cgi script, since the two fields were set up in a
# very similar fashion.
#
# (basically replace each occurance of 'milestone' with 'version', and
# you'll have the original script)
#
# Matt Masson <matthew@zeroknowledge.com>
#


use diagnostics;
use strict;
use lib ".";

require "CGI.pl";
require "globals.pl";




# TestProduct:  just returns if the specified product does exists
# CheckProduct: same check, optionally  emit an error text
# TestMilestone:  just returns if the specified product/version combination exists
# CheckMilestone: same check, optionally emit an error text

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
        print "���ߤޤ���'$prod' �ץ�����Ȥ�¸�ߤ��ޤ���";
        PutTrailer();
        exit;
    }
}

sub TestMilestone ($$)
{
    my ($prod,$mile) = @_;

    # does the product exist?
    SendSQL("SELECT product,value
             FROM milestones
             WHERE product=" . SqlQuote($prod) . " and value=" . SqlQuote($mile));
    return FetchOneColumn();
}

sub CheckMilestone ($$)
{
    my ($prod,$mile) = @_;

    # do we have the milestone?
    unless ($mile) {
        print "���ߤޤ��󡢥ޥ��륹�ȡ��󤬻��ꤵ��Ƥ��ޤ���";
        PutTrailer();
        exit;
    }

    CheckProduct($prod);

    unless (TestMilestone $prod,$mile) {
        print "���ߤޤ��󡢥ץ������ '$prod' �Υޥ��륹�ȡ��� '$mile' ��¸�ߤ��ޤ���";
        PutTrailer();
        exit;
    }
}


#
# Displays the form to edit a milestone
#

sub EmitFormElements ($$$)
{
    my ($product, $milestone, $sortkey) = @_;

    print "  <TH ALIGN=\"right\">�ޥ��륹�ȡ���:</TH>\n";
    print "  <TD><INPUT SIZE=64 MAXLENGTH=64 NAME=\"milestone\" VALUE=\"" .
        value_quote($milestone) . "\">\n";
    print "</TR><TR>\n";
    print "  <TH ALIGN=\"right\">�������ѤΥ���:</TH>\n";
    print "  <TD><INPUT SIZE=64 MAXLENGTH=64 NAME=\"sortkey\" VALUE=\"" .
        value_quote($sortkey) . "\">\n";
    print "      <INPUT TYPE=HIDDEN NAME=\"product\" VALUE=\"" .
        value_quote($product) . "\"></TD>\n";
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
    print "���ߤޤ��󡢤��ʤ��� 'editcomponents' ���롼�פΥ��С��ǤϤ���ޤ���\n";
    print "���Τ��ᡢ�ޥ��륹�ȡ�����ɲá��Խ���������뤳�ȤϤǤ��ޤ���\n";
    PutTrailer();
    exit;
}


#
# often used variables
#
my $product = trim($::FORM{product} || '');
my $milestone = trim($::FORM{milestone} || '');
my $sortkey = trim($::FORM{sortkey} || '0');
my $action  = trim($::FORM{action}  || '');
my $localtrailer;
if ($milestone) {
    $localtrailer = "����˥ޥ��륹�ȡ����<A HREF=\"editmilestones.cgi?product=" . url_quote($product) . "\">�Խ�</A>";
} else {
    $localtrailer = "����˥ޥ��륹�ȡ����<A HREF=\"editmilestones.cgi\">�Խ�</A>";
}



#
# product = '' -> Show nice list of milestones
#

unless ($product) {
    PutHeader("�ץ����������");

    SendSQL("SELECT products.product,products.description,'xyzzy'
             FROM products 
             GROUP BY products.product
             ORDER BY products.product");
    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH ALIGN=\"left\">�ޥ��륹�ȡ����Խ� ...</TH>\n";
    print "  <TH ALIGN=\"left\">����</TH>\n";
    print "  <TH ALIGN=\"left\">�Х���</TH>\n";
    print "</TR>";
    while ( MoreSQLData() ) {
        my ($product, $description, $bugs) = FetchSQLData();
        $description ||= "<FONT COLOR=\"red\">missing</FONT>";
        $bugs ||= "none";
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editmilestones.cgi?product=", url_quote($product), "\"><B>$product</B></A></TD>\n";
        print "  <TD VALIGN=\"top\">$description</TD>\n";
        print "  <TD VALIGN=\"top\">$bugs</TD>\n";
    }
    print "</TR></TABLE>\n";

    PutTrailer();
    exit;
}



#
# action='' -> Show nice list of milestones
#

unless ($action) {
    PutHeader("$product �Υޥ��륹�ȡ��������");
    CheckProduct($product);

    SendSQL("SELECT value,sortkey
             FROM milestones
             WHERE product=" . SqlQuote($product) . "
             ORDER BY sortkey,value");

    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0><TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH ALIGN=\"left\">�ޥ��륹�ȡ����Խ� ...</TH>\n";
    #print "  <TH ALIGN=\"left\">�Х�</TH>\n";
    print "  <TH ALIGN=\"left\">�������ѤΥ���</TH>\n";
    print "  <TH ALIGN=\"left\">Action</TH>\n";
    print "</TR>";
    while ( MoreSQLData() ) {
        my ($milestone,$sortkey,$bugs) = FetchSQLData();
        $bugs ||= 'none';
        print "<TR>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editmilestones.cgi?product=", url_quote($product), "&milestone=", url_quote($milestone), "&action=edit\"><B>$milestone</B></A></TD>\n";
        #print "  <TD VALIGN=\"top\">$bugs</TD>\n";
        print "  <TD VALIGN=\"top\" ALIGN=\"right\">$sortkey</TD>\n";
        print "  <TD VALIGN=\"top\"><A HREF=\"editmilestones.cgi?product=", url_quote($product), "&milestone=", url_quote($milestone), "&action=del\"><B>���</B></A></TD>\n";
        print "</TR>";
    }
    print "<TR>\n";
    print "  <TD VALIGN=\"top\" COLSPAN=\"2\">�����ޥ��륹�ȡ�����ɲ�</TD>\n";
    print "  <TD VALIGN=\"top\" ALIGN=\"middle\"><A HREF=\"editmilestones.cgi?product=", url_quote($product) . "&action=add\">�ɲ�</A></TD>\n";
    print "</TR></TABLE>\n";

    PutTrailer();
    exit;
}




#
# action='add' -> present form for parameters for new milestone
#
# (next action will be 'new')
#

if ($action eq 'add') {
    PutHeader("$product �Υޥ��륹�ȡ�����ɲ�");
    CheckProduct($product);

    #print "This page lets you add a new milestone to a $::bugzilla_name tracked product.\n";

    print "<FORM METHOD=POST ACTION=editmilestones.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements($product, $milestone, 0);

    print "</TABLE>\n<HR>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"�ɲ�\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"new\">\n";
    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/�����/�̤�/;
    PutTrailer($other);
    exit;
}



#
# action='new' -> add milestone entered in the 'action=add' screen
#

if ($action eq 'new') {
    PutHeader("$product �˿����ޥ��륹�ȡ�����ɲ���");
    CheckProduct($product);

    # Cleanups and valididy checks

    unless ($milestone) {
        print "�����ޥ��륹�ȡ����̾�������Ϥ��Ƥ���������<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
    }
    if (TestMilestone($product,$milestone)) {
        print "'$milestone' �Ȥ����ޥ��륹�ȡ���ϴ���¸�ߤ��ޤ���<b>Back</b> �ܥ���򲡤��ơ����ľ���Ƥ���������\n";
        PutTrailer($localtrailer);
        exit;
    }

    # Add the new milestone
    SendSQL("INSERT INTO milestones ( " .
          "value, product, sortkey" .
          " ) VALUES ( " .
          SqlQuote($milestone) . "," .
          SqlQuote($product) . ", $sortkey)");

    # Make versioncache flush
    unlink "data/versioncache";

    print "OK����λ�Ǥ���<p>\n";
    PutTrailer($localtrailer);
    exit;
}




#
# action='del' -> ask if user really wants to delete
#
# (next action would be 'delete')
#

if ($action eq 'del') {
    PutHeader("$product �Υޥ��륹�ȡ������");
    CheckMilestone($product, $milestone);

    SendSQL("SELECT count(bug_id),product,target_milestone
             FROM bugs
             GROUP BY product,target_milestone
             HAVING product=" . SqlQuote($product) . "
                AND target_milestone=" . SqlQuote($milestone));
    my $bugs = FetchOneColumn();

    SendSQL("SELECT defaultmilestone FROM products " .
            "WHERE product=" . SqlQuote($product));
    my $defaultmilestone = FetchOneColumn();

    print "<TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>\n";
    print "<TR BGCOLOR=\"#6666FF\">\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Part</TH>\n";
    print "  <TH VALIGN=\"top\" ALIGN=\"left\">Value</TH>\n";

    print "</TR><TR>\n";
    print "  <TH ALIGN=\"left\" VALIGN=\"top\">�ץ������:</TH>\n";
    print "  <TD VALIGN=\"top\">$product</TD>\n";
    print "</TR><TR>\n";
    print "  <TH ALIGN=\"left\" VALIGN=\"top\">�ޥ��륹�ȡ���:</TH>\n";
    print "  <TD VALIGN=\"top\">$milestone</TD>\n";
    print "</TR><TR>\n";
    print "  <TH ALIGN=\"left\" VALIGN=\"top\">�Х���:</TH>\n";
    print "  <TD VALIGN=\"top\">", $bugs || 'none' , "</TD>\n";
    print "</TR></TABLE>\n";

    print "<H2>��ǧ</H2>\n";

    if ($bugs) {
        if (!Param("allowbugdeletion")) {
            print "�����櫓����ޤ��󡣤��Υޥ��륹�ȡ���ˤϥХ��� $bugs�� ¸�ߤ��ޤ��������ΥХ����̤Υޥ��륹�ȡ���˳�����Ƥʤ��ȡ����Υޥ��륹�ȡ���Ϻ���Ǥ��ޤ���";
            PutTrailer($localtrailer);
            exit;
        }
        print "<TABLE BORDER=0 CELLPADDING=20 WIDTH=\"70%\" BGCOLOR=\"red\"><TR><TD>\n",
              "���Υޥ��륹�ȡ���ˤϴ��˥Х�����Ͽ����Ƥ��ޤ������Υޥ��륹�ȡ����������ȡ���Ͽ����Ƥ���<B><BLINK>���٤Ƥ�</BLINK></B>�Х���������ޤ���",
              "���Υޥ��륹�ȡ���ΥХ�����⸫�뤳�Ȥ��Ǥ��ʤ��ʤ�ޤ�!\n",
              "</TD></TR></TABLE>\n";
    }

    if ($defaultmilestone eq $milestone) {
        print "���ߤޤ��󡢤���Ϥ��Υץ�����ȤΥǥե���ȥޥ��륹�ȡ���ʤΤǡ�������뤳�ȤϤǤ��ޤ���";
        PutTrailer($localtrailer);
        exit;
    }

    print "<P>Do you really want to delete this milestone?<P>\n";
    print "<FORM METHOD=POST ACTION=editmilestones.cgi>\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"�Ϥ���������ޤ�\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"delete\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"product\" VALUE=\"" .
        value_quote($product) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"milestone\" VALUE=\"" .
        value_quote($milestone) . "\">\n";
    print "</FORM>";

    PutTrailer($localtrailer);
    exit;
}



#
# action='delete' -> really delete the milestone
#

if ($action eq 'delete') {
    PutHeader("$product �Υޥ��륹�ȡ��������");
    CheckMilestone($product,$milestone);

    # lock the tables before we start to change everything:

    SendSQL("LOCK TABLES attachments WRITE,
                         bugs WRITE,
                         bugs_activity WRITE,
                         milestones WRITE,
                         dependencies WRITE");

    # According to MySQL doc I cannot do a DELETE x.* FROM x JOIN Y,
    # so I have to iterate over bugs and delete all the indivial entries
    # in bugs_activies and attachments.

    if (Param("allowbugdeletion")) {

        SendSQL("SELECT bug_id
             FROM bugs
             WHERE product=" . SqlQuote($product) . "
               AND target_milestone=" . SqlQuote($milestone));
        while (MoreSQLData()) {
            my $bugid = FetchOneColumn();

            PushGlobalSQLState();
            SendSQL("DELETE FROM attachments WHERE bug_id=$bugid");
            SendSQL("DELETE FROM bugs_activity WHERE bug_id=$bugid");
            SendSQL("DELETE FROM dependencies WHERE blocked=$bugid");
            PopGlobalSQLState();
        }
        print "ź�եե�����ȥХ���ư������¸�ط����������ޤ�����<BR>\n";


        # Deleting the rest is easier:

        SendSQL("DELETE FROM bugs
             WHERE product=" . SqlQuote($product) . "
               AND target_milestone=" . SqlQuote($milestone));
        print "�Х���ݡ��Ȥ��������ޤ�����<BR>\n";
    }

    SendSQL("DELETE FROM milestones
             WHERE product=" . SqlQuote($product) . "
               AND value=" . SqlQuote($milestone));
    print "�ޥ��륹�ȡ��󤬺������ޤ�����<P>\n";
    SendSQL("UNLOCK TABLES");

    unlink "data/versioncache";
    PutTrailer($localtrailer);
    exit;
}



#
# action='edit' -> present the edit milestone form
#
# (next action would be 'update')
#

if ($action eq 'edit') {
    PutHeader("$product �Υޥ��륹�ȡ�����Խ�");
    CheckMilestone($product,$milestone);

    SendSQL("SELECT sortkey FROM milestones WHERE product=" .
            SqlQuote($product) . " AND value = " . SqlQuote($milestone));
    my $sortkey = FetchOneColumn();

    print "<FORM METHOD=POST ACTION=editmilestones.cgi>\n";
    print "<TABLE BORDER=0 CELLPADDING=4 CELLSPACING=0><TR>\n";

    EmitFormElements($product, $milestone, $sortkey);

    print "</TR></TABLE>\n";

    print "<INPUT TYPE=HIDDEN NAME=\"milestoneold\" VALUE=\"" .
        value_quote($milestone) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"sortkeyold\" VALUE=\"" .
        value_quote($sortkey) . "\">\n";
    print "<INPUT TYPE=HIDDEN NAME=\"action\" VALUE=\"update\">\n";
    print "<INPUT TYPE=SUBMIT VALUE=\"����\">\n";

    print "</FORM>";

    my $other = $localtrailer;
    $other =~ s/�����/�̤�/;
    PutTrailer($other);
    exit;
}



#
# action='update' -> update the milestone
#

if ($action eq 'update') {
    PutHeader("$product �Υޥ��륹�ȡ���򹹿�");

    my $milestoneold = trim($::FORM{milestoneold} || '');
    my $sortkeyold = trim($::FORM{sortkeyold} || '0');

    CheckMilestone($product,$milestoneold);

    SendSQL("LOCK TABLES bugs WRITE,
                         milestones WRITE,
                         products WRITE");

    if ($sortkey != $sortkeyold) {
        SendSQL("UPDATE milestones SET sortkey=$sortkey
                 WHERE product=" . SqlQuote($product) . "
                   AND value=" . SqlQuote($milestoneold));
        unlink "data/versioncache";
        print "�������ѤΥ����򹹿�<BR>\n";
    }
    if ($milestone ne $milestoneold) {
        unless ($milestone) {
            print "���ߤޤ��󡢥ޥ��륹�ȡ���̾�����Ǥ��ޤ���Ǥ�����";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }
        if (TestMilestone($product,$milestone)) {
            print "���ߤޤ���'$milestone' �ޥ��륹�ȡ���ϴ��˻��Ѥ���Ƥ��ޤ���";
            PutTrailer($localtrailer);
            SendSQL("UNLOCK TABLES");
            exit;
        }
        SendSQL("UPDATE bugs
                 SET target_milestone=" . SqlQuote($milestone) . ",
                 delta_ts=delta_ts
                 WHERE target_milestone=" . SqlQuote($milestoneold) . "
                   AND product=" . SqlQuote($product));
        SendSQL("UPDATE milestones
                 SET value=" . SqlQuote($milestone) . "
                 WHERE product=" . SqlQuote($product) . "
                   AND value=" . SqlQuote($milestoneold));
        SendSQL("UPDATE products " .
                "SET defaultmilestone = " . SqlQuote($milestone) .
                "WHERE product = " . SqlQuote($product) .
                "  AND defaultmilestone = " . SqlQuote($milestoneold));
        unlink "data/versioncache";
        print "�ޥ��륹�ȡ���򹹿����ޤ�����<BR>\n";
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
