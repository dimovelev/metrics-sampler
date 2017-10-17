#!/bin/bash
JAVA=java
BASEDIR="$( cd -P "$( dirname "$0" )/.." && pwd )"
VERSION="${project.version}"
if [[ "$OS" == Windows* ]]; then
        CLASSPATH_SEPARATOR=";"
else
        CLASSPATH_SEPARATOR=":"
fi
CLASSPATH="lib/*${CLASSPATH_SEPARATOR}lib.local/*"
MAIN_CLASS=org.metricssampler.MetricsSampler
OPTS_CONTROL=
OPTS_START=
OPTS_CONFIG=
JAVA_OPTS=

if [ -x $BASEDIR/bin/local.sh ]; then
	. $BASEDIR/bin/local.sh
fi

if [ -d $BASEDIR/scripts ]; then
	chmod u+x $BASEDIR/scripts/*.sh
fi

CMD="$JAVA -cp $CLASSPATH $JAVA_OPTS $MAIN_CLASS"
pushd $BASEDIR > /dev/null

if [ ! -e logs ]; then
        mkdir logs
fi

case "$1" in
	start)
		nohup $CMD $1 $OPTS_CONTROL $OPTS_CONFIG $OPTS_START ${@:2} > logs/console.out 2>&1 &
		echo "Started with pid $!"
		;;
	stop)
		$CMD $1 $OPTS_CONTROL ${@:2}
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
		$CMD $1 $OPTS_CONTROL ${@:2}
		echo $MSG
		RETURN_CODE=$(echo "$MSG" | grep 'Stopped' | wc -l)
		exit $RETURN_CODE
 		;;
	check|test|metadata)
		$CMD $1 $OPTS_CONFIG ${@:2}
	    exit $?
		;;
	*)
		$CMD ${@:1}
	    exit $?
		;;
esac
popd > /dev/null
