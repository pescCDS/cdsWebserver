#!/bin/bash
mvn -DskipTests install  

IMAGE=networkserver_db_image
CONTAINER=network-db

if [[ "$(docker images -q $IMAGE 2> /dev/null)" == "" ]]; then
  docker build -t networkserver_db_image --no-cache ./src/main/docker/database
fi


RUNNING=$(docker inspect --format="{{ .State.Running }}" $CONTAINER 2> /dev/null)

if [ $? -eq 1 ]; then
  echo "$CONTAINER does not exist, creating now..."
  docker run -d --name network-db networkserver_db_image
else
  docker start network-db
fi

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" $CONTAINER)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" $CONTAINER)

echo "OK - $CONTAINER is running. IP: $NETWORK, StartedAt: $STARTED"

if [[ $1 == Start ]]; then
  echo "Starting network server application..."
  java -Dspring.profiles.active=dev -Ddb.server=$NETWORK -jar ./target/network-server.jar
else
  echo "You can start the network server app using the following command: "
  echo "java -Dspring.profiles.active=dev -Ddb.server=$NETWORK -jar ./target/network-server.jar"
  echo "or use your favorite IDE to develop and run"
fi

