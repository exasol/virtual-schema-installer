package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.InstallerException;
import com.exasol.errorreporting.ExaError;

public class DialectProfileProvider {
    public static DialectProfile provideProfile(final Dialect dialect) {
        if (dialect.equals(Dialect.POSTGRESQL)) {
            return new PostgresDialectProfile(dialect);
        } else if (dialect.equals(Dialect.MYSQL)) {
            return new MysqlDialectProfile(dialect);
        } else {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-8")
                    .message("Unsupported dialect: {{dialect}}", dialect.name()).toString());
        }
    }
}