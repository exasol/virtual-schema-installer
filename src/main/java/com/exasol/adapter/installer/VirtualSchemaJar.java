package com.exasol.adapter.installer;

import java.nio.file.Path;

/**
 * Represents a Virtual Schema JAR file.
 */
public class VirtualSchemaJar {
    private final String jarPath;
    private final String jarName;

    /**
     * Instantiate a new {@link VirtualSchemaJar}.
     *
     * @param jarPath the jar path
     * @param jarName the jar name
     */
    public VirtualSchemaJar(final String jarPath, final String jarName) {
        this.jarPath = jarPath;
        this.jarName = jarName;
    }

    /**
     * Gets jar path.
     *
     * @return the jar path
     */
    public String getJarPath() {
        return this.jarPath;
    }

    /**
     * Gets jar name.
     *
     * @return the jar name
     */
    public String getJarName() {
        return this.jarName;
    }

    public Path getVirtualSchemaPath() {
        return Path.of(this.jarPath, this.jarName);
    }
}