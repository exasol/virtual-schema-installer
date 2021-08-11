package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.cli.ParseException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.license.StartTrialRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
class ElasticSearchInstallerIT extends AbstractIntegrationTest {
    private static final String ELASTIC_SEARCH_INDEX = "index";
    private static final String ELASTICSEARCH_USERNAME = "user";
    private static final String ELASTICSEARCH_PASSWORD = "pass";
    public static final String ELASTICSEARCH_DOCKER_IMAGE_REFERENCE = "docker.elastic.co/elasticsearch/elasticsearch:7.10.1";
    private static final int ELASTIC_SEARCH_PORT = 9200;

    @Container
    private static final ElasticsearchContainer ELASTIC_SEARCH = new ElasticsearchContainer(
            ELASTICSEARCH_DOCKER_IMAGE_REFERENCE).withReuse(true);

    @BeforeAll
    static void beforeAll() throws IOException {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD));
        final RestHighLevelClient client = new RestHighLevelClient(RestClient
                .builder(HttpHost.create(ELASTIC_SEARCH.getHttpHostAddress())) //
                .setHttpClientConfigCallback(
                        httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
        client.license().startTrial(new StartTrialRequest(true), RequestOptions.DEFAULT);
        client.indices().create(new CreateIndexRequest(ELASTIC_SEARCH_INDEX), RequestOptions.DEFAULT);
        final IndexRequest indexRequest = new IndexRequest(ELASTIC_SEARCH_INDEX).source("{\"int_field\": 1}",
                XContentType.JSON);
        client.index(indexRequest, RequestOptions.DEFAULT);
        client.close();
    }

    @Test
    void testInstallVirtualSchema()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "ELASTIC_SEARCH_VIRTUAL_SCHEMA_1";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_NAME_KEY, "x-pack-sql-jdbc.jar", //
                "--" + JDBC_DRIVER_PATH_KEY, "target/elasticsearch-driver", //
                "--" + EXA_HOST_KEY, "localhost", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_SCHEMA_NAME_KEY, EXASOL_SCHEMA_NAME, //
                "--" + EXA_ADAPTER_NAME_KEY, EXASOL_ADAPTER_NAME, //
                "--" + EXA_CONNECTION_NAME_KEY, CONNECTION_NAME, //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, ELASTIC_SEARCH.getMappedPort(ELASTIC_SEARCH_PORT).toString(), //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.ELASTICSEARCH.toString().toLowerCase(),
                ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD, "SELECT \"int_field\" FROM " + virtualSchemaName + ".\"" + ELASTIC_SEARCH_INDEX + "\"");
    }

    @Test
    void testInstallVirtualSchemaWithDefaultValues()
            throws SQLException, BucketAccessException, TimeoutException, ParseException, IOException {
        final String virtualSchemaName = "ELASTIC_SEARCH_VIRTUAL_SCHEMA_2";
        final String[] args = new String[] { //
                "--" + JDBC_DRIVER_PATH_KEY, "target/elasticsearch-driver", //
                "--" + EXA_PORT_KEY, EXASOL.getMappedPort(8563).toString(), //
                "--" + EXA_BUCKET_FS_PORT_KEY, EXASOL.getMappedPort(2580).toString(), //
                "--" + EXA_VIRTUAL_SCHEMA_NAME_KEY, virtualSchemaName, //
                "--" + SOURCE_HOST_KEY, EXASOL.getHostIp(), //
                "--" + SOURCE_PORT_KEY, ELASTIC_SEARCH.getMappedPort(ELASTIC_SEARCH_PORT).toString(), //
        };
        assertVirtualSchemaWasCreated(virtualSchemaName, args, Dialect.ELASTICSEARCH.toString().toLowerCase(),
                ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD, "SELECT \"int_field\" FROM " + virtualSchemaName + ".\"" + ELASTIC_SEARCH_INDEX + "\"");
    }
}