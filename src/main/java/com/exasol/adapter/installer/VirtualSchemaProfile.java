package com.exasol.adapter.installer;

import java.util.List;

import com.exasol.adapter.installer.dialect.DialectProperty;

/**
 * Virtual Schema Profile is dialect-based information need to install a Virtual Schema.
 */
public interface VirtualSchemaProfile {
    /**
     * Get a JDBC driver name.
     *
     * @return JDBC driver name
     */
    String getJdbcDriverName();

    /**
     * Get a JDBC config.
     *
     * @return JDBC config
     */
    File getJdbcConfig();

    /**
     * Get a bucketFS port.
     *
     * @return bucketFS port
     */
    String getBucketFsPort();

    /**
     * Get an Exasol port.
     *
     * @return Exasol port
     */
    String getExaPort();

    /**
     * Get an Exasol ip.
     *
     * @return Exasol ip
     */
    String getExaIp();

    /**
     * Get an adapter schema name.
     *
     * @return adapter schema name
     */
    String getAdapterSchemaName();

    /**
     * Get an adapter name.
     *
     * @return adapter name
     */
    String getAdapterName();

    /**
     * Get a connection name.
     *
     * @return connection name
     */
    String getConnectionName();

    /**
     * Get a connection string.
     *
     * @return connection string
     */
    String getConnectionString();

    /**
     * Get a virtual schema name.
     *
     * @return virtual schema name
     */
    String getVirtualSchemaName();

    /**
     * Get dialect-specific properties.
     *
     * @return dialect-specific properties
     */
    List<DialectProperty> getDialectProperties();
}