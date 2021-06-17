package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;

public class PostgresDialectProfile extends AbstractVirtualSchemaProfile {
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
    protected String getNoSecurity() {
        return "NO";
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