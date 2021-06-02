package com.exasol.adapter.installer;

/**
 * This interface provides a {@link Jar}.
 */
public interface JarProvider {
    /**
     * Provide a {@link Jar}.
     *
     * @return new instance of {@link Jar}
     */
    Jar provideJar();
}