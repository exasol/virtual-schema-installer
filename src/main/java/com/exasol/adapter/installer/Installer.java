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

/**
 * This class contains Postgres Virtual Schema installation logic.
 */
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

    private Installer(final InstallerBuilder builder) {
        this.exaAdapterName = builder.exaAdapterName;
        this.postgresDatabaseName = builder.postgresDatabaseName;
        this.exaPassword = builder.exaPassword;
        this.exaBucketWritePassword = builder.exaBucketWritePassword;
        this.postgresPort = builder.postgresPort;
        this.postgresUsername = builder.postgresUsername;
        this.virtualSchemaJarProvider = builder.virtualSchemaJarProvider;
        this.exaSchemaName = builder.exaSchemaName;
        this.jdbcDriverJarProvider = builder.jdbcDriverJarProvider;
        this.exaConnectionName = builder.exaConnectionName;
        this.exaBucketFsPort = builder.exaBucketFsPort;
        this.postgresPassword = builder.postgresPassword;
        this.postgresMappedSchema = builder.postgresMappedSchema;
        this.exaIp = builder.exaIp;
        this.postgresIp = builder.postgresIp;
        this.exaUsername = builder.exaUsername;
        this.exaPort = builder.exaPort;
        this.exaBucketName = builder.exaBucketName;
        this.additionalProperties = builder.additionalProperties;
        this.exaVirtualSchemaName = builder.exaVirtualSchemaName;
    }

    /**
     * Install Postgres Virtual Schema to the Exasol database.
     */
    public void install() throws SQLException, BucketAccessException, TimeoutException, FileNotFoundException {
        final JarFile virtualSchemaJarFile = this.virtualSchemaJarProvider.getJar();
        final JarFile jdbcDriverJarFile = this.jdbcDriverJarProvider.getJar();
        uploadFilesToBucket(virtualSchemaJarFile, jdbcDriverJarFile);
        try (final Connection connection = getConnection()) {
            installVirtualSchema(connection, List.of(virtualSchemaJarFile.getName(), jdbcDriverJarFile.getName()));
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:exa:" + this.exaIp + ":" + this.exaPort, this.exaUsername,
                this.exaPassword);
    }

    private void uploadFilesToBucket(final JarFile virtualSchemaJarFile, final JarFile jdbcDriverJarFile)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final WriteEnabledBucket bucket = getBucket();
        uploadVsJarToBucket(bucket, virtualSchemaJarFile);
        uploadDriverToBucket(bucket, jdbcDriverJarFile);
    }

    private WriteEnabledBucket getBucket() {
        return WriteEnabledBucket.builder()//
                .ipAddress(this.exaIp) //
                .httpPort(this.exaBucketFsPort) //
                .name(this.exaBucketName) //
                .writePassword(this.exaBucketWritePassword) //
                .build();
    }

    private void installVirtualSchema(final Connection connection, final List<String> jarNames) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            createSchema(statement);
            createAdapterScript(statement, jarNames);
            createConnection(statement);
            createVirtualSchema(statement);
        }
    }

    private void createSchema(final Statement statement) throws SQLException {
        statement.execute("CREATE SCHEMA IF NOT EXISTS " + this.exaSchemaName);
    }

    private void createAdapterScript(final Statement statement, final List<String> jarNames) throws SQLException {
        final String createAdapterScriptStatement = prepareAdapterScriptStatement(jarNames);
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

    private String prepareAdapterScriptStatement(final List<String> jarNames) {
        final StringBuilder adapterScript = new StringBuilder( //
                "CREATE OR REPLACE JAVA ADAPTER SCRIPT " + this.exaSchemaName + "." + this.exaAdapterName + " AS"
                        + LINE_SEPARATOR //
                        + "  %scriptclass com.exasol.adapter.RequestDispatcher;" + LINE_SEPARATOR //
        );
        for (final String jarName : jarNames) {
            adapterScript.append(getPathInBucketFor(jarName));
        }
        return adapterScript.toString();
    }

    private String getPathInBucketFor(final String jarName) {
        return "%jar /buckets/bfsdefault/" + this.exaBucketName + "/" + jarName + ";" + LINE_SEPARATOR;
    }

    private void uploadVsJarToBucket(final WriteEnabledBucket bucket, final JarFile virtualSchemaJarFile)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(virtualSchemaJarFile.getPath(), virtualSchemaJarFile.getName());
    }

    private void uploadDriverToBucket(final WriteEnabledBucket bucket, final JarFile jdbcDriverJarFile)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(jdbcDriverJarFile.getPath(), jdbcDriverJarFile.getName());
    }

    public static InstallerBuilder builder() {
        return new InstallerBuilder();
    }

    public static final class InstallerBuilder {
        // Files related fields
        private VirtualSchemaJarProvider virtualSchemaJarProvider;
        private JdbcDriverJarProvider jdbcDriverJarProvider;
        // Credentials
        private String exaUsername;
        private String exaPassword;
        private String exaBucketWritePassword;
        private String postgresUsername;
        private String postgresPassword;
        // Exasol related fields
        private String exaIp;
        private int exaPort;
        private int exaBucketFsPort;
        private String exaBucketName;
        private String exaSchemaName;
        private String exaAdapterName;
        private String exaConnectionName;
        private String exaVirtualSchemaName;
        // Postgres related fields
        private String postgresIp;
        private String postgresPort;
        private String postgresDatabaseName;
        private String postgresMappedSchema;
        private String[] additionalProperties;

        private InstallerBuilder() {
        }

        public InstallerBuilder virtualSchemaJarProvider(final VirtualSchemaJarProvider virtualSchemaJarProvider) {
            this.virtualSchemaJarProvider = virtualSchemaJarProvider;
            return this;
        }

        public InstallerBuilder jdbcDriverJarProvider(final JdbcDriverJarProvider jdbcDriverJarProvider) {
            this.jdbcDriverJarProvider = jdbcDriverJarProvider;
            return this;
        }

        public InstallerBuilder exaUsername(final String exaUsername) {
            this.exaUsername = exaUsername;
            return this;
        }

        public InstallerBuilder exaPassword(final String exaPassword) {
            this.exaPassword = exaPassword;
            return this;
        }

        public InstallerBuilder exaBucketWritePassword(final String exaBucketWritePassword) {
            this.exaBucketWritePassword = exaBucketWritePassword;
            return this;
        }

        public InstallerBuilder postgresUsername(final String postgresUsername) {
            this.postgresUsername = postgresUsername;
            return this;
        }

        public InstallerBuilder postgresPassword(final String postgresPassword) {
            this.postgresPassword = postgresPassword;
            return this;
        }

        public InstallerBuilder exaIp(final String exaIp) {
            this.exaIp = exaIp;
            return this;
        }

        public InstallerBuilder exaPort(final int exaPort) {
            this.exaPort = exaPort;
            return this;
        }

        public InstallerBuilder exaBucketFsPort(final int exaBucketFsPort) {
            this.exaBucketFsPort = exaBucketFsPort;
            return this;
        }

        public InstallerBuilder exaBucketName(final String exaBucketName) {
            this.exaBucketName = exaBucketName;
            return this;
        }

        public InstallerBuilder exaSchemaName(final String exaSchemaName) {
            this.exaSchemaName = exaSchemaName;
            return this;
        }

        public InstallerBuilder exaAdapterName(final String exaAdapterName) {
            this.exaAdapterName = exaAdapterName;
            return this;
        }

        public InstallerBuilder exaConnectionName(final String exaConnectionName) {
            this.exaConnectionName = exaConnectionName;
            return this;
        }

        public InstallerBuilder exaVirtualSchemaName(final String exaVirtualSchemaName) {
            this.exaVirtualSchemaName = exaVirtualSchemaName;
            return this;
        }

        public InstallerBuilder postgresIp(final String postgresIp) {
            this.postgresIp = postgresIp;
            return this;
        }

        public InstallerBuilder postgresPort(final String postgresPort) {
            this.postgresPort = postgresPort;
            return this;
        }

        public InstallerBuilder postgresDatabaseName(final String postgresDatabaseName) {
            this.postgresDatabaseName = postgresDatabaseName;
            return this;
        }

        public InstallerBuilder postgresMappedSchema(final String postgresMappedSchema) {
            this.postgresMappedSchema = postgresMappedSchema;
            return this;
        }

        public InstallerBuilder additionalProperties(final String[] additionalProperties) {
            this.additionalProperties = additionalProperties;
            return this;
        }

        public Installer build() {
            return new Installer(this);
        }
    }
}