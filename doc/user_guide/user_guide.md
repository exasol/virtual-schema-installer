# Virtual Schema Installer User Guide

## Pre-requisites

* Java 11 or later installed.
* Internet connection.
* Exasol and the datasource running.
* Exasol and the datasource can establish a connection (networking and firewall settings)/

## How to Use the Installer

1. Download the latest available release of [Virtual Schema Installer](https://github.com/exasol/virtual-schema-installer/releases)
1. Download a third-party JDBC driver (depending on dialect):
    * [PostgreSQL](https://jdbc.postgresql.org/download.html)
    * [MySQL](https://dev.mysql.com/downloads/connector/j/)
1. (Optional) Create a file `.virtual-schema-installer/credentials` in your home directory to store credentials.
   If the file is missing, the installer will ask you to input credentials via console.
   
    ```
   exasol_username=<exasol username>
   exasol_password=<exasol password>
   exasol_bucket_write_password=<write password of the /buckets/bfsdefault bucket>
   source_username=<username to connect to the datasource>
   source_password=<password to connect to the datasource>
   ```
    
1. Run the installer in terminal:

    ```shell
    java -jar virtual-schema-installer-<version>.jar --dialect <dialect> --<argument_1> <value_1> --<argument_2> <value_2> ...
    ```

### Installer Arguments

The only mandatory argument is `dialect`.

| Argument                   | Description                                              | Default (if presents)                         |
|---------------------------|----------------------------------------------------------|------------------------------------------------|
| --credentials-file        | Path to the file where credentials are stored            | `<HOME>/.virtual-schema-installer/credentials` |
| --dialect                 | Dialect to install                                       |  See table below                               |  
| --exa-bucketfs-port       | A port on which BucketFS is listening                    | `2580`                                         | 
| --exa-host                | A host to connect to the Exasol database                 | `localhost`                                    |
| --exa-port                | A port on which the Exasol database is listening         | `8563`                                         |
| --exa-adapter-name        | Exasol adapter script name                               |  dialect name + `_ADAPTER_SCRIPT`              | 
| --exa-schema-name         | A name for an Exasol schema that holds an adapter script |  `ADAPTER`                                     |
| --exa-virtual-schema-name | Exasol virtual schema name                               |  dialect name + `_VIRTUAL_SCHEMA`              |
| --help                    | Print help.                                              |                                                |
| --jdbc-driver-name        | Name of the source JDBC driver file                      |  Depending on dialect (see table below)        | 
| --jdbc-driver-path        | Path to the source JDBC driver file                      |  Current directory                             | 
| --property                | Virtual Schema property (multiple values allowed)        |                                                | 
| --source-host             | A host to connect to the source database                 | `localhost`                                    | 
| --source-port             | A port on which the source database is listening         |  Depending on dialect (see table below)        |

**Important note**: Please, avoid any spaces when specifying `--property` values. A correct example: `--property CATALOG_NAME='test'`.
Incorrect examples: `--property CATALOG_NAME = 'test'`, `--property TABLE_FILTER='SIMPLE_TABLE, TEST_TABLE'` (these values won't be parsed correctly, and you'll get an error).

### Dialects Default Values

Here you can find names of all supported dialects and dialect-specific default values.

| Dialect    | Default jdbc-driver-name   | Default source-port |
|------------|----------------------------|---------------------|
| POSTGRESQL | `postgresql.jar`           | `5432`              |
| MYSQL      | `mysql-connector-java.jar` | `3306`              |

### Examples

* Installing PostgreSQL Virtual Schema with all default values:
  
  ```shell
  java -jar virtual-schema-installer-<version>.jar --dialect POSTGRESQL --property CATALOG_NAME='test' --property SCHEMA_NAME='postgres_schema'
  ```

* Installing MySQL Virtual Schema with customized arguments:

  ```shell
  java -jar virtual-schema-installer-<version>.jar --dialect mysql --jdbc-driver-name mysql-connector.jar --jdbc-driver-path target/mysql-driver --exa-host localhost --exa-port 32769 --exa-bucketfs-port 32770 --exa-schema-name MY_SCHEMA --exa-adapter-name MY_ADAPTER_SCRIPT --exa-connection-name MY_JDBC_CONNECTION --exa-virtual-schema-name MYSQL_VIRTUAL_SCHEMA_1 --source-host 172.17.0.1 --source-port 32783 --property CATALOG_NAME='MYSQL_SCHEMA' --property TABLE_FILTER='SIMPLE_TABLE' --credentials-file /tmp/installer_credentials
  ```
  
## Things to Be Aware of

* Currently, all files are uploaded to the `/buckets/bfsdefault/drivers/jdbc` bucket directory.
* The installer downloads a virtual Schema Adapter JAR from Exasol GitHub and saves it in the default temporary directory.
* The installer only creates a new Exasol schema if a schema with a specified named doesn't exist.
* The installer creates Exasol schema, adapter, connection and virtual schema names with quotation. It means the names you specify on the console are case-sensitive.
* If a connection with the same name already exists, the installer replaces it.
* If an adapter script with the same name already exists, the installer replaces it.
