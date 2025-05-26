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
            return "データベース$n は既に存在します。もし本当にこの名前をバックアップ用に使用したいならば、気をつけて今あるデータベースをどこかへ移してからやり直してください。";
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
         "この Bugzilla の管理者のメールアドレス。",
         "t",
         'THE MAINTAINER HAS NOT YET BEEN SET');

DefParam("urlbase",
         "Bugzilla の URL の前に加えるURL。",
         "t",
         "http://cvs-mirror.mozilla.org/webtools/bugzilla/",
         \&check_urlbase);

sub check_urlbase {
    my ($url) = (@_);
    if ($url !~ m:^http.*/$:) {
        return "http で始まりスラッシュ(/)で終わる、正しい URL 表記で書いてください。";
    }
    return "";
}

DefParam("cookiepath", 
  "Bugzilla を設置した document root 以下にあるディレクトリパス。スラッシュ (/) で始まることを確認してください。<!--
Directory path under your document root that holds your Bugzilla installation. Make sure to begin with a /. -->",
  "t",
  "/");

DefParam("usequip",
        "オンにすると、Bugzilla は検索結果リストの上部にヘッドライン (quip) を表示します。ヘッドラインはユーザが追加することができます。",
        "b",
        1);

# Added parameter - JMR, 2/16/00
DefParam("usebuggroups",
         "オンにすると、Bugzilla は各プロダクトに関連したバググループを作成するようになり、検索時に使用します。<!--
If this is on, Bugzilla will associate a bug group with each product in the database, and use it for querying bugs.-->",
         "b",
         0); 

# Added parameter - JMR, 2/16/00
DefParam("usebuggroupsentry",
         "オンにすると、プロダクトバググループを使用して、バグを登録できるユーザを制限します。usebuggroups もオンにしてください。",
         "b",
         0); 

DefParam("shadowdb",
         "読み出し専用のコピーとなる、別のデータベースの名前を指定します。このことにより、長く遅い読み出し処理はこのDBに対して行われるようになり、そのほかのDB処理をロックしなくてすむようになります。<br>文字列を入力すると、その名前でデータベースを作成します。現在使用されているデータベース名は指定しないでください!",
         "t",
         "",
         \&check_shadowdb);

DefParam("queryagainstshadowdb",
         "これをオンにして、さらに shadowdb がセットしてあると、検索はシャドウDBに行われます。",
         "b",
         0);
         

# Adding in four parameters for LDAP authentication.  -JMR, 7/28/00
DefParam("useLDAP",
         "オンにすると、ユーザー認証に、Bugzilla データベースの代わりに LDAP ディレクトリを使用します。(ユーザープロファイルはデータベースのほうに格納されますが、メールアドレスによって LDAP ユーザーと照合されます)。",
         "b",
         0);


DefParam("LDAPserver",
         "LDAPサーバのアドレス(:ポート)。(例: ldap.company.com または ldap.company.com:ポート番号)",
         "t",
         "");


DefParam("LDAPBaseDN",
         "ユーザー認証に対するベースDN(識別名)。(例: \"ou=People,o=Company\")",
         "t",
         "");


DefParam("LDAPmailattribute",
         "LDAPディレクトリの中で、メールアドレスを含むユーザーの属性名。",
         "t",
         "mail");
#End of LDAP parameters


DefParam("mostfreqthreshold",
         "<A HREF=\"duplicates.cgi\">最も頻繁に報告されるバグのページ</a>に掲載されるために最低限必要なバグの重複数。もし大きなデータベースがあり、このページを読み込むのに時間がかかるのであれば、この数を増やしてみてください。",
         "t",
         "2");


DefParam("mybugstemplate",
         "「私のバグ」のリストを呼び出すための URL。%userid% はユーザのログイン名に置き換えられます。",
         "t",
         "buglist.cgi?bug_status=NEW&amp;bug_status=ASSIGNED&amp;bug_status=REOPENED&amp;email1=%userid%&amp;emailtype1=exact&amp;emailassigned_to1=1&amp;emailreporter1=1");
    

DefParam("shutdownhtml",
         "この項目が空でないときは、Bugzilla はまったく使用できなくなり、その代わりにこのテキストが Bugzilla のどのページでも表示されます。",
         "l",
         "");

DefParam("sendmailnow",
         "オンにすると、Bugzilla は sendmail にどのメールもすぐに送付するよう指示します。もし多くのユーザーによるたくさんのメールのトラフィックがあるときオンにすれば、Bugzillaの動作は遅くなるでしょう。Bugzillaの扱う規模が小さいときにお勧めします。",
         "b",
         1);

DefParam("passwordmail",
q{パスワード通知のメール本文です。このテキスト中の %mailaddress% はその人のメールアドレスに変換されます。%login% はその人のログイン名に変換されます(たいていはメールアドレスと同じです。) %<i>それ以外</i>% は、このページで定義されたパラメータに変換されます。},
         "l",
         q{From: bugzilla-daemon
To: %mailaddress%
Subject: Your Bugzilla password.

Bugzilla を利用するには、以下を用いてください:

 メールアドレス: %login%
     パスワード: %password%

パスワードの変更は以下へ行ってください:

 %urlbase%userprefs.cgi

-------------------------------------------------------------------

To use the wonders of Bugzilla, you can use the following:

 E-mail address: %login%
       Password: %password%

 To change your password, go to:
 %urlbase%userprefs.cgi
});


DefParam("newchangedmail",
q{バグに変化があったときに送られるメールの本文です。この文の中の、%to% はメールを受け取る人に置き換えられます。%bugid% はバグ番号になります。%diffs% はバグレポートの変更点です。%neworchanged$ は新規バグの場合は "New" に、既存バグの変更の場合は空文字列になります。%summary% はこのバグの要約です。%reasonsheader% は何故その人がメールを受け取ったのかという理由が簡潔に書かれたリストになります、これはメールヘッダ (たとえば X-Bugzilla-Reason) に向いています。%reasonsbody% はユーザがこのメールを受け取った理由が %reasonsheader% より分かり易く書かれています。%<i>それ以外</i>% は、このページで定義されたパラメータに変換されます。},
         "l",
"From: bugzilla-daemon
To: %to%
Subject: [Bug %bugid%] %neworchanged%%summary%
X-Bugzilla-Reason: %reasonsheader%

%urlbase%show_bug.cgi?id=%bugid%

%diffs%



%reasonsbody%");



DefParam("whinedays",
         "バグレポートを NEW のステータスで手付かずのまま何日放置しておくか。その期限を過ぎたら、cronjob で担当者に警告メールを送ります。",
         "t",
         7,
         \&check_numeric);


DefParam("whinemail",
         "手をつけられないまま <b>whinedays</b>を越えた NEW バグを抱えた人に送るメールの本文。このテキスト中、%email% は担当者のメールアドレスに置き換えられます。%userid% は担当者のログイン名(普通はメールアドレスと同じ)に置き換えられます。%<i>それ以外</i>% 形式はこのページで定義されるような定義に置き換わります。<p> このメッセージに正しい From: アドレスを載せておくのはよい考えです。もしメールが宛先不明で戻ってくれば、担当者のアドレスが不正だと気付くことができるからです。",
         "l",
         q{From: %maintainer%
To: %email%
Subject: Your Bugzilla buglist needs attention.

  [このメールは自動的に生成されています]

  Bugzillaバグシステム (%urlbase%) に、あなたの担当するバグが
  報告されています。
  
  これらは NEW 状態のまま、%whinedays% 日経過しました。
  そのことを確認して行動をとってください。

  これは一般的には次の3つのうちのいずれかになります:

  (1) このバグにすぐ対処することにしたなら (バグを INVALID とする
      など)、すぐに処理してください。

  (2) 自分の対処するバグではないと考えたなら、他の人に割り当て直し
      てください。
      (ヒント: 誰が担当するか分からなかったら、適切なコンポーネント
      を選び、「コンポーネントの初期担当者に再割り当てする」を
      選択してください。)

  (3) このバグは自分の担当ではあると思うけれど、今すぐ解決できない
      ときは、「バグを引き受ける」コマンドを使ってください。

  すべての NEWバグのリストを得るには、以下の URL を使用してください。

    %urlbase%buglist.cgi?bug_status=NEW&assigned_to=%userid%

  あるいは、一般的な検索ページを使用することもできます。

    %urlbase%query.cgi

  下に添付されているものは、一週間以上手つかずの
  あなたが担当している NEW バグの URL です。
  これらのバグに対処するまで、一日一回このメッセージを送ります!

});



DefParam("defaultquery",
          "バグ検索ページでの最初のデフォルト検索条件です。URL形式でパラメーターを指定しているので読みにくいです。Sorry!",
         "t",
         "bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailqa_contact2=1&order=%22Importance%22");


DefParam("letsubmitterchoosepriority",
         "これをオンにすると、バグを報告する人が、当初の優先順位を設定できるようになります。オフにすると、下で指定された優先順位になります。",
         "b",
         1);


sub check_priority {
    my ($value) = (@_);
    GetVersionTable();
    if (lsearch(\@::legal_priority, $value) < 0) {
        return "正しい優先順位を指定してください。次のうちから選べます: " .
            join(", ", @::legal_priority);
    }
    return "";
}

DefParam("defaultpriority",
         "新規登録バグにセットされる優先順位です。",
         "t",
         "P2",
         \&check_priority);


DefParam("usetargetmilestone",
         "ターゲットマイルストーンの項目を利用しますか?",
         "b",
         0);

DefParam("nummilestones",
         "ターゲットマイルストーンを使用する場合に、いくつのマイルストーンを表示するか設定します。",
         "t",
         10,
         \&check_numeric);

DefParam("curmilestone",
         "ターゲットマイルストーンを使用する場合に、現在作業対象となっているマイルストーンを設定します。",
         "t",
         1,
         \&check_numeric);

DefParam("musthavemilestoneonaccept",
         "ターゲットマイルストーンを使用する場合に、「バグを引き受ける」コマンドを選ぶときはマイルストーンが必ずセットされているべきですか?",
         "b",
         0);

DefParam("useqacontact",
         "QA コンタクトの項目を使用しますか?",
         "b",
         0);

DefParam("usestatuswhiteboard",
         "ホワイトボードを使用しますか?",
         "b",
         0);

DefParam("usebrowserinfo",
         "バグレポートの OSとプラットフォーム欄に、ユーザーが使用している Webブラウザからの情報を入力しますか?",
         "b",
         1);

DefParam("usedependencies",
         "依存関係 (あるバグが他のバグに依存しているというマーク付けができるようになる) を使用しますか?",
         "b",
         1);

DefParam("usevotes",
         "バグに対して投票できるようにしますか? この項目が効果を発揮するためには、<a href=\"editproducts.cgi\">プロダクト編集ページ</a> で、プロダクトごとの最大投票数を 0 以外に変更する必要があります。",
         "b",
         1);

DefParam("webdotbase",
         "依存関係にあるバグをグラフ化することができます。このパラメータを以下のうちどれかにセットします:
<ul>
<li>\'dot\' コマンドへの完全なファイルパス (<a href=\"http://www.graphviz.org\">GraphViz</a> に含まれるコマンドで、ローカルでグラフ生成を行ないます)。
</li>
<li> リモートでグラフを生成する <a href=\"http://www.research.att.com/~north/cgi-bin/webdot.cgi\">webdot パッケージ</a>が設置された場所への URL プレフィクス。
</li>
<li>空白は依存関係グラフを使用不可にする。</li>
</ul>
デフォルト値は誰でもアクセスできる webdot サーバになっています。この値を変更するときは、webdot サーバが data/webdot ディレクトリからファイルを読み込むことのできるようにしておいてください。Apache においては、.htaccess ファイルを編集すれば可能になります。別のシステムでは方法は異なります。.htaccess ファイルがなくなったときは、checksetup.pl を実行して再生成してください。",
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
            return "ファイルパス \"$value\" は正しい実行ファイルではありません。ローカルでグラフを生成するつもりなら、'dot' への完全なファイルパスを指定してください。";
        }
        # Check .htaccess allows access to generated images
        if(-e "data/webdot/.htaccess") {
            open HTACCESS, "data/webdot/.htaccess";
            if(! grep(/ \\\.png\$/,<HTACCESS>)) {
                print "依存関係グラフの画像にアクセスできない状態になっています。そのファイルを変更していないのであれば、data/webdot/.htaccess を削除して、checksetup.pl を再実行して作り直してください。\n";
            }
            close HTACCESS;
        }
    }
    return "";
}

DefParam("expectbigqueries",
         "これをオンにすると、mysql に対してバグ検索をする前に <tt>set option SQL_BIG_TABLES=1</tt> を送信します。そうすると少し遅くなりますが、巨大な一時テーブルを必要とするような要求に対しても <tt>The table ### is full</tt> のエラーが出なくなります。",
         "b",
         0);

DefParam("emailregexp",
         '正しいメールアドレスの評価に使用する正規表現を定義します。デフォルトでは、完全なメールアドレスに一致します。他にも、@ の無いローカルユーザ名 ("local usernames, no @ allowed.") の <tt>^[^@, ]*$</tt> はよく使われます。',
         "t",
         q:^[^@]+@[^@]+\\.[^@]+$:);

DefParam("emailregexpdesc",
         "ここには、<tt>emailregexp</tt> で許可される正しいメールアドレスについての説明を英語で書きます。",
         "l",
         "A legal address must contain exactly one '\@', and at least one '.' after the \@.");

DefParam("emailsuffix",
         "メールを実際に送信するときに、アドレスに追加する文字列です。<tt>emailregexp</tt> をローカルユーザー名のみ許すような設定に変更したけれども、username\@my.local.hostname に配信したい場合に有用です。",
         "t",
         "");


DefParam("voteremovedmail",
q{バグから投票が取り除かれた時に送信するメールの本文です。%to% はそのバグに投票していた人に置き換えられます。%bugid% はバグ番号に置き換えられます。%reason% は、なぜそのバグが除かれたかの短かい理由に置き換わります。%votesremoved%, %votesold% そして %votesnew% はそれぞれ、削除された投票数と、そのバグの削除以前の票数、削除後の票数になります。%votesremovedtext%, %votesoldtext% and %votesnewtext% は、それらと同じですが、文章になります。たとえば "You had 2 votes on this bug." %count% は後方互換性のためにサポートされます。%<i>それ以外</i>% はこのページで定義されるそれぞれの定義に置き換わります。},
         "l",
"From: bugzilla-daemon
To: %to%
Subject: [Bug %bugid%] Some or all of your votes have been removed.

bug %bugid% から、あなたの投票がいくつか取り除かれました。

%votesoldtext%

%votesnewtext%

Reason: %reason%

%urlbase%show_bug.cgi?id=%bugid%
");
         
DefParam("allowbugdeletion",
         q{プロダクトとコンポーネント、バージョンを編集するページで、プロダクト(またはコンポーネント、バージョン) を削除したとき関連するバグすべてを削除できるようにします。これは結構怖いやり方なので、オンにする前にどこで何が起きるのかを把握しておいてください。},
         "b",
         0);


DefParam("allowemailchange",
         q{ユーザが自分自身のメールアドレスを「環境設定」ページで変更できるようになります。補足すると、この変更はどちらのアドレスにもメールすることで有効になるため、オンにしてもユーザが正しくないアドレスを使うことを許すものではありません。},
         "b",
         0);


DefParam("allowuserdeletion",
         q{ユーザを編集するページで、ユーザを削除することができるようになります。しかし、データベース上の他のテーブルにあるそのユーザへの参照をうまく消去するコードはありません。ですので、そのような削除は危険です。オンにする前にどこで何が起きるのかを把握しておいてください。},
         "b",
         0);

DefParam("browserbugmessage",
         "Bugzilla がブラウザから予期しないデータを受け取ったとき、問題の原因を表示するのに加えて、この HTML も出力します。",
         "l",
         "this may indicate a bug in your browser.\n");

#
# Parameters to force users to comment their changes for different actions.
DefParam("commentonaccept", 
         "バグを引き受けたときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonclearresolution", 
         "バグの処理方法がクリアされたときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonconfirm", 
         "承認されていないバグ (UNCONFIRMED) を承認するときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonresolve", 
         "バグを解決したときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonreassign", 
         "再割り当てしたときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonreassignbycomponent", 
         "コンポーネントの初期担当者に再割り当てしたとき、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonreopen", 
         "バグを再開 (REOPENED) するときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonverify", 
         "バグを VERIFIED にするとき、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonclose", 
         "バグを CLOSED にするとき、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("commentonduplicate",
         "バグを ほかのバグの重複であるとマークするときに、コメントを入れなければならないようにします。",
         "b", 0 );
DefParam("supportwatchers",
         "ユーザが、他の人に送られるメールのコピーを自分にも送信してもらえるようにします。休暇を取っている人の作業代行や、特定の開発者に関連したバグの QA をしている人に便利です。",
         "b", 0 );


DefParam("move-enabled",
         "オンにすると、特定の人は、決められたデータベースにバグを移行させることができるようになります。",
         "b",
         0);
DefParam("move-button-text",
         "Move ボタンに表示される文字列。バグが移行することになる場所を説明してください。",
         "t",
         'Move To Bugscape');
DefParam("move-to-url",
         "Bugzilla からバグを移行させることを許可したデータベースの URL です。",
         "t",
         '');
DefParam("move-to-address",
         "バグを移行すると、移行先のデータベースにメールを送信します。そのデータベースがバグ登録を受け付けているメールアドレスを記入してください。",
         "t",
         'bugzilla-import');
DefParam("moved-from-address",
         "バグを移行すると、移行先のデータベースにメールを送信します。このメールの送信元メールアドレスを記入してください。エラーメッセージはそこへ送信されます。",
         "t",
         'bugzilla-admin');
DefParam("movers",
         "バグを移行させることができ、 移行させたバグを (移行に失敗したとき) 再開できる権限を持つ人のリスト。",
         "t",
         '');
DefParam("moved-default-product",
         "ほかのデータベースからここに移行したバグに割り当てられるプロダクト。",
         "t",
         '');
DefParam("moved-default-component",
         "ほかのデータベースからここに移行したバグに割り当てられるコンポーネント。",
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
         "パッチの最大サイズ (キロバイト)。Bugzilla はこのサイズ以上のパッチは受け取りません。どんなサイズのパッチでも受け付ける (Webサーバの制限に従う) なら、この値を 0 にしてください。" ,
         "t",
         '1000');

DefParam("maxattachmentsize" , 
         "パッチ以外の添付ファイルの最大サイズ (キロバイト)。Bugzilla はこのサイズ以上の添付ファイルは受け取りません。どんなサイズのファイルでも受け付ける (Webサーバの制限に従う) なら、この値を 0 にしてください。" , 
         "t" , 
         '1000');

1;
