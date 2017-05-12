# EDExchange Directory Server Docker configuration.

This README file provide a description of EDExchange and how to start development work on the EDExchange project.

EDExchange is a web app that provides a secure network for exchanging documents between organizations. EDExchange is open source, provides web services (SOAP and REST), and allows for customization. By becoming an EDExchange member you can send and receive documents at no cost in a secure and reliable way.

The EDExchange project contains two modules: the Directory Server module and the Network Server module.

The Directory Server module is the directory of Network Servers and facilitates communication between the Network Servers. The "production" Directory Server is maintained by the PESC organization (Postsecondary Electronic Standards Council). The Directory Server module code provides a reference Directory Server for your local testing purposes.

The Network Server module is used by vendors and institutions to exchange documents. While you can elect to only send, or both send and receive documents, in either case you can use the Network Server module code to test these transactions. It is intended that you will use the Network Server module in one of the following ways:

* in production as is
* as a reference for customizing or extending your own Network Server
* as a model for creating your own Network Server from scratch

The EDExchange API can be accessed by Both SOAP and a REST-based clients. See the Docs link here for more information: https://edex-directory-pilot.ccctechcenter.org/home#/home

NOTE: If you want to receive documents you must host a Network Server on the Internet using a domain name of your choosing (E.g. edexchange.butte.edu) in addition to using the Network Server module code.  

Both the Network Server and the Directory Server are implemented as Spring Boot applications. This means that when they are compiled and packaged, the result is an executable JAR file that can be invoked from the command line like
any other Java executable. Docker is used to streamline and automate development and testing efforts.

PROJECT STRUCTURE
This section describes the EDExchange project with brief descriptions in parentheses.
<pre>
edex<br>
|-- docker-compose.yml  ( docker compose config intended for QA and integration tests. )<br>
|-- network-server-dev-setup.sh  ( shell script use to prepare for network server development )<br>
|-- docker-compose-network-dev.yml ( docker compose config use with the shell script to setup a dev environment )<br>
|-- directory-server-pilot.template ( Cloud Formation template - describes AWS infrastructure )<br>
|-- pom.xml (Maven config for EDExchange project)<br>
|-- README.md ( This README file )<br>
|-- docker ( Docker related resources )<br>
|   |-- container-data ( container mapped volumes )<br>
|   |   |-- edex-proxy ( mapped proxy/Apache log files )<br>
|   |   |   |-- logs<br>
|   |   |   |   |-- access.log ( Apache access log )<br>
|   |   |   |   |-- error.log  ( Apache error log )<br>
|   |   |   |   |-- other_vhosts_access.log<br>
|   |-- proxy-server-image ( Docker configs for the proxy server used in the QA docker environment )<br>
|   |   |-- 000-default.conf<br>
|   |   |-- Dockerfile<br>
|-- directoryServer ( the code and resources that make up the EDExchange Directory Server )<br>
|   |-- pom.xml<br>
|   |-- src<br>
|   |   |-- main<br>
|   |   |   |-- docker ( docker configs for the directory server and the directory server's database )<br>
|   |   |   |   |-- database<br>
|   |   |   |   |   |-- Dockerfile<br>
|   |   |   |   |-- Dockerfile<br>
|   |   |   |   |-- directory-server.sh (/opt/directory-server -- used to start in normal and debug mode, stop and restart the server)<br>
|   |   |   |   |-- redeploy.sh ( /opt/redeploy.sh -- used to redeploy the JAR and restart the server)<br>
|   |   |   |-- java ( Java source code )<br>
|   |-- target (compiled code and resources after "mvn package docker:build")<br>
|-- networkServer<br>
|   |-- pom.xml<br>
|   |-- src<br>
|   |   |-- main<br>
|   |   |   |-- docker<br>
|   |   |   |   |-- database<br>
|   |   |   |   |   |-- Dockerfile<br>
|   |   |   |   |-- Dockerfile<br>
|   |   |   |   |-- network-server.sh<br>
|   |   |   |   |-- redeploy.sh<br>
|   |-- target (compiled code after "mvn package docker:build")<br>
</pre>


-----------------------------
DEVELOPMENT PREREQUISITES
-----------------------------
Linux or MAC OS.  Windows will also work but Windows build scripts have not been created yet.
Java
Docker
Docker Compose

For the integration tests, TCP must be enabled for the Docker daemon.
https://docs.docker.com/engine/reference/commandline/daemon/
You can verify that the Docker daemon is operating over TCP by browsing to http://localhsot:2375/images/json


-----------------------------
NETWORK SERVER DEVELOPMENT
-----------------------------
To prepare you development environment, invoke "network-server-dev-setup.sh" from the root directory.  The script can
take a while to run on the first execution, so be patient.  This is because the script is compiling the packages and
downloading, building and starting the docker images.  When the script completes, you are ready to begin development.

You should see output similar to the following where the IP address may differ from system to system:

> OK - network-db is running. IP: 172.17.0.2, StartedAt: 2016-03-15T22:46:14.341949823Z
> OK - network-app is running. IP: 172.17.0.3, StartedAt: 2016-03-15T22:46:14.553862761Z
> OK - directory-db is running. IP: 172.17.0.4, StartedAt: 2016-03-15T22:46:14.821677929Z
> OK - directory-app is running. IP: 172.17.0.5, StartedAt: 2016-03-15T22:46:18.158739391Z
> OK - dev-network-db is running. IP: 172.17.0.6, StartedAt: 2016-03-15T22:46:18.61340071Z
> You can start your development network server app using the following command:
> java -Dspring.profiles.active=dev -Ddb.server=172.17.0.6 -Ddirectory.server=172.17.0.5 -Ddirectory.port=8080 -jar ./target/network-server.jar
> or use your favorite IDE to develop and run
> A reference network server is running at 172.17.0.3 on port 8080

The output shows that several Docker containers are running in the dev environment:

"network-db" is the database server that is used by a "test" Network Server intended to be used to test document
exchange.
"network-app" is the Network Server application intended to be used to test document exchange.
"directory-db" and "directory-app" provide a running Directory Server.
"dev-network-db" is a database server intended to be used by the Network Server running on the host, i.e. your dev
host. The intent here is that you'll develop your code on your host dev machine and point it at the database server
so that you are not required to install and configure a database instance.

As the output indicates, you can run your local Network Server on the host machine using the following command:

java -Dspring.profiles.active=dev -Ddb.server=172.17.0.6 -Ddirectory.server=172.17.0.5 -Ddirectory.port=8080 -jar ./target/network-server.jar

Or, alternatively:

./networkServer/target/network-server.jar --spring.profiles.active=dev --db.server=172.17.0.6 --directory.server=172.17.0.5 --directory.port=8080

Note: The Network Server uses Spring's dev tools. With dev tools, whenever a class in the class path changes,the
application is automatically restarted. In other words, if you change a class and invoke "mvn compile", your changes
will automatically be deployed to the running server.

Login Credentials: When required to login, the default login is "admin" / "admin".

------------------------------
DIRECTORY SERVER DEVELOPMENT
------------------------------
To prepare your development environment, invoke "dev-setup.sh" from the cdsWebserver/directoryServer. The script can
take a while to run on the first execution, so be patient. This is because the script is compiling the packages and
downloading, building and starting the Docker images.  When the script completes, you are ready to begin development.

You should see output similar to the following where the IP address may differ from system to system:

> OK - directory-db is running. IP: 172.17.0.4, StartedAt: 2016-03-15T22:46:14.821677929Z
> You can start the directory server app using the following command:
> java -Dspring.profiles.active=dev -Ddb.server=172.17.0.4 -jar ./target/directory-server.jar
> or use your favorite IDE to develop and run

You can alternatively run the Directory Server with this command:

./target/directory-server.jar --spring.profiles.active=dev --db.server=172.17.0.4

Login Credentials: When required to login, the default login is "admin" / "password".

----------------------------------
DIRECTORY SERVER PILOT ENVIRONMENT
----------------------------------
Review the Architecture diagram DirectoryServerAWSArchitecture.png in the cdsWebserver directory.

Prerequisites:
> An AWS account
> The AWS CLI configured with the appropriate credentials
> An AWS keypair that will be used to access the environment

This process provisions in AWS, using Cloud Formation, the following resources:
> A Virtual Private Cloud, its associated network resources, and security groups (VPC)
> 2 Directory Server instances (EC2)
> A Load Balancer (ELB)
> A MariaDB instance (RDS)
> A DNS entry for directory-server-pilot.ccctechcenter.org (Route 53)

Initial stack creation:
> If desired, modify the command below to supply a different value for the AWS CloudFormation template file location, the desired DNS entry (RecordSetName), and the AWS keypair to use (KeyPairName)
```aws cloudformation create-stack --stack-name directory-server-pilot --template-body file:////home/git/cdsWebserver/directory-server-pilot.template --parameters "ParameterKey=RecordSetName,ParameterValue=edex-directory-pilot.ccctechcenter.org.,ParameterKey=KeyPairName,ParameterValue=edExchange_Pilot_Key"```

Stack update: NOTE: This is destructive - it will re-create any instances that have been modified in the template
```aws cloudformation update-stack --stack-name directory-server-pilot --template-body file:////home/git/cdsWebserver/directory-server-pilot.template --parameters "ParameterKey=RecordSetName,ParameterValue=edex-directory-pilot.ccctechcenter.org.,ParameterKey=KeyPairName,ParameterValue=edExchange_Pilot_Key"```

Directory server deployment process (manual steps outlined below. Initially, at CCCTC, our Jenkins server will be utilized to automate this process): 
> On each directory server in the stack:
```git clone https://github.com/jhwhetstone/cdsWebserver```
```mvn -DskipTests -am -pl directoryServer package docker:build```
```profile=pilot DIRECTORY_DB_HOST=<hostname of database> docker-compose -f docker-compose-directory-pilot.yml up -d```

-------------------------------
Full Stack Environment (QA)
-------------------------------

The full stack environment is configured in the docker-compose.yml file. It comprises of a Network Server, a Directory Server, and a separate MySQL database for each. Initialize this environment using the steps below.

Build the prerequisite Maven packages and run the Docker containers. Supply a valid Spring Boot profile as a single argument to the scripts. If no argument is specified, the default profile "dev" is used.

```. ./setup.sh [qa|dev]```

These resources can be accessed locally at the following locations:
Network Server -- http://localhost:8081/home
Directory Server -- http://localhost:8080/home

Stop the environment using the following command:

```. ./shutdown.sh [qa|dev]```

TODO: Create automated integration and unit tests to leverage the QA Environment.
