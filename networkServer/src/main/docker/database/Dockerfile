From debian:8.3
MAINTAINER Wes Owen <wowen@ccctechcenter.org>
LABEL Description="This image is used to build the EdExchange Directory database server"

ENV MYSQL_ROOT_PASSWORD admin123
ENV MYSQL_USER ccctc
ENV MYSQL_PASSWORD ccc.dev

RUN apt-get -y update

RUN echo "mariadb-server-10.0 mysql-server/root_password password ${MYSQL_ROOT_PASSWORD}" | debconf-set-selections
RUN echo "mariadb-server-10.0 mysql-server/root_password_again password ${MYSQL_ROOT_PASSWORD}" | debconf-set-selections
RUN apt-get install -y mariadb-server-10.0
RUN apt-get clean

RUN /usr/bin/mysqld_safe & \
    sleep 7s &&\
	echo "GRANT ALL ON *.* TO ${MYSQL_USER}@'%' IDENTIFIED BY '${MYSQL_PASSWORD}' WITH GRANT OPTION; FLUSH PRIVILEGES" | mysql -uroot -p${MYSQL_ROOT_PASSWORD}

RUN echo "bind_address = 0.0.0.0" >> /etc/mysql/conf.d/mariadb.cnf

RUN unset MYSQL_ROOT_PASSWORD
RUN unset MYSQL_USER
RUN unset MYSQL_PASSWORD

# if this isn't set then I get an error when I try to use mysql in the container
ENV TERM xterm-256color

EXPOSE 3306
CMD exec mysqld_safe

