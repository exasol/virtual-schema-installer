package com.exasol.adapter.installer;

/**
 * This interface provide a Virtual Schema JAR.
 */
public interface VirtualSchemaJarProvider {
    /**
     * Provide a Virtual Schema JAR file.
     *
     * @return new instance of {@link VirtualSchemaJar}
     */
    VirtualSchemaJar provideJar();
}