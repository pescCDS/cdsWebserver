#!/bin/bash

mvn -DskipTests install

docker-compose -f docker-compose-network-dev.yml up -d

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" network-db)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" network-db)
echo "OK - network-db is running. IP: $NETWORK, StartedAt: $STARTED"

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" network-app)
NETWORK_SERVER_IP=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" network-app)
echo "OK - network-app is running. IP: $NETWORK_SERVER_IP, StartedAt: $STARTED"

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" directory-db)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" directory-db)
echo "OK - directory-db is running. IP: $NETWORK, StartedAt: $STARTED"

DIRECTORY_STARTED=$(docker inspect --format="{{ .State.StartedAt }}" directory-app)
DIRECTORY_NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" directory-app)
echo "OK - directory-app is running. IP: $DIRECTORY_NETWORK, StartedAt: $DIRECTORY_STARTED"

STARTED=$(docker inspect --format="{{ .State.StartedAt }}" dev-network-db)
NETWORK=$(docker inspect --format="{{ .NetworkSettings.IPAddress }}" dev-network-db)
echo "OK - dev-network-db is running. IP: $NETWORK, StartedAt: $STARTED"

echo "You can start your development network server app using the following command: "
echo "java -Dspring.profiles.active=dev -Ddb.server=$NETWORK -Ddirectory.server=$DIRECTORY_NETWORK -Ddirectory.port=8080 -jar ./networkServer/target/network-server.jar"
echo "or use your favorite IDE to develop and run"


