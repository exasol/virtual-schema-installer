package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.LINE_SEPARATOR;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.WriteEnabledBucket;
import com.exasol.errorreporting.ExaError;

/**
 * This class contains a Virtual Schema installation logic.
 */
public class Installer {
    private static final Logger LOGGER = Logger.getLogger(Installer.class.getName());
    private static final String PATH_TO_DRIVER_IN_BUCKET = "drivers/jdbc/";
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
    private final String exaIp;
    private final int exaPort;
    private final int exaBucketFsPort;
    private final String exaBucketName;
    private final String exaSchemaName;
    private final String exaAdapterName;
    private final String exaConnectionName;
    private final String exaVirtualSchemaName;
    private final String connectionString;
    private final List<String> dialectSpecificProperties;

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
        this.exaIp = builder.exaIp;
        this.connectionString = builder.connectionString;
        this.exaUsername = builder.exaUsername;
        this.exaPort = builder.exaPort;
        this.exaBucketName = builder.exaBucketName;
        this.dialectSpecificProperties = builder.dialectSpecificProperties;
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
        return DriverManager.getConnection("jdbc:exa:" + this.exaIp + ":" + this.exaPort, this.exaUsername,
                this.exaPassword);
    }

    private void uploadFilesToBucket(final File virtualSchemaJarFile, final JdbcDriver jdbcDriver)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final WriteEnabledBucket bucket = getBucket();
        uploadVsJarToBucket(bucket, virtualSchemaJarFile);
        uploadDriverToBucket(bucket, jdbcDriver);
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
        LOGGER.info(() -> "Creating connection object with the following connection string: " + LINE_SEPARATOR
                + this.connectionString);
        statement.execute("CREATE OR REPLACE CONNECTION " + this.exaConnectionName + " TO '" + this.connectionString
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
        for (final String property : this.dialectSpecificProperties) {
            createVirtualSchemaStatement.append(property).append(LINE_SEPARATOR);
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
        return "%jar /buckets/bfsdefault/default/" + PATH_TO_DRIVER_IN_BUCKET + jarName + ";" + LINE_SEPARATOR;
    }

    private void uploadVsJarToBucket(final WriteEnabledBucket bucket, final File virtualSchemaJarFile)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFileNonBlocking(virtualSchemaJarFile.getPathWithName(),
                PATH_TO_DRIVER_IN_BUCKET + virtualSchemaJarFile.getName());
    }

    private void uploadDriverToBucket(final WriteEnabledBucket bucket, final JdbcDriver jdbcDriver)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        uploadJdbcDriverJar(bucket, jdbcDriver);
        if (jdbcDriver.hasConfig()) {
            uploadJdbcDriverConfig(bucket, jdbcDriver);
        }
    }

    private void uploadJdbcDriverJar(final WriteEnabledBucket bucket, final JdbcDriver jdbcDriver)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final File jdbcDriverJar = jdbcDriver.getJar();
        bucket.uploadFileNonBlocking(jdbcDriverJar.getPathWithName(),
                PATH_TO_DRIVER_IN_BUCKET + jdbcDriverJar.getName());
    }

    private void uploadJdbcDriverConfig(final WriteEnabledBucket bucket, final JdbcDriver jdbcDriver)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final File config = jdbcDriver.getConfig();
        bucket.uploadFileNonBlocking(config.getPathWithName(), PATH_TO_DRIVER_IN_BUCKET + config.getName());
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
        private String exaIp;
        private int exaPort;
        private int exaBucketFsPort;
        private String exaBucketName;
        private String exaSchemaName;
        private String exaAdapterName;
        private String exaConnectionName;
        private String exaVirtualSchemaName;
        private String connectionString;
        private List<String> dialectSpecificProperties;

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
            this.exaUsername = InputString.validate(exaUsername);
            return this;
        }

        public InstallerBuilder exaPassword(final String exaPassword) {
            this.exaPassword = InputString.validate(exaPassword);
            return this;
        }

        public InstallerBuilder exaBucketWritePassword(final String exaBucketWritePassword) {
            this.exaBucketWritePassword = InputString.validate(exaBucketWritePassword);
            return this;
        }

        public InstallerBuilder sourceUsername(final String sourceUsername) {
            this.sourceUsername = InputString.validate(sourceUsername);
            return this;
        }

        public InstallerBuilder sourcePassword(final String sourcePassword) {
            this.sourcePassword = InputString.validate(sourcePassword);
            return this;
        }

        public InstallerBuilder exaIp(final String exaIp) {
            this.exaIp = InputString.validate(exaIp);
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

        public InstallerBuilder exaBucketName(final String exaBucketName) {
            this.exaBucketName = InputString.validate(exaBucketName);
            return this;
        }

        public InstallerBuilder exaSchemaName(final String exaSchemaName) {
            this.exaSchemaName = InputString.validate(exaSchemaName);
            return this;
        }

        public InstallerBuilder exaAdapterName(final String exaAdapterName) {
            this.exaAdapterName = InputString.validate(exaAdapterName);
            return this;
        }

        public InstallerBuilder exaConnectionName(final String exaConnectionName) {
            this.exaConnectionName = InputString.validate(exaConnectionName);
            return this;
        }

        public InstallerBuilder exaVirtualSchemaName(final String exaVirtualSchemaName) {
            this.exaVirtualSchemaName = InputString.validate(exaVirtualSchemaName);
            return this;
        }

        public InstallerBuilder connectionString(final String connectionString) {
            this.connectionString = InputString.validate(connectionString);
            return this;
        }

        public InstallerBuilder dialectSpecificProperties(final List<String> dialectSpecificProperties) {
            final List<String> properties = new ArrayList<>(dialectSpecificProperties.size());
            for (final String additionalProperty : dialectSpecificProperties) {
                properties.add(InputString.validate(additionalProperty));
            }
            this.dialectSpecificProperties = properties;
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