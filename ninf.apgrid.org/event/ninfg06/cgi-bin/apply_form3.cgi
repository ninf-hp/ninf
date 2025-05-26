#! /bin/sh
#
#  Required:
#    cgiparse
#    mklock
#
#  $Id: apply_form3.cgi,v 1.3 2005/08/16 08:58:16 tatebe Exp $

# QUERY_STRING is encapsulated by query_str
if [ "X$FORM_query_str" = "X" ]; then
	ERRLOC=AP-3-1
	ERRMSG="You cannot run this CGI directly."
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
QUERY_STRING=$FORM_query_str
SAVED_QUERY_STRING=$QUERY_STRING
eval `cgiparse -form`
# again
if [ "X$FORM_query_str" = "X" ]; then
	ERRLOC=AP-3-2
	ERRMSG="You cannot run this CGI directly."
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
QUERY_STRING=$FORM_query_str
eval `cgiparse -form`

# email
if [ "X$FORM_email" = "X" ]; then
	ERRLOC=AP-3-3
	ERRMSG="Email ���ɥ쥹����������Ƥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_email"`
if [ "X$FORM_email" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=AP-3-4
	ERRMSG="Email ���ɥ쥹���ְ�äƤ��뤫����������ʸ����ޤ�Ǥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

## nauthors
#if [ "X$FORM_nauthors" = "X" ]; then
#	ERRLOC=AP-3-5
#	ERRMSG="�����Կ������Ϥ���Ƥ��Ƥ��ޤ���"
#	printerrmsg $ERRLOC "$ERRMSG"
#	exit 1
#fi
#if [ $FORM_nauthors -lt 1 -o $FORM_nauthors -gt 50 ]; then
#	ERRLOC=AP-3-6
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

textitle=`quotetex "$FORM_title"`
texname=`quotetex "$FORM_name"`
texorganization=`quotetex "$FORM_organization"`
#texBushO=`quotetex "$FORM_BushO"`
#texaddress=`quotetex "$FORM_address"`

#texzip=`quotetex "$FORM_zip"`
#textel=`quotetex "$FORM_tel"`
#texfax=`quotetex "$FORM_fax"`

texabstract=`quotetex "$FORM_abstract"`
texcomment=`quotetex "$FORM_comment"`

#
#
#

if [ "X$FORM_modification" = X ]; then
	MODETITLE="ȯɽ������"
else
	MODETITLE="ȯɽ�����߽���"
fi

printprologue "$NINFG $MODETITLE (��λ)"
echo "<H1>$NINFG $MODETITLE (��λ)</H1>"

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
	exit 1
    fi
fi

#

SEQFILE=$SUBDIR/seq

DOUBLEREGISTER=0
TMPQUERY=/tmp/apply_form3_query-$$
echo $SAVED_QUERY_STRING > $TMPQUERY

# modification
if [ "X$FORM_modification" != X ]; then
    SEQ=$FORM_modification
else # $FORM_modification

# double registration check
CHECK_STATUS=0
if [ -f $SEQFILE ]; then
    i=1
    SEQ=`cat $SEQFILE`
    while [ $i -lt $SEQ ]; do
	if [ -f $SUBDIR/spool/$i ]; then
	    diff $SUBDIR/spool/$i $TMPQUERY >/dev/null 2>&1 \
		&& CHECK_STATUS=1 && break
	fi
	i=`expr $i + 1`;
    done
fi
if [ $CHECK_STATUS = 1 ]; then
    rm -f $TMPQUERY
    DOUBLEREGISTER=1
    SEQ=$i
fi

# If missing, create indispensable directories and files.
createdir "$SUBDIR"
createdir "$SUBDIR/spool"

if [ "X$DOUBLEREGISTER" = X0 ]; then

# Lock
LOCKFILE=$LOCKPREFIX
amllock $LOCKFILE

# If missing, initialize and create a SEQFILE.
if [ ! -f $SEQFILE ]; then
    /bin/echo 1 > $SEQFILE || {
	amlunlock $LOCKFILE
	ERRLOC=AP-3-9
	ERRMSG="[System Error] Cannot create a SEQFILE.  Please contact an administrator."
	printerrmsg $ERRLOC "$ERRMSG"
	senderrmail $ERRLOC "$ERRMSG" $SEQFILE
	exit 0
    }
fi

# Increment a sequential number
SEQ=`cat $SEQFILE`
NSEQ=`expr $SEQ + 1`
echo $NSEQ > $SEQFILE

# summary file
SUMMARYFILE=$SUBDIR/summary
echo `date '+%Y/%m/%d %H:%M:%S'` [$SEQ] $ORIGINAL_SECURE_ADDRESS >> $SUMMARYFILE

# Unlock
amlunlock $LOCKFILE

fi # DOUBLEREGISTER

fi # $FORM_modification XXX - summary file is not updated.

# query string
QUERYFILE=$SUBDIR/spool/$SEQ
SPOOLFILE=$QUERYFILE.$ORIGINAL_SECURE_ADDRESS
REVISION=0
if [ "X$DOUBLEREGISTER" = X0 ]; then
    if [ "X$FORM_modification" != X ]; then
	# modification
	if [ ! -f $QUERYFILE ]; then
	    ERRLOC=AP-3-10
	    ERRMSG="[��Ͽ�ֹ�����] ����˾�θ������Ѥ��뤳�ȤϤǤ��ޤ���"
	    printerrmsg $ERRLOC "$ERRMSG"
	    exit 1
	fi
	if [ "X$FORM_verifypasswd" = "X" ]; then
	    ERRLOC=AP-3-11
	    ERRMSG="[�ѥ��������] �ѥ���ɤ����Ϥ���Ƥ��ޤ���"
	    printerrmsg $ERRLOC "$ERRMSG"
	    exit 1
	fi
	MAILAPPLICANT=$QUERYFILE-applicant
	grep "^$FORM_verifypasswd\$" $MAILAPPLICANT > /dev/null 2>&1 || {
	    ERRLOC=AP-3-12
	    ERRMSG="[�ѥ��������] ����˾�θ������Ѥ��뤳�ȤϤǤ��ޤ���"
	    printerrmsg $ERRLOC "$ERRMSG"
	    exit 1
	}
#	if [ ! -f $SPOOLFILE ]; then
#	    ERRLOC=AP-3-13
#	    ERRMSG="[�᡼������] Ϣ����᡼�륢�ɥ쥹���Ѥ��뤳�ȤϤǤ��ޤ���"
#	    printerrmsg $ERRLOC "$ERRMSG"
#	    exit 1
#	fi
	# back up
	mv $QUERYFILE $QUERYFILE\$$$
	set +f
	for file in $QUERYFILE.*; do
	    mv $file $file\$$$
	    REVISION=`expr $REVISION + 1`
	done
	# delete unnecessary files.
	rm -f $QUERYFILE-*
	set -f
    fi
    mv $TMPQUERY $QUERYFILE
fi

#
#
#

MAILKANJI=$QUERYFILE-kanji

if [ ! -f $MAILKANJI ]; then

MKAUTHMAIL=/tmp/$SEQ.authormail-$$

cat > $MKAUTHMAIL <<EOF
quotetex () {
    echo \$1 | nkf -e |
    sed '{
	s/\\\\/\\\\\\\\\\\\\\\\/g
	s/\\$/\\\\$/g
	s/&/\\\\&/g
	s/%/\\\\%/g
	s/#/\\\\#/g
	s/_/\\\\_/g
	s/{/\\\\{/g
	s/}/\\\\}/g
	}' | nkf -j | tr '\\015' '\\012'
}
EOF

i=1
while [ $i -le $FORM_nauthors ]; do
    echo texauthors$i=\`quotetex \"\$FORM_authors$i\"\` >> $MKAUTHMAIL
    echo texaug$i=\`quotetex \"\$FORM_aug$i\"\` >> $MKAUTHMAIL
    echo echo \"\\authors\{\$texauthors$i\}\{\$texaug$i\}\{\`[ \"X\$FORM_option$i\" = \"X��\" ] \&\& echo �� \| nkf -j\`}\" >> $MKAUTHMAIL
    i=`expr $i + 1`
done

# spool file
cat > $SPOOLFILE <<EOF
%Subject: [$SEQ:$REVISION] $ORIGINAL_SECURE_ADDRESS
%Posted: `date`
%From: $ORIGINAL_SECURE_ADDRESS
%
\title{$textitle}
EOF
. $MKAUTHMAIL >> $SPOOLFILE
cat >> $SPOOLFILE <<EOF
\abstract{$texabstract}
\name{$texname}
#\zip{$texzip}
#\address{$texaddress}
\organization{$texorganization}
#\section{$texBushO}
#\tel{$textel}
#\fax{$texfax}
\email{`quotetex $ORIGINAL_SECURE_ADDRESS`}
\comment{$texcomment}
%\systemcomment{`quotetex "$MESSAGE"`}
%
EOF

# mail to NinfG Secretary
NINFGSECRETARY=$SECRETARY

cat <<EOF | cat - $SPOOLFILE \
    | /usr/lib/sendmail -f $NINFGSECRETARY $NINFGSECRETARY
Subject: [$SEQ:$REVISION] $ORIGINAL_SECURE_ADDRESS
Posted: `date`
From: $ORIGINAL_SECURE_ADDRESS
To: $NINFGSECRETARY

EOF

# remove temporary files
rm -f $MKAUTHMAIL

touch $MAILKANJI

fi # MAILKANJI

#

MAILAPPLICANT=$QUERYFILE-applicant
MAILPASSWD=`$PERL -e '
    $accept_char = "0123456789abcdefghijklmnopqrstuvwxyz";
    srand(time + $$);
    for ($i=0; $i < 8; $i++) {
	$key .= substr($accept_char, rand(length($accept_char)), 1);
    }
    print $key;
'`

if [ ! -f $MAILAPPLICANT ]; then

euctitle=`echo $FORM_title | nkf -e`
eucabstract=`echo $FORM_abstract | nkf -e`

cat <<EOF | nkf -j | /usr/lib/sendmail -f $SECRETARY $ORIGINAL_SECURE_ADDRESS
Subject: $NINFG application completed
To: $ORIGINAL_SECURE_ADDRESS
$NINFG �� $MODETITLE �򾵤�ޤ�����

�����ֹ�� $SEQ �Ǥ���
���������Ƥγ�ǧ�������ѤΥѥ���ɤ� $MAILPASSWD �Ǥ���

ȯɽ����

$euctitle

����

$eucabstract

---
$NINFG �¹԰Ѱ�
$SECRETARY
EOF

echo $MAILPASSWD > $MAILAPPLICANT

fi # MAILAPPLICANT

#
#
#

cat<<EOF
<p>
$NINFG �� $MODETITLE �򾵤�ޤ�����
�����ֹ�� <b>$SEQ</b> �Ǥ���
EOF
if [ "X$FORM_modification" = X ]; then
    cat <<EOF
<P>
24���ְ���ˤ������ߤΥ᡼�륢�ɥ쥹
$ORIGINAL_SECURE_ADDRESS �˳�ǧ�᡼�뤬�Ϥ��ޤ���
�����Ϥ��ʤ����ˤϡ�
&lt;$SECRETARY&gt;
�ޤǤ�Ϣ��������
</P>
EOF
fi

SECURE_ADDRESS=`echo "$ORIGINAL_SECURE_ADDRESS" | tr "[:upper:]" "[:lower:]"`

# Initialize ML setting
if [ -f "$MLDIR/$MLNAME/ini" ]; then
    MEMBERDIR=$MLDIR/$MLNAME/members
    ACTIVEDIR=$MLDIR/$MLNAME/actives
    if [ ! -f "$MEMBERDIR/$SECURE_ADDRESS" ]; then
	echo "<p>"
	echo "�������ߤΥ᡼�륢�ɥ쥹�� $MLNAME ML ����Ͽ����Ƥ��ޤ���"
	echo "$MLNAME ML �Ǥϡ�$NINFG ��Ϣ�Τ�������Ϥ����Ƥ���ޤ��Τǡ�"
	echo "����<a href=\"$MLURL\">��Ͽ</a>�򤪤����ᤤ�����ޤ���"
    elif [ ! -f "$ACTIVEDIR/$SECURE_ADDRESS" ]; then
	    echo "<p>"
	    echo "�������ߤΥ᡼�륢�ɥ쥹�� $MLNAME ML ����Ͽ����Ƥ��ޤ�����"
	    echo "��������ʤ�����ˤʤäƤ��ޤ���"
	    echo "ɬ�פǤ����顤<a href=\"$MLURL\">�����ѹ�</a>�򤪴ꤤ���ޤ���"
    fi
fi

cat <<EOF
<p>
<a href="$NINFGPAGE">$NINFG</a>
EOF

#
#
#

MKAUTHTABLE=/tmp/$SEQ.authlist-$$
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

i=1
while [ $i -le $FORM_nauthors ]; do
    echo printauthors$i=\`secureprint \"\$FORM_authors$i\"\` >> $MKAUTHTABLE
    echo printaug$i=\`secureprint \"\$FORM_aug$i\"\` >> $MKAUTHTABLE
    echo echo \"\$printauthors$i \(\$printaug$i\) \`\[ \"X\$FORM_option$i\" = \"X��\" \] \&\& echo ����\`\<br\>\" >> $MKAUTHTABLE
    i=`expr $i + 1`
done

cat <<EOF
<p>
<hr>
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

<HR>

</body></html>
EOF

rm -f $MKAUTHTABLE

exit 0
