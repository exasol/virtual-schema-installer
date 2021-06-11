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
import com.exasol.errorreporting.ExaError;

/**
 * This class contains Postgres Virtual Schema installation logic.
 */
public class Installer {
    private static final Logger LOGGER = Logger.getLogger(Installer.class.getName());

    // Files related fields
    private final JarFile virtualSchemaJarFile;
    private final JarFile jdbcDriverJarFile;

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
    private final int postgresPort;
    private final String postgresDatabaseName;
    private final String postgresMappedSchema;

    private final String[] additionalProperties;

    private Installer(final InstallerBuilder builder) {
        this.virtualSchemaJarFile = builder.virtualSchemaJarFile;
        this.jdbcDriverJarFile = builder.jdbcDriverJarFile;
        this.exaAdapterName = builder.exaAdapterName;
        this.postgresDatabaseName = builder.postgresDatabaseName;
        this.exaPassword = builder.exaPassword;
        this.exaBucketWritePassword = builder.exaBucketWritePassword;
        this.postgresPort = builder.postgresPort;
        this.postgresUsername = builder.postgresUsername;
        this.exaSchemaName = builder.exaSchemaName;
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
        uploadFilesToBucket(this.virtualSchemaJarFile, this.jdbcDriverJarFile);
        try (final Connection connection = getConnection()) {
            installVirtualSchema(connection,
                    List.of(this.virtualSchemaJarFile.getName(), this.jdbcDriverJarFile.getName()));
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
        private JarFile virtualSchemaJarFile;
        private JarFile jdbcDriverJarFile;
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
        private int postgresPort;
        private String postgresDatabaseName;
        private String postgresMappedSchema;
        private String[] additionalProperties;

        private InstallerBuilder() {
        }

        public InstallerBuilder virtualSchemaJarFile(final JarFile virtualSchemaJarFile) {
            this.virtualSchemaJarFile = virtualSchemaJarFile;
            return this;
        }

        public InstallerBuilder jdbcDriverJarFile(final JarFile jdbcDriverJarFile) {
            this.jdbcDriverJarFile = jdbcDriverJarFile;
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

        public InstallerBuilder exaPort(final String exaPort) {
            this.exaPort = convertToInteger(exaPort, "exaPort");
            return this;
        }

        private int convertToInteger(final String value, final String parameterName) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException exception) {
                throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-6")
                        .message("Invalid value for parameter {{parameter}}. The value should only contain digits.", parameterName).toString(), exception);
            }
        }

        public InstallerBuilder exaBucketFsPort(final String exaBucketFsPort) {
            this.exaBucketFsPort = convertToInteger(exaBucketFsPort, "exaBucketFsPort");
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
            this.postgresPort = convertToInteger(postgresPort,"postgresPort");
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
            validateInput();
            return new Installer(this);
        }

        private void validateInput() {
            validateVirtualSchemaJarFile();
            validateJdbcDriverJarFile();
            validateExaUsername();
            validateExaPassword();
            validateExaBucketWritePassword();
            validatePostgresUsername();
            validatePostgresPassword();
            validateExaIp();
            validateExaBucketName();
            validateExaSchemaName();
            validateExaAdapterName();
            validateExaConnectionName();
            validateExaVirtualSchemaName();
            validatePostgresIp();
            validatePostgresDatabaseName();
            validatePostgresMappedSchema();
            validateAdditionalProperties();
        }

        private void validateVirtualSchemaJarFile() {

        }

        private void validateJdbcDriverJarFile() {

        }

        private void validateExaUsername() {
        }

        private void validateExaPassword() {

        }

        private void validateExaBucketWritePassword() {

        }

        private void validatePostgresUsername() {

        }

        private void validatePostgresPassword() {

        }

        private void validateExaIp() {

        }

        private void validateExaBucketName() {

        }

        private void validateExaSchemaName() {

        }

        private void validateExaAdapterName() {

        }

        private void validateExaConnectionName() {

        }

        private void validateExaVirtualSchemaName() {

        }

        private void validatePostgresIp() {

        }

        private void validatePostgresDatabaseName() {

        }

        private void validatePostgresMappedSchema() {

        }

        private void validateAdditionalProperties() {

        }
    }
}