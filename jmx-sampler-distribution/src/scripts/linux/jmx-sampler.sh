#!/bin/bash
JAVA=java
JAVA_OPTS=
# Uncomment this option if you are running this application with Java >=1.6 and are connecting to a Java 1.5 JMX client.
#JAVA_OPTS="$JAVA_OPTS -Dsun.lang.ClassLoader.allowArraySyntax=true" 
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
		nohup $JAVA -Dlogback.configurationFile=$LOGCONFIG $JAVA_OPTS -cp "${BASEDIR}/lib/*" org.jmxsampler.Runner start $CONFIG &
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
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $JAVA_OPTS -cp "${BASEDIR}/lib/*" org.jmxsampler.Runner check $CONFIG
		;;
	metadata)
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $JAVA_OPTS -cp "${BASEDIR}/lib/*" org.jmxsampler.Runner metadata $CONFIG
		;;
	*)
		cat <<EOF
Usage: $0 [start|stop|status|check|metadata]

start     Starts the application as a daemon in the background.
stop      Stops a running daemon (if any). This also cleans up the pid file if the application has crashed.
status    Outputs whether the daemon is running or not.
check     Goes through all samplers and checks whether each rule matches at least one metric. Everything is logged to STDOUT.
metadata  Goes through all samplers and outputs the metadata of their readers. Use it to see what metrics are available and build
          your rules based on that.
EOF
		;;
esac
