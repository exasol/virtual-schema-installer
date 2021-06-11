package com.exasol.adapter.installer;

/**
 * This interface provides a {@link JarFile}.
 */
public interface JarProvider {
    /**
     * Get a {@link JarFile}.
     *
     * @return new instance of {@link JarFile}
     */
    JarFile getJar();
}