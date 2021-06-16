package com.exasol.adapter.installer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputStringTest {
    @ParameterizedTest
    @ValueSource(strings = { "some-jdbc-driver-1.4.5.jar", "SOME_ADAPTER_SCRIPT", "some/path",
            "SOME_PROPERTY='MY_PROPERTY'" })
    void testAllowedValues(final String value) {
        assertDoesNotThrow(() -> InputString.validate(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "some;file-name", "^_^", "$value" })
    void testNotAllowedValues(final String value) {
        assertThrows(InstallerException.class, () -> InputString.validate(value));
    }
}