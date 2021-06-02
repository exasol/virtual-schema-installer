package com.exasol.adapter.installer;

import java.util.Map;

import lombok.Getter;

/**
 * The type User input.
 */
@Getter
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
}