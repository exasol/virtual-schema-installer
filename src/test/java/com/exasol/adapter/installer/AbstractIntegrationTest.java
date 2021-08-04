package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.testcontainers.junit.jupiter.Container;

import com.exasol.adapter.installer.main.Runner;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.matcher.TypeMatchMode;

abstract class AbstractIntegrationTest {
    protected static final String EXASOL_SCHEMA_NAME = "ADAPTER";
    protected static final String EXASOL_ADAPTER_NAME = "MY_ADAPTER_SCRIPT";
    protected static final String CONNECTION_NAME = "JDBC_CONNECTION";
    protected static final String SIMPLE_TABLE = "SIMPLE_TABLE";
    private static final String EXASOL_DOCKER_IMAGE_REFERENCE = "7.0.10";

    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>(
            EXASOL_DOCKER_IMAGE_REFERENCE).withReuse(true);

    protected void assertVirtualSchemaWasCreated(final String virtualSchemaName, final String[] args,
            final String dialect, final String username, final String password)
            throws ParseException, SQLException, BucketAccessException, TimeoutException, IOException {
        final Path tempFile = createCredentialsFile(username, password);
        installVirtualSchema(args, tempFile, dialect);
        final ResultSet actualResultSet = EXASOL.createConnection().createStatement()
                .executeQuery("SELECT * FROM " + virtualSchemaName + "." + SIMPLE_TABLE);
        assertThat(actualResultSet, table().row(1).matches(TypeMatchMode.NO_JAVA_TYPE_CHECK));
    }

    protected static void createSchema(final Statement statement, final String schemaName) throws SQLException {
        statement.execute("CREATE SCHEMA " + schemaName);
    }

    protected static void createSimpleTestTable(final Statement statement, String schemaName) throws SQLException {
        final String qualifiedTableName = schemaName + "." + SIMPLE_TABLE;
        statement.execute("CREATE TABLE " + qualifiedTableName + " (x INT)");
        statement.execute("INSERT INTO " + qualifiedTableName + " VALUES (1)");
    }

    private Path createCredentialsFile(final String username, final String password) throws IOException {
        final String credentials = EXASOL_USERNAME_KEY + "=" + EXASOL.getUsername() + "\n" //
                + EXASOL_PASSWORD_KEY + "=" + EXASOL.getPassword() + "\n" //
                + EXASOL_BUCKET_WRITE_PASSWORD_KEY + "=" + EXASOL.getDefaultBucket().getWritePassword() + "\n" //
                + SOURCE_USERNAME_KEY + "=" + username + "\n" //
                + SOURCE_PASSWORD_KEY + "=" + password + "\n";
        final Path tempFile = Files.createTempFile("installer_credentials", "temp");
        Files.write(tempFile, credentials.getBytes());
        return tempFile;
    }

    private String[] resolveArguments(final String[] args, final Path tempFile, final String dialect) {
        final String[] newArgs = new String[args.length + 4];
        System.arraycopy(args, 0, newArgs, 0, args.length);
        newArgs[newArgs.length - 4] = "--" + DIALECT_KEY;
        newArgs[newArgs.length - 3] = dialect;
        newArgs[newArgs.length - 2] = "--" + CREDENTIALS_FILE_KEY;
        newArgs[newArgs.length - 1] = tempFile.toString();
        return newArgs;
    }

    private void installVirtualSchema(final String[] args, final Path tempFile, final String dialect)
            throws SQLException, BucketAccessException, FileNotFoundException, ParseException, TimeoutException {
        Runner.main(resolveArguments(args, tempFile, dialect));
    }
}
