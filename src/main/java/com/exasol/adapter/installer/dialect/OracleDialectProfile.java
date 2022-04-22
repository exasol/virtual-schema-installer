package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;

/**
 * An Oracle dialect profile.
 */
public class OracleDialectProfile extends AbstractVirtualSchemaProfile implements VirtualSchemaProfile {
    /**
     * Instantiate a new {@link OracleDialectProfile}.
     *
     * @param userInput user input
     */
    public OracleDialectProfile(final UserInput userInput) {
        super(userInput);
    }

    @Override
    protected String getDriverMain() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    protected String getDriverPrefix() {
        return "jdbc:oracle:thin:";
    }

    @Override
    protected boolean isSecurityManagerEnabled() {
        return true;
    }

    @Override
    protected String getDefaultPort() {
        return "1521";
    }

    @Override
    protected String getDefaultDriverName() {
        return "ojdbc8.jar";
    }

    @Override
    protected boolean isConfigRequired() {
        return true;
    }

    @Override
    public String getConnectionString() {
        final String connectionString = getDriverPrefix() + "@//" + getHost() + ":" + getPort();
        if (hasAdditionalConnectionProperties()) {
            return connectionString + "/" + getAdditionalConnectionProperties();
        } else {
            return connectionString;
        }
    }
}