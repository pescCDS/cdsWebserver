# Dockerfile to build the EdExchange Directory Server

# docker build -t wowen/directory_server --no-cache .
# docker run -d -p 80:80 -p 3306:3306 --name edex-ds -v //opt/projects/cccdev/cdsWebserver/:/opt/edexchange wowen/directory_server

From tomcat:7
MAINTAINER Wes Owen <wowen@ccctechcenter.org>
LABEL Description="This image is used to build the EdExchange Directory Server"

ENV MYSQL_ROOT_PASSWORD admin123
ENV MYSQL_USER ccctc
ENV MYSQL_PASSWORD ccc.dev
ENV JAVA_HOME /usr

RUN apt-get -y update && apt-get install -y apache2

RUN echo "mariadb-server-10.0 mysql-server/root_password password ${MYSQL_ROOT_PASSWORD}" | debconf-set-selections
RUN echo "mariadb-server-10.0 mysql-server/root_password_again password ${MYSQL_ROOT_PASSWORD}" | debconf-set-selections
RUN apt-get install -y mariadb-server-10.0
RUN apt-get clean

# adds the ServerName value to the apache config
RUN echo "ServerName EdExchange" >> /etc/apache2/apache2.conf

RUN /usr/bin/mysqld_safe & \
    sleep 7s &&\
	echo "GRANT ALL ON *.* TO ${MYSQL_USER}@'%' IDENTIFIED BY '${MYSQL_PASSWORD}' WITH GRANT OPTION; FLUSH PRIVILEGES" | mysql -uroot -p${MYSQL_ROOT_PASSWORD}

RUN echo "bind_address = 0.0.0.0" >> /etc/mysql/conf.d/mariadb.cnf

EXPOSE 80
EXPOSE 3306

RUN unset MYSQL_ROOT_PASSWORD
RUN unset MYSQL_USER
RUN unset MYSQL_PASSWORD

# add the virtual directory from the host
RUN mkdir /opt/edexchange

# this has the ajp proxy directives in it
ADD ./000-default.conf /etc/apache2/sites-available/000-default.conf
RUN a2enmod proxy_ajp

# the context file will have the resource connection
ADD ./context.xml /usr/local/tomcat/conf/context.xml

# needed to connect the database
ADD ./mariadb-java-client-1.1.7.jar /usr/local/tomcat/lib/mariadb-java-client-1.1.7.jar

# development scripts
ADD ./tomcat-start.sh /opt/tomcat-start.sh
ADD ./tomcat-stop.sh /opt/tomcat-stop.sh
ADD ./webapp-deploy.sh /opt/webapp-deploy.sh
ADD ./webapp-remove.sh /opt/webapp-remove.sh

# multi-application startup
ADD ./startup.sh /opt/startup.sh

CMD /opt/startup.sh
