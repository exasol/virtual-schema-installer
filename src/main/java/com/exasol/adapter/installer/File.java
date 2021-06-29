package com.exasol.adapter.installer;

import java.nio.file.Path;

/**
 * Represents a file.
 */
public class File {
    private final String path;
    private final String name;

    /**
     * Instantiate a new {@link File}.
     *
     * @param path path
     * @param name name
     */
    public File(final String path, final String name) {
        this.path = InputString.validate(path);
        this.name = InputString.validate(name);
    }

    /**
     * Get a file name.
     *
     * @return file name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get path to the file (including the file name).
     *
     * @return file path with the name
     */
    public Path getPathWithName() {
        return Path.of(this.path, this.name);
    }
}