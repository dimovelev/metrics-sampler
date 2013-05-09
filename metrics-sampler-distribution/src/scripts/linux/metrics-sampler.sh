#!/bin/bash
JAVA=java
BASEDIR="$( cd -P "$( dirname "$0" )/.." && pwd )"
if [ -f $BASEDIR/config/logback.xml ]; then
	LOGCONFIG=$BASEDIR/config/logback.xml
else
	LOGCONFIG=$BASEDIR/config/logback.default.xml
fi
if [ -f $BASEDIR/config/logback-console.xml ]; then
	LOGCONFIG_CONSOLE=$BASEDIR/config/logback-console.xml
else
	LOGCONFIG_CONSOLE=$BASEDIR/config/logback-console.default.xml
fi
CONTROL_PORT=28111
CONTROL_HOST=localhost
VERSION="${project.version}"
CLASSPATH="lib/*:lib.local/*"
JAVA_OPTS="-Dlogback.configurationFile=$LOGCONFIG"
MAIN_CLASS=org.metricssampler.Runner
if [ "$2" == "" ]; then
	CONFIG="$BASEDIR/config/config.xml"
else
	CONFIG="$BASEDIR/config/$2"
fi

if [ -x $BASEDIR/bin/local.sh ]; then
	. $BASEDIR/bin/local.sh
fi

pushd $BASEDIR > /dev/null

if [ ! -e logs ]; then
        mkdir logs
fi

case "$1" in
	start)
		nohup $JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN_CLASS $1 -c $CONFIG -h $CONTROL_HOST -p $CONTROL_PORT > logs/console.out 2>&1 &
		echo "Started with pid $!"
		;;
	stop)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $MAIN_CLASS $1 -h $CONTROL_HOST -p $CONTROL_PORT
		echo "Stopped"
		;;
	restart)
		popd > /dev/null
		$0 stop
		sleep 2
		$0 start $2
		exit
		;;
	status)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $MAIN_CLASS $1 -h $CONTROL_HOST -p $CONTROL_PORT
		echo $MSG
		RETURN_CODE=$(echo "$MSG" | grep 'Stopped' | wc -l)
		exit $RETURN_CODE
 		;;
	check)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $MAIN_CLASS $1 -c $CONFIG -s $3
		;;
	test)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $MAIN_CLASS $1 -c $CONFIG -s $3
		;;
	metadata)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS -Dlogback.configurationFile=$LOGCONFIG_CONSOLE $MAIN_CLASS $1 -c $CONFIG -i $3
		;;
	*) 
		cat <<EOF
metrics-sampler version $VERSION
Usage: `basename $0` [start|stop|restart|status|check|test|metadata] [<config.xml>] [options]

start              Start the application as a daemon in the background.
stop               Stop the application running as a daemon.
status             Check whether the applicatin is running as a daemon in the background.
restart            Stop and start the application.
check <samplers>   Go through the given samplers and check whether each rule matches at least one metric. Samplers is a comma separated list of sampler names. If not given, will use all. Everything is logged to STDOUT.
test  <samplers>   Calls the given samplers once and exits. Samplers is a comma separated list of sampler names.
metadata <inputs>  Goes through all samplers and outputs the metadata of their readers. Use it to see what metrics are available and build
          your rules based on that.
EOF
		;;
esac
popd > /dev/null
