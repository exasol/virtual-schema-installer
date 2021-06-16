package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.matcher.TypeMatchMode;

@Tag("integration")
@Testcontainers
class MySQLInstallerIT {
    private static final String EXASOL_SCHEMA_NAME = "ADAPTER";
    private static final String EXASOL_ADAPTER_NAME = "MY_ADAPTER_SCRIPT";
    private static final String CONNECTION_NAME = "JDBC_CONNECTION";
    private static final String MYSQL_SCHEMA = "MYSQL_SCHEMA";
    private static final String SIMPLE_TABLE = "SIMPLE_TABLE";
    private static final String EXASOL_DOCKER_IMAGE_REFERENCE = "7.0.5";
    public static final String MYSQL_DOCKER_IMAGE_REFERENCE = "mysql:8.0.23";
    private static final int MYSQL_PORT = 3306;

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>(MYSQL_DOCKER_IMAGE_REFERENCE)
            .withUsername("root").withPassword("");
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>(
            EXASOL_DOCKER_IMAGE_REFERENCE).withReuse(true);

    @BeforeAll
    static void beforeAll() throws SQLException {
        final Statement sourceStatement = createStatement();
        createSchema(sourceStatement, MYSQL_SCHEMA);
        createSimpleTestTable(sourceStatement);
    }

    private static Statement createStatement() throws SQLException {
        return MYSQL.createConnection("").createStatement();
    }

    private static void createSchema(final Statement statement, final String schemaName) throws SQLException {
        statement.execute("CREATE SCHEMA " + schemaName);
    }

    private static void createSimpleTestTable(final Statement statement) throws SQLException {
        final String qualifiedTableName = MYSQL_SCHEMA + "." + SIMPLE_TABLE;
        statement.execute("CREATE TABLE " + qualifiedTableName + " (x INT)");
        statement.execute("INSERT INTO " + qualifiedTableName + " VALUES (1)");
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "MYSQL_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/nysql-driver", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_IP_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, MYSQL.getMappedPort(MYSQL_PORT).toString(), //
                "--" + SOURCE_DATABASE_NAME_KEY, MYSQL.getDatabaseName(), //
                "--" + SOURCE_MAPPED_SCHEMA_KEY, MYSQL_SCHEMA //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args);
    }

    private void assertVirtualSchemaWasCreated(final String virtualSchemaName, final String[] args)
            throws ParseException, SQLException, BucketAccessException, TimeoutException, IOException {
        final Path tempFile = createCredentialsFile();
        installVirtualSchema(args, tempFile);
        final ResultSet actualResultSet = EXASOL.createConnection().createStatement()
                .executeQuery("SELECT * FROM " + virtualSchemaName + "." + SIMPLE_TABLE);
        assertThat(actualResultSet, table().row(1).matches(TypeMatchMode.NO_JAVA_TYPE_CHECK));
    }

    private Path createCredentialsFile() throws IOException {
        final String credentials = EXASOL_USERNAME_KEY + "=" + EXASOL.getUsername() + "\n" //
                + EXASOL_PASSWORD_KEY + "=" + EXASOL.getPassword() + "\n" //
                + EXASOL_BUCKET_WRITE_PASSWORD_KEY + "=" + EXASOL.getDefaultBucket().getWritePassword() + "\n" //
                + SOURCE_USERNAME_KEY + "=" + MYSQL.getUsername() + "\n" //
                + SOURCE_PASSWORD_KEY + "=" + MYSQL.getPassword() + "\n";
        final Path tempFile = Files.createTempFile("installer_credentials", "temp");
        Files.write(tempFile, credentials.getBytes());
        return tempFile;
    }

    private String[] resolveArguments(final String[] args, final Path tempFile) {
        final String[] newArgs = new String[args.length + 4];
        System.arraycopy(args, 0, newArgs, 0, args.length);
        newArgs[newArgs.length - 4] = "--" + DIALECT_KEY;
        newArgs[newArgs.length - 3] = Dialect.MYSQL.toString().toLowerCase();
        newArgs[newArgs.length - 2] = "--" + CREDENTIALS_FILE_KEY;
        newArgs[newArgs.length - 1] = tempFile.toString();
        return newArgs;
    }

    private void installVirtualSchema(final String[] args, final Path tempFile)
            throws SQLException, BucketAccessException, FileNotFoundException, ParseException, TimeoutException {
        Runner.main(resolveArguments(args, tempFile));
    }
}