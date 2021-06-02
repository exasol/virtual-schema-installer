package com.exasol.adapter.installer;

/**
 * The Installer exception.
 */
public class InstallerException extends RuntimeException {
    private static final long serialVersionUID = 2135321592579639349L;

    /**
     * Instantiate a new {@link InstallerException}.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InstallerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiate a new {@link InstallerException}.
     *
     * @param message the message
     */
    public InstallerException(final String message) {
        super(message);
    }
}