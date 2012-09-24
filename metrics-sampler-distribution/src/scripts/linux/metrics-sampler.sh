#!/bin/bash
JAVA=java
BASEDIR=$(dirname $0)
BASEDIR=$(readlink -f $BASEDIR/..)
LOGCONFIG=$BASEDIR/config/logback.xml
LOGCONFIG_CONSOLE=$BASEDIR/config/logback-console.xml
SHUTDOWN_PORT=28111
JAVA_OPTS="-Dlogback.configurationFile=$LOGCONFIG -Dshutdown.port=$SHUTDOWN_PORT"
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
		$JAVA $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE -cp "lib/*" org.metricssampler.Runner stop $CONFIG
		echo "Stopped"
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
Usage: `basename $0` [start|stop|check|test|metadata] [<config.xml>]

start     Starts the application as a daemon in the background.
stop      Stops a running daemon (if any).
check     Goes through all samplers and checks whether each rule matches at least one metric. Everything is logged to STDOUT.
test      Calls all enabled samplers once and exits.
metadata  Goes through all samplers and outputs the metadata of their readers. Use it to see what metrics are available and build
          your rules based on that.
EOF
		;;
esac
popd > /dev/null
