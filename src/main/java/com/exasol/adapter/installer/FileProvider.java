package com.exasol.adapter.installer;

/**
 * This interface provides a {@link File}.
 */
public interface FileProvider {
    /**
     * Get a {@link File}.
     *
     * @return new instance of {@link File}
     */
    File getFile();
}