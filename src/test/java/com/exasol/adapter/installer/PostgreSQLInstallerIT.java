package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.jdbc.ConnectFailed;

@Tag("integration")
@Testcontainers
class PostgreSQLInstallerIT extends AbstractIntegrationTest {
    private static final String POSTGRES_SCHEMA = "postgres_schema";
    private static final String POSTGRES_CONTAINER_NAME = "postgres:14.1";

    @Container
    private static final PostgreSQLContainer<? extends PostgreSQLContainer<?>> POSTGRES = new PostgreSQLContainer<>(
            POSTGRES_CONTAINER_NAME);

    @BeforeAll
    static void beforeAll() throws SQLException {
        final Statement statementPostgres = createConnection();
        createSchema(statementPostgres, POSTGRES_SCHEMA);
        createSimpleTestTable(statementPostgres, POSTGRES_SCHEMA);
    }

    private static Statement createConnection() throws SQLException {
        return POSTGRES.createConnection("").createStatement();
    }

    @Test
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "POSTGRES_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "postgresql.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/postgresql-driver", //
                "--" + EXA_HOST_KEY, getExaHost(), //
                "--" + EXA_PORT_KEY, getExaPort(), //
                "--" + EXA_CERTIFICATE_FINGERPRINT_KEY, getExaCertificateFingerprint(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, getExaBucketFsPort(), //
                "--" + EXA_SCHEMA_NAME_KEY, EXASOL_SCHEMA_NAME, //
                "--" + EXA_ADAPTER_NAME_KEY, EXASOL_ADAPTER_NAME, //
                "--" + EXA_CONNECTION_NAME_KEY, CONNECTION_NAME, //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, POSTGRES.getMappedPort(5432).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "CATALOG_NAME='" + POSTGRES.getDatabaseName() + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + POSTGRES_SCHEMA + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "TABLE_FILTER='" + SIMPLE_TABLE.toLowerCase(Locale.ROOT) + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "EXCLUDED_CAPABILITIES='LIMIT'" //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.POSTGRESQL.name().toLowerCase(Locale.ROOT),
                POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "POSTGRES_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/postgresql-driver", //
                "--" + EXA_HOST_KEY, getExaHost(), //
                "--" + EXA_PORT_KEY, getExaPort(), //
                "--" + EXA_CERTIFICATE_FINGERPRINT_KEY, getExaCertificateFingerprint(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, getExaBucketFsPort(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, POSTGRES.getMappedPort(5432).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + POSTGRES_SCHEMA + "'", //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, POSTGRES.getDatabaseName(), //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.POSTGRESQL.name().toLowerCase(Locale.ROOT),
                POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    @Test
    void testInstallVirtualSchemaWithoutCertificateFingerprintFails()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "POSTGRES_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/postgresql-driver", //
                "--" + EXA_HOST_KEY, getExaHost(), //
                "--" + EXA_PORT_KEY, getExaPort(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, getExaBucketFsPort(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, POSTGRES.getMappedPort(5432).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + POSTGRES_SCHEMA + "'", //
                "--" + ADDITIONAL_CONNECTION_PROPERTIES_KEY, POSTGRES.getDatabaseName(), //
        };

        final String dialect = Dialect.POSTGRESQL.name().toLowerCase(Locale.ROOT);
        final String username = POSTGRES.getUsername();
        final String password = POSTGRES.getPassword();
        final ConnectFailed exception = assertThrows(ConnectFailed.class,
                () -> assertVirtualSchemaWasCreated(virtualSchemaName, args, dialect, username, password));
        assertThat(exception.getMessage(),
                containsString("TLS connection to host (" + EXASOL.getHost() + ") failed: PKIX path building failed"));
    }
}