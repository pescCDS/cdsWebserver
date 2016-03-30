#!/bin/bash
mvn -DskipTests package docker:build

IMAGE=directoryserver_db_image
CONTAINER=directory-db

if [[ "$(docker images -q $IMAGE 2> /dev/null)" == "" ]]; then
  docker build -t directoryserver_db_image --no-cache ./src/main/docker/database
fi


RUNNING=$(docker inspect --format="{{ .State.Running }}" $CONTAINER 2> /dev/null)

if [ $? -eq 1 ]; then
  echo "$CONTAINER does not exist, creating now..."
  docker run -d --name directory-db directoryserver_db_image
else
  docker start directory-db
fi

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" $CONTAINER)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" $CONTAINER)

echo "OK - $CONTAINER is running. IP: $NETWORK, StartedAt: $STARTED"

if [[ $1 == Start ]]; then
  echo "Starting directory server application..."
  java -Dspring.profiles.active=dev -Ddb.server=$NETWORK -jar ./target/directory-server.jar
else
  echo "You can start the directory server app using the following command: "
  echo "java -Dspring.profiles.active=dev -Ddb.server=$NETWORK -jar ./target/directory-server.jar"
  echo "or use your favorite IDE to develop and run"
fi

