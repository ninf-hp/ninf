#! /bin/sh
#
#  Required:
#    cgiparse
#
#  $Id: apply_form2.cgi,v 1.1 2005/07/22 10:18:17 tatebe Exp $

# QUERY_STRING is encapsulated by query_str
if [ "X$FORM_query_str" = "X" ]; then
	ERRLOC=AP-2-1
	ERRMSG="You cannot run this CGI directly."
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
QUERY_STRING=$FORM_query_str
eval `cgiparse -form`

if [ "X$FORM_email" = "X" ]; then
	ERRLOC=AP-2-2
	ERRMSG="Email アドレスが記入されていません．"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_email"`
if [ "X$FORM_email" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=AP-2-3
	ERRMSG="Email アドレスが間違っているか，おかしな文字を含んでいます．"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

#if [ "X$FORM_nauthors" = "X" ]; then
#	ERRLOC=AP-2-4
#	ERRMSG="共著者数が入力されていていません．"
#	printerrmsg $ERRLOC "$ERRMSG"
#	exit 1
#fi
#if [ $FORM_nauthors -lt 1 -o $FORM_nauthors -gt 50 ]; then
#	ERRLOC=AP-2-5
#	ERRMSG="共著者数が多すぎるか，「半角」数字ではありません．"
#	printerrmsg $ERRLOC "$ERRMSG"
#	exit 1
#fi

#

printtitle=`secureprint "$FORM_title"`
printname=`secureprint "$FORM_name"`
printorganization=`secureprint "$FORM_organization"`
#printBushO=`secureprint "$FORM_BushO"`
#printaddress=`secureprint "$FORM_address"`

#printzip=`secureprint "$FORM_zip"`
#printtel=`secureprint "$FORM_tel"`
#printfax=`secureprint "$FORM_fax"`

printabstract=`secureprint "$FORM_abstract"`
printcomment=`secureprint "$FORM_comment"`

#
#
#

if [ "X$FORM_modification" = X ]; then
	MODETITLE="発表申込み"
	BUTTONTITLE="発表申込"
else
	MODETITLE="発表申込み修正"
	BUTTONTITLE="申込修正"
fi

printprologue "$NINFG $MODETITLE (確認)"
echo "<H1>$NINFG $MODETITLE (確認)</H1>"

#
MKAUTHTABLE=/tmp/ninfg-apply-$$
cat > $MKAUTHTABLE <<EOF
secureprint () {
	echo \$1 | nkf -e |
	sed '{
	    s/&/\&amp;/g
	    s/</\&lt;/g
	    s/>/\&gt;/g
	    s/"/\&quot;/g
	}'
}
EOF

#i=1
#while [ $i -le $FORM_nauthors ]; do
#    echo printauthors$i=\`secureprint \"\$FORM_authors$i\"\` >> $MKAUTHTABLE
#    echo printaug$i=\`secureprint \"\$FORM_aug$i\"\` >> $MKAUTHTABLE
#    echo echo \"\$printauthors$i \(\$printaug$i\) \`\[ \"X\$FORM_option$i\" = \"X○\" \] \&\& echo 登壇\`\<br\>\" >> $MKAUTHTABLE
#    i=`expr $i + 1`
#done

#
#
#

cat <<EOF
<FORM METHOD="POST" ACTION="ninfg.cgi">

<table>
<tr valign=top><th align=right>発表題目：</th><td>$printtitle</td></tr>
</table>
<dl>
<dt>概要：
<dd>$printabstract
</dl>
<table>
<tr valign=top>
<th align=right>発表者氏名：</th>
<td>
EOF
. $MKAUTHTABLE
cat<<EOF
</td>
</tr>
<tr valign=top><th align=right>申込者連絡先：</th><td></td></tr>
<tr valign=top><td align=right>氏名：</td><td>$printname</td></tr>
<tr valign=top><td align=right>組織名：</td><td>$printorganization</td></tr>
#<tr valign=top><td align=right>部署名：</td><td>$printBushO</td></tr>
#<tr valign=top><td align=right>住所：</td><td>$printaddress</td></tr>
#<tr valign=top><td align=right>郵便番号：</td><td>$printzip</td></tr>
#<tr valign=top><td align=right>電話番号：</td><td>$printtel</td></tr>
#<tr valign=top><td align=right>FAX 番号：</td><td>$printfax</td></tr>
<tr valign=top><td align=right>Email：</td><td>$ORIGINAL_SECURE_ADDRESS</td></tr>
</table>
<dl>
<dt>コメント：
<dd>$printcomment
</dl>

<INPUT TYPE="submit" VALUE="$BUTTONTITLE">

<input type="hidden" name="query_str" value="$SAVED_QUERY_STRING">
<input type="hidden" name="command" value="apply_form3.cgi">
</FORM>

<P>
内容を確認した後、「<B>$BUTTONTITLE</B>」ボタンを押して下さい。
24時間以内に上記の電子メールアドレスに確認メールが届きます。
万一届かない場合には、
&lt;$SECRETARY&gt;
までご連絡下さい。
</P>

<HR>

</body></html>
EOF

rm -f $MKAUTHTABLE

exit 0
