#!/bin/sh

printf "Location: http://ninf.apgrid.org/bugzilla/"
case ${QUERY_STRING} in
ja)
	printf "ja/\n"
	;;
*)
	printf "en/\n"
	;;
esac
echo
