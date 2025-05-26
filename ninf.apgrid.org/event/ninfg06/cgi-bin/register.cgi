#!/bin/sh
#
# $Id: register.cgi,v 1.8 2005/08/17 04:04:09 tatebe Exp $

MAILTO=$SECRETARY

if [ "X$FORM_name" = "X" ]; then
	ERRLOC=REG-1-1
	ERRMSG="お名前を入力して下さい"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

if [ "X$FORM_kana" = "X" ]; then
	ERRLOC=REG-1-2
	ERRMSG="ふりがなを入力して下さい"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

if [ "X$FORM_mail1" = "X" -o "X$FORM_mail2" = "X" ]; then
	ERRLOC=REG-1-3
	ERRMSG="メールアドレスを入力して下さい"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

if [ "$FORM_mail1" != "$FORM_mail2" ]; then
	ERRLOC=REG-1-4
	ERRMSG="2つのメールアドレスが一致していません"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
ORIGINAL_SECURE_ADDRESS=`secureaddr "$FORM_mail1"`
if [ "X$FORM_mail1" != "X$ORIGINAL_SECURE_ADDRESS" ]; then
	ERRLOC=REG-1-5
	ERRMSG="メールアドレスが間違っているか，おかしな文字を含んでいます．"
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

printprologue "$NINFG 参加登録（完了）"
echo "<H1>$NINFG 参加登録（完了）</H1>"

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

$NINFG の参加登録を承りました．

受付番号は $SEQ です．

---
$NINFG 実行委員
$SECRETARY
EOF

###

fi # DOUBLEREGISTER

###

echo '次の通り、参加登録を受け付けました。'
echo '<hr>'

echo '受付番号: '
echo '<b>'
echo `secureprint "$SEQ"`
echo '</b>'

echo '<br>'
echo 'お名前: '
echo '<b>'
echo `secureprint "$FORM_name"`
echo '</b>'

echo '<br>'
echo 'ふりがな: '
echo '<b>'
echo `secureprint "$FORM_kana"`
echo '</b>'

echo '<br>'
echo '所属: '
echo '<b>'
echo `secureprint "$FORM_organization"`
echo '</b>'

echo '<br>'
echo 'メールアドレス: '
echo '<b>'
echo `secureprint "$FORM_mail1"`
echo '</b>'

echo '<br>'
echo 'チュートリアル: '
echo '<b>'
if [ "X$FORM_tutorial" = "Xyes" ]; then
    echo '初〜中級'
else
    echo '中〜上級'
fi
echo '</b>'

echo '<br>'
echo '懇親会: '
echo '<b>'
if [ "X$FORM_reception" = "Xyes" ]; then
    echo '参加'
else
    echo '不参加'
fi
echo '</b>'

echo '<br>'
echo 'コメント: '
echo '<b>'
echo `secureprint "$FORM_comment"`
echo '</b>'

cat <<EOF
<hr>

24時間以内に $ORIGINAL_SECURE_ADDRESS に確認メールが届きます。
万一届かない場合には、$SECRETARY までご連絡下さい。
<p>
また、ワークショップ、懇親会の参加を取消される場合も、
$SECRETARY までご連絡下さい。

</body>
</html>
EOF

exit 0
