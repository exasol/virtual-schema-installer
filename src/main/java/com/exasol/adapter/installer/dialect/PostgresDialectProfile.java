package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;

/**
 * A Postgres dialect profile.
 */
public class PostgresDialectProfile extends AbstractVirtualSchemaProfile {
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
    protected boolean getNoSecurity() {
        return false;
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
        return "postgres.jar";
    }
}