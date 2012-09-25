#!/bin/bash
JAVA=java
BASEDIR=$(dirname $0)
BASEDIR=$(readlink -f $BASEDIR/..)
LOGCONFIG=$BASEDIR/config/logback.xml
LOGCONFIG_CONSOLE=$BASEDIR/config/logback-console.xml
CONTROL_PORT=28111
CONTROL_HOST=localhost
JAVA_OPTS="-Dlogback.configurationFile=$LOGCONFIG -Dcontrol.host=$CONTROL_HOST -Dcontrol.port=$CONTROL_PORT"
VERSION="${project.version}"

if [ -x $BASEDIR/bin/local.sh ]; then
	. $BASEDIR/bin/local.sh
fi
if [ "$2" == "" ]; then
	CONFIG="$BASEDIR/config/config.xml"
else
	CONFIG="$BASEDIR/config/$2"
fi

pushd $BASEDIR > /dev/null

if [ ! -e logs ]; then
        mkdir logs
fi

case "$1" in
	start)
		nohup $JAVA $JAVA_OPTS -cp "lib/*" org.metricssampler.Runner start $CONFIG  > logs/console.out 2>&1 &
		echo "Started with pid $!"
		;;
	stop)
		$JAVA $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "lib/*" org.metricssampler.Stop
		echo "Stopped"
		;;
	restart)
		$0 stop $2
		sleep 2
		$0 start $2
		;;
	status)
		$JAVA $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "lib/*" org.metricssampler.Status
		;;
	check)
		$JAVA $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "lib/*" org.metricssampler.Runner check $CONFIG
		;;
	test)
		$JAVA $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "lib/*" org.metricssampler.Runner test $CONFIG
		;;
	metadata)
		$JAVA $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "lib/*" org.metricssampler.Runner metadata $CONFIG
		;;
	*)
		cat <<EOF
metrics-sampler version $VERSION
Usage: `basename $0` [start|stop|restart|status|check|test|metadata] [<config.xml>]

start     Starts the application as a daemon in the background.
stop      Stops a running daemon (if any).
status    Checks whether the daemon is running or no.
restart   Stops the running daemon (if any) and then starts it.
check     Goes through all samplers and checks whether each rule matches at least one metric. Everything is logged to STDOUT.
test      Calls all enabled samplers once and exits.
metadata  Goes through all samplers and outputs the metadata of their readers. Use it to see what metrics are available and build
          your rules based on that.
EOF
		;;
esac
popd > /dev/null
