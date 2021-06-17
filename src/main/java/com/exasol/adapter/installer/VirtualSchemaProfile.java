package com.exasol.adapter.installer;

import java.util.List;

public interface VirtualSchemaProfile {
    String getJdbcDriverName();

    File getJdbcConfig();

    String getBucketFsPort();

    String getExaPort();

    String getExaIp();

    String getBucketName();

    String getAdapterSchemaName();

    String getAdapterName();

    String getConnectionName();

    String getConnectionString();

    String getVirtualSchemaName();

    List<String> getDialectSpecificProperties();
}