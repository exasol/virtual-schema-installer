package com.exasol.adapter.installer.dialect;

public interface DialectProfile {
    String getDefaultPort();

    String getDefaultDatabaseName();

    String getDialectName();
}
