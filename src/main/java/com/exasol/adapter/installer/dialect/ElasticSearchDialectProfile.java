package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;

/**
 * A ElasticSearch dialect profile.
 */
public class ElasticSearchDialectProfile extends AbstractVirtualSchemaProfile implements VirtualSchemaProfile {
    /**
     * Instantiate a new {@link ElasticSearchDialectProfile}.
     *
     * @param userInput user input
     */
    public ElasticSearchDialectProfile(final UserInput userInput) {
        super(userInput);
    }

    @Override
    protected String getDriverMain() {
        return "org.elasticsearch.xpack.sql.jdbc.EsDriver";
    }

    @Override
    protected String getDriverPrefix() {
        return "jdbc:es:";
    }

    @Override
    protected boolean isSecurityManagerEnabled() {
        return false;
    }

    @Override
    protected String getDefaultPort() {
        return "9200";
    }

    @Override
    protected String getDefaultDriverName() {
        return "x-pack-sql-jdbc.jar";
    }

    @Override
    protected boolean isConfigRequired() {
        return true;
    }

    @Override
    public String getConnectionString() {
        final String connectionString = getDriverPrefix() + "//" + getHost() + ":" + getPort();
        if (hasAdditionalConnectionProperties()) {
            return connectionString + "/" + getAdditionalConnectionProperties();
        } else {
            return connectionString;
        }
    }
}
