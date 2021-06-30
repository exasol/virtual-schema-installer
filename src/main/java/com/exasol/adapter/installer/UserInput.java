package com.exasol.adapter.installer;

import java.util.Map;

import com.exasol.adapter.installer.dialect.Dialect;

/**
 * The type User input.
 */
public class UserInput {
    private final Dialect dialect;
    private final String[] additionalProperties;
    private final Map<String, String> parameters;

    /**
     * Instantiate a new {@link UserInput}.
     *
     * @param dialectName          the dialect name
     * @param parameters           the parameters
     * @param additionalProperties the additional properties
     */
    public UserInput(final String dialectName, final Map<String, String> parameters,
            final String[] additionalProperties) {
        this.dialect = Dialect.valueOf(dialectName.toUpperCase());
        this.parameters = parameters;
        this.additionalProperties = additionalProperties;
    }

    /**
     * Get dialect.
     *
     * @return the dialect
     */
    public Dialect getDialect() {
        return this.dialect;
    }

    /**
     * Get additional properties.
     *
     * @return the additional properties
     */
    public String[] getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * Get parameters.
     *
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return this.parameters;
    }
}