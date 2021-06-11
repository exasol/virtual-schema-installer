package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;

import com.exasol.bucketfs.BucketAccessException;

public class Runner {
    public static void main(final String[] args)
            throws SQLException, BucketAccessException, FileNotFoundException, ParseException, TimeoutException {
        final Map<String, String> options = getUserInputOptions();
        final UserInput userInput = new UserInputParser().parseUserInput(args, options);
        final VirtualSchemaJarProvider virtualSchemaJarProvider = new VirtualSchemaGitHubJarDownloader(
                userInput.getDialect());
        final Map<String, String> parameters = userInput.getParameters();
        final String jdbcDriverPath = getOrDefault(parameters, JDBC_DRIVER_PATH_KEY, JDBC_DRIVER_PATH_DEFAULT);
        final String jdbcDriverName = getOrDefault(parameters, JDBC_DRIVER_NAME_KEY, JDBC_DRIVER_NAME_DEFAULT);
        final JdbcDriverJarProvider jdbcDriverJarProvider = new JdbcDriverLocalFileProvider(jdbcDriverPath,
                jdbcDriverName);
        final PropertyReader propertyReader = new PropertyReader(
                getOrDefault(parameters, CREDENTIALS_FILE_KEY, CREDENTIALS_FILE_DEFAULT));
        final Installer installer = createInstaller(userInput, virtualSchemaJarProvider, jdbcDriverJarProvider,
                parameters, propertyReader);
        installer.install();
    }

    private static Installer createInstaller(final UserInput userInput,
            final VirtualSchemaJarProvider virtualSchemaJarProvider, final JdbcDriverJarProvider jdbcDriverJarProvider,
            final Map<String, String> parameters, final PropertyReader propertyReader) {
        return Installer.builder() //
                .virtualSchemaJarFile(virtualSchemaJarProvider.getJar()) //
                .jdbcDriverJarFile(jdbcDriverJarProvider.getJar()) //
                .exaUsername(propertyReader.readProperty(EXASOL_USERNAME_KEY))
                .exaPassword(propertyReader.readProperty(EXASOL_PASSWORD_KEY))
                .exaBucketWritePassword(propertyReader.readProperty(EXASOL_BUCKET_WRITE_PASSWORD_KEY))
                .postgresUsername(propertyReader.readProperty(POSTGRES_USERNAME_KEY))
                .postgresPassword(propertyReader.readProperty(POSTGRES_PASSWORD_KEY)) //
                .exaIp(getOrDefault(parameters, EXA_IP_KEY, EXA_IP_DEFAULT)) //
                .exaPort(getOrDefault(parameters, EXA_PORT_KEY, EXA_PORT_DEFAULT)) //
                .exaBucketFsPort(getOrDefault(parameters, EXA_BUCKET_FS_PORT_KEY, EXA_BUCKET_FS_PORT_DEFAULT)) //
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
    }

    private static Map<String, String> getUserInputOptions() {
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
        return options;
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