#!/bin/bash
JAVA=java
JAVA_OPTS=
BASEDIR=$(dirname $0)
BASEDIR=$(readlink -f $BASEDIR/..)
LOGCONFIG=$BASEDIR/config/logback.xml
LOGCONFIG_CONSOLE=$BASEDIR/config/logback-console.xml
if [ -x $BASEDIR/bin/local.sh ]; then
	. $BASEDIR/bin/local.sh
fi
if [ "$2" == "" ]; then
	CONFIG="$BASEDIR/config/config.xml"
else
	CONFIG="$BASEDIR/config/$2"
fi
PIDFILE=$BASEDIR/metrics-sampler.pid

pushd $BASEDIR

if [ ! -e logs ]; then
        mkdir logs
fi

case "$1" in
	start)
		if [ -e $PIDFILE ]; then
			echo "PIDFILE $PIDFILE exists"
			exit 1
		fi
		nohup $JAVA -Dlogback.configurationFile=$LOGCONFIG $JAVA_OPTS -cp "lib/*" org.metricssampler.Runner start $CONFIG  > logs/console.out 2>&1 &
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
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $JAVA_OPTS -cp "lib/*" org.metricssampler.Runner check $CONFIG
		;;
	test)
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $JAVA_OPTS -cp "lib/*" org.metricssampler.Runner test $CONFIG
		;;
	metadata)
		$JAVA -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $JAVA_OPTS -cp "lib/*" org.metricssampler.Runner metadata $CONFIG
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
popd
