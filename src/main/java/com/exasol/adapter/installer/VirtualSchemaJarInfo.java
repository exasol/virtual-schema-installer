package com.exasol.adapter.installer;

import lombok.Getter;

/**
 * Represents a Virtual Schema JAR file.
 */
@Getter
public class VirtualSchemaJarInfo {
    private final String jarPath;
    private final String jarName;

    /**
     * Instantiates a new {@link VirtualSchemaJarInfo}.
     *
     * @param jarPath the jar path
     * @param jarName the jar name
     */
    public VirtualSchemaJarInfo(final String jarPath, final String jarName) {
        this.jarPath = jarPath;
        this.jarName = jarName;
    }
}