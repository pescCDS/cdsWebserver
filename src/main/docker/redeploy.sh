#!/bin/bash
docker exec edex-ds //opt/tomcat-stop.sh
docker exec edex-ds //opt/webapp-remove.sh
mvn -Denv=ci -f /d/projects/cccdev/cdsWebserver/ clean package
docker exec edex-ds //opt/webapp-deploy.sh
docker exec edex-ds //opt/tomcat-start.sh
docker exec -ti edex-ds bash -c "tail -f logs/catalina.out"