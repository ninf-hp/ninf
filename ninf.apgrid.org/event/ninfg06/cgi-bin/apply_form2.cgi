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
	ERRMSG="Email ���ɥ쥹����������Ƥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_email"`
if [ "X$FORM_email" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=AP-2-3
	ERRMSG="Email ���ɥ쥹���ְ�äƤ��뤫����������ʸ����ޤ�Ǥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

#if [ "X$FORM_nauthors" = "X" ]; then
#	ERRLOC=AP-2-4
#	ERRMSG="�����Կ������Ϥ���Ƥ��Ƥ��ޤ���"
#	printerrmsg $ERRLOC "$ERRMSG"
#	exit 1
#fi
#if [ $FORM_nauthors -lt 1 -o $FORM_nauthors -gt 50 ]; then
#	ERRLOC=AP-2-5
#	ERRMSG="�����Կ���¿�����뤫����Ⱦ�ѡ׿����ǤϤ���ޤ���"
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
	MODETITLE="ȯɽ������"
	BUTTONTITLE="ȯɽ����"
else
	MODETITLE="ȯɽ�����߽���"
	BUTTONTITLE="��������"
fi

printprologue "$NINFG $MODETITLE (��ǧ)"
echo "<H1>$NINFG $MODETITLE (��ǧ)</H1>"

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
#    echo echo \"\$printauthors$i \(\$printaug$i\) \`\[ \"X\$FORM_option$i\" = \"X��\" \] \&\& echo ����\`\<br\>\" >> $MKAUTHTABLE
#    i=`expr $i + 1`
#done

#
#
#

cat <<EOF
<FORM METHOD="POST" ACTION="ninfg.cgi">

<table>
<tr valign=top><th align=right>ȯɽ���ܡ�</th><td>$printtitle</td></tr>
</table>
<dl>
<dt>���ס�
<dd>$printabstract
</dl>
<table>
<tr valign=top>
<th align=right>ȯɽ�Ի�̾��</th>
<td>
EOF
. $MKAUTHTABLE
cat<<EOF
</td>
</tr>
<tr valign=top><th align=right>������Ϣ���衧</th><td></td></tr>
<tr valign=top><td align=right>��̾��</td><td>$printname</td></tr>
<tr valign=top><td align=right>�ȿ�̾��</td><td>$printorganization</td></tr>
#<tr valign=top><td align=right>����̾��</td><td>$printBushO</td></tr>
#<tr valign=top><td align=right>���ꡧ</td><td>$printaddress</td></tr>
#<tr valign=top><td align=right>͹���ֹ桧</td><td>$printzip</td></tr>
#<tr valign=top><td align=right>�����ֹ桧</td><td>$printtel</td></tr>
#<tr valign=top><td align=right>FAX �ֹ桧</td><td>$printfax</td></tr>
<tr valign=top><td align=right>Email��</td><td>$ORIGINAL_SECURE_ADDRESS</td></tr>
</table>
<dl>
<dt>�����ȡ�
<dd>$printcomment
</dl>

<INPUT TYPE="submit" VALUE="$BUTTONTITLE">

<input type="hidden" name="query_str" value="$SAVED_QUERY_STRING">
<input type="hidden" name="command" value="apply_form3.cgi">
</FORM>

<P>
���Ƥ��ǧ�����塢��<B>$BUTTONTITLE</B>�ץܥ���򲡤��Ʋ�������
24���ְ���˾嵭���Żҥ᡼�륢�ɥ쥹�˳�ǧ�᡼�뤬�Ϥ��ޤ���
�����Ϥ��ʤ����ˤϡ�
&lt;$SECRETARY&gt;
�ޤǤ�Ϣ��������
</P>

<HR>

</body></html>
EOF

rm -f $MKAUTHTABLE

exit 0
