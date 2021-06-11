package com.exasol.adapter.installer;

/**
 * This class provide a locally stored JDBC driver JAR.
 */
public class JdbcDriverLocalFileProvider implements JdbcDriverJarProvider {
    private final JarFile jarFile;

    /**
     * Instantiate a new {@link JdbcDriverLocalFileProvider}
     *
     * @param jdbcDriverPath JDBC driver path
     * @param jdbcDriverName JDBC driver name
     */
    public JdbcDriverLocalFileProvider(final String jdbcDriverPath, final String jdbcDriverName) {
        this.jarFile = new JarFile(jdbcDriverPath, jdbcDriverName);
    }

    @Override
    public JarFile getJar() {
        return this.jarFile;
    }
}