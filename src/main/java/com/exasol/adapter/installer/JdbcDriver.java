package com.exasol.adapter.installer;

public class JdbcDriver {
    private final File jarFile;
    private final File config;

    public JdbcDriver(final File jarFile, final File config) {
        this.jarFile = jarFile;
        this.config = config;
    }

    public File getJarFile() {
        return this.jarFile;
    }

    public boolean hasConfig() {
        return this.config != null;
    }

    public File getConfig() {
        return this.config;
    }
}