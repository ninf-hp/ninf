#! /bin/sh
#
#  Required:
#
#  $Id: apply_form.cgi,v 1.1 2005/07/22 10:18:17 tatebe Exp $

if [ "X$FORM_email" = "X" ]; then
	ERRLOC=AP-1-1
	ERRMSG="Email アドレスが記入されていません．"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
if [ "X$FORM_email" != "X$FORM_email1" ]; then
	ERRLOC=AP-1-2
	ERRMSG="確認用 Email アドレスと一致していません．"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_email"`
if [ "X$FORM_email" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=AP-1-3
	ERRMSG="Email アドレスが間違っているか，おかしな文字を含んでいます．"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
#

printtitle=`secureprint "$FORM_title"`
printname=`secureprint "$FORM_name"`
printorganization=`secureprint "$FORM_organization"`

#
#
#

if [ "X$FORM_modification" = X ]; then
	MODETITLE="発表申込み"
else
	MODETITLE="発表申込み修正"
fi

printprologue "$NINFG $MODETITLE (2)"
echo "<H1>$NINFG $MODETITLE (2)</H1>"

#
MKAUTHTABLE=/tmp/ninfg-apply-$$
cat > $MKAUTHTABLE <<EOF
secureprint () {
	echo \$1 |
	sed '{
	    s/&/\&amp;/g
	    s/</\&lt;/g
	    s/>/\&gt;/g
	    s/"/\&quot;/g
	}'
}

echo "<DD>"
echo "<table>"
EOF

FORM_nauthors=1
i=1
while [ $i -le $FORM_nauthors ]; do
	cat >> $MKAUTHTABLE <<EOF
OPTIONYES=
OPTIONNO=
[ "X\$FORM_printoption$i" = "X○" ] && OPTIONYES=selected
[ "X\$FORM_printoption$i" = "X×" ] && OPTIONNO=selected

echo "<tr>"
echo "<th align=right>氏名${i}：</th>"
echo "<td><INPUT TYPE=\"text\" NAME=\"authors${i}\" value=\"\$FORM_printauthors$i\" MAXLENGTH=\"32\" SIZE=\"12\"></td>"
echo "<th align=right>所属${i}：</th>"
echo "<td><INPUT TYPE=\"text\" NAME=\"aug${i}\" value=\"\$FORM_printaug$i\" MAXLENGTH=\"32\" SIZE=\"10\"></td>"
#echo "<th>登壇者：</th>"
#echo "<td><SELECT NAME=\"option${i}\" SIZE=\"1\">"
#echo "  <OPTION \$OPTIONNO>×  <OPTION \$OPTIONYES>○  </SELECT></td>"
echo "</tr>"
EOF
	i=`expr $i + 1`
done

cat >> $MKAUTHTABLE <<EOF
echo "</table>"
EOF

#
#
#

CURRENTTIME=`date '+%Y%m%d%H%M'`

MESSAGE=
if [ $CURRENTTIME -lt $STARTTIME ]; then
    MESSAGE="まだ申込み受付を開始していません．恐れ入りますが，期間中に申込み下さい．"
    CGIEXIT=1
elif [ $CURRENTTIME -ge $REALENDTIME ]; then
    MESSAGE="申込み受付期間は終了しました．"
    CGIEXIT=1
elif [ $CURRENTTIME -ge $ENDTIME ]; then
    TMAXFILE=$SUBDIR/max
    TSEQFILE=$SUBDIR/seq
    TCANCELFILE=$SUBDIR/cancel
    FINISHFILE=$SUBDIR/xxx
    if [ -f $FINISHFILE ]; then
	MESSAGE="申込み受付期間は終了しました．"
	CGIEXIT=1
    elif [ -f $TMAXFILE -a -f $TSEQFILE ]; then
	TMAX=0`cat $TMAXFILE`
	TSEQ=0`cat $TSEQFILE`
	if [ -f $TCANCELFILE ]; then
	    CSEQ=`awk 'BEGIN { nlines=0 } { nlines++ } END { print nlines }' $TCANCELFILE`
	    TSEQ=`expr $TSEQ - $CSEQ`
	fi
	if [ $TSEQ -le $TMAX ]; then
	    MESSAGE="申込み受付延長期間中です．"
	else
	    MESSAGE="申込み受付期間は終了しました．"
	    CGIEXIT=1
	fi
    else
	MESSAGE="申込み受付延長期間中です．"
    fi
fi
if [ "X$MESSAGE" != "X" ]; then
    echo "<font size=+2 color=red>$MESSAGE</font>"
    if [ "X$CGIEXIT" = "X1" ]; then
	printepilogue
	rm -f $MKAUTHTABLE
	exit 1
    fi
fi

cat <<EOF
<p>
<FORM METHOD="POST" ACTION="ninfg.cgi">

<table>
<tr><th align=right>発表題目：</th><td>$printtitle</td></tr>
<tr><td align=right>氏名：</td><td>$printname</td></tr>
<tr><td align=right>組織名：</td><td>$printorganization</td></tr>
<tr><td align=right>Email：</td><td>$ORIGINAL_SECURE_ADDRESS</td></tr>
</table>

<p>
【記入上の注意】<BR>
<UL>
<LI>英数字はいわゆる「半角」文字で入力して下さい。
</UL>

<DL>
<DT>概要（５０文字程度）：
<DD><INPUT TYPE="text" value="$FORM_printabstract" NAME="abstract" MAXLENGTH="150" SIZE="70">

<DT>発表者氏名：
<DD>
所属は、略称でご記入をお願いいたします。
EOF

. $MKAUTHTABLE

cat <<EOF
<DT>コメント：
<DD>（他に何か御希望があればどうぞ）<BR>
<TEXTAREA NAME="comment" ROWS=10 COLS=60>$FORM_printcomment</TEXTAREA>

<DT><INPUT TYPE="submit" VALUE="確認画面へ">

</DL>

<input type="hidden" name="query_str" value="$QUERY_STRING">
<input type="hidden" name="command" value="apply_form2.cgi">
</FORM>

<HR>

申込みに関する問い合わせは、
&lt;$SECRETARY&gt;
までメールでお願いいたします。

</body>
</html>
EOF

rm -f $MKAUTHTABLE

exit 0
