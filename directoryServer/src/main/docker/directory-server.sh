#!/bin/sh


SERVICE_NAME=directory-server
PATH_TO_JAR=/usr/local/directory-server.jar
OPTS="-Xmx256m"
PID_PATH_NAME=/tmp/directory-server-pid
DEBUG_ARGS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
case $1 in
    start)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi

        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup java -jar -Ddb-server=$DB_SERVER -Dspring-profiles-active=$SPRING_PROFILES_ACTIVE $OPTS $PATH_TO_JAR & echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    debug)
        echo "Starting $SERVICE_NAME in debug mode..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup java -jar -Ddb-server=$DB_SERVER -Dspring-profiles-active=$SPRING_PROFILES_ACTIVE $DEBUG_ARGS $OPTS $PATH_TO_JAR & echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup java -jar -Ddb-server=$DB_SERVER -Dspring-profiles-active=$SPRING_PROFILES_ACTIVE $OPTS $PATH_TO_JAR & echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac