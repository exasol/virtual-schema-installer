package com.exasol.adapter.installer.dialect;

import java.util.Map;

public class MysqlDialectProfile extends AbstractDialectProfile {
    private final Map<String, String> dialectParameters = Map.of( //
            DEFAULT_PORT_KEY, "3306", //
            DEFAULT_DATABASE_KEY, "" //
    );

    public MysqlDialectProfile(final Dialect dialect) {
        super(dialect.name());
    }

    @Override
    protected Map<String, String> getDialectParameters() {
        return this.dialectParameters;
    }
}
