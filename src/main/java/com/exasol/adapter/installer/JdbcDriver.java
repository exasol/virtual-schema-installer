package com.exasol.adapter.installer;

/**
 * Represents a JDBC driver to install to a Virtual Schema.
 */
public class JdbcDriver {
    private final File jarFile;
    private final File config;

    /**
     * Instantiate a new {@link JdbcDriver}.
     *
     * @param jarFile JAR file
     * @param config  config
     */
    public JdbcDriver(final File jarFile, final File config) {
        this.jarFile = jarFile;
        this.config = config;
    }

    /**
     * Get a JAR file.
     *
     * @return JAR file
     */
    public File getJarFile() {
        return this.jarFile;
    }

    /**
     * Check if this JDBC driver has a config file.
     *
     * @return true if this JDBC driver has a config file
     */
    public boolean hasConfig() {
        return this.config != null;
    }

    /**
     * Get a config.
     *
     * @return config
     */
    public File getConfig() {
        return this.config;
    }
}