package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
class OracleInstallerIT extends AbstractIntegrationTest {
    private static final String ORACLE_SCHEMA = "ORACLE_SCHEMA";
    public static final String ORACLE_DOCKER_IMAGE_REFERENCE = "gvenzl/oracle-xe";
    private static final int ORACLE_PORT = 1521;

    @Container
    private static final OracleContainer ORACLE = new OracleContainer(ORACLE_DOCKER_IMAGE_REFERENCE).withReuse(false);

    @BeforeAll
    static void beforeAll() throws SQLException, UnsupportedOperationException, IOException, InterruptedException {
        ORACLE.execInContainer("resetPassword", ORACLE.getPassword());
        final Statement sourceStatement = createStatement();
        createOracleUser(sourceStatement);
        createSimpleTestTable(sourceStatement, ORACLE_SCHEMA);
    }

    private static void createOracleUser(final Statement statementOracle) throws SQLException {
        final String username = ORACLE_SCHEMA;
        final String password = ORACLE_SCHEMA;
        statementOracle.execute("CREATE USER " + username + " IDENTIFIED BY " + password);
        statementOracle.execute("GRANT CONNECT TO " + username);
        statementOracle.execute("GRANT CREATE SESSION TO " + username);
        statementOracle.execute("GRANT UNLIMITED TABLESPACE TO " + username);
    }

    private static Statement createStatement() throws SQLException {
        return DriverManager.getConnection(ORACLE.getJdbcUrl(), "sys as sysdba", ORACLE.getPassword())
                .createStatement();
    }

    @Test
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "ORACLE_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "ojdbc8.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/oracle-driver", //
                "--" + EXA_HOST_KEY, getExaHost(), //
                "--" + EXA_PORT_KEY, getExaPort(), //
                "--" + EXA_CERTIFICATE_FINGERPRINT_KEY, getExaCertificateFingerprint(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, getExaBucketFsPort(), //
                "--" + EXA_SCHEMA_NAME_KEY, EXASOL_SCHEMA_NAME, //
                "--" + EXA_ADAPTER_NAME_KEY, EXASOL_ADAPTER_NAME, //
                "--" + EXA_CONNECTION_NAME_KEY, CONNECTION_NAME, //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, ORACLE.getMappedPort(ORACLE_PORT).toString(), //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, "xe", //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + ORACLE_SCHEMA + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "TABLE_FILTER='" + SIMPLE_TABLE + "'", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.ORACLE.toString().toLowerCase(), ORACLE_SCHEMA,
                ORACLE_SCHEMA);
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "ORACLE_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/oracle-driver", //
                "--" + EXA_HOST_KEY, getExaHost(), //
                "--" + EXA_PORT_KEY, getExaPort(), //
                "--" + EXA_CERTIFICATE_FINGERPRINT_KEY, getExaCertificateFingerprint(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, getExaBucketFsPort(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, ORACLE.getMappedPort(ORACLE_PORT).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + ORACLE_SCHEMA + "'", //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, "xe", //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.ORACLE.toString().toLowerCase(),
                ORACLE.getUsername(), ORACLE.getPassword());
    }
}