package com.exasol.adapter.installer;

import java.nio.file.Path;

/**
 * Represents a JAR file.
 */
public class Jar {
    private final String jarPath;
    private final String jarName;

    /**
     * Instantiate a new {@link Jar}.
     *
     * @param jarPath JAR path
     * @param jarName JAR name
     */
    public Jar(final String jarPath, final String jarName) {
        this.jarPath = jarPath;
        this.jarName = jarName;
    }

    /**
     * Get JAR name.
     *
     * @return JAR name
     */
    public String getJarName() {
        return this.jarName;
    }

    /**
     * Get path to the JAR file (including the JAR name).
     *
     * @return JAR path
     */
    public Path getJarPath() {
        return Path.of(this.jarPath, this.jarName);
    }
}