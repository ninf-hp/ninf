#!/bin/sh
#
# $Id: register.cgi,v 1.8 2005/08/17 04:04:09 tatebe Exp $

MAILTO=$SECRETARY

if [ "X$FORM_name" = "X" ]; then
	ERRLOC=REG-1-1
	ERRMSG="��̾�������Ϥ��Ʋ�����"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

if [ "X$FORM_kana" = "X" ]; then
	ERRLOC=REG-1-2
	ERRMSG="�դ꤬�ʤ����Ϥ��Ʋ�����"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

if [ "X$FORM_mail1" = "X" -o "X$FORM_mail2" = "X" ]; then
	ERRLOC=REG-1-3
	ERRMSG="�᡼�륢�ɥ쥹�����Ϥ��Ʋ�����"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

if [ "$FORM_mail1" != "$FORM_mail2" ]; then
	ERRLOC=REG-1-4
	ERRMSG="2�ĤΥ᡼�륢�ɥ쥹�����פ��Ƥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_mail1"`
if [ "X$FORM_mail1" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=REG-1-5
	ERRMSG="�᡼�륢�ɥ쥹���ְ�äƤ��뤫����������ʸ����ޤ�Ǥ��ޤ���"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

printprologue "$NINFG ������Ͽ�ʴ�λ��"
echo "<H1>$NINFG ������Ͽ�ʴ�λ��</H1>"

#####

SEQFILE=$SUBDIR/join/seq

DOUBLEREGISTER=0
TMPQUERY=/tmp/join_query-$$
echo $QUERY_STRING > $TMPQUERY

# double registration check
CHECK_STATUS=0
if [ -f $SEQFILE ]; then
    i=1
    SEQ=`cat $SEQFILE`
    while [ $i -lt $SEQ ]; do
	if [ -f $SUBDIR/join/spool/$i ]; then
	    diff $SUBDIR/join/spool/$i $TMPQUERY >/dev/null 2>&1 \
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
createdir "$SUBDIR/join"
createdir "$SUBDIR/join/spool"

###

if [ "X$DOUBLEREGISTER" = X0 ]; then

# Lock
LOCKFILE=$LOCKPREFIX
amllock $LOCKFILE

# If missing, initialize and create a SEQFILE.
if [ ! -f $SEQFILE ]; then
    /bin/echo 1 > $SEQFILE || {
	amlunlock $LOCKFILE
	ERRLOC=REG-1-6
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
SUMMARYFILE=$SUBDIR/join/summary
echo `date '+%Y/%m/%d %H:%M:%S'` [$SEQ] $ORIGINAL_SECURE_ADDRESS >> $SUMMARYFILE

# Unlock
amlunlock $LOCKFILE

# query string
QUERYFILE=$SUBDIR/join/spool/$SEQ
SPOOLFILE=$QUERYFILE.$ORIGINAL_SECURE_ADDRESS
mv $TMPQUERY $QUERYFILE

#####

ISO2022JP_name=`echo "$FORM_name" | nkf -Ej`
ISO2022JP_kana=`echo "$FORM_kana" | nkf -Ej`
ISO2022JP_org=`echo "$FORM_organization" | nkf -Ej`
ISO2022JP_comment=`echo "$FORM_comment" | nkf -Ej`
RECEPTION=$FORM_reception
TUTORIAL=$FORM_tutorial

cat > $SPOOLFILE <<EOF
"$SEQ","$ISO2022JP_name","$ISO2022JP_kana","$ISO2022JP_org","$FORM_mail1","$TUTORIAL","$RECEPTION","$REMOTE_ADDR","$ISO2022JP_comment"

REMOTE_ADDR $REMOTE_ADDR
REMOTE_HOST $REMOTE_HOST
REMOTE_PORT $REMOTE_PORT
HTTP_REFERER $HTTP_REFERER
HTTP_USER_AGENT $HTTP_USER_AGENT
QUERY_STRING $QUERY_STRING
EOF

cat <<EOF | cat - $SPOOLFILE | /usr/lib/sendmail -f $MAILTO $MAILTO
Content-Type: Text/Plain; charset=iso-2022-jp
Content-Transfer-Encoding: 7bit
Subject: [NinfG06:$SEQ] $ORIGINAL_SECURE_ADDRESS
Posted: `date`
From: $ORIGINAL_SECURE_ADDRESS
To: $MAILTO

EOF

###

cat <<EOF | nkf -j | /usr/lib/sendmail -f $SECRETARY $ORIGINAL_SECURE_ADDRESS
MIME-Version: 1.0
Content-Type: Text/Plain; charset=iso-2022-jp
Content-Transfer-Encoding: 7bit
Subject: $NINFG registration completed
To: $ORIGINAL_SECURE_ADDRESS

$NINFG �λ�����Ͽ�򾵤�ޤ�����

�����ֹ�� $SEQ �Ǥ���

---
$NINFG �¹԰Ѱ�
$SECRETARY
EOF

###

fi # DOUBLEREGISTER

###

echo '�����̤ꡢ������Ͽ������դ��ޤ�����'
echo '<hr>'

echo '�����ֹ�: '
echo '<b>'
echo `secureprint "$SEQ"`
echo '</b>'

echo '<br>'
echo '��̾��: '
echo '<b>'
echo `secureprint "$FORM_name"`
echo '</b>'

echo '<br>'
echo '�դ꤬��: '
echo '<b>'
echo `secureprint "$FORM_kana"`
echo '</b>'

echo '<br>'
echo '��°: '
echo '<b>'
echo `secureprint "$FORM_organization"`
echo '</b>'

echo '<br>'
echo '�᡼�륢�ɥ쥹: '
echo '<b>'
echo `secureprint "$FORM_mail1"`
echo '</b>'

echo '<br>'
echo '���塼�ȥꥢ��: '
echo '<b>'
if [ "X$FORM_tutorial" = "Xyes" ]; then
    echo '������'
else
    echo '������'
fi
echo '</b>'

echo '<br>'
echo '���Ʋ�: '
echo '<b>'
if [ "X$FORM_reception" = "Xyes" ]; then
    echo '����'
else
    echo '�Ի���'
fi
echo '</b>'

echo '<br>'
echo '������: '
echo '<b>'
echo `secureprint "$FORM_comment"`
echo '</b>'

cat <<EOF
<hr>

24���ְ���� $ORIGINAL_SECURE_ADDRESS �˳�ǧ�᡼�뤬�Ϥ��ޤ���
�����Ϥ��ʤ����ˤϡ�$SECRETARY �ޤǤ�Ϣ��������
<p>
�ޤ����������åס����Ʋ�λ��ä��ä������⡢
$SECRETARY �ޤǤ�Ϣ��������

</body>
</html>
EOF

exit 0
