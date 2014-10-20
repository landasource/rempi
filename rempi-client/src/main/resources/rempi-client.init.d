#!/bin/sh
### BEGIN INIT INFO
# Provides: rempi-client
# Required-Start:
# Required-Stop:
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
# Short-Description: start vnc server
# Description:
### END INIT INFO

### sudo update-rc.d /etc/init.d/servod defaults

### install cron
# sudo crontab -e
# add line
# * * * * * service rempi-client start

cd /home/pi/rempi

SCRIPT='./rempi-client.sh'

# Carry out specific functions when asked to by the system
case "$1" in
  start)
    echo "Starting rempi"
    su root -c ${SCRIPT}
    ;;
  stop)
    sudo ${SCRIPT} stop
    echo "Rempi client has stopped"
    ;;
  *)
    echo "Usage: /etc/init.d/rempi-client {start|stop}"
    exit 1
    ;;
esac
