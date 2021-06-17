package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.UserInput;

public class MysqlDialectProfile extends AbstractVirtualSchemaProfile {
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
    protected String getNoSecurity() {
        return "YES";
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