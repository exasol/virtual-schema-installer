package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;
import static java.lang.Integer.parseInt;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.apache.commons.cli.ParseException;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.WriteEnabledBucket;

import lombok.Builder;

/**
 * This class contains Postgres Virtual Schema installation logic.
 */
@Builder
public class Installer {
    private static final Logger LOGGER = Logger.getLogger(Installer.class.getName());

    // Files related fields
    private final String virtualSchemaJarName;
    private final String virtualSchemaJarPath;
    private final String jdbcDriverName;
    private final String jdbcDriverPath;

    // Credentials
    private final String exaUsername;
    private final String exaPassword;
    private final String exaBucketWritePassword;
    private final String postgresUsername;
    private final String postgresPassword;

    // Exasol related fields
    private final String exaIp;
    private final int exaPort;
    private final int exaBucketFsPort;
    private final String exaBucketName;
    private final String exaSchemaName;
    private final String exaAdapterName;
    private final String exaConnectionName;
    private final String exaVirtualSchemaName;

    // Postgres related fields
    private final String postgresIp;
    private final String postgresPort;
    private final String postgresDatabaseName;
    private final String postgresMappedSchema;

    private final String[] additionalProperties;

    private Path getVirtualSchemaPath() {
        return Path.of(this.virtualSchemaJarPath, this.virtualSchemaJarName);
    }

    private Path getJdbcDriverPath() {
        return Path.of(this.jdbcDriverPath, this.jdbcDriverName);
    }

    /**
     * Install Postgres Virtual Schema to the Exasol database.
     */
    public void install() throws SQLException, BucketAccessException, TimeoutException, FileNotFoundException {
        uploadFilesToBucket();
        try (final Connection connection = DriverManager.getConnection("jdbc:exa:" + this.exaIp + ":" + this.exaPort,
                this.exaUsername, this.exaPassword); final Statement statement = connection.createStatement()) {
            installVirtualSchema(statement);
        }
    }

    private void uploadFilesToBucket() throws BucketAccessException, TimeoutException, FileNotFoundException {
        final WriteEnabledBucket bucket = getBucket();
        uploadVsJarToBucket(bucket);
        uploadDriverToBucket(bucket);
    }

    private WriteEnabledBucket getBucket() {
        return WriteEnabledBucket.builder()//
                .ipAddress(this.exaIp) //
                .httpPort(this.exaBucketFsPort) //
                .name(this.exaBucketName) //
                .writePassword(this.exaBucketWritePassword) //
                .build();
    }

    private void installVirtualSchema(final Statement statement) throws SQLException {
        createSchema(statement);
        createAdapterScript(statement);
        createConnection(statement);
        createVirtualSchema(statement);
    }

    private void createSchema(final Statement statement) throws SQLException {
        statement.execute("CREATE SCHEMA IF NOT EXISTS " + this.exaSchemaName);
    }

    private void createAdapterScript(final Statement statement) throws SQLException {
        final String createAdapterScriptStatement = prepareAdapterScriptStatement(this.exaSchemaName,
                this.exaAdapterName);
        LOGGER.info(() -> "Installing adapter script with the following command: " + LINE_SEPARATOR
                + createAdapterScriptStatement);
        statement.execute(createAdapterScriptStatement);
    }

    private void createConnection(final Statement statement) throws SQLException {
        final String connectionString = "jdbc:postgresql://" + this.postgresIp + ":" + this.postgresPort + "/"
                + this.postgresDatabaseName;
        LOGGER.info(() -> "Creating connection object with the following connection string: " + LINE_SEPARATOR
                + connectionString);
        statement.execute("CREATE OR REPLACE CONNECTION " + this.exaConnectionName + " TO '" + connectionString
                + "' USER '" + this.postgresUsername + "' IDENTIFIED BY '" + this.postgresPassword + "'");
    }

    private void createVirtualSchema(final Statement statement) throws SQLException {
        final String createVirtualSchemaStatement = prepareVirtualSchemaStatement();
        LOGGER.info(() -> "Installing virtual schema with the following command: " + LINE_SEPARATOR
                + createVirtualSchemaStatement);
        statement.execute(createVirtualSchemaStatement);
    }

    private String prepareVirtualSchemaStatement() {
        final StringBuilder createVirtualSchemaStatement = new StringBuilder(
                "CREATE VIRTUAL SCHEMA " + this.exaVirtualSchemaName + LINE_SEPARATOR //
                        + "USING " + this.exaSchemaName + "." + this.exaAdapterName + LINE_SEPARATOR //
                        + "WITH" + LINE_SEPARATOR //
                        + "SCHEMA_NAME = '" + this.postgresMappedSchema + "'" + LINE_SEPARATOR //
                        + "CONNECTION_NAME = '" + this.exaConnectionName + "'" + LINE_SEPARATOR);
        for (final String additionalProperty : this.additionalProperties) {
            createVirtualSchemaStatement.append(additionalProperty).append(LINE_SEPARATOR);
        }
        return createVirtualSchemaStatement.toString();
    }

    private String prepareAdapterScriptStatement(final String schemaName, final String adapterName) {
        return "CREATE OR REPLACE JAVA ADAPTER SCRIPT " + schemaName + "." + adapterName + " AS" + LINE_SEPARATOR //
                + "  %scriptclass com.exasol.adapter.RequestDispatcher;" + LINE_SEPARATOR //
                + "%jar /buckets/bfsdefault/" + this.exaBucketName + "/" + this.virtualSchemaJarName + ";"
                + LINE_SEPARATOR //
                + "%jar /buckets/bfsdefault/" + this.exaBucketName + "/" + this.jdbcDriverName + ";";
    }

    private void uploadVsJarToBucket(final WriteEnabledBucket bucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(getVirtualSchemaPath(), this.virtualSchemaJarName);
    }

    private void uploadDriverToBucket(final WriteEnabledBucket bucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(getJdbcDriverPath(), this.jdbcDriverName);
    }

    public static void main(final String[] args)
            throws ParseException, SQLException, BucketAccessException, TimeoutException, FileNotFoundException {
        final Map<String, String> options = new HashMap<>();
        options.put(JDBC_DRIVER_NAME_KEY, getDescription(JDBC_DRIVER_NAME_DESCRIPTION, JDBC_DRIVER_NAME_DEFAULT));
        options.put(JDBC_DRIVER_PATH_KEY, getDescription(JDBC_DRIVER_PATH_DESCRIPTION, "current directory"));
        options.put(EXA_IP_KEY, getDescription(EXA_IP_DESCRIPTION, EXA_IP_DEFAULT));
        options.put(EXA_PORT_KEY, getDescription(EXA_PORT_DESCRIPTION, EXA_PORT_DEFAULT));
        options.put(EXA_BUCKET_FS_PORT_KEY, getDescription(EXA_BUCKET_FS_PORT_DESCRIPTION, EXA_BUCKET_FS_PORT_DEFAULT));
        options.put(EXA_BUCKET_NAME_KEY, getDescription(EXA_BUCKET_NAME_DESCRIPTION, EXA_BUCKET_NAME_DEFAULT));
        options.put(EXA_SCHEMA_NAME_KEY, getDescription(EXA_SCHEMA_NAME_DESCRIPTION, EXA_SCHEMA_NAME_DEFAULT));
        options.put(EXA_ADAPTER_NAME_KEY, getDescription(EXA_ADAPTER_NAME_DESCRIPTION, EXA_ADAPTER_NAME_DEFAULT));
        options.put(EXA_CONNECTION_NAME_KEY,
                getDescription(EXA_CONNECTION_NAME_DESCRIPTION, EXA_CONNECTION_NAME_DEFAULT));
        options.put(EXA_VIRTUAL_SCHEMA_NAME_KEY,
                getDescription(EXA_VIRTUAL_SCHEMA_NAME_DESCRIPTION, EXA_VIRTUAL_SCHEMA_NAME_DEFAULT));
        options.put(POSTGRES_IP_KEY, getDescription(POSTGRES_IP_DESCRIPTION, POSTGRES_IP_DEFAULT));
        options.put(POSTGRES_PORT_KEY, getDescription(POSTGRES_PORT_DESCRIPTION, POSTGRES_PORT_DEFAULT));
        options.put(POSTGRES_DATABASE_NAME_KEY,
                getDescription(POSTGRES_DATABASE_NAME_DESCRIPTION, POSTGRES_DATABASE_NAME_DEFAULT));
        options.put(POSTGRES_MAPPED_SCHEMA_KEY, getDescription(POSTGRES_MAPPED_SCHEMA_DESCRIPTION, "no default value"));
        options.put(CREDENTIALS_FILE_KEY, getDescription(CREDENTIALS_FILE_DESCRIPTION, CREDENTIALS_FILE_DEFAULT));

        final UserInput userInput = new UserInputParser().parseUserInput(args, options);
        final VirtualSchemaJarInfo jarInfo = new VirtualSchemaJarManager().downloadJar(userInput.getDialect());
        final Map<String, String> parameters = userInput.getParameters();
        final PropertyReader propertyReader = new PropertyReader(
                getOrDefault(parameters, CREDENTIALS_FILE_KEY, CREDENTIALS_FILE_DEFAULT));
        final Installer installer = Installer.builder() //
                .exaUsername(propertyReader.readProperty(EXASOL_USERNAME_KEY))
                .exaPassword(propertyReader.readProperty(EXASOL_PASSWORD_KEY))
                .exaBucketWritePassword(propertyReader.readProperty(EXASOL_BUCKET_WRITE_PASSWORD_KEY))
                .postgresUsername(propertyReader.readProperty(POSTGRES_USERNAME_KEY))
                .postgresPassword(propertyReader.readProperty(POSTGRES_PASSWORD_KEY)) //
                .virtualSchemaJarName(jarInfo.getJarName()) //
                .virtualSchemaJarPath(jarInfo.getJarPath()) //
                .jdbcDriverName(getOrDefault(parameters, JDBC_DRIVER_NAME_KEY, JDBC_DRIVER_NAME_DEFAULT)) //
                .jdbcDriverPath(getOrDefault(parameters, JDBC_DRIVER_PATH_KEY, JDBC_DRIVER_PATH_DEFAULT)) //
                .exaIp(getOrDefault(parameters, EXA_IP_KEY, EXA_IP_DEFAULT)) //
                .exaPort(parseInt(getOrDefault(parameters, EXA_PORT_KEY, EXA_PORT_DEFAULT))) //
                .exaBucketFsPort(parseInt(getOrDefault(parameters, EXA_BUCKET_FS_PORT_KEY, EXA_BUCKET_FS_PORT_DEFAULT))) //
                .exaBucketName(getOrDefault(parameters, EXA_BUCKET_NAME_KEY, EXA_BUCKET_NAME_DEFAULT)) //
                .exaSchemaName(getOrDefault(parameters, EXA_SCHEMA_NAME_KEY, EXA_SCHEMA_NAME_DEFAULT)) //
                .exaAdapterName(getOrDefault(parameters, EXA_ADAPTER_NAME_KEY, EXA_ADAPTER_NAME_DEFAULT)) //
                .exaConnectionName(getOrDefault(parameters, EXA_CONNECTION_NAME_KEY, EXA_CONNECTION_NAME_DEFAULT)) //
                .exaVirtualSchemaName(
                        getOrDefault(parameters, EXA_VIRTUAL_SCHEMA_NAME_KEY, EXA_VIRTUAL_SCHEMA_NAME_DEFAULT)) //
                .postgresIp(getOrDefault(parameters, POSTGRES_IP_KEY, POSTGRES_IP_DEFAULT)) //
                .postgresPort(getOrDefault(parameters, POSTGRES_PORT_KEY, POSTGRES_PORT_DEFAULT)) //
                .postgresDatabaseName(
                        getOrDefault(parameters, POSTGRES_DATABASE_NAME_KEY, POSTGRES_DATABASE_NAME_DEFAULT)) //
                .postgresMappedSchema(
                        getOrDefault(parameters, POSTGRES_MAPPED_SCHEMA_KEY, POSTGRES_MAPPED_SCHEMA_DEFAULT)) //
                .additionalProperties(getOrDefault(userInput.getAdditionalProperties(), ADDITIONAL_PROPERTIES_DEFAULT)) //
                .build();
        installer.install();
    }

    private static String[] getOrDefault(final String[] additionalProperties,
            final String[] additionalPropertiesDefault) {
        return additionalProperties == null ? additionalPropertiesDefault : additionalProperties;
    }

    private static String getOrDefault(final Map<String, String> userInput, final String key,
            final String defaultValue) {
        if (!userInput.containsKey(key) || userInput.get(key) == null || userInput.get(key).isEmpty()) {
            return defaultValue;
        } else {
            return userInput.get(key);
        }
    }

    private static String getDescription(final String description, final String defaultValue) {
        return description + " (default: " + defaultValue + ").";
    }
}
