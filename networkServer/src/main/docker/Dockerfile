FROM openjdk:8

RUN mkdir /opt/edexchange

# development script to stop the app, redeploy the jar and restart
ADD ./redeploy.sh /opt/redeploy.sh

# application and startup script
ADD ./network-server.jar /usr/local/network-server.jar
ADD ./network-server.sh /usr/local/network-server.sh

RUN chmod 755 /usr/local/network-server.sh

CMD /usr/local/network-server.sh start && tail -f /dev/null
