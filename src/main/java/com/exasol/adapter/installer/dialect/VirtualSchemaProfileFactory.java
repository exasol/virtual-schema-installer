package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.*;
import com.exasol.errorreporting.ExaError;

/**
 * A factory class to provide a Virtual Schema profile.
 */
public class VirtualSchemaProfileFactory {
    private VirtualSchemaProfileFactory() {
    }

    /**
     * Provide a new virtual schema profile.
     *
     * @param userInput user input
     * @return virtual schema profile
     */
    public static VirtualSchemaProfile getProfileFor(final UserInput userInput) {
        final Dialect dialect = userInput.getDialect();
        switch (dialect) {
        case POSTGRESQL:
            return new PostgresDialectProfile(userInput);
        case MYSQL:
            return new MysqlDialectProfile(userInput);
        case SQLSERVER:
            return new SqlServerDialectProfile(userInput);
        case DB2:
            return new Db2DialectProfile(userInput);
        case ELASTICSEARCH:
            return new ElasticSearchDialectProfile(userInput);
        case ORACLE:
            return new OracleDialectProfile(userInput);
        default:
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-8")
                    .message("Unsupported dialect: {{dialect}}", dialect.name()).toString());
        }
    }
}