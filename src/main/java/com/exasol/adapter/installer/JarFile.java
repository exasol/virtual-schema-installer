package com.exasol.adapter.installer;

import java.nio.file.Path;

/**
 * Represents a JAR file.
 */
public class JarFile {
    private final InputString path;
    private final InputString name;

    /**
     * Instantiate a new {@link JarFile}.
     *
     * @param path JAR path
     * @param name JAR name
     */
    public JarFile(final String path, final String name) {
        this.path = InputString.of(path);
        this.name = InputString.of(name);
    }

    /**
     * Get JAR name.
     *
     * @return JAR name
     */
    public String getName() {
        return this.name.toString();
    }

    /**
     * Get path to the JAR file (including the JAR name).
     *
     * @return JAR path
     */
    public Path getPath() {
        return Path.of(this.path.toString(), this.name.toString());
    }
}