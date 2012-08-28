#!/bin/bash
JAVA=java
BASEDIR=$(dirname $0)
BASEDIR=$(readlink -f $BASEDIR/..)
LOGCONFIG=$BASEDIR/config/logback.xml
LOGCONFIG_CONSOLE=$BASEDIR/config/logback-console.xml
CONFIG=$BASEDIR/config/config.xml
PIDFILE=$BASEDIR/jmx-sampler.pid

if [ ! -e $BASEDIR/logs ]; then
        mkdir $BASEDIR/logs
fi

case "$1" in
	start)
		if [ -e $PIDFILE ]; then
			echo "PIDFILE $PIDFILE exists"
			exit 1
		fi
		nohup $JAVA -Dlogback.configurationFile=$LOGCONFIG -cp "${BASEDIR}/lib/*" org.jmxsampler.Runner start $CONFIG &
		echo $! > $PIDFILE
		echo "Started with pid $!"
		;;
	stop)
		if [ ! -e $PIDFILE ]; then
			echo "PIDFILE $PIDFILE does not exist"
			exit 2
		fi
		kill `cat $PIDFILE`
		rm $PIDFILE
		echo "Stopped"
		;;
	status)
		if [ ! -e $PIDFILE ]; then
			echo "Stopped (pid file $PIDFILE does not exist)"
		else
			PID=$(cat $PIDFILE)
			if [ -e /proc/$PID ]; then
				echo "Running (pid $PID)"
			else
				echo "Killed (pid file $PIDFILE exists but the process is not running)"
			fi
		fi
		;;
	check)
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "${BASEDIR}/lib/*" org.jmxsampler.Runner check $CONFIG
		;;
	metadata)
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "${BASEDIR}/lib/*" org.jmxsampler.Runner metadata $CONFIG
		;;
	*)
		echo "Usage: $0 start|stop|status|check|metadata"
		;;
esac
