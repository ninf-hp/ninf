#! /bin/sh
#
#  Required:
#    cgiparse
#    nkf
#    mklock
#
#  $Id: gfarm.cgi,v 1.5 2005/08/16 08:58:16 tatebe Exp $

NINFG="Ninf-G Workshop 2006"
NINFGPAGE=http://ninf.apgrid.org/event/ninfg06/
NINFGPREFIX=ninfg06
NINFGDOMAIN=m.aist.go.jp
SECRETARY="yoshio.tanaka@aist.go.jp"
ADMIN=yoshio.tanaka@aist.go.jp

MLNAME=ninf-ja
MLDIR=/usr/local/PHASE/mail-lists
MLURL=http://ninf.apgrid.org/ml/

PERL=/usr/local/bin/perl

# application period YYYYMMDDHH
  STARTTIME=200512151000
    ENDTIME=200601070000
REALENDTIME=200601100000

SUBDIR=/home/yoshio/ninfg06-application
LOCKPREFIX=/var/tmp/.lock.ninfg.

#TABLEPASSWDFILE=$SUBDIR/table.passwd
#MOVEPASSWDFILE=$SUBDIR/table.passwd

NINFGCGIDIR=/usr/local/htdocs/ninf_htdocs/contents/event/ninfg06/cgi-bin

# For cgiparse
PATH=$NINFGCGIDIR:/home/yoshio/bin:$PATH
export PATH

# file access permission should be 666
umask 0

#### NO NEED TO CHANGE AFTER HERE

# noglob
set -f

secureaddr () {
    echo "$1" | sed -n -e 's/^\([A-Za-z0-9._-]*\)@\([A-Za-z0-9._-]*\)$/\1@\2/p'
}

securetel () {
    echo "$1" | tr -cd '[:alnum:]-+()'
}

secureprint () {
    echo "$1" | nkf -e |
    sed '{
	s/&/\&amp;/g
	s/</\&lt;/g
	s/>/\&gt;/g
	s/"/\&quot;/g
	}'
}

quotetex () {
    echo "$1" | nkf -e |
    sed '{
	s/\\/\\\\/g
	s/\$/\\$/g
	s/&/\\&/g
	s/%/\\%/g
	s/#/\\#/g
	s/_/\\_/g
	s/{/\\{/g
	s/}/\\}/g
	}' | nkf -j | tr '\015' '\012'
}

createdir () {
    HTACCESS=.htaccess
    [ -d "$1" ] || (mkdir -p "$1" || {
	ERRLOC=CD-1-1
	ERRMSG="[System Error] Cannot create a directory $1.  Please contact an administrator."
	printerrmsg $ERRLOC "$ERRMSG"
	senderrmail $ERRLOC "$ERRMSG" $SEQFILE
	exit 1
    })
    if [ ! -f "$1/$HTACCESS" ]; then
	/bin/echo Options -Indexes > "$1/$HTACCESS" || exit 1
    fi
}

printerrmsg () {
    ERRNUM=`secureprint "$1"`
    ERRMSG=`secureprint "$2"`
    shift
    shift

    if [ "X$REQUEST_METHOD" != "X" ]; then
	PREFIX0="<p>"
	PREFIX1="<br>"
    fi

    printprologue

    echo $PREFIX0 ERROR $ERRNUM: $ERRMSG
    if [ $# -gt 0 ]; then
	echo $PREFIX0 `secureprint "$1"`
	shift
    fi
    while [ $# -gt 0 ]; do
	echo $PREFIX1 `secureprint "$1"`
	shift
    done

    printepilogue
}

senderrmail () {
    ERRNUM=$1
    ERRMSG=$2
    if [ "X$ADMIN" = "X" ]; then
	printerrmsg 0 "Administration email address is not specified." "$0" "$*"
	exit 1
    fi
    /usr/lib/sendmail -f $ADMIN $ADMIN <<EOF
Subject: [Ninf-G-apply: error $ERRNUM] $ERRMSG
To: $ADMIN
$0
$3
$4

REMOTE_ADDR $REMOTE_ADDR
REMOTE_HOST $REMOTE_HOST
REMOTE_PORT $REMOTE_PORT

--------
`env`
EOF
}

# printprologue $title
printprologue () {
    if [ "X$CGIMODE" = "X1" ]; then
	cat <<EOF

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML LANG="ja">
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=euc-jp">
<META HTTP-EQUIV="Content-Style-Type" CONTENT="text/css">
<LINK REL="STYLESHEET" HREF="../default-style.css" TYPE="text/css">

<TITLE LANG="ja">$1</TITLE>
</HEAD>

<BODY>
EOF
    fi
}

printepilogue () {
    if [ "X$CGIMODE" = "X1" ]; then
	cat <<EOF

<p>
お問い合わせは，$NINFG 実行委員
&lt;$SECRETARY&gt;
までお願いします．
<p>
<hr>
<a href="$NINFGPAGE">$NINFG</a>
</body>
</html>
EOF
    fi
}

# amllock $LOCKFILE
amllock () {
    MAX_IT=5

    IT=0
    until mklock $1; do
	sleep 1
	IT=`expr $IT + 1`
	if [ $IT = $MAX_IT ]; then
	    break
	fi
    done
    if [ $IT = $MAX_IT ]; then
	ERRLOC=LOCK-1
	ERRMSG="[System Error] Cannot obtain a lock.  Please try again later or contact an administrator."
	printerrmsg $ERRLOC "$ERRMSG"
	senderrmail $ERRLOC "$ERRMSG"
	exit 1
    fi
}

amlunlock () {
    rm -f $1
}

#
#
#

CGIMODE=1

echo "Content-type: text/html"
echo ""

# Parse CGI argument.
eval `cgiparse -init` || {
	ERRLOC=1
	ERRMSG="Initialization failed."
	ERRARG="`which cgiparse`"
	printerrmsg $ERRLOC "$ERRMSG"
	senderrmail $ERRLOC "$ERRMSG" "$ERRARG"
	exit 1
}
SAVED_QUERY_STRING=$QUERY_STRING
eval `cgiparse -form` || {
	ERRLOC=2
	ERRMSG="You cannot run this CGI directly."
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
}

if [ "X$FORM_command" = "X" ]; then
	ERRLOC=3
	ERRMSG="Invalid access."
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi
SECURE_COMMAND=`echo "$FORM_command" | tr -cd "[:alnum:]_."`
if [ "X$FORM_command" != "X$SECURE_COMMAND" ]; then
	ERRLOC=4
	ERRMSG="Invalid option."
	printerrmsg $ERRLOC "$ERRMSG"
	exit 1
fi

. $SECURE_COMMAND || exit 1

exit 0
