FROM java:8

# add the virtual directory from the host
RUN mkdir /opt/edexchange

# development script to stop the app, copy the new jar to /usr/local and restart
ADD ./redeploy.sh /opt/redeploy.sh

# copy the app and automated startup script
ADD ./directory-server.jar /usr/local/directory-server.jar
ADD ./directory-server.sh /usr/local/directory-server.sh

RUN chmod 755 /usr/local/directory-server.sh

CMD /usr/local/directory-server.sh start && tail -f /dev/null
