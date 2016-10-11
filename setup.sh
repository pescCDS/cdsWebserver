#!/bin/bash

mvn -DskipTests -am -pl networkServer,directoryServer install 

profile=${1:-dev} docker-compose up -d

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" network-db)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" network-db)
echo "OK - network-db is running. Internal IP: $NETWORK, StartedAt: $STARTED"

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" network-app)
NETWORK_SERVER_IP=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" network-app)
echo "OK - network-app is running. Internal IP: $NETWORK_SERVER_IP, StartedAt: $STARTED"

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" directory-db)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" directory-db)
echo "OK - directory-db is running. Internal IP: $NETWORK, StartedAt: $STARTED"

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" directory-app)
DIRECTORY_SERVER_IP=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" directory-app)
echo "OK - directory-app is running. Internal IP: $DIRECTORY_SERVER_IP, StartedAt: $STARTED"

echo "Ed Exchange servers can be accessed with the following URLs: "
echo "Network server   -- http://$(hostname):8081/home "
echo "Directory server -- http://$(hostname):8080/home "

