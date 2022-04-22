package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;

/**
 * A Postgres dialect profile.
 */
public class PostgresDialectProfile extends AbstractVirtualSchemaProfile implements VirtualSchemaProfile {
    /**
     * Instantiate a new {@link PostgresDialectProfile}.
     *
     * @param userInput user input
     */
    public PostgresDialectProfile(final UserInput userInput) {
        super(userInput);
    }

    @Override
    protected String getDriverMain() {
        return " org.postgresql.Driver";
    }

    @Override
    protected String getDriverPrefix() {
        return "jdbc:postgresql:";
    }

    @Override
    protected boolean isSecurityManagerEnabled() {
        return true;
    }

    @Override
    protected boolean isConfigRequired() {
        return false;
    }

    @Override
    public String getDefaultPort() {
        return "5432";
    }

    @Override
    protected String getDefaultDriverName() {
        return "postgresql.jar";
    }

    @Override
    public String getConnectionString() {
        final String connectionString = getDriverPrefix() + "//" + getHost() + ":" + getPort() + "/";
        if (hasAdditionalConnectionProperties()) {
            return connectionString + getAdditionalConnectionProperties();
        } else {
            return connectionString;
        }
    }
}