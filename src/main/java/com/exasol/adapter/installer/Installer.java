package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.LINE_SEPARATOR;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

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
    private final VirtualSchemaJarProvider virtualSchemaJarProvider;
    private final JdbcDriverJarProvider jdbcDriverJarProvider;

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

    /**
     * Install Postgres Virtual Schema to the Exasol database.
     */
    public void install() throws SQLException, BucketAccessException, TimeoutException, FileNotFoundException {
        final Jar virtualSchemaJar = this.virtualSchemaJarProvider.provideJar();
        final Jar jdbcDriverJar = this.jdbcDriverJarProvider.provideJar();
        uploadFilesToBucket(virtualSchemaJar, jdbcDriverJar);
        try (final Connection connection = getConnection(); final Statement statement = connection.createStatement()) {
            installVirtualSchema(statement, List.of(virtualSchemaJar.getJarName(), jdbcDriverJar.getJarName()));
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:exa:" + this.exaIp + ":" + this.exaPort, this.exaUsername,
                this.exaPassword);
    }

    private void uploadFilesToBucket(final Jar virtualSchemaJar, final Jar driverJar)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final WriteEnabledBucket bucket = getBucket();
        uploadVsJarToBucket(bucket, virtualSchemaJar);
        uploadDriverToBucket(bucket, driverJar);
    }

    private WriteEnabledBucket getBucket() {
        return WriteEnabledBucket.builder()//
                .ipAddress(this.exaIp) //
                .httpPort(this.exaBucketFsPort) //
                .name(this.exaBucketName) //
                .writePassword(this.exaBucketWritePassword) //
                .build();
    }

    private void installVirtualSchema(final Statement statement, final List<String> jarsInBucket) throws SQLException {
        createSchema(statement);
        createAdapterScript(statement, jarsInBucket);
        createConnection(statement);
        createVirtualSchema(statement);
    }

    private void createSchema(final Statement statement) throws SQLException {
        statement.execute("CREATE SCHEMA IF NOT EXISTS " + this.exaSchemaName);
    }

    private void createAdapterScript(final Statement statement, final List<String> jarsInBucket) throws SQLException {
        final String createAdapterScriptStatement = prepareAdapterScriptStatement(jarsInBucket);
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

    private String prepareAdapterScriptStatement(final List<String> jarsInBucket) {
        final StringBuilder adapterScript = new StringBuilder( //
                "CREATE OR REPLACE JAVA ADAPTER SCRIPT " + this.exaSchemaName + "." + this.exaAdapterName + " AS"
                        + LINE_SEPARATOR //
                        + "  %scriptclass com.exasol.adapter.RequestDispatcher;" + LINE_SEPARATOR //
        );
        for (final String jarName : jarsInBucket) {
            adapterScript.append("%jar /buckets/bfsdefault/").append(this.exaBucketName).append("/").append(jarName)
                    .append(";").append(LINE_SEPARATOR);
        }
        return adapterScript.toString();
    }

    private void uploadVsJarToBucket(final WriteEnabledBucket bucket, final Jar virtualSchemaJar)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(virtualSchemaJar.getJarPath(), virtualSchemaJar.getJarName());
    }

    private void uploadDriverToBucket(final WriteEnabledBucket bucket, final Jar jdbcDriverJar)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(jdbcDriverJar.getJarPath(), jdbcDriverJar.getJarName());
    }
}