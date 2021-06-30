package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;

/**
 * A Mysql dialect profile.
 */
public class MysqlDialectProfile extends AbstractVirtualSchemaProfile {
    /**
     * Instantiate a new {@link MysqlDialectProfile}.
     *
     * @param userInput user input
     */
    public MysqlDialectProfile(final UserInput userInput) {
        super(userInput);
    }

    @Override
    protected String getDriverMain() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    protected String getDriverPrefix() {
        return "jdbc:mysql:";
    }

    @Override
    protected boolean isSecurityManagerEnabled() {
        return false;
    }

    @Override
    protected String getDefaultPort() {
        return "3306";
    }

    @Override
    protected String getDefaultDriverName() {
        return "mysql-connector-java.jar";
    }

    @Override
    protected boolean isConfigRequired() {
        return true;
    }
}