package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
class PostgreSQLInstallerIT extends AbstractIntegrationTest {
    private static final String POSTGRES_SCHEMA = "postgres_schema";
    private static final String POSTGRES_CONTAINER_NAME = "postgres:13.1";

    @Container
    private static final PostgreSQLContainer<? extends PostgreSQLContainer<?>> POSTGRES = new PostgreSQLContainer<>(
            POSTGRES_CONTAINER_NAME);

    @BeforeAll
    static void beforeAll() throws SQLException {
        final Statement statementPostgres = createConnection();
        createSchema(statementPostgres, POSTGRES_SCHEMA);
        createPostgresSimpleTestTable(statementPostgres);
    }

    private static Statement createConnection() throws SQLException {
        return POSTGRES.createConnection("").createStatement();
    }

    private static void createSchema(final Statement statementPostgres, final String schemaName) throws SQLException {
        statementPostgres.execute("CREATE SCHEMA " + schemaName);
    }

    private static void createPostgresSimpleTestTable(final Statement statement) throws SQLException {
        final String qualifiedTableName = POSTGRES_SCHEMA + "." + SIMPLE_TABLE;
        statement.execute("CREATE TABLE " + qualifiedTableName + " (x INT)");
        statement.execute("INSERT INTO " + qualifiedTableName + " VALUES (1)");
    }

    @Test
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "POSTGRES_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "postgresql.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/postgresql-driver", //
                "--" + EXA_HOST_KEY, "localhost", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
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
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, POSTGRES.getMappedPort(5432).toString(), //
                "--" + ADDITIONAL_PROPERTY_KEY, "CATALOG_NAME='" + POSTGRES.getDatabaseName() + "'", //
                "--" + ADDITIONAL_PROPERTY_KEY, "SCHEMA_NAME='" + POSTGRES_SCHEMA + "'" //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.POSTGRESQL.name().toLowerCase(Locale.ROOT),
                POSTGRES.getUsername(), POSTGRES.getPassword());
    }
}