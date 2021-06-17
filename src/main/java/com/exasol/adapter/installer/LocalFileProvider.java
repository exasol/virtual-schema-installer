package com.exasol.adapter.installer;

/**
 * This class provide a locally stored JDBC driver JAR.
 */
public class LocalFileProvider implements FileProvider {
    private final File jarFile;

    /**
     * Instantiate a new {@link LocalFileProvider}
     *
     * @param path JDBC driver path
     * @param name JDBC driver name
     */
    public LocalFileProvider(final String path, final String name) {
        this.jarFile = new File(path, name);
    }

    @Override
    public File getFile() {
        return this.jarFile;
    }
}