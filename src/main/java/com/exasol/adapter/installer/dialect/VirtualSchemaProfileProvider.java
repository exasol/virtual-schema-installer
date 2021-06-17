package com.exasol.adapter.installer.dialect;

import static com.exasol.adapter.installer.dialect.Dialect.POSTGRESQL;

import com.exasol.adapter.installer.InstallerException;
import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;
import com.exasol.errorreporting.ExaError;

public class VirtualSchemaProfileProvider {
    private VirtualSchemaProfileProvider() {
    }

    public static VirtualSchemaProfile provideProfile(final UserInput userInput) {
        final Dialect dialect = userInput.getDialect();
        if (dialect.equals(POSTGRESQL)) {
            return new PostgresDialectProfile(userInput);
        } else if (dialect.equals(Dialect.MYSQL)) {
            return new MysqlDialectProfile(userInput);
        } else {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-8")
                    .message("Unsupported dialect: {{dialect}}", dialect.name()).toString());
        }
    }
}