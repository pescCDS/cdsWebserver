# EdExchange Directory Server docker configuration.

This README file provide a description of how to get started development work on the Ed Exchange project.

The Ed Exchange project contains 2 modules:  the Directory Server module and the Network Server module.
The Network Server module is the project that vendors and institutions use to exchange documents.  The
Directory Server module is the project that maintains a directory of network servers and facilitates
communication between the network servers.

Both the Network and Directory servers are implmented as Spring Boot applications.  This means that when they
are compiled and packaged, the result is an executable JAR file that can be invoked from the command line like
any other Java executable.

Docker has been utilized to streamline and automate development and testing efforts.

PROJECT STRUCTURE
This section describes the Ed Exchange project with brief descriptions in parentheses.

edex
|-- docker-compose.yml  ( docker compose config intended for QA and integration tests. )
|-- network-server-dev-setup.sh  ( shell script use to prepare for network server development )
|-- docker-compose-network-dev.yml ( docker compose config use with the shell script to setup a dev environment )
|-- directory-server-pilot.template ( cloudformation template - describes AWS infrastructure )
|-- pom.xml (Maven config for Ed Exchange project)
|-- README.md ( this README file )
|-- docker ( Docker related resources )
|   |-- container-data ( container mapped volumes )
|   |   |-- edex-proxy ( mapped proxy/Apache log files )
|   |   |   |-- logs
|   |   |   |   |-- access.log ( Apache access log )
|   |   |   |   |-- error.log  ( Apache error log )
|   |   |   |   |-- other_vhosts_access.log
|   |-- proxy-server-image ( Docker configs for the proxy server used in the QA docker environment )
|   |   |-- 000-default.conf
|   |   |-- Dockerfile
|-- directoryServer ( the code and resources that make up the Ed Exchange Directory Server )
|   |-- pom.xml
|   |-- src
|   |   |-- main
|   |   |   |-- docker ( docker configs for the directory server and the directory server's database )
|   |   |   |   |-- database
|   |   |   |   |   |-- Dockerfile
|   |   |   |   |-- Dockerfile
|   |   |   |   |-- directory-server.sh (/opt/directory-server -- used to start in normal and debug mode, stop and restart the server)
|   |   |   |   |-- redeploy.sh ( /opt/redeploy.sh -- used to redeploy the JAR and restart the server)
|   |   |   |-- java ( Java source code )
|   |-- target (compiled code and resources after "mvn package docker:build")
|-- networkServer
|   |-- pom.xml
|   |-- src
|   |   |-- main
|   |   |   |-- docker
|   |   |   |   |-- database
|   |   |   |   |   |-- Dockerfile
|   |   |   |   |-- Dockerfile
|   |   |   |   |-- network-server.sh
|   |   |   |   |-- redeploy.sh
|   |-- target (compiled code after "mvn package docker:build")



-----------------------------
DEVELOPMENT PREREQUISITES
-----------------------------
Linux or MAC OS.  Windows will also work but windows build scripts have not been created yet.
Java
Docker
Docker compose

For the integration tests, TCP must be enabled for the docker daemon.
https://docs.docker.com/engine/reference/commandline/daemon/
You can verify that the docker daemon is operating over TCP by browsing to http://localhsot:2375/images/json


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

The output shows that several docker containers are running in the dev environment:

"network-db" is the database server that is used by a "test" network server intended to be used to test document
exchange.
"network-app" is the network server application intended to be used to test document exchange.
"directory-db" and "directory-app" provide a running directory server.
"dev-network-db" is a database server intended to be used by the network server running on the host.  I.e. Your dev
host.  The intent here is that you'll develop your code on your host dev machine and point it at the database server
so that you are not required to install and configure a database instance.

As the output indicates, you can run your local network server on the host machine using the following command:

java -Dspring.profiles.active=dev -Ddb.server=172.17.0.6 -Ddirectory.server=172.17.0.5 -Ddirectory.port=8080 -jar ./target/network-server.jar

or alternatively

./networkServer/target/network-server.jar --spring.profiles.active=dev --db.server=172.17.0.6 --directory.server=172.17.0.5 --directory.port=8080

Note: the network server utilizes Spring's dev tools.  With dev tools, whenever a class in the class path changes,the
application is automatically restarted.  In other words, if you change a class and invoke "mvn compile", your changes
will automatically be deployed to the running server.

Login Credentials: when required to login, the default login is "user" / "password"

------------------------------
DIRECTORY SERVER DEVELOPMENT
------------------------------
To prepare your development environment, invoke "dev-setup.sh" from the directoryServer directory.  The script can
take a while to run on the first execution, so be patient.  This is because the script is compiling the packages and
downloading, building and starting the docker images.  When the script completes, you are ready to begin development.


You should see output similar to the following where the IP address may differ from system to system:

> OK - directory-db is running. IP: 172.17.0.4, StartedAt: 2016-03-15T22:46:14.821677929Z
> You can start the directory server app using the following command:
> java -Dspring.profiles.active=dev -Ddb.server=172.17.0.4 -jar ./target/directory-server.jar
> or use your favorite IDE to develop and run

You can alternatively run the directory server with

./target/directory-server.jar --spring.profiles.active=dev --db.server=172.17.0.4

Login Credentials: when required to login, the default login is "admin" / "password"

----------------------------------
DIRECTORY SERVER PILOT ENVIRONMENT
----------------------------------
Review the Architecture diagram DirectoryServerAWSArchitecture.pn

Prerequisites: 
> An AWS account
> The aws cli configured with the appropriate credentials
> An AWS keypair that will be used to access the environment

This process provisions in AWS, using cloudformation, the following resources:
> A Virtual Private Cloud, its associated network resources, and security groups (VPC)
> 2 Directory Server instances (EC2)
> A Load Balancer (ELB)
> A MariaDb instance (RDS)
> A DNS entry for directory-server-pilot.ccctechcenter.org (Route 53)

Initial stack creation: 
> If desired, modify the command below to supply a different value for the cloudformation template file location, the desired Dns entry (RecordSetName), and the Aws keypair to use (KeyPairName)
```aws cloudformation create-stack --stack-name directory-server-pilot --template-body file:////home/git/cdsWebserver/directory-server-pilot.template --parameters "ParameterKey=RecordSetName,ParameterValue=edex-directory-pilot.ccctechcenter.org.,ParameterKey=KeyPairName,ParameterValue=edExchange_Pilot_Key"```

Stack update: NOTE: this is destructive - it will re-create any instances that have been modified in the template
```aws cloudformation update-stack --stack-name directory-server-pilot --template-body file:////home/git/cdsWebserver/directory-server-pilot.template --parameters "ParameterKey=RecordSetName,ParameterValue=edex-directory-pilot.ccctechcenter.org.,ParameterKey=KeyPairName,ParameterValue=edExchange_Pilot_Key"```

Directory server deployment process (manual steps outlined below. Initially, at CCCTC, our Jenkins server will be utilized to automate this process): 
> On each directory server in the stack:
```git clone https://github.com/owenwe/cdsWebserver```
```mvn -DskipTests -am -pl directoryServer package docker:build```
```profile=pilot DIRECTORY_DB_HOST=<hostname of database> docker-compose -f docker-compose-directory-pilot.yml up -d```

-------------------------------
Full Stack Environment (QA)
-------------------------------

The full stack environment is configured in the docker-compose.yml file. It comprises of a network server, a directory server, and a separate mysql database for each. This environment can be initialized with the following steps. 

Build the prerequisite maven packages and run the docker containers. Supply a valid springboot profile as a single argument to the scripts. If no argument is specified, the default profile "dev" will be used.

```. ./setup.sh [qa|dev]```

These resources can be accessed locally at the following locations:
Network server -- http://localhost:8081/home 
Directory server -- http://localhost:8080/home 

You can stop the environment using the following 

```. ./shutdown.sh [qa|dev]```

TODO: Create automated integration and unit tests to leverage the QA Environment.



