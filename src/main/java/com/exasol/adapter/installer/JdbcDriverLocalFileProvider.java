package com.exasol.adapter.installer;

/**
 * This class provide a locally stored JDBC driver JAR.
 */
public class JdbcDriverLocalFileProvider implements JdbcDriverJarProvider {
    private final Jar jar;

    /**
     * Instantiates a new {@link JdbcDriverLocalFileProvider}
     *
     * @param jdbcDriverPath JDBC driver path
     * @param jdbcDriverName JDBC driver name
     */
    public JdbcDriverLocalFileProvider(final String jdbcDriverPath, final String jdbcDriverName) {
        this.jar = new Jar(jdbcDriverPath, jdbcDriverName);
    }

    @Override
    public Jar provideJar() {
        return this.jar;
    }
}