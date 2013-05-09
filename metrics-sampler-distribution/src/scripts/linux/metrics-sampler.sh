#!/bin/bash
JAVA=java
BASEDIR="$( cd -P "$( dirname "$0" )/.." && pwd )"
VERSION="${project.version}"
if [[ "$OS" == Windows* ]]; then
        CLASSPATH_SEPARATOR=";"
else
        CLASSPATH_SEPARATOR=":"
fi
CLASSPATH="lib/*${CP_SEPARATOR}lib.local/*"
MAIN_CLASS=org.metricssampler.MetricsSampler
OPTS_CONTROL=
OPTS_ALL=
OPTS=CONFIG=
JAVA_OPTS=

if [ -x $BASEDIR/bin/local.sh ]; then
	. $BASEDIR/bin/local.sh
fi

pushd $BASEDIR > /dev/null

if [ ! -e logs ]; then
        mkdir logs
fi

case "$1" in
	start)
		nohup $JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN_CLASS $1 $OPTS_ALL ${@:2} > logs/console.out 2>&1 &
		echo "Started with pid $!"
		;;
	stop)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN_CLASS $1 $OPTS_CONTROL ${@:2}
		echo "Stopped"
		;;
	restart)
		popd > /dev/null
		$0 stop ${@:2}
		sleep 2
		$0 start ${@:2}
		exit
		;;
	status)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN_CLASS $1 $OPTS_CONTROL ${@:2}
		echo $MSG
		RETURN_CODE=$(echo "$MSG" | grep 'Stopped' | wc -l)
		exit $RETURN_CODE
 		;;
	check|test|metadata)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN_CLASS $1 $OPTS_CONFIG ${@:2}
		;;
	*)
		$JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN_CLASS ${@:1}
		;;
esac
popd > /dev/null
