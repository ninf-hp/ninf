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
# Contributor(s): Myk Melez <myk@mozilla.org>

############################################################################
# Script Initialization
############################################################################

# Make it harder for us to do dangerous things in Perl.
use diagnostics;
use strict;

use lib qw(.);

use vars qw($template $vars);

# Include the Bugzilla CGI and general utility library.
require "CGI.pl";

# Establish a connection to the database backend.
ConnectToDatabase();
quietly_check_login();

# Use the "Token" module that contains functions for doing various
# token-related tasks.
use Token;

################################################################################
# Data Validation / Security Authorization
################################################################################

# Throw an error if the form does not contain an "action" field specifying
# what the user wants to do.
$::FORM{'a'}
  || DisplayError("I could not figure out what you wanted to do.")
  && exit;

# Assign the action to a global variable.
$::action = $::FORM{'a'};

# If a token was submitted, make sure it is a valid token that exists in the
# database and is the correct type for the action being taken.
if ($::FORM{'t'}) {
  # Assign the token and its SQL quoted equivalent to global variables.
  $::token = $::FORM{'t'};
  $::quotedtoken = SqlQuote($::token);
  
  # Make sure the token contains only valid characters in the right amount.
  my $validationerror = ValidatePassword($::token);
  if ($validationerror) {
      DisplayError('あなたが入力したトークンは正しくありません');
      exit;
  }


  Token::CleanTokenTable();

  # Make sure the token exists in the database.
  SendSQL( "SELECT tokentype FROM tokens WHERE token = $::quotedtoken" );
  (my $tokentype = FetchSQLData())
    || DisplayError("あなたが送信したトークンは存在しないか、期限切れか、キャンセルされました。")
    && exit;

  # Make sure the token is the correct type for the action being taken.
  if ( grep($::action eq $_ , qw(cfmpw cxlpw chgpw)) && $tokentype ne 'password' ) {
    DisplayError("このトークンはパスワード変更に使われているものではありません。");
    Token::Cancel($::token, "ユーザがトークンを使ってパスワードを変更しようとしました");
    exit;
  }
  if ( ($::action eq 'cxlem') 
      && (($tokentype ne 'emailold') && ($tokentype ne 'emailnew')) ) {
    DisplayError("このトークンは、メールアドレス変更のキャンセルに使われているものではありません。");
    Token::Cancel($::token, 
                  "ユーザがトークンを使ってメールアドレスの変更をキャンセルしようとしました");
    exit;
  }
  if ( grep($::action eq $_ , qw(cfmem chgem)) 
      && ($tokentype ne 'emailnew') ) {
    DisplayError("このトークンは、メールアドレスの変更に使われているものではありません。<!--That token cannot be used to change your email address.-->");
    Token::Cancel($::token, 
                  "ユーザがトークンを使ってメールアドレスを変更しようとしました");
    exit;
  }
}

# If the user is requesting a password change, make sure they submitted
# their login name and it exists in the database.
if ( $::action eq 'reqpw' ) {
    defined $::FORM{'loginname'}
      || DisplayError("パスワード変更を要求するときは、ログイン名を入力してください。")
      && exit;

    # Make sure the login name looks like an email address.  This function
    # displays its own error and stops execution if the login name looks wrong.
    CheckEmailSyntax($::FORM{'loginname'});

    my $quotedloginname = SqlQuote($::FORM{'loginname'});
    SendSQL("SELECT userid FROM profiles WHERE login_name = $quotedloginname");
    FetchSQLData()
      || DisplayError("Bugzilla にはそのログイン名のアカウントは存在しません。")
      && exit;
}

# If the user is changing their password, make sure they submitted a new
# password and that the new password is valid.
if ( $::action eq 'chgpw' ) {
    defined $::FORM{'password'}
      && defined $::FORM{'matchpassword'}
      || DisplayError("新しいパスワードを送信しないと、あなたのパスワードを変更できません。")
      && exit;

     my $passworderror = ValidatePassword($::FORM{'password'}, $::FORM{'matchpassword'});
     if ( $passworderror ) {
         DisplayError($passworderror);
         exit;
     }
}

################################################################################
# Main Body Execution
################################################################################

# All calls to this script should contain an "action" variable whose value
# determines what the user wants to do.  The code below checks the value of
# that variable and runs the appropriate code.

if ($::action eq 'reqpw') { 
    requestChangePassword(); 
} elsif ($::action eq 'cfmpw') { 
    confirmChangePassword(); 
} elsif ($::action eq 'cxlpw') { 
    cancelChangePassword(); 
} elsif ($::action eq 'chgpw') { 
    changePassword(); 
} elsif ($::action eq 'cfmem') {
    confirmChangeEmail();
} elsif ($::action eq 'cxlem') {
    cancelChangeEmail();
} elsif ($::action eq 'chgem') {
    changeEmail();
} else { 
    # If the action that the user wants to take (specified in the "a" form field)
    # is none of the above listed actions, display an error telling the user 
    # that we do not understand what they would like to do.
    DisplayError("あなたが何をしようとしたのか理解できませんでした。");
}

exit;

################################################################################
# Functions
################################################################################

sub requestChangePassword {
    Token::IssuePasswordToken($::FORM{'loginname'});

    $vars->{'title'} = "パスワード変更のリクエスト";
    $vars->{'message'} = "パスワード変更のためのトークンがあなたにメールされました。メールの説明にしたがってパスワードを変更してください。";

    print "Content-Type: text/html; charset=EUC-JP\n\n";
    $template->process("global/message.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

sub confirmChangePassword {
    $vars->{'title'} = "パスワード変更";
    $vars->{'token'} = $::token;
    
    print "Content-Type: text/html; charset=EUC-JP\n\n";
    $template->process("account/password/set-forgotten-password.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

sub cancelChangePassword {    
    Token::Cancel($::token, "パスワード変更の取り消し");

    $vars->{'title'} = "パスワード変更リクエストの取り消し";
    $vars->{'message'} = "あなたのリクエストは取り消されました。";

    print "Content-Type: text/html; charset=EUC-JP\n\n";
    $template->process("global/message.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

sub changePassword {
    # Quote the password and token for inclusion into SQL statements.
    my $cryptedpassword = Crypt($::FORM{'password'});
    my $quotedpassword = SqlQuote($cryptedpassword);

    # Get the user's ID from the tokens table.
    SendSQL("SELECT userid FROM tokens WHERE token = $::quotedtoken");
    my $userid = FetchSQLData();
    
    # Update the user's password in the profiles table and delete the token
    # from the tokens table.
    SendSQL("LOCK TABLES profiles WRITE , tokens WRITE");
    SendSQL("UPDATE   profiles
             SET      cryptpassword = $quotedpassword
             WHERE    userid = $userid");
    SendSQL("DELETE FROM tokens WHERE token = $::quotedtoken");
    SendSQL("UNLOCK TABLES");

    InvalidateLogins($userid);

    $vars->{'title'} = "パスワードが変更されました";
    $vars->{'message'} = "パスワードが変更されました。";

    print "Content-Type: text/html; charset=EUC-JP\n\n";
    $template->process("global/message.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

sub confirmChangeEmail {
    # Return HTTP response headers.
    print "Content-Type: text/html; charset=EUC-JP\n\n";

    $vars->{'title'} = "変更メールの確認";
    $vars->{'token'} = $::token;

    $template->process("account/email/confirm.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

sub changeEmail {

    # Get the user's ID from the tokens table.
    SendSQL("SELECT userid, eventdata FROM tokens 
              WHERE token = $::quotedtoken");
    my ($userid, $eventdata) = FetchSQLData();
    my ($old_email, $new_email) = split(/:/,$eventdata);
    my $quotednewemail = SqlQuote($new_email);

    # Check the user entered the correct old email address
    if($::FORM{'email'} ne $old_email) {
        DisplayError("メールアドレスの確認に失敗しました");
        exit;
    }
    # The new email address should be available as this was 
    # confirmed initially so cancel token if it is not still available
    if (! ValidateNewUser($new_email,$old_email)) {
        DisplayError("$new_email のアカウントは既に存在します。");
        Token::Cancel($::token,"$new_email のアカウントは既に存在します");
        exit;
    } 

    # Update the user's login name in the profiles table and delete the token
    # from the tokens table.
    SendSQL("LOCK TABLES profiles WRITE , tokens WRITE");
    SendSQL("UPDATE   profiles
         SET      login_name = $quotednewemail
         WHERE    userid = $userid");
    SendSQL("DELETE FROM tokens WHERE token = $::quotedtoken");
    SendSQL("DELETE FROM tokens WHERE userid = $userid 
                                  AND tokentype = 'emailnew'");
    SendSQL("UNLOCK TABLES");

    # Return HTTP response headers.
    print "Content-Type: text/html; charset=EUC-JP\n\n";

    # Let the user know their email address has been changed.

    $vars->{'title'} = "Bugzilla ログイン名は変更されました";
    $vars->{'message'} = "あなたの Bugzilla ログイン名は変更されました。";

    $template->process("global/message.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

sub cancelChangeEmail {
    # Get the user's ID from the tokens table.
    SendSQL("SELECT userid, tokentype, eventdata FROM tokens 
             WHERE token = $::quotedtoken");
    my ($userid, $tokentype, $eventdata) = FetchSQLData();
    my ($old_email, $new_email) = split(/:/,$eventdata);

    if($tokentype eq "emailold") {
        $vars->{'message'} = "あなたのアカウントから $new_email への、メールアドレス変更リクエストは取り消されました。";

        SendSQL("SELECT login_name FROM profiles WHERE userid = $userid");
        my $actualemail = FetchSQLData();
        
        # check to see if it has been altered
        if($actualemail ne $old_email) {
            my $quotedoldemail = SqlQuote($old_email);

            SendSQL("LOCK TABLES profiles WRITE");
            SendSQL("UPDATE   profiles
                 SET      login_name = $quotedoldemail
                 WHERE    userid = $userid");
            SendSQL("UNLOCK TABLES");
            $vars->{'message'} .= 
                "  あなたの旧アカウント設定は復活しました。";
        } 
    } 
    else {
        $vars->{'message'} = "$old_email のアカウントから $new_email への、メールアドレス変更リクエストは取り消されました";
    }
    Token::Cancel($::token, $vars->{'message'});

    SendSQL("LOCK TABLES tokens WRITE");
    SendSQL("DELETE FROM tokens 
             WHERE userid = $userid 
             AND tokentype = 'emailold' OR tokentype = 'emailnew'");
    SendSQL("UNLOCK TABLES");

    # Return HTTP response headers.
    print "Content-Type: text/html; charset=EUC-JP\n\n";

    $vars->{'title'} = "メールアドレス変更リクエストの取り消し";

    $template->process("global/message.html.tmpl", $vars)
      || ThrowTemplateError($template->error());
}

