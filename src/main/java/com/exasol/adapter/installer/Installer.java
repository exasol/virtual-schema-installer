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

import com.exasol.adapter.installer.dialect.DialectProperty;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.WriteEnabledBucket;
import com.exasol.errorreporting.ExaError;

/**
 * This class contains a Virtual Schema installation logic.
 */
public class Installer {
    private static final Logger LOGGER = Logger.getLogger(Installer.class.getName());
    private static final String DEFAULT_BUCKETFS_NAME = "bfsdefault";
    private static final String DEFAULT_BUCKET_NAME = "default";
    private static final String DEFAULT_PATH_TO_FILE_IN_BUCKET = "drivers/jdbc";
    // Files related fields
    private final File virtualSchemaJarFile;
    private final JdbcDriver jdbcDriver;
    // Credentials
    private final String exaUsername;
    private final String exaPassword;
    private final String exaBucketWritePassword;
    private final String sourceUsername;
    private final String sourcePassword;
    // Virtual Schema related fields
    private final String exaHost;
    private final int exaPort;
    private final int exaBucketFsPort;
    private final String exaSchemaName;
    private final String exaAdapterName;
    private final String exaConnectionName;
    private final String exaVirtualSchemaName;
    private final String connectionString;
    private final List<DialectProperty> dialectProperties;

    private Installer(final InstallerBuilder builder) {
        this.virtualSchemaJarFile = builder.virtualSchemaJarFile;
        this.jdbcDriver = builder.jdbcDriver;
        this.exaAdapterName = builder.exaAdapterName;
        this.exaPassword = builder.exaPassword;
        this.exaBucketWritePassword = builder.exaBucketWritePassword;
        this.sourceUsername = builder.sourceUsername;
        this.exaSchemaName = builder.exaSchemaName;
        this.exaConnectionName = builder.exaConnectionName;
        this.exaBucketFsPort = builder.exaBucketFsPort;
        this.sourcePassword = builder.sourcePassword;
        this.exaHost = builder.exaHost;
        this.connectionString = builder.connectionString;
        this.exaUsername = builder.exaUsername;
        this.exaPort = builder.exaPort;
        this.dialectProperties = builder.dialectProperties;
        this.exaVirtualSchemaName = builder.exaVirtualSchemaName;
    }

    /**
     * Install a Virtual Schema to the Exasol database.
     */
    public void install() throws SQLException, BucketAccessException, TimeoutException, FileNotFoundException {
        uploadFilesToBucket(this.virtualSchemaJarFile, this.jdbcDriver);
        try (final Connection connection = getConnection()) {
            installVirtualSchema(connection,
                    List.of(this.virtualSchemaJarFile.getName(), this.jdbcDriver.getJar().getName()));
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:exa:" + this.exaHost + ":" + this.exaPort, this.exaUsername,
                this.exaPassword);
    }

    private void uploadFilesToBucket(final File virtualSchemaJarFile, final JdbcDriver jdbcDriver)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final WriteEnabledBucket bucket = getBucket(DEFAULT_BUCKETFS_NAME, DEFAULT_BUCKET_NAME);
        uploadFileToBucket(bucket, virtualSchemaJarFile, DEFAULT_PATH_TO_FILE_IN_BUCKET);
        uploadDriverToBucket(bucket, jdbcDriver);
    }

    private WriteEnabledBucket getBucket(final String bucketFsName, final String bucketName) {
        return WriteEnabledBucket.builder()//
                .serviceName(bucketFsName) //
                .ipAddress(this.exaHost) //
                .httpPort(this.exaBucketFsPort) //
                .name(bucketName) //
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
        statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + this.exaSchemaName + "\"");
    }

    private void createAdapterScript(final Statement statement, final List<String> jarNames) throws SQLException {
        final String createAdapterScriptStatement = prepareAdapterScriptStatement(jarNames);
        LOGGER.info(() -> "Installing adapter script with the following command: " + LINE_SEPARATOR
                + createAdapterScriptStatement);
        statement.execute(createAdapterScriptStatement);
    }

    private void createConnection(final Statement statement) throws SQLException {
        LOGGER.info(() -> "Creating connection object with the following connection string: " + LINE_SEPARATOR
                + this.connectionString);
        statement.execute("CREATE OR REPLACE CONNECTION \"" + this.exaConnectionName + "\" TO '" + this.connectionString
                + "' USER '" + this.sourceUsername + "' IDENTIFIED BY '" + this.sourcePassword + "'");
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
                        + "CONNECTION_NAME = '" + this.exaConnectionName + "'" + LINE_SEPARATOR);
        for (final DialectProperty property : this.dialectProperties) {
            createVirtualSchemaStatement //
                    .append(property.getKey()).append("='").append(property.getValue()).append("'")
                    .append(LINE_SEPARATOR);
        }
        return createVirtualSchemaStatement.toString();
    }

    private String prepareAdapterScriptStatement(final List<String> jarNames) {
        final StringBuilder adapterScript = new StringBuilder( //
                "CREATE OR REPLACE JAVA ADAPTER SCRIPT \"" + this.exaSchemaName + "\".\"" + this.exaAdapterName
                        + "\" AS" + LINE_SEPARATOR //
                        + "  %scriptclass com.exasol.adapter.RequestDispatcher;" + LINE_SEPARATOR //
        );
        for (final String jarName : jarNames) {
            adapterScript.append(getPathInBucketFor(DEFAULT_BUCKETFS_NAME, DEFAULT_BUCKET_NAME,
                    DEFAULT_PATH_TO_FILE_IN_BUCKET, jarName));
        }
        return adapterScript.toString();
    }

    private String getPathInBucketFor(final String bucketFsName, final String bucketName, final String path,
            final String jarName) {
        return "%jar /buckets/" + bucketFsName + "/" + bucketName + "/" + path + "/" + jarName + ";" + LINE_SEPARATOR;
    }

    private void uploadFileToBucket(final WriteEnabledBucket bucket, final File file, final String pathInBucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(file.getPathWithName(), pathInBucket + "/" + file.getName());
    }

    private void uploadDriverToBucket(final WriteEnabledBucket bucket, final JdbcDriver jdbcDriver)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        uploadFileToBucket(bucket, jdbcDriver.getJar(), DEFAULT_PATH_TO_FILE_IN_BUCKET);
        if (jdbcDriver.hasConfig()) {
            uploadFileToBucket(bucket, jdbcDriver.getConfig(), DEFAULT_PATH_TO_FILE_IN_BUCKET);
        }
    }

    /**
     * Create a new builder for {@link Installer}.
     * 
     * @return new builder
     */
    public static InstallerBuilder builder() {
        return new InstallerBuilder();
    }

    /**
     * A builder for installer.
     */
    public static final class InstallerBuilder {
        // Files related fields
        private File virtualSchemaJarFile;
        private JdbcDriver jdbcDriver;
        // Credentials
        private String exaUsername;
        private String exaPassword;
        private String exaBucketWritePassword;
        private String sourceUsername;
        private String sourcePassword;
        // Virtual Schema related fields
        private String exaHost;
        private int exaPort;
        private int exaBucketFsPort;
        private String exaSchemaName;
        private String exaAdapterName;
        private String exaConnectionName;
        private String exaVirtualSchemaName;
        private String connectionString;
        private List<DialectProperty> dialectProperties;

        private InstallerBuilder() {
        }

        public InstallerBuilder virtualSchemaJarFile(final File virtualSchemaJarFile) {
            this.virtualSchemaJarFile = virtualSchemaJarFile;
            return this;
        }

        public InstallerBuilder jdbcDriver(final JdbcDriver jdbcDriver) {
            this.jdbcDriver = jdbcDriver;
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

        public InstallerBuilder sourceUsername(final String sourceUsername) {
            this.sourceUsername = UserInputValidator.validateLiteralString(sourceUsername);
            return this;
        }

        public InstallerBuilder sourcePassword(final String sourcePassword) {
            this.sourcePassword = UserInputValidator.validateLiteralString(sourcePassword);
            return this;
        }

        public InstallerBuilder exaHost(final String exaHost) {
            this.exaHost = exaHost;
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
                        .message("Invalid value for parameter {{parameter}}. The value should only contain digits.",
                                parameterName)
                        .toString(), exception);
            }
        }

        public InstallerBuilder exaBucketFsPort(final String exaBucketFsPort) {
            this.exaBucketFsPort = convertToInteger(exaBucketFsPort, "exaBucketFsPort");
            return this;
        }

        public InstallerBuilder exaSchemaName(final String exaSchemaName) {
            this.exaSchemaName = UserInputValidator.validateExasolIdentifier(exaSchemaName);
            return this;
        }

        public InstallerBuilder exaAdapterName(final String exaAdapterName) {
            this.exaAdapterName = UserInputValidator.validateExasolIdentifier(exaAdapterName);
            return this;
        }

        public InstallerBuilder exaConnectionName(final String exaConnectionName) {
            this.exaConnectionName = UserInputValidator.validateExasolIdentifier(exaConnectionName);
            return this;
        }

        public InstallerBuilder exaVirtualSchemaName(final String exaVirtualSchemaName) {
            this.exaVirtualSchemaName = UserInputValidator.validateExasolIdentifier(exaVirtualSchemaName);
            return this;
        }

        public InstallerBuilder connectionString(final String connectionString) {
            this.connectionString = UserInputValidator.validateLiteralString(connectionString);
            return this;
        }

        public InstallerBuilder dialectProperties(final List<DialectProperty> dialectProperties) {
            this.dialectProperties = dialectProperties;
            return this;
        }

        /**
         * Create a new {@link Installer}.
         * 
         * @return new installer
         */
        public Installer build() {
            return new Installer(this);
        }
    }
}