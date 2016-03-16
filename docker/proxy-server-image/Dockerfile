# Dockerfile to build the EdExchange proxy server

From debian:8.3
MAINTAINER Wes Owen <wowen@ccctechcenter.org>
LABEL Description="This image is used to build the EdExchange proxy server. It must be used in conjunction with the app server image and the database image using docker composse"

ENV JAVA_HOME /usr

RUN apt-get -y update && apt-get install -y apache2
RUN apt-get clean

# adds the ServerName value to the apache config
RUN echo "ServerName EdExchange" >> /etc/apache2/apache2.conf

EXPOSE 80 443

# this has the ajp proxy directives in it
ADD ./000-default.conf /etc/apache2/sites-available/000-default.conf

STOPSIGNAL SIGWINCH

RUN a2enmod rewrite
RUN a2enmod proxy_http

CMD /usr/sbin/apache2ctl -D FOREGROUND
