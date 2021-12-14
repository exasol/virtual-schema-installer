package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
class SqlServerInstallerIT extends AbstractIntegrationTest {
    private static final String MS_SQL_SCHEMA = "MS_SQL_SCHEMA";
    private static final int MS_SQL_PORT = 1433;
    public static final String DOCKER_IMAGE_REFERENCE = "mcr.microsoft.com/mssql/server:2019-CU14-ubuntu-20.04";

    @Container
    private static final MSSQLServerContainer MS_SQL_SERVER = new MSSQLServerContainer(DOCKER_IMAGE_REFERENCE);

    @BeforeAll
    static void beforeAll() throws SQLException {
        final Statement sourceStatement = createStatement();
        createSchema(sourceStatement, MS_SQL_SCHEMA);
        createSimpleTestTable(sourceStatement, MS_SQL_SCHEMA);
    }

    private static Statement createStatement() throws SQLException {
        return MS_SQL_SERVER.createConnection("").createStatement();
    }

    @Test
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "SQL_SERVER_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "mssql-jdbc.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/sqlserver-driver", //
                "--" + EXA_HOST_KEY, "localhost", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_SCHEMA_NAME_KEY, EXASOL_SCHEMA_NAME, //
                "--" + EXA_ADAPTER_NAME_KEY, EXASOL_ADAPTER_NAME, //
                "--" + EXA_CONNECTION_NAME_KEY, CONNECTION_NAME, //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, MS_SQL_SERVER.getMappedPort(MS_SQL_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "CATALOG_NAME='master'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + MS_SQL_SCHEMA + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "TABLE_FILTER='" + SIMPLE_TABLE + "'", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.SQLSERVER.toString().toLowerCase(),
                MS_SQL_SERVER.getUsername(), MS_SQL_SERVER.getPassword());
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "SQL_SERVER_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/sqlserver-driver", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, MS_SQL_SERVER.getMappedPort(MS_SQL_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + MS_SQL_SCHEMA + "'", //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, "databaseName=master", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.SQLSERVER.toString().toLowerCase(),
                MS_SQL_SERVER.getUsername(), MS_SQL_SERVER.getPassword());
    }
}