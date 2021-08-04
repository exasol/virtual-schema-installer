package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;

/**
 * A SQl Server dialect profile.
 */
public class SqlServerDialectProfile extends AbstractVirtualSchemaProfile {
    /**
     * Instantiate a new {@link SqlServerDialectProfile}.
     *
     * @param userInput user input
     */
    public SqlServerDialectProfile(final UserInput userInput) {
        super(userInput);
    }

    @Override
    protected String getDriverMain() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    protected String getDriverPrefix() {
        return "jdbc:sqlserver:";
    }

    @Override
    protected boolean isSecurityManagerEnabled() {
        return true;
    }

    @Override
    protected String getDefaultPort() {
        return "1433";
    }

    @Override
    protected String getDefaultDriverName() {
        return "mssql-jdbc.jar";
    }

    @Override
    protected boolean isConfigRequired() {
        return true;
    }

    @Override
    public String getConnectionString() {
        final String connectionString = getDriverPrefix() + "//" + getHost() + ":" + getPort();
        if (hasAdditionalConnectionProperties()) {
            return connectionString + ";" + getAdditionalConnectionProperties();
        } else {
            return connectionString;
        }
    }
}