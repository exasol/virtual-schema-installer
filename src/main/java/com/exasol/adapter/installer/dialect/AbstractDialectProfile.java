package com.exasol.adapter.installer.dialect;

import java.util.Map;

public abstract class AbstractDialectProfile implements DialectProfile {
    protected static final String DEFAULT_PORT_KEY = "DEFAULT_PORT";
    protected static final String DEFAULT_DATABASE_KEY = "DEFAULT_DATABASE";
    private final String dialectName;

    protected AbstractDialectProfile(final String dialectName) {
        this.dialectName = dialectName;
    }

    @Override
    public String getDefaultPort() {
        return getDialectParameters().getOrDefault(DEFAULT_PORT_KEY, "");
    }

    @Override
    public String getDefaultDatabaseName() {
        return getDialectParameters().getOrDefault(DEFAULT_DATABASE_KEY, "");
    }

    @Override
    public String getDialectName() {
        return this.dialectName;
    }

    protected abstract Map<String, String> getDialectParameters();
}