package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.adapter.installer.dialect.VirtualSchemaProfileProvider;
import com.exasol.bucketfs.BucketAccessException;

public class Runner {
    private static final String CREDENTIALS_FILE_DEFAULT = FILE_SEPARATOR + ".virtual-schema-installer" + FILE_SEPARATOR
            + "credentials";
    private static final String JDBC_DRIVER_PATH_DEFAULT = "";

    public static void main(final String[] args)
            throws SQLException, BucketAccessException, FileNotFoundException, ParseException, TimeoutException {
        final Map<String, String> options = getUserInputOptions();
        final UserInput userInput = new UserInputParser().parseUserInput(args, options);
        final Map<String, String> parameters = userInput.getParameters();
        final PropertyReader propertyReader = new PropertyReader(
                getOrDefault(parameters, CREDENTIALS_FILE_KEY, CREDENTIALS_FILE_DEFAULT));
        final File virtualSchemaJar = getVirtualSchemaJarFile(userInput.getDialect());
        final VirtualSchemaProfile virtualSchemaProfile = VirtualSchemaProfileProvider.provideProfile(userInput);
        final String jdbcDriverName = virtualSchemaProfile.getJdbcDriverName();
        final JdbcDriver jdbcDriver = getJdbcDriver(parameters, virtualSchemaProfile, jdbcDriverName);
        final Installer installer = createInstaller(virtualSchemaProfile, virtualSchemaJar, jdbcDriver, propertyReader);
        installer.install();
    }

    private static File getVirtualSchemaJarFile(final Dialect dialect) {
        final FileProvider virtualSchemaJarProvider = new VirtualSchemaGitHubJarDownloader(dialect);
        return virtualSchemaJarProvider.getFile();
    }

    private static JdbcDriver getJdbcDriver(final Map<String, String> parameters,
            final VirtualSchemaProfile dialectProfile, final String jdbcDriverName) {
        final String jdbcDriverPath = getOrDefault(parameters, JDBC_DRIVER_PATH_KEY, JDBC_DRIVER_PATH_DEFAULT);
        final FileProvider jdbcDriverJarProvider = new LocalFileProvider(jdbcDriverPath, jdbcDriverName);
        return new JdbcDriver(jdbcDriverJarProvider.getFile(), dialectProfile.getJdbcConfig());
    }

    private static Installer createInstaller(final VirtualSchemaProfile virtualSchemaProfile,
            final File virtualSchemaJar, final JdbcDriver jdbcDriver, final PropertyReader propertyReader) {
        return Installer.builder() //
                .virtualSchemaJarFile(virtualSchemaJar) //
                .jdbcDriver(jdbcDriver) //
                .exaUsername(propertyReader.readProperty(EXASOL_USERNAME_KEY))
                .exaPassword(propertyReader.readProperty(EXASOL_PASSWORD_KEY))
                .exaBucketWritePassword(propertyReader.readProperty(EXASOL_BUCKET_WRITE_PASSWORD_KEY))
                .sourceUsername(propertyReader.readProperty(SOURCE_USERNAME_KEY))
                .sourcePassword(propertyReader.readProperty(SOURCE_PASSWORD_KEY)) //
                .exaIp(virtualSchemaProfile.getExaIp()) //
                .exaPort(virtualSchemaProfile.getExaPort()) //
                .exaBucketFsPort(virtualSchemaProfile.getBucketFsPort()) //
                .exaBucketName(virtualSchemaProfile.getBucketName()) //
                .exaSchemaName(virtualSchemaProfile.getAdapterSchemaName()) //
                .exaAdapterName(virtualSchemaProfile.getAdapterName()) //
                .exaConnectionName(virtualSchemaProfile.getConnectionName()) //
                .exaVirtualSchemaName(virtualSchemaProfile.getVirtualSchemaName()) //
                .connectionString(virtualSchemaProfile.getConnectionString()) //
                .dialectSpecificProperties(virtualSchemaProfile.getDialectSpecificProperties()) //
                .build();
    }

    private static Map<String, String> getUserInputOptions() {
        final Map<String, String> options = new HashMap<>();
        options.put(JDBC_DRIVER_NAME_KEY, JDBC_DRIVER_NAME_DESCRIPTION);
        options.put(JDBC_DRIVER_PATH_KEY, JDBC_DRIVER_PATH_DESCRIPTION);
        options.put(EXA_IP_KEY, EXA_IP_DESCRIPTION);
        options.put(EXA_PORT_KEY, EXA_PORT_DESCRIPTION);
        options.put(EXA_BUCKET_FS_PORT_KEY, EXA_BUCKET_FS_PORT_DESCRIPTION);
        options.put(EXA_SCHEMA_NAME_KEY, EXA_SCHEMA_NAME_DESCRIPTION);
        options.put(EXA_ADAPTER_NAME_KEY, EXA_ADAPTER_NAME_DESCRIPTION);
        options.put(EXA_CONNECTION_NAME_KEY, EXA_CONNECTION_NAME_DESCRIPTION);
        options.put(EXA_VIRTUAL_SCHEMA_NAME_KEY, EXA_VIRTUAL_SCHEMA_NAME_DESCRIPTION);
        options.put(SOURCE_IP_KEY, SOURCE_IP_DESCRIPTION);
        options.put(SOURCE_PORT_KEY, SOURCE_PORT_DESCRIPTION);
        options.put(CREDENTIALS_FILE_KEY, CREDENTIALS_FILE_DESCRIPTION);
        return options;
    }

    private static String getOrDefault(final Map<String, String> userInput, final String key,
            final String defaultValue) {
        if (!userInput.containsKey(key) || userInput.get(key) == null || userInput.get(key).isEmpty()) {
            return defaultValue;
        } else {
            return userInput.get(key);
        }
    }
}