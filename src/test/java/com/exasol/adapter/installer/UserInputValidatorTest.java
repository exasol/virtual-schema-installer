package com.exasol.adapter.installer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UserInputValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = { "TEST_ADAPTER", "my_test_schema" })
    void testValidateAllowedExasolIdentifier(final String value) {
        assertDoesNotThrow(() -> UserInputValidator.validateExasolIdentifier(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "MY ADAPTER", "SCHEMA!NAME" })
    void testValidateNotAllowedExasolIdentifier(final String value) {
        assertThrows(InstallerException.class, () -> UserInputValidator.validateExasolIdentifier(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "some-jdbc-driver-1.4.5.jar", "db2jcc4.jar", "mssql-jdbc-7.2.2.jre8.jar" })
    void testValidateAllowedFileNames(final String value) {
        assertDoesNotThrow(() -> UserInputValidator.validateFileName(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "some;file-name", "^_^", "path/file.jar" })
    void testValidateNotAllowedFileNames(final String value) {
        assertThrows(InstallerException.class, () -> UserInputValidator.validateFileName(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "linux/path", "windows\\path", ".directory", ".directory/my folder" })
    void testValidateAllowedPath(final String value) {
        assertDoesNotThrow(() -> UserInputValidator.validatePath(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "not allowed!", "$path" })
    void testValidateNotAllowedPath(final String value) {
        assertThrows(InstallerException.class, () -> UserInputValidator.validatePath(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "My string!@#$%^&*()>?}{/123", "тест" })
    void testValidateAllowedLiteralString(final String value) {
        assertDoesNotThrow(() -> UserInputValidator.validateLiteralString(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "'string'", "not''allowed''''" })
    void testValidateNotAllowedLiteralString(final String value) {
        assertThrows(InstallerException.class, () -> UserInputValidator.validateLiteralString(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "SOME_PROPERTY", "another_property_123" })
    void testValidateAllowedPropertyKey(final String value) {
        assertDoesNotThrow(() -> UserInputValidator.validatePropertyKey(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "SOME PROPERTY", "PROPERTY='value'" })
    void testValidateNotAllowedPropertyKey(final String value) {
        assertThrows(InstallerException.class, () -> UserInputValidator.validatePropertyKey(value));
    }
}