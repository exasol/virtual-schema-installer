package com.exasol.adapter.installer.dialect;

import static com.exasol.adapter.installer.dialect.Dialect.POSTGRESQL;

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
        if (dialect.equals(POSTGRESQL)) {
            return new PostgresDialectProfile(userInput);
        } else if (dialect.equals(Dialect.MYSQL)) {
            return new MysqlDialectProfile(userInput);
        } else if (dialect.equals(Dialect.SQLSERVER)) {
            return new SqlServerDialectProfile(userInput);
        } else {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-8")
                    .message("Unsupported dialect: {{dialect}}", dialect.name()).toString());
        }
    }
}