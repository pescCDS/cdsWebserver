#!/bin/bash
/usr/local/network-server.sh stop
cp /opt/edexchange/network-server.jar /usr/local/network-server.jar
/usr/local/network-server.sh restart

