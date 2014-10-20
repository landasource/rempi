#!/bin/bash
# Executes Rempi client

cd /home/pi/rempi
LOCKFILE=/home/pi/rempi/lock.pid

if [ "$1" = 'stop' ]; then
	kill -SIGTERM `cat ${LOCKFILE}`
	exit
fi

if [ -e ${LOCKFILE} ] ; then
	
	# check process
	if ps -A | grep `cat ${LOCKFILE}`; then
		echo "already running"
		exit
	fi
fi

# make sure the lockfile is removed when we exit and then claim it
trap "rm -f ${LOCKFILE}; exit" INT TERM EXIT
echo $$ > ${LOCKFILE}

# do stuff
exec sudo java -Dname=rempi-client -jar rempi-client-0.0.1.jar 192.168.2.10 9000 raspberry

rm -f ${LOCKFILE}

echo $$ > pid
