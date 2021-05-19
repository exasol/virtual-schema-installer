package com.exasol.adapter.installer;

import java.util.Map;

import lombok.Getter;

@Getter
public class UserInput {
    private final Dialect dialect;
    private final String[] additionalProperties;
    private final Map<String, String> parameters;

    public UserInput(final String dialectName, final Map<String, String> parameters,
            final String[] additionalProperties) {
        this.dialect = Dialect.valueOf(dialectName.toUpperCase());
        this.parameters = parameters;
        this.additionalProperties = additionalProperties;
    }
}