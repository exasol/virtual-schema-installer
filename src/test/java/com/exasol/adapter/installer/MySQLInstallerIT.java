package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.IOException;
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

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
class MySQLInstallerIT extends AbstractIntegrationTest {
    private static final String MYSQL_SCHEMA = "MYSQL_SCHEMA";
    public static final String MYSQL_DOCKER_IMAGE_REFERENCE = "mysql:8.0.23";
    private static final int MYSQL_PORT = 3306;

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>(MYSQL_DOCKER_IMAGE_REFERENCE)
            .withUsername("root").withPassword("");

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
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "MYSQL_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "mysql-connector-java.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/mysql-driver", //
                "--" + EXA_HOST_KEY, "localhost", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_SCHEMA_NAME_KEY, EXASOL_SCHEMA_NAME, //
                "--" + EXA_ADAPTER_NAME_KEY, EXASOL_ADAPTER_NAME, //
                "--" + EXA_CONNECTION_NAME_KEY, CONNECTION_NAME, //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, MYSQL.getMappedPort(MYSQL_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "CATALOG_NAME='" + MYSQL_SCHEMA + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "TABLE_FILTER='" + SIMPLE_TABLE + "'", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.MYSQL.toString().toLowerCase(),
                MYSQL.getUsername(), MYSQL.getPassword());
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "MYSQL_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/mysql-driver", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, MYSQL.getMappedPort(MYSQL_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "CATALOG_NAME='" + MYSQL_SCHEMA + "'" //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.MYSQL.toString().toLowerCase(),
                MYSQL.getUsername(), MYSQL.getPassword());
    }
}