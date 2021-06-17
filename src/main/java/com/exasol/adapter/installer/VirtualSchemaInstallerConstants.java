package com.exasol.adapter.installer;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.exasol.adapter.installer.dialect.Dialect;

public class VirtualSchemaInstallerConstants {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String CREDENTIALS_FILE_KEY = "credentials_file";
    public static final String CREDENTIALS_FILE_DESCRIPTION = "Path to the file where credentials are stored";
    public static final String CREDENTIALS_FILE_DEFAULT = FILE_SEPARATOR + ".virtual-schema-installer" + FILE_SEPARATOR
            + "credentials";

    // Credentials properties
    public static final String EXASOL_USERNAME_KEY = "exasol_username";
    public static final String EXASOL_PASSWORD_KEY = "exasol_password";
    public static final String EXASOL_BUCKET_WRITE_PASSWORD_KEY = "exasol_bucket_write_password";
    public static final String SOURCE_USERNAME_KEY = "source_username";
    public static final String SOURCE_PASSWORD_KEY = "source_password";

    // User input
    public static final String DIALECT_KEY = "dialect";
    public static final String DIALECT_DESCRIPTION = "Select one of the supported dialects: " + Arrays
            .stream(Dialect.values()).map(dialect -> dialect.toString().toLowerCase()).collect(Collectors.joining(","));

    public static final String JDBC_DRIVER_NAME_KEY = "jdbc-driver-name";
    public static final String JDBC_DRIVER_NAME_DEFAULT = "postgresql.jar";
    public static final String JDBC_DRIVER_NAME_DESCRIPTION = "Name of the PostgreSQL JDBC driver file";

    public static final String JDBC_DRIVER_PATH_KEY = "jdbc-driver-path";
    public static final String JDBC_DRIVER_PATH_DEFAULT = "";
    public static final String JDBC_DRIVER_PATH_DESCRIPTION = "Path to the PostgreSQL JDBC driver file";

    public static final String EXA_IP_KEY = "exa-ip";
    public static final String EXA_IP_DEFAULT = "localhost";
    public static final String EXA_IP_DESCRIPTION = "An IP address to connect to the Exasol database";

    public static final String EXA_PORT_KEY = "exa-port";
    public static final String EXA_PORT_DEFAULT = "8563";
    public static final String EXA_PORT_DESCRIPTION = "A port on which the Exasol database is listening";

    public static final String EXA_BUCKET_FS_PORT_KEY = "exa-bucketfs-port";
    public static final String EXA_BUCKET_FS_PORT_DEFAULT = "2580";
    public static final String EXA_BUCKET_FS_PORT_DESCRIPTION = "A port on which BucketFS is listening";

    public static final String EXA_BUCKET_NAME_KEY = "exa-bucket-name";
    public static final String EXA_BUCKET_NAME_DEFAULT = "default";
    public static final String EXA_BUCKET_NAME_DESCRIPTION = "A bucket name to upload jars";

    public static final String EXA_SCHEMA_NAME_KEY = "exa-schema-name";
    public static final String EXA_SCHEMA_NAME_DEFAULT = "ADAPTER";
    public static final String EXA_SCHEMA_NAME_DESCRIPTION = "A name for an Exasol schema that holds the adapter script";

    public static final String EXA_ADAPTER_NAME_KEY = "exa-adapter-name";
    public static final String EXA_ADAPTER_NAME_DEFAULT = "dialect name + _ADAPTER_SCRIPT";
    public static final String EXA_ADAPTER_NAME_DESCRIPTION = "A name for an Exasol adapter script";

    public static final String EXA_CONNECTION_NAME_KEY = "exa-connection-name";
    public static final String EXA_CONNECTION_NAME_DEFAULT = "dialect name + _JDBC_CONNECTION";
    public static final String EXA_CONNECTION_NAME_DESCRIPTION = "A name for an Exasol connection to the source database";

    public static final String EXA_VIRTUAL_SCHEMA_NAME_KEY = "exa-virtual-schema-name";
    public static final String EXA_VIRTUAL_SCHEMA_NAME_DEFAULT = "dialect name + _VIRTUAL_SCHEMA";
    public static final String EXA_VIRTUAL_SCHEMA_NAME_DESCRIPTION = "A name for a virtual schema";

    public static final String SOURCE_IP_KEY = "source-ip";
    public static final String SOURCE_IP_DEFAULT = "localhost";
    public static final String SOURCE_IP_DESCRIPTION = "An IP address to connect to the source database";

    public static final String SOURCE_PORT_KEY = "source-port";
    public static final String SOURCE_PORT_DESCRIPTION = "A port on which the source database is listening";

    public static final String SOURCE_DATABASE_NAME_KEY = "source-database-name";
    public static final String SOURCE_DATABASE_NAME_DESCRIPTION = "A source database name to connect to";

    public static final String SOURCE_MAPPED_SCHEMA_KEY = "source-mapped-schema";
    public static final String SOURCE_MAPPED_SCHEMA_DEFAULT = "";
    public static final String SOURCE_MAPPED_SCHEMA_DESCRIPTION = "A source schema to map in Virtual Schema";

    public static final String ADDITIONAL_PROPERTY_KEY = "property";
    public static final String[] ADDITIONAL_PROPERTIES_DEFAULT = new String[0];
    public static final String HELP_KEY = "help";
    public static final String DIALECT_SPECIFIC_DEFAULT = "Depends on a dialect";
}