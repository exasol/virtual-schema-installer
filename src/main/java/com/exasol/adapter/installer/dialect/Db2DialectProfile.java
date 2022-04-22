package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;

/**
 * A DB2 dialect profile.
 */
public class Db2DialectProfile extends AbstractVirtualSchemaProfile implements VirtualSchemaProfile {
    /**
     * Instantiate a new {@link Db2DialectProfile}.
     *
     * @param userInput user input
     */
    public Db2DialectProfile(final UserInput userInput) {
        super(userInput);
    }

    @Override
    protected String getDriverMain() {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    protected String getDriverPrefix() {
        return "jdbc:db2:";
    }

    @Override
    protected boolean isSecurityManagerEnabled() {
        return false;
    }

    @Override
    protected String getDefaultPort() {
        return "50000";
    }

    @Override
    protected String getDefaultDriverName() {
        return "jcc.jar";
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