package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
class Db2InstallerIT extends AbstractIntegrationTest {
    private static final String DB2_SCHEMA = "DB2INST1";
    private static final int DB2_PORT = 50000;
    public static final String DOCKER_IMAGE_REFERENCE = "ibmcom/db2:11.5.7.0";

    @Container
    private static final Db2Container DB2 = new Db2Container(DOCKER_IMAGE_REFERENCE);

    @BeforeAll
    static void beforeAll() throws SQLException {
        final Statement sourceStatement = createStatement();
        createSimpleTestTable(sourceStatement, DB2_SCHEMA);
    }

    private static Statement createStatement() throws SQLException {
        return DB2.createConnection("").createStatement();
    }

    @Test
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "DB2_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "jcc.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/db2-driver", //
                "--" + EXA_HOST_KEY, "localhost", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_SCHEMA_NAME_KEY, EXASOL_SCHEMA_NAME, //
                "--" + EXA_ADAPTER_NAME_KEY, EXASOL_ADAPTER_NAME, //
                "--" + EXA_CONNECTION_NAME_KEY, CONNECTION_NAME, //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, DB2.getMappedPort(DB2_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + DB2_SCHEMA + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "TABLE_FILTER='" + SIMPLE_TABLE + "'", //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, "test", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.DB2.toString().toLowerCase(), DB2.getUsername(),
                DB2.getPassword());
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "DB2_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/db2-driver", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, DB2.getMappedPort(DB2_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + DB2_SCHEMA + "'", //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, "test", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.DB2.toString().toLowerCase(), DB2.getUsername(),
                DB2.getPassword());
    }
}