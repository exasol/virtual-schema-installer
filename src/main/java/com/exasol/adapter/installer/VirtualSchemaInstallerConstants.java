package com.exasol.adapter.installer;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.exasol.adapter.installer.dialect.Dialect;

/**
 * Virtual Schema installer constants.
 */
public class VirtualSchemaInstallerConstants {
    private VirtualSchemaInstallerConstants() {
    }

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    public static final String CREDENTIALS_FILE_KEY = "credentials_file";
    public static final String CREDENTIALS_FILE_DESCRIPTION = "Path to the file where credentials are stored";

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
    public static final String JDBC_DRIVER_NAME_DESCRIPTION = "Name of the source JDBC driver file";

    public static final String JDBC_DRIVER_PATH_KEY = "jdbc-driver-path";
    public static final String JDBC_DRIVER_PATH_DESCRIPTION = "Path to the source JDBC driver file";

    public static final String EXA_IP_KEY = "exa-ip";
    public static final String EXA_IP_DESCRIPTION = "An IP address to connect to the Exasol database";

    public static final String EXA_PORT_KEY = "exa-port";
    public static final String EXA_PORT_DESCRIPTION = "A port on which the Exasol database is listening";

    public static final String EXA_BUCKET_FS_PORT_KEY = "exa-bucketfs-port";
    public static final String EXA_BUCKET_FS_PORT_DESCRIPTION = "A port on which BucketFS is listening";

    public static final String EXA_SCHEMA_NAME_KEY = "exa-schema-name";
    public static final String EXA_SCHEMA_NAME_DESCRIPTION = "A name for an Exasol schema that holds the adapter script";

    public static final String EXA_ADAPTER_NAME_KEY = "exa-adapter-name";
    public static final String EXA_ADAPTER_NAME_DESCRIPTION = "A name for an Exasol adapter script";

    public static final String EXA_CONNECTION_NAME_KEY = "exa-connection-name";
    public static final String EXA_CONNECTION_NAME_DESCRIPTION = "A name for an Exasol connection to the source database";

    public static final String EXA_VIRTUAL_SCHEMA_NAME_KEY = "exa-virtual-schema-name";
    public static final String EXA_VIRTUAL_SCHEMA_NAME_DESCRIPTION = "A name for a virtual schema";

    public static final String SOURCE_IP_KEY = "source-ip";
    public static final String SOURCE_IP_DESCRIPTION = "An IP address to connect to the source database";

    public static final String SOURCE_PORT_KEY = "source-port";
    public static final String SOURCE_PORT_DESCRIPTION = "A port on which the source database is listening";

    public static final String ADDITIONAL_PROPERTY_KEY = "property";
    public static final String HELP_KEY = "help";
}