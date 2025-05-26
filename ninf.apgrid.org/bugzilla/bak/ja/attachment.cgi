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
#                 Myk Melez <myk@mozilla.org>

################################################################################
# Script Initialization
################################################################################

# Make it harder for us to do dangerous things in Perl.
use diagnostics;
use strict;

use lib qw(.);

use vars qw(
  $template
  $vars
);

# Include the Bugzilla CGI and general utility library.
require "CGI.pl";

# Establish a connection to the database backend.
ConnectToDatabase();

# Check whether or not the user is logged in and, if so, set the $::userid 
# and $::usergroupset variables.
quietly_check_login();

################################################################################
# Main Body Execution
################################################################################

# All calls to this script should contain an "action" variable whose value
# determines what the user wants to do.  The code below checks the value of
# that variable and runs the appropriate code.

# Determine whether to use the action specified by the user or the default.
my $action = $::FORM{'action'} || 'view';

if ($action eq "view")  
{ 
  validateID();
  view(); 
}
elsif ($action eq "viewall") 
{ 
  ValidateBugID($::FORM{'bugid'});
  viewall(); 
}
elsif ($action eq "enter") 
{ 
  confirm_login();
  ValidateBugID($::FORM{'bugid'});
  enter(); 
}
elsif ($action eq "insert")
{
  confirm_login();
  ValidateBugID($::FORM{'bugid'});
  ValidateComment($::FORM{'comment'});
  validateFilename();
  validateData();
  validateDescription();
  validateIsPatch();
  validateContentType() unless $::FORM{'ispatch'};
  validateObsolete() if $::FORM{'obsolete'};
  insert();
}
elsif ($action eq "edit") 
{ 
  quietly_check_login();
  validateID();
  validateCanEdit($::FORM{'id'});
  edit(); 
}
elsif ($action eq "update") 
{ 
  confirm_login();
  ValidateComment($::FORM{'comment'});
  validateID();
  validateCanEdit($::FORM{'id'});
  validateDescription();
  validateIsPatch();
  validateContentType() unless $::FORM{'ispatch'};
  validateIsObsolete();
  validateStatuses();
  update();
}
else 
{ 
  DisplayError("���ʤ������򤷤褦�Ȥ����Τ�����Ǥ��ޤ���Ǥ�����")
  }

exit;

################################################################################
# Data Validation / Security Authorization
################################################################################

sub validateID
{
    # Validate the value of the "id" form field, which must contain an
    # integer that is the ID of an existing attachment.

    detaint_natural($::FORM{'id'})
        || DisplayError("������ź�եե������ֹ�����Ϥ��Ƥ���������") 
      && exit;
  
  # Make sure the attachment exists in the database.
  SendSQL("SELECT bug_id FROM attachments WHERE attach_id = $::FORM{'id'}");
  MoreSQLData()
    || DisplayError("ź�եե����� #$::FORM{'id'} ��¸�ߤ��ޤ���") 
    && exit;

  # Make sure the user is authorized to access this attachment's bug.
  my ($bugid) = FetchSQLData();
  ValidateBugID($bugid);
}

sub validateCanEdit
{
    my ($attach_id) = (@_);

    # If the user is not logged in, claim that they can edit. This allows
    # the edit scrren to be displayed to people who aren't logged in.
    # People not logged in can't actually commit changes, because that code
    # calls confirm_login, not quietly_check_login, before calling this sub
    return if $::userid == 0;

    # People in editbugs can edit all attachments
    return if UserInGroup("editbugs");

    # Bug 97729 - the submitter can edit their attachments
    SendSQL("SELECT attach_id FROM attachments WHERE " .
            "attach_id = $attach_id AND submitter_id = $::userid");

    FetchSQLData()
      || DisplayError("ź�եե����� #$attach_id ���Խ����븢�¤�����ޤ���")
      && exit;
}

sub validateDescription
{
  $::FORM{'description'}
    || DisplayError("ź�եե���������������Ϥ��Ƥ���������")
      && exit;
}

sub validateIsPatch
{
  # Set the ispatch flag to zero if it is undefined, since the UI uses
  # an HTML checkbox to represent this flag, and unchecked HTML checkboxes
  # do not get sent in HTML requests.
  $::FORM{'ispatch'} = $::FORM{'ispatch'} ? 1 : 0;

  # Set the content type to text/plain if the attachment is a patch.
  $::FORM{'contenttype'} = "text/plain" if $::FORM{'ispatch'};
}

sub validateContentType
{
  if (!$::FORM{'contenttypemethod'})
  {
    DisplayError("�ե���������������ˡ��<em>��ưǧ��</em>, <em>�ꥹ�Ȥ�������</em>, or <em>��ư�����Ϥ���</em> ��������Ǥ���������");
    exit;
  }
  elsif ($::FORM{'contenttypemethod'} eq 'autodetect')
  {
    # The user asked us to auto-detect the content type, so use the type
    # specified in the HTTP request headers.
    if ( !$::FILE{'data'}->{'contenttype'} )
    {
      DisplayError("���ʤ��� Bugzilla �˥ե���������μ�ưǧ����ؼ����ޤ��������������ե�����򥢥åץ��ɤ���Ȥ� Web �֥饦�����ե��������������Ǥ��ޤ���Ǥ��������Τ��ᡢ�ե�����������ư�����Ϥ��Ƥ���������");
      exit;
    }
    $::FORM{'contenttype'} = $::FILE{'data'}->{'contenttype'};
  }
  elsif ($::FORM{'contenttypemethod'} eq 'list')
  {
    # The user selected a content type from the list, so use their selection.
    $::FORM{'contenttype'} = $::FORM{'contenttypeselection'};
  }
  elsif ($::FORM{'contenttypemethod'} eq 'manual')
  {
    # The user entered a content type manually, so use their entry.
    $::FORM{'contenttype'} = $::FORM{'contenttypeentry'};
  }
  else
  {
    my $htmlcontenttypemethod = html_quote($::FORM{'contenttypemethod'});
    DisplayError("�ե����फ������������ɤ�������Ƥ��ޤ���
<em>�ե��������</em>�ϡ�<em>��ưǧ��</em>, <em>�ꥹ�Ȥ�������</em>, or <em>��ư�����Ϥ���</em> �Τɤ줫�ˤʤ�Ϥ��Ǥ�����<em>$htmlcontenttypemethod</em> �ˤʤäƤ��ޤ���");
    exit;
  }

  if ( $::FORM{'contenttype'} !~ /^(application|audio|image|message|model|multipart|text|video)\/.+$/ )
  {
    my $htmlcontenttype = html_quote($::FORM{'contenttype'});
    DisplayError("�ե�������� <em>$htmlcontenttype</em> ������������ޤ��������������� <em>foo/bar</em> �����ǡ�<em>foo</em> ����ʬ�� <em>application, audio, image, message, model, multipart, text,</em> �ޤ��� <em>video</em> �ˤʤ�ޤ���");
    exit;
  }
}

sub validateIsObsolete
{
  # Set the isobsolete flag to zero if it is undefined, since the UI uses
  # an HTML checkbox to represent this flag, and unchecked HTML checkboxes
  # do not get sent in HTML requests.
  $::FORM{'isobsolete'} = $::FORM{'isobsolete'} ? 1 : 0;
}

sub validateStatuses
{
  # Get a list of attachment statuses that are valid for this attachment.
  PushGlobalSQLState();
  SendSQL("SELECT  attachstatusdefs.id
           FROM    attachments, bugs, attachstatusdefs
           WHERE   attachments.attach_id = $::FORM{'id'}
           AND     attachments.bug_id = bugs.bug_id
           AND     attachstatusdefs.product = bugs.product");
  my @statusdefs;
  push(@statusdefs, FetchSQLData()) while MoreSQLData();
  PopGlobalSQLState();
  
  foreach my $status (@{$::MFORM{'status'}})
  {
    grep($_ == $status, @statusdefs)
      || DisplayError("���Ϥ��줿���ơ������ΰ�Ĥ���ź�եե�����Υ��ơ������Ȥ�������������ޤ���")
        && exit;
    # We have tested that the status is valid, so it can be detainted
    detaint_natural($status);
  }
}

sub validateData
{
  $::FORM{'data'}
    || DisplayError("ź�դ��褦�Ȥ����ե�����϶��Ǥ�!")
      && exit;

  my $len = length($::FORM{'data'});

  my $maxpatchsize = Param('maxpatchsize');
  my $maxattachmentsize = Param('maxattachmentsize');
  
  # Makes sure the attachment does not exceed either the "maxpatchsize" or 
  # the "maxattachmentsize" parameter.
  if ( $::FORM{'ispatch'} && $maxpatchsize && $len > $maxpatchsize*1024 )
  {
    my $lenkb = sprintf("%.0f", $len/1024);
    DisplayError("���ʤ���ź�դ��褦�Ȥ����ե������ ${lenkb}KB (����Х���) �Ǥ����ѥå��κ��祵������ ${maxpatchsize}KB �Ǥ����ѥå��򤤤��Ĥ���ʬ�䤷�ƤߤƤ���������");
    exit;
  } elsif ( !$::FORM{'ispatch'} && $maxattachmentsize && $len > $maxattachmentsize*1024 ) {
    my $lenkb = sprintf("%.0f", $len/1024);
    DisplayError("���ʤ���ź�դ��褦�Ȥ����ե������ ${lenkb}KB (����Х���) �Ǥ����ѥå��ʳ���ź�եե�����κ��祵������ ${maxpatchsize}KB �Ǥ��������Ǥ���� JPG �� PNG �Τ褦�ʰ��̥ե����ޥåȤ��Ѵ�����Ȥ���web ����̤ξ����֤��� URL ��˵������롢�����Ȥ˵������ƥ�󥯤���ʤɤ��ƤߤƤ���������");
    exit;
  }
}

sub validateFilename
{
  defined $::FILE{'data'}
    || DisplayError("ź�դ���ե����뤬���ꤵ��Ƥ��ޤ���")
      && exit;
}

sub validateObsolete
{
  # Make sure the attachment id is valid and the user has permissions to view
  # the bug to which it is attached.
  foreach my $attachid (@{$::MFORM{'obsolete'}}) {
    detaint_natural($attachid)
      || DisplayError("���ʤ����ѺѤߤˤ�����ź�եե�������ֹ�Τ�����Ĥϡ�����������ޤ���") 
        && exit;
  
    SendSQL("SELECT bug_id, isobsolete, description 
             FROM attachments WHERE attach_id = $attachid");

    # Make sure the attachment exists in the database.
    MoreSQLData()
      || DisplayError("ź�եե����� #$attachid ��¸�ߤ��ޤ���") 
        && exit;

    my ($bugid, $isobsolete, $description) = FetchSQLData();

    if ($bugid != $::FORM{'bugid'})
    {
      $description = html_quote($description);
      DisplayError("ź�եե����� #$attachid ($description) �� bug #$bugid ��ź�դ���Ƥ��ޤ������������ʤ��ϡ�������ѺѤߤˤ��褦�Ȥ�������ǡ�bug #$::FORM{'bugid'} �˥ե�����򿷤���ź�դ��褦�Ȥ��Ƥ��ޤ���");
      exit;
    }

    if ( $isobsolete )
    {
      $description = html_quote($description);
      DisplayError("ź�եե����� #$attachid ($description) �ϴ����ѺѤߤˤʤäƤ��ޤ���");
      exit;
    }

    # Check that the user can modify this attachment
    validateCanEdit($attachid);
  }

}

################################################################################
# Functions
################################################################################

sub view
{
  # Display an attachment.

  # Retrieve the attachment content and its content type from the database.
  SendSQL("SELECT mimetype, thedata FROM attachments WHERE attach_id = $::FORM{'id'}");
  my ($contenttype, $thedata) = FetchSQLData();
    
  # Return the appropriate HTTP response headers.
  print "Content-Type: $contenttype\n\n";

  print $thedata;
}


sub viewall
{
  # Display all attachments for a given bug in a series of IFRAMEs within one HTML page.

  # Retrieve the attachments from the database and write them into an array
  # of hashes where each hash represents one attachment.
  SendSQL("SELECT attach_id, creation_ts, mimetype, description, ispatch, isobsolete 
           FROM attachments WHERE bug_id = $::FORM{'bugid'} ORDER BY attach_id");
  my @attachments; # the attachments array
  while (MoreSQLData())
  {
    my %a; # the attachment hash
    ($a{'attachid'}, $a{'date'}, $a{'contenttype'}, 
     $a{'description'}, $a{'ispatch'}, $a{'isobsolete'}) = FetchSQLData();

    # Format the attachment's creation/modification date into something readable.
    if ($a{'date'} =~ /^(\d\d)(\d\d)(\d\d)(\d\d)(\d\d)(\d\d)(\d\d)$/) {
        $a{'date'} = "$3/$4/$2&nbsp;$5:$6";
    }

    # Flag attachments as to whether or not they can be viewed (as opposed to
    # being downloaded).  Currently I decide they are viewable if their MIME type 
    # is either text/*, image/*, or application/vnd.mozilla.*.
    # !!! Yuck, what an ugly hack.  Fix it!
    $a{'isviewable'} = ( $a{'contenttype'} =~ /^(text|image|application\/vnd\.mozilla\.)/ );

    # Retrieve a list of status flags that have been set on the attachment.
    PushGlobalSQLState();
    SendSQL("SELECT    name 
             FROM      attachstatuses, attachstatusdefs 
             WHERE     attach_id = $a{'attachid'} 
             AND       attachstatuses.statusid = attachstatusdefs.id
             ORDER BY  sortkey");
    my @statuses;
    push(@statuses, FetchSQLData()) while MoreSQLData();
    $a{'statuses'} = \@statuses;
    PopGlobalSQLState();

    # Add the hash representing the attachment to the array of attachments.
    push @attachments, \%a;
  }

  # Retrieve the bug summary for displaying on screen.
  SendSQL("SELECT short_desc FROM bugs WHERE bug_id = $::FORM{'bugid'}");
  my ($bugsummary) = FetchSQLData();

  # Define the variables and functions that will be passed to the UI template.
  $vars->{'bugid'} = $::FORM{'bugid'};
  $vars->{'bugsummary'} = $bugsummary;
  $vars->{'attachments'} = \@attachments;

  # Return the appropriate HTTP response headers.
  print "Content-Type: text/html; charset=EUC-JP\n\n";

  # Generate and return the UI (HTML page) from the appropriate template.
  $template->process("attachment/show-multiple.html.tmpl", $vars)
    || ThrowTemplateError($template->error());
}


sub enter
{
  # Display a form for entering a new attachment.

  # Retrieve the attachments the user can edit from the database and write
  # them into an array of hashes where each hash represents one attachment.
  my $canEdit = "";
  if (!UserInGroup("editbugs")) {
      $canEdit = "AND submitter_id = $::userid";
  }
  SendSQL("SELECT attach_id, description 
           FROM attachments
           WHERE bug_id = $::FORM{'bugid'}
           AND isobsolete = 0 $canEdit
           ORDER BY attach_id");
  my @attachments; # the attachments array
  while ( MoreSQLData() ) {
    my %a; # the attachment hash
    ($a{'id'}, $a{'description'}) = FetchSQLData();

    # Add the hash representing the attachment to the array of attachments.
    push @attachments, \%a;
  }

  # Retrieve the bug summary for displaying on screen.
  SendSQL("SELECT short_desc FROM bugs WHERE bug_id = $::FORM{'bugid'}");
  my ($bugsummary) = FetchSQLData();

  # Define the variables and functions that will be passed to the UI template.
  $vars->{'bugid'} = $::FORM{'bugid'};
  $vars->{'bugsummary'} = $bugsummary;
  $vars->{'attachments'} = \@attachments;

  # Return the appropriate HTTP response headers.
  print "Content-Type: text/html; charset=EUC-JP\n\n";

  # Generate and return the UI (HTML page) from the appropriate template.
  $template->process("attachment/create.html.tmpl", $vars)
    || ThrowTemplateError($template->error());
}


sub insert
{
  # Insert a new attachment into the database.

  # Escape characters in strings that will be used in SQL statements.
  my $filename = SqlQuote($::FILE{'data'}->{'filename'});
  my $description = SqlQuote($::FORM{'description'});
  my $contenttype = SqlQuote($::FORM{'contenttype'});
  my $thedata = SqlQuote($::FORM{'data'});

  # Insert the attachment into the database.
  SendSQL("INSERT INTO attachments (bug_id, filename, description, mimetype, ispatch, submitter_id, thedata) 
           VALUES ($::FORM{'bugid'}, $filename, $description, $contenttype, $::FORM{'ispatch'}, $::userid, $thedata)");

  # Retrieve the ID of the newly created attachment record.
  SendSQL("SELECT LAST_INSERT_ID()");
  my $attachid = FetchOneColumn();

  # Insert a comment about the new attachment into the database.
  my $comment = "Created an attachment (id=$attachid)\n$::FORM{'description'}\n";
  $comment .= ("\n" . $::FORM{'comment'}) if $::FORM{'comment'};

  use Text::Wrap;
  $Text::Wrap::columns = 80;
  $Text::Wrap::huge = 'overflow';
  $comment = Text::Wrap::wrap('', '', $comment);

  AppendComment($::FORM{'bugid'}, 
                $::COOKIE{"Bugzilla_login"},
                $comment);

  # Make existing attachments obsolete.
  my $fieldid = GetFieldID('attachments.isobsolete');
  foreach my $attachid (@{$::MFORM{'obsolete'}}) {
    SendSQL("UPDATE attachments SET isobsolete = 1 WHERE attach_id = $attachid");
    SendSQL("INSERT INTO bugs_activity (bug_id, attach_id, who, bug_when, fieldid, removed, added) 
             VALUES ($::FORM{'bugid'}, $attachid, $::userid, NOW(), $fieldid, '0', '1')");
  }

  # Send mail to let people know the attachment has been created.  Uses a 
  # special syntax of the "open" and "exec" commands to capture the output of 
  # "processmail", which "system" doesn't allow, without running the command 
  # through a shell, which backticks (``) do.
  #system ("./processmail", $bugid , $::userid);
  #my $mailresults = `./processmail $bugid $::userid`;
  my $mailresults = '';
  open(PMAIL, "-|") or exec('./processmail', $::FORM{'bugid'}, $::COOKIE{'Bugzilla_login'});
  $mailresults .= $_ while <PMAIL>;
  close(PMAIL);
 
  # Define the variables and functions that will be passed to the UI template.
  $vars->{'bugid'} = $::FORM{'bugid'};
  $vars->{'attachid'} = $attachid;
  $vars->{'description'} = $description;
  $vars->{'mailresults'} = $mailresults;
  $vars->{'contenttypemethod'} = $::FORM{'contenttypemethod'};
  $vars->{'contenttype'} = $::FORM{'contenttype'};

  # Return the appropriate HTTP response headers.
  print "Content-Type: text/html; charset=EUC-JP\n\n";

  # Generate and return the UI (HTML page) from the appropriate template.
  $template->process("attachment/created.html.tmpl", $vars)
    || ThrowTemplateError($template->error());
}


sub edit
{
  # Edit an attachment record.  Users with "editbugs" privileges, (or the 
  # original attachment's submitter) can edit the attachment's description,
  # content type, ispatch and isobsolete flags, and statuses, and they can
  # also submit a comment that appears in the bug.
  # Users cannot edit the content of the attachment itself.

  # Retrieve the attachment from the database.
  SendSQL("SELECT description, mimetype, bug_id, ispatch, isobsolete 
           FROM attachments WHERE attach_id = $::FORM{'id'}");
  my ($description, $contenttype, $bugid, $ispatch, $isobsolete) = FetchSQLData();

  # Flag attachment as to whether or not it can be viewed (as opposed to
  # being downloaded).  Currently I decide it is viewable if its content
  # type is either text/.* or application/vnd.mozilla.*.
  # !!! Yuck, what an ugly hack.  Fix it!
  my $isviewable = ( $contenttype =~ /^(text|image|application\/vnd\.mozilla\.)/ );

  # Retrieve a list of status flags that have been set on the attachment.
  my %statuses;
  SendSQL("SELECT  id, name 
           FROM    attachstatuses JOIN attachstatusdefs 
           WHERE   attachstatuses.statusid = attachstatusdefs.id 
           AND     attach_id = $::FORM{'id'}");
  while ( my ($id, $name) = FetchSQLData() )
  {
    $statuses{$id} = $name;
  }

  # Retrieve a list of statuses for this bug's product, and build an array 
  # of hashes in which each hash is a status flag record.
  # ???: Move this into versioncache or its own routine?
  my @statusdefs;
  SendSQL("SELECT   id, name 
           FROM     attachstatusdefs, bugs 
           WHERE    bug_id = $bugid 
           AND      attachstatusdefs.product = bugs.product 
           ORDER BY sortkey");
  while ( MoreSQLData() )
  {
    my ($id, $name) = FetchSQLData();
    push @statusdefs, { 'id' => $id , 'name' => $name };
  }

  # Retrieve a list of attachments for this bug as well as a summary of the bug
  # to use in a navigation bar across the top of the screen.
  SendSQL("SELECT attach_id FROM attachments WHERE bug_id = $bugid ORDER BY attach_id");
  my @bugattachments;
  push(@bugattachments, FetchSQLData()) while (MoreSQLData());
  SendSQL("SELECT short_desc FROM bugs WHERE bug_id = $bugid");
  my ($bugsummary) = FetchSQLData();

  # Define the variables and functions that will be passed to the UI template.
  $vars->{'attachid'} = $::FORM{'id'}; 
  $vars->{'description'} = $description; 
  $vars->{'contenttype'} = $contenttype; 
  $vars->{'bugid'} = $bugid; 
  $vars->{'bugsummary'} = $bugsummary; 
  $vars->{'ispatch'} = $ispatch; 
  $vars->{'isobsolete'} = $isobsolete; 
  $vars->{'isviewable'} = $isviewable; 
  $vars->{'statuses'} = \%statuses; 
  $vars->{'statusdefs'} = \@statusdefs; 
  $vars->{'attachments'} = \@bugattachments; 

  # Return the appropriate HTTP response headers.
  print "Content-Type: text/html; charset=EUC-JP\n\n";

  # Generate and return the UI (HTML page) from the appropriate template.
  $template->process("attachment/edit.html.tmpl", $vars)
    || ThrowTemplateError($template->error());
}


sub update
{
  # Update an attachment record.

  # Get the bug ID for the bug to which this attachment is attached.
  SendSQL("SELECT bug_id FROM attachments WHERE attach_id = $::FORM{'id'}");
  my $bugid = FetchSQLData() 
    || DisplayError("�Х��ֹ椬�狼��ʤ��ʤ�ޤ�����")
    && exit;

  # Lock database tables in preparation for updating the attachment.
  SendSQL("LOCK TABLES attachments WRITE , attachstatuses WRITE , 
           attachstatusdefs READ , fielddefs READ , bugs_activity WRITE");

  # Get a copy of the attachment record before we make changes
  # so we can record those changes in the activity table.
  SendSQL("SELECT description, mimetype, ispatch, isobsolete 
           FROM attachments WHERE attach_id = $::FORM{'id'}");
  my ($olddescription, $oldcontenttype, $oldispatch, $oldisobsolete) = FetchSQLData();

  # Get the list of old status flags.
  SendSQL("SELECT    attachstatusdefs.name 
           FROM      attachments, attachstatuses, attachstatusdefs
           WHERE     attachments.attach_id = $::FORM{'id'}
           AND       attachments.attach_id = attachstatuses.attach_id
           AND       attachstatuses.statusid = attachstatusdefs.id
           ORDER BY  attachstatusdefs.sortkey
          ");
  my @oldstatuses;
  while (MoreSQLData()) {
    push(@oldstatuses, FetchSQLData());
  }
  my $oldstatuslist = join(', ', @oldstatuses);

  # Update the database with the new status flags.
  SendSQL("DELETE FROM attachstatuses WHERE attach_id = $::FORM{'id'}");
  foreach my $statusid (@{$::MFORM{'status'}}) 
  {
    SendSQL("INSERT INTO attachstatuses (attach_id, statusid) VALUES ($::FORM{'id'}, $statusid)");
  }

  # Get the list of new status flags.
  SendSQL("SELECT    attachstatusdefs.name 
           FROM      attachments, attachstatuses, attachstatusdefs
           WHERE     attachments.attach_id = $::FORM{'id'}
           AND       attachments.attach_id = attachstatuses.attach_id
           AND       attachstatuses.statusid = attachstatusdefs.id
           ORDER BY  attachstatusdefs.sortkey
          ");
  my @newstatuses;
  while (MoreSQLData()) {
    push(@newstatuses, FetchSQLData());
  }
  my $newstatuslist = join(', ', @newstatuses);

  # Quote the description and content type for use in the SQL UPDATE statement.
  my $quoteddescription = SqlQuote($::FORM{'description'});
  my $quotedcontenttype = SqlQuote($::FORM{'contenttype'});

  # Update the attachment record in the database.
  # Sets the creation timestamp to itself to avoid it being updated automatically.
  SendSQL("UPDATE  attachments 
           SET     description = $quoteddescription , 
                   mimetype = $quotedcontenttype , 
                   ispatch = $::FORM{'ispatch'} , 
                   isobsolete = $::FORM{'isobsolete'} , 
                   creation_ts = creation_ts
           WHERE   attach_id = $::FORM{'id'}
         ");

  # Record changes in the activity table.
  if ($olddescription ne $::FORM{'description'}) {
    my $quotedolddescription = SqlQuote($olddescription);
    my $fieldid = GetFieldID('attachments.description');
    SendSQL("INSERT INTO bugs_activity (bug_id, attach_id, who, bug_when, fieldid, removed, added) 
             VALUES ($bugid, $::FORM{'id'}, $::userid, NOW(), $fieldid, $quotedolddescription, $quoteddescription)");
  }
  if ($oldcontenttype ne $::FORM{'contenttype'}) {
    my $quotedoldcontenttype = SqlQuote($oldcontenttype);
    my $fieldid = GetFieldID('attachments.mimetype');
    SendSQL("INSERT INTO bugs_activity (bug_id, attach_id, who, bug_when, fieldid, removed, added) 
             VALUES ($bugid, $::FORM{'id'}, $::userid, NOW(), $fieldid, $quotedoldcontenttype, $quotedcontenttype)");
  }
  if ($oldispatch ne $::FORM{'ispatch'}) {
    my $fieldid = GetFieldID('attachments.ispatch');
    SendSQL("INSERT INTO bugs_activity (bug_id, attach_id, who, bug_when, fieldid, removed, added) 
             VALUES ($bugid, $::FORM{'id'}, $::userid, NOW(), $fieldid, $oldispatch, $::FORM{'ispatch'})");
  }
  if ($oldisobsolete ne $::FORM{'isobsolete'}) {
    my $fieldid = GetFieldID('attachments.isobsolete');
    SendSQL("INSERT INTO bugs_activity (bug_id, attach_id, who, bug_when, fieldid, removed, added) 
             VALUES ($bugid, $::FORM{'id'}, $::userid, NOW(), $fieldid, $oldisobsolete, $::FORM{'isobsolete'})");
  }
  if ($oldstatuslist ne $newstatuslist) {
    my ($removed, $added) = DiffStrings($oldstatuslist, $newstatuslist);
    my $quotedremoved = SqlQuote($removed);
    my $quotedadded = SqlQuote($added);
    my $fieldid = GetFieldID('attachstatusdefs.name');
    SendSQL("INSERT INTO bugs_activity (bug_id, attach_id, who, bug_when, fieldid, removed, added) 
             VALUES ($bugid, $::FORM{'id'}, $::userid, NOW(), $fieldid, $quotedremoved, $quotedadded)");
  }

  # Unlock all database tables now that we are finished updating the database.
  SendSQL("UNLOCK TABLES");

  # If this installation has enabled the request manager, let the manager know
  # an attachment was updated so it can check for requests on that attachment
  # and fulfill them.  The request manager allows users to request database
  # changes of other users and tracks the fulfillment of those requests.  When
  # an attachment record is updated and the request manager is called, it will
  # fulfill those requests that were requested of the user performing the update
  # which are requests for the attachment being updated.
  #my $requests;
  #if (Param('userequestmanager'))
  #{
  #  use Request;
  #  # Specify the fieldnames that have been updated.
  #  my @fieldnames = ('description', 'mimetype', 'status', 'ispatch', 'isobsolete');
  #  # Fulfill pending requests.
  #  $requests = Request::fulfillRequest('attachment', $::FORM{'id'}, @fieldnames);
  #  $vars->{'requests'} = $requests; 
  #}

  # If the user submitted a comment while editing the attachment, 
  # add the comment to the bug.
  if ( $::FORM{'comment'} )
  {
    use Text::Wrap;
    $Text::Wrap::columns = 80;
    $Text::Wrap::huge = 'wrap';

    # Append a string to the comment to let users know that the comment came from
    # the "edit attachment" screen.
    my $comment = qq|(From update of attachment $::FORM{'id'})\n| . $::FORM{'comment'};

    my $wrappedcomment = "";
    foreach my $line (split(/\r\n|\r|\n/, $comment))
    {
      if ( $line =~ /^>/ )
      {
        $wrappedcomment .= $line . "\n";
      }
      else
      {
        $wrappedcomment .= wrap('', '', $line) . "\n";
      }
    }

    # Get the user's login name since the AppendComment function needs it.
    my $who = DBID_to_name($::userid);
    # Mention $::userid again so Perl doesn't give me a warning about it.
    my $neverused = $::userid;

    # Append the comment to the list of comments in the database.
    AppendComment($bugid, $who, $wrappedcomment);

  }

  # Send mail to let people know the bug has changed.  Uses a special syntax
  # of the "open" and "exec" commands to capture the output of "processmail",
  # which "system" doesn't allow, without running the command through a shell,
  # which backticks (``) do.
  #system ("./processmail", $bugid , $::userid);
  #my $mailresults = `./processmail $bugid $::userid`;
  my $mailresults = '';
  open(PMAIL, "-|") or exec('./processmail', $bugid, DBID_to_name($::userid));
  $mailresults .= $_ while <PMAIL>;
  close(PMAIL);
 
  # Define the variables and functions that will be passed to the UI template.
  $vars->{'attachid'} = $::FORM{'id'}; 
  $vars->{'bugid'} = $bugid; 
  $vars->{'mailresults'} = $mailresults; 

  # Return the appropriate HTTP response headers.
  print "Content-Type: text/html; charset=EUC-JP\n\n";

  # Generate and return the UI (HTML page) from the appropriate template.
  $template->process("attachment/updated.html.tmpl", $vars)
    || ThrowTemplateError($template->error());
}
