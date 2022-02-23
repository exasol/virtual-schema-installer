# Virtual Schema Installer 0.1.0, released 2021-??-??

Code name: Initial implementation

## Summary 

This CLI tools helps you to install Virtual Schemas. Currently supported dialects:

* DB2
* ElasticSearch
* Microsoft SQL Server
* MySQL
* Oracle
* PostgreSQL

## Features

* #1: Create initial implementation for Postgres VS dialect.
* #7: Added support for MySQL Virtual Schema installation.
* #17: Added new dialects: DB2, ElasticSearch, Microsoft SQL Server, Oracle
* #25: Added support for TLS certificates when connecting to Exasol.

## Refactoring

* #3: Created an interface for obtaining JAR files.
* #4: Validated user input.
* #9: Added cache check for downloaded JAR files.
* #14: Improved user input validation.
* #23: Updated dependencies.

## Documentation

* #5: Added user guide.

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:bucketfs-java:2.3.0`
* Added `com.exasol:error-reporting-java:0.4.1`
* Added `com.exasol:exasol-jdbc:7.1.4`
* Added `commons-cli:commons-cli:1.5.0`
* Added `org.kohsuke:github-api:1.301`
* Added `org.postgresql:postgresql:42.3.3`

### Runtime Dependency Updates

* Added `org.jacoco:org.jacoco.agent:0.8.7`

### Test Dependency Updates

* Added `com.exasol:exasol-testcontainers:6.1.1`
* Added `com.exasol:hamcrest-resultset-matcher:1.5.1`
* Added `com.ibm.db2:jcc:11.5.7.0`
* Added `com.microsoft.sqlserver:mssql-jdbc:10.2.0.jre8`
* Added `com.oracle.ojdbc:ojdbc8:19.3.0.0`
* Added `mysql:mysql-connector-java:8.0.28`
* Added `org.apache.httpcomponents:httpclient:4.5.13`
* Added `org.apache.logging.log4j:log4j-api:2.17.1`
* Added `org.elasticsearch.client:elasticsearch-rest-high-level-client:7.16.1`
* Added `org.elasticsearch.plugin:x-pack-sql-jdbc:7.16.1`
* Added `org.hamcrest:hamcrest:2.2`
* Added `org.junit.jupiter:junit-jupiter:5.8.2`
* Added `org.testcontainers:db2:1.16.2`
* Added `org.testcontainers:elasticsearch:1.16.2`
* Added `org.testcontainers:junit-jupiter:1.16.2`
* Added `org.testcontainers:mssqlserver:1.16.2`
* Added `org.testcontainers:mysql:1.16.2`
* Added `org.testcontainers:oracle-xe:1.16.2`
* Added `org.testcontainers:postgresql:1.16.2`

### Plugin Dependency Updates

* Added `com.exasol:artifact-reference-checker-maven-plugin:0.4.0`
* Added `com.exasol:error-code-crawler-maven-plugin:0.7.1`
* Added `com.exasol:project-keeper-maven-plugin:1.3.4`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.14`
* Added `org.apache.maven.plugins:maven-assembly-plugin:3.3.0`
* Added `org.apache.maven.plugins:maven-clean-plugin:2.5`
* Added `org.apache.maven.plugins:maven-compiler-plugin:3.8.1`
* Added `org.apache.maven.plugins:maven-dependency-plugin:2.8`
* Added `org.apache.maven.plugins:maven-deploy-plugin:2.7`
* Added `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3`
* Added `org.apache.maven.plugins:maven-install-plugin:2.4`
* Added `org.apache.maven.plugins:maven-jar-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-resources-plugin:2.6`
* Added `org.apache.maven.plugins:maven-site-plugin:3.3`
* Added `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3`
* Added `org.codehaus.mojo:versions-maven-plugin:2.8.1`
* Added `org.jacoco:jacoco-maven-plugin:0.8.7`
* Added `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0`
