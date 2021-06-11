package com.exasol.adapter.installer;

import java.util.Map;

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

    public Dialect getDialect() {
        return this.dialect;
    }

    public String[] getAdditionalProperties() {
        return this.additionalProperties;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}