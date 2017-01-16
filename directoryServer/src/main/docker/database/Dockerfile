FROM postgres

# expose the 5432 port to outside the container
EXPOSE 5432

# Add the initdb script where the default postgres images looks for it.
ADD initdb.sql /docker-entrypoint-initdb.d/initdb.sql
