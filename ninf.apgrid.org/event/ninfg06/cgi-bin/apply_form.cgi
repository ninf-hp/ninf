#! /bin/sh
#
#  Required:
#
#  $Id: apply_form.cgi,v 1.1 2005/07/22 10:18:17 tatebe Exp $

if [ "X$FORM_email" = "X" ]; then
	ERRLOC=AP-1-1
	ERRMSG="Email ���ɥ쥹����������Ƥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
if [ "X$FORM_email" != "X$FORM_email1" ]; then
	ERRLOC=AP-1-2
	ERRMSG="��ǧ�� Email ���ɥ쥹�Ȱ��פ��Ƥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_email"`
if [ "X$FORM_email" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=AP-1-3
	ERRMSG="Email ���ɥ쥹���ְ�äƤ��뤫����������ʸ����ޤ�Ǥ��ޤ���"
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
	MODETITLE="ȯɽ������"
else
	MODETITLE="ȯɽ�����߽���"
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
[ "X\$FORM_printoption$i" = "X��" ] && OPTIONYES=selected
[ "X\$FORM_printoption$i" = "X��" ] && OPTIONNO=selected

echo "<tr>"
echo "<th align=right>��̾${i}��</th>"
echo "<td><INPUT TYPE=\"text\" NAME=\"authors${i}\" value=\"\$FORM_printauthors$i\" MAXLENGTH=\"32\" SIZE=\"12\"></td>"
echo "<th align=right>��°${i}��</th>"
echo "<td><INPUT TYPE=\"text\" NAME=\"aug${i}\" value=\"\$FORM_printaug$i\" MAXLENGTH=\"32\" SIZE=\"10\"></td>"
#echo "<th>���żԡ�</th>"
#echo "<td><SELECT NAME=\"option${i}\" SIZE=\"1\">"
#echo "  <OPTION \$OPTIONNO>��  <OPTION \$OPTIONYES>��  </SELECT></td>"
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
    MESSAGE="�ޤ������߼��դ򳫻Ϥ��Ƥ��ޤ��󡥶�������ޤ�����������˿����߲�������"
    CGIEXIT=1
elif [ $CURRENTTIME -ge $REALENDTIME ]; then
    MESSAGE="�����߼��մ��֤Ͻ�λ���ޤ�����"
    CGIEXIT=1
elif [ $CURRENTTIME -ge $ENDTIME ]; then
    TMAXFILE=$SUBDIR/max
    TSEQFILE=$SUBDIR/seq
    TCANCELFILE=$SUBDIR/cancel
    FINISHFILE=$SUBDIR/xxx
    if [ -f $FINISHFILE ]; then
	MESSAGE="�����߼��մ��֤Ͻ�λ���ޤ�����"
	CGIEXIT=1
    elif [ -f $TMAXFILE -a -f $TSEQFILE ]; then
	TMAX=0`cat $TMAXFILE`
	TSEQ=0`cat $TSEQFILE`
	if [ -f $TCANCELFILE ]; then
	    CSEQ=`awk 'BEGIN { nlines=0 } { nlines++ } END { print nlines }' $TCANCELFILE`
	    TSEQ=`expr $TSEQ - $CSEQ`
	fi
	if [ $TSEQ -le $TMAX ]; then
	    MESSAGE="�����߼��ձ�Ĺ������Ǥ���"
	else
	    MESSAGE="�����߼��մ��֤Ͻ�λ���ޤ�����"
	    CGIEXIT=1
	fi
    else
	MESSAGE="�����߼��ձ�Ĺ������Ǥ���"
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
<tr><th align=right>ȯɽ���ܡ�</th><td>$printtitle</td></tr>
<tr><td align=right>��̾��</td><td>$printname</td></tr>
<tr><td align=right>�ȿ�̾��</td><td>$printorganization</td></tr>
<tr><td align=right>Email��</td><td>$ORIGINAL_SECURE_ADDRESS</td></tr>
</table>

<p>
�ڵ��������ա�<BR>
<UL>
<LI>�ѿ����Ϥ������Ⱦ�ѡ�ʸ�������Ϥ��Ʋ�������
</UL>

<DL>
<DT>���סʣ���ʸ�����١ˡ�
<DD><INPUT TYPE="text" value="$FORM_printabstract" NAME="abstract" MAXLENGTH="150" SIZE="70">

<DT>ȯɽ�Ի�̾��
<DD>
��°�ϡ�ά�ΤǤ������򤪴ꤤ�������ޤ���
EOF

. $MKAUTHTABLE

cat <<EOF
<DT>�����ȡ�
<DD>��¾�˲������˾������Фɤ�����<BR>
<TEXTAREA NAME="comment" ROWS=10 COLS=60>$FORM_printcomment</TEXTAREA>

<DT><INPUT TYPE="submit" VALUE="��ǧ���̤�">

</DL>

<input type="hidden" name="query_str" value="$QUERY_STRING">
<input type="hidden" name="command" value="apply_form2.cgi">
</FORM>

<HR>

�����ߤ˴ؤ����䤤��碌�ϡ�
&lt;$SECRETARY&gt;
�ޤǥ᡼��Ǥ��ꤤ�������ޤ���

</body>
</html>
EOF

rm -f $MKAUTHTABLE

exit 0
