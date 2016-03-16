#!/bin/bash
/usr/local/directory-server.sh stop
cp /opt/edexchange/directory-server.jar /usr/local/directory-server.jar
/usr/local/directory-server.sh start
