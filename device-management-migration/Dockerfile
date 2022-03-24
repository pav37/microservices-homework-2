FROM liquibase/liquibase:3.10.3

ADD db /liquibase/changelog
CMD ["sh", "-c", "docker-entrypoint.sh --url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} --username=${MIGRATION_DB_USER} --password=${MIGRATION_DB_PASSWORD} --classpath=/liquibase/changelog --changeLogFile=Changelog.xml update"]