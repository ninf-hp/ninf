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

PutHeader("Bugzilla 検索ページヘルプ","ヘルプ", "検索フォームの使い方を学ぶページです");





print qq{

<br>

<form action="none"> <!-- Cause NS4.x is stupid. Die NS4.x you eeeevil eeeevil program! -->

<a name="top"></a>

<p><center><b><font size="+2">Bugzilla の検索フォームを使用するためのヘルプ</font></b><br>2001年1月20日 - 
<a href="mailto:netdemonz\@yahoo.com">Brian Bober (netdemon)</a>.  
<BR><I>Further heavy mutiliations by <a href="mailto:tara\@tequilarista.org">Tara Heranandez</A>, April 20, 2001.</I></CENTER>

<br><center><img width="329" height="220" src="ant.jpg" border="2" alt="Da Ant"></center>

<p><br><center><h3>章立て</h3></center>

<p>検索ページは次のようなセクションに分けられる:

<p><a href="#bugsettings">バグの設定(Bug Settings)</a> 
<br><a href="#peopleinvolved">関係者(People Involved)</a> 
<br><a href="#textsearch">テキスト検索のオプション(Text Search Options)</a>
<br><a href="#moduleoptions">モジュールオプション(Module Options)</a> 
<br><a href="#advancedquerying">高度な検索(Advanced Querying)</a>
<br><a href="#therest">フォームの下の部分(The Bottom Of The Form)</a>

<p>「わたしはもう <a href="http://www.mozilla.org/bugs/">Bugzilla</a> の使い方を知っている。だけど Bugzilla とこのドキュメントの筆者に関する<a href="#info">情報</a>を知りたい」<br>
「OK、どうやら私が見つけたバグは Bugzilla に無いようだ。
では、どのようにバグを <a href="enter_bug.cgi">投稿</a>
するべきか?」 - <a href= "docs/html/Bugzilla-Guide.html#BUG_WRITING">
まずはガイドラインを読もう</a>!

<p><br><center><h3>コツ(Tips)</h3></center> 検索ページのすべての
フィールドを埋める必要はない。フィールドに記入することであなたの検索条件を
制限する。例えば Status のようなリストボックスでは、Ctrlを押しながら
クリックすることで、選択を解除することが出来る。あなたが上手に
なるまでは、非常に単純な検索条件を指定し、長いバグリストの中を自分で探し回る、
"力まかせ" な方法を使うことも出来る。他の人の検索速度を低下させないために、
この方法を使いすぎないようにしてほしい。もしかすると、
多くの人が同時に検索を行っているかもしれないからだ。最終的には、
私はすぐに Boolean Chart について学ぶことをおすすめする。これはとても強力だからだ。
また、ほとんどの Bugzilla のページの<a href="#bottom">最下部</a>には、
ナビゲーションバーがあり、これは<a href="./">フロントページ</a>に
おける重要なリンクを含んでいる。

<p><a href="query.cgi">検索</a>に戻ろう。
もし既にフォームになにか書き込んでいるのなら、
ブラウザでバックボタンを押したいと考えているかも知れない。
もし全部読み終えたときには、<a href="#samplequery">サンプルの検索</a>を
実行してみて欲しい!

};





print qq{

<a name="bugsettings"></a>

<p><br><center><h3>バグの設定(Bug Settings)</h3></center>

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

<b>ステータス</b> と <b>処理方法</b> のフィールドは、バグのライフサイク
ルを定義し、追跡する。<b>プラットフォーム</b> と <b>OS</b> は、
バグが存在する環境をあらわす。<b>優先順位</b> と <b>重要度</b>
は追跡のためのものだ。

<a name="status"></a>
<p><b>ステータス</b> 

<ul>

<li><b>UNCONFIRMED</b> - このバグを修正しなければならないかを誰も確認していない。ユーザが権限をもっていれば、このバグを承認 (confirm) して ステータスを NEW にすることができる。<a href="userprefs.cgi?tab=permissions">あなたの権限</a> はここで見ることができる。バグは、いきなり解決され RESOLVED になることもあるけれど、たいていはそのバグの担当者によって承認される。UNCONFIRMED バグは、報告者の投稿したバグが実際に再現することを誰かが確かめるまでは、未承認 (unconfirmed) のままである。

<li><b>NEW</b> - このバグは最近担当者のバグリストに追加されたもので、
これは処理される必要がある。この状態のバグは引き受けられ ASSIGNED となるか、
他の人に割り当てられるか、NEWのままであるか、解決されて 
RESOLVED にマークされる。

<li><b>ASSIGNED</b> - このバグはまだ解決されていないが、これを修正できると考える誰かに割り当てられている。バグはこの状態から他の人に再割り当てされ、NEW になることがあり、あるいは解決されて RESOLVED にマークされる。

<li><b>REOPENED</b> - このバグは一度解決 (resolved) されたが、その処理方法 (resolution) が誤っていたように思われる。たとえば WORKSFORME バグは、情報が集まりそのバグを再現できるようになったとき REOPENED になる。ここからバグは ASSIGNED か RESOLVED のどちらかになる。

<li><b>RESOLVED</b> - 解決され、QA (品質評価担当者) による検証を待ってい
る。ここからバグは再度未解決状態の REOPENED になるか、VERIFIED になるか、あるいはこのまま CLOSED にして終了する。

<li><b>VERIFIED</b>- QA がバグと処理方法を見た上で、適切な処理がなされたと同意した。

<li><b>CLOSED</b> - このバグは死んだものと見なされ、処理方法が適切である。バグの報告されていた製品は終結したか出荷された。
ゾンビとして蘇ることを選んだバグはどれも、REOPENED になる。このステータスは今までほとんど使われていない。
</ul>

<a name="resolution"></a>
<p><b>処理方法</b> 

<p><b>処理方法</b> のフィールドは、バグに何が起こったかを示すものである。

<p>まだ何の処理方法も存在しない: 未解決のステータス (NEW, ASSIGNED, REOPENED) のどれかにあたるバグはすべて、処理方法が空に設定されている。
それ以外のバグはすべて、後述する処理方法のどれかにマークされている。

<ul>
<li><b>FIXED</b> - バグは修正され、ツリーにチェックインされ試験された。
<li><b>INVALID</b> - バグではないとされた問題。
<li><b>WONTFIX</b> - 将来にわたって修正されることはないとされた問題。
<li><b>LATER</b> - 製品の現バージョンでは修正されることがないとされた問題。
<li><b>REMIND</b> - このバージョンでは修正される見込みがないが、その可能性もまだ残っている問題。
<li><b>DUPLICATE</b> - 既に存在しているバグと重複している問題。duplicate とマークするためには、重複しているバグ番号が必要であり、その番号はバグのコメントに書き込まれる。
<li><b>WORKSFORME</b> - このバグを再現するすべての試みが無駄に終わり、コードを読んでもこのようなことが何故起こるのか全くつかめない場合。もしもっと情報があとで現れたら、再度バグを割り当て直すことにし、現在は単に記録しておく。
</ul>

<a name="platform"></a>
<p><b>プラットフォーム</b>
<p><b>プラットフォーム</b> フィールドは、バグが報告されたハードウェアプラットフォームである。以下のプラットフォームを含む:

<ul>
<li>All (すべてのプラットフォームで起きる; クロスプラットフォームなバグ)<br>
<li>Macintosh
<li>PC
<li>Sun
<li>HP
</ul>
<p><b>Note:</b> "All" オプションを選択することは、すべてのプラットフォームに割り当てられたバグを選択することにはならない。これはすべてのプラットフォームでこのバグが <b>起きる</b> というときに選択される。

<a name="opsys"></a>
<p><b>OS</b>
<p><b>OS</b>のフィールドは、バグが報告されたオペレーティングシステムである。以下のオペレーティングシステムを含む:

<ul>
<li>All (すべてのプラットフォームで起きる; クロスプラットフォームのバグ)
<li>Windows 95
<li>Windows 2000
<li>Mac System 8.0
<li>Linux
<li>Other (これらのOSのどれにも属さないOS)<br>
</ul>

<p>オペレーティングシステムはプラットフォームを暗に示すが、常にそうではない。例えば、Linux は PC や Mac そのほかのプラットフォームでも動作する。

<a name="priority"></a>
<p><b>優先順位</b>

<p><b>優先順位</b>は重要性と修正されるための順番を表わす。このフィールドはプログラマやエンジニアが自分の仕事の優先順位をつけるために使われている。優先度には P1 (最優先) から、 P5(最も低い優先度)まである。

<a name="severity"></a>
<p><b>重要度</b>

<p><b>重要度</b> は、バグの深刻度を表わす。

<ul>
<li><b>Blocker</b> - 開発とテストの一方または両方を妨げるもの。<br>
<li><b>Critical</b> - クラッシュ、データの損失、深刻なメモリリーク。<br>
<li><b>Major</b> - 主要な機能の欠如。<br>
<li><b>Normal</b> - 普通のバグ。<br>
<li><b>Minor</b> - マイナーな機能の欠如、あるいは現在簡単な代替手段がある他の問題。<br>
<li><b>Trivial</b> - 単語の打ち間違いやテキストの配置ミスなどの、表層上の問題。<br>
<li><b>Enhancement</b> - 機能拡張のリクエスト。<br>
</ul>

};












print qq{
<a name="peopleinvolved"></a>
<p><br><center><h3>関係者</h3></center>
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

この部分は、検索を強力にするため複雑になり、残念ながら分かりにくいものとなっている。
ここでは、メールアドレスに関係するバグを検索できる。

<p>

ひとつのメールアドレスに関係したバグを探すためには:

<ul>
  <li> メールアドレスの一部をテキストフィールドにタイプする。
  <li> そのアドレスが含まれていると思われるフィールドのチェックボックスをクリックする。
</ul>

<p>

二つの異なったメールアドレスを探すことも出来る。その両方が特定できているのなら、その両方のメールアドレスにマッチするバグだけが表示される。これは例えば、Ralph によって作成され、Fred に割り当てられたバグを検索するときに有用である。

<p>
ドロップダウンメニューを使って、どの検索方法を使うかを指定できる。メールアドレス文字列の一部による一致、<a href="http://jt.mozilla.gr.jp/bugs/text-searching.html">正規表現 (Regular Expressions)</a>の使用、またはメールアドレスを正確に入力しての完全一致検索。
};











print qq{
<a name="textsearch"></a>
<p><br><center><h3>テキスト検索</h3></center>
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


<p>この章では、値を入力しすべてのバグの中から検索を行うことが出来る (あるいは他のフィールドに記入することによって、検索するバグを絞りこむことが出来る)。 
正規表現やテキスト検索について知りたいと考えるのならば、<a href="http://jt.mozilla.gr.jp/bugs/text-searching.html">Bugzilla テキスト検索</a>
を見るといいかもしれない。これらのフィールドに続く選択肢によって、どの検索方法を使うかが決まる。<br>

<a name="summaries"></a>
<h4>要約</h4>
<p>この項目で、要約を検索する事が出来る。要約はバグを一行で表現したものである。

<a name="descriptions"></a>
<h4>コメント</h4>

<p>この項目で、コメントを検索することができる。コメントはどんな人も
追加する事が可能だ。ほとんどのバグでコメントはもっとも大きな検索可能エリアを持っている。
多くの検索結果を望むときは、コメントを検索するとよい。
<BR><B>Note:</B>バグの中で、コメントはひどく大きくなることがあり、このタイプの検索をおこなうと長い時間がかかることがある。

<a name="url"></a>
<h4>URL</h4>
<p>この項目で、URL のフィールドを検索することが出来る。この項目はバグに関連するウェブページのURLを含む。

<a name="statuswhiteboard"></a>
<h4>ステータス ホワイトボード</h4>
<p>この項目で、バグのホワイトボードを検索することが出来る。ホワイトボードが含んでいるのは、エンジニアの書き込んだ一般的な情報だ。
};


print qq{
<a name="keywords"></a>
<h4>キーワード</h4>
<br><br>それぞれのバグは特定のキーワードを持つことが出来る。バグの報告者かユーザが適切な権限を持っていれば、これらのキーワードを編集することが出来る。以下に、この Bugzilla が保持しているキーワードのリストを挙げる:
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
<p><br><center><h3>モジュールオプション</h3></center>

<br>

<p>モジュールオプションは、探したいバグがどのプロダクト・モジュール・バージョンなのかを選択する。一つ以上のプロダクト・バージョン・コンポーネント、あるいはマイルストーンを指定することで、検索を絞りこむ事ができる。

<p><a name="product"></a>
<h4>プロダクト</h4> 


<p>Mozilla プロジェクト内部のサブプロジェクトは似通ったものであるが、開発中の商品の中には切り離されたプロダクトもいくつかある。それぞれのプロダクトはそれ固有のコンポーネントをもっている。

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
<h4>バージョン</h4>

<p>この項目は単に検索したいバグに記録されているバージョンである。多くのバグは   Other というバージョンにマークされ、代わりにマイルストーンが指定される (マイルストーンについては、下記参照)。

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
<h4>コンポーネント</h4>
<p>それぞれのプロダクトはコンポーネントを持っていて、それぞれについてのバグが登録できるようになっている。コンポーネントはプロダクトの一部であり、モジュールオーナーに割り当てられる。次のリストは、コンポーネントとそれに関連するプロダクトである。:
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
<h4>マイルストーン</h4>

<p>このセクションを選択することにより、ターゲットマイルストーンに値がセットしてあるバグを検索できる。マイルストーンはバージョンと同様のものである。それは試験的なある日付で、そこで大規模なバグの減少がおこり、比較的安定したリリースが生み出される。例えば、Mozilla.prg は "M10" から "M18" という形式のマイルストーンを持っていたが、現在は "Mozilla0.9" という形式になっている。Bigzilla マイルストーンは "Bugzilla 2.12"、"Bugzilla 2.14" などの形式である。


};














print qq{
<a name="incexcoptions"></a>
<p><br><center><h3>包含/排斥(Inclusion/Exclusion) オプション</h3></center>

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

<p>包含/排斥(Inclusion/Exclusion) オプションは、
入力した値に基づいて包含と排斥ができる強力なセクションである。

<P><b>バグ番号: [text] [のみを含む／を除外する]</b>

<p>カンマ区切りのバグ番号のリストを入力して、検索結果にそれを含む、または検索結果から除外することができる。
将来的には範囲指定も出来ることといいのだが（例えば [1-1000] とすることで、 
1から1000というように）、残念ながら今はまだ出来ない。

<p><b>次の期間に更新されたバグのみ: 過去 [text] 日以内</b>

<p>指定した日数以内に状態が変化したバグ

<p><b>[text] 票以上の投票があるバグのみ</b>

<p>指定した以上の投票があるバグ

<p><b>この項目が [fields]</b> 変更後の値: (オプション) [text]

<p>
 ここでは、バグレポートにある項目の値を指定する。もしひとつかそれ以上の項目を選んだ場合、右側のボックスにその項目のうちひとつを入力しなくてはならない。検索初心者にはこの項目が何を意味するのかは理解しがたいだろう。これはバグレポート情報のさまざまな項目にマッチする。
オプションは、項目の変化した値を入力することもできる。たとえば、バグレポートが jon\@netscape.com から brian\@netscape.com に割り当てられたなら、assigned_to を選び 変更後の値に「brian\@netscape.com」と入力することができる。

<p><b>この期間内に変更されたバグのみ: [text] から [text] まで</b>

<p>これで、フィールドが変更された値を変更することが出来る。"Now" はエントリーとして使用可能である。他のエントリーはmm/dd/yyyy あるいは yyyy-mm-dd という書式で指定できる。


};












print qq{
<a name="advancedquerying"></a>
<p><br><center><h3>"Boolean Charts" を使用した高度な検索</h3></center>

<p>Bugzilla の検索ページは、かなりつかいやすいように設計されている。
しかし、そのような使いやすさは、往々にして力不足をもたらす。この高度な
検索のセクションは、とても強力な検索ができるよう設計されている。
しかし、これは学ぶのも説明するのも簡単なことではない。<br> <p> <center>

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

</center> <br> <p>高度な検索 (または ブーリアンチャート) は一つの "term(語)" か
らはじまる。ひとつの term は二つのプルダウンメニューとテキストフィールドの組み合わせである。
メニューから選ぶことにより、次のことを特定する:
<p>フィールド 1: 用語を探す場所<br>
フィールド 2: 何がマッチするのかを決定する<br>
フィールド 3: 検索する語<br> <br> <center>

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

<br> <p>本当に面白いことが始まるのは、"Or" または "And" ボタンを押した
ときである。もし "Or" ボタンを押したなら、最初のものの下に、二つ目の 
term を得ることになる。その term にも検索指定することができ、検索結果はどちらかの term にもマッチしたものすべてになる。<br> <p> <center>

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

</center> <br> <p>"And" ボタンを押したときには、オリジナルの term の下に新しい
term を得ることが出来る - それは "AND" という単語で区切られており、
検索結果はその両方の term に一致するものとなる。

<p>"And" と "Or" をさらにクリックすることもできる。そしてページは複数の
term を持つことになる。"Or" は "And" よりも高い優先権を持っている。
"Or" はその周りに()を備えていると考えることができる。

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

<p>一番わかりにくいのは、"別のブーリアンチャートを追加する" ボタンである。
これはほとんど "And" ボタンと同じである。
これは、ひとつのバグに複数の項目が関係づけられているフィールドの
一つ ("Comments", "CC", そしてすべての "changed [something]" のエントリーを含む) を使いたいときに必要になるだろう。
もし、複数の term があった場合、
それがこれらのフィールドの一つ (コメントなど) についてすべて
述べているものであったとすると、
そのフィールドの異なった複数のインスタンスに従うのか、
ひとつのインスタンスに従うのかはっきりしない。そこで、
両方の方法を与え、term が異なった チャートに現れない限りは
常に同じインスタンスを表すようにする。

<p>例えば: もし "優先順位 が P5 に変更になったもの" と "優先順位が 
person\@addr によって変更があった場合", それは適宜 優先順位 を P5 にに
変更した人を検索するだけである。しかし、もし本当にあるときにその人物に
よって マイルストーンが変更になり、あるときにマイルストーンの優先順位が 
P5 にだれか(ほかの誰かの可能性がある) によって変更されたバグをすべて検
索したいときに、二つの term を異なったチャートに置くことになるだろう。
};


print qq{
<a name="therest"></a>
<center><h3>フォームの残りの部分</h3></center>
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
<p>あなたはここまですべてを見終えた、しかし「このフォームの下にあるガラクタは何だ？」
あなたがログインしているときはいつでも、現在の検索条件を、デフォルト検索条件として記憶させておくことができる。 また、検索結果の表示のソート方法を選ぶこともできる。 終わったら、'検索／実行' をクリックしよう。 
};




print qq{

<a name="info"></a>

<br><center><h3>このドキュメントについて</h3></center>

<p>いくつかの古いバージョンの Bugzilla のドキュメント (Terry Weissman, Tara Hernandez ほかの人々による) を採用し、 <a href="mailto:netdemonz\@yahoo.com">Brian Bober</a> が書いた。
irc.mozilla.org - #mozilla, #mozwebtools, #mozillazine などで、私に話しかけることが出来る。私は netdemon という名前で通っている。

<P>Bugzilla の使用法についてのドキュメントは、Mozilla.org その他のサイトで利用可能である:
<br><a href="http://www.mozilla.org/quality/help/beginning-duplicate-finding.html\"> 
今までに検索されたバグをどのように検索するか</a><br>
<a href="http://jt.mozilla.gr.jp/bugs/">Bugzilla の一般的な情報</a><br>
<a href="http://jt.mozilla.gr.jp/quality/help/bugzilla-helper.jp.html">Mozilla バグレポートフォーム</a><br>
<a href="http://jt.mozilla.gr.jp/bugs/text-searching.html">Bugzilla のテキスト検索</a><br>
<a href="http://jt.mozilla.gr.jp/quality/bug-writing-guidelines.html">バグレポートのガイドライン</a><br>

<p>私の執筆動機はあたらしい Bugzilla のユーザがどのように Bugzilla 
の検索フォームを利用するかと言うことを学ぶ方法を与えることによってエンジニアを助けることである。
私は query.cgi を書き終えて、このように言いたい、"What the heck, I'll write this too".

<p><br><center><h3>なぜこれを使うのか?</h3></center>

<p>あなたは検索ページを見て、こう言うことだろう。「このページはちょっと難しいようだ。お、思うんだけど別に検索する必要なんてないよね」
バグを投稿する前に重複したものを見つけることは非常に重要である。
それは <a href="http://jt.mozilla.gr.jp/quality/bug-writing-guidelines.html">バグレポートのガイドライン</a>
に明記されている。バグレポートを読む人々は忙しく、常にバグに圧倒されている。
だから、あなたは重複を探す人すべてにとってとても親切なことをしているのだ。

};








print qq{
<a name="samplequery"></a>
<p><br><center><h3>Sample Query</h3></center>

<p>では実際に<b>バグを検索しよう!</b> すぐに使える「もじら組」のデーターベースを拝借することにする。
<BR>まず、検索ページを<a target="_blank" href="http://bugzilla.mozilla.gr.jp/query.cgi">
新しいウィンドウ</a>で開いてこのドキュメントを検索ページの両方が見やすいようにしよう。
<p>そして次のようにする:
<ul>
<li>"ステータス"フィールドのすべてのフィールドを選択（または選択解除）する
<li>テキスト検索部で、要約に「改行」、説明文に「メール」と入力する
（「説明文に「メール」があり、かつ、要約に「改行」が含まれる」という意味）
</ul>

<p>結果のひとつとして次のようなバグが表示されるはずだ:
<a href="http://bugzilla.mozilla.gr.jp/show_bug.cgi?id=551">bug 551- メール表示ウィンドウで改行がダブる</a>
};

print qq{
<hr>

<a name="bottom"></a>

</form>

};



PutFooter();
