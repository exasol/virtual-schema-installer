package com.exasol.adapter.installer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.exasol.errorreporting.ExaError;

/**
 * Validates user input.
 */
public class UserInputValidator {
    private UserInputValidator() {
    }

    /**
     * Validate an Exasol identifier.
     *
     * @param value the value
     * @return validated string
     */
    public static String validateExasolIdentifier(final String value) {
        final Set<Character> allowedSpecialCharacters = Set.of('_', '-', '.');
        return validateString(value, allowedSpecialCharacters);
    }

    /**
     * Validate a name of a file.
     *
     * @param value the value
     * @return validated string
     */
    public static String validateFileName(final String value) {
        final Set<Character> allowedSpecialCharacters = Set.of('_', '-', '.');
        return validateString(value, allowedSpecialCharacters);
    }

    /**
     * Validate a path.
     *
     * @param value the value
     * @return validated string
     */
    public static String validatePath(final String value) {
        final Set<Character> allowedSpecialCharacters = Set.of('_', '-', '\\', '/', ' ', '.');
        return validateString(value, allowedSpecialCharacters);
    }

    /**
     * Validate a literal string.
     *
     * @param literalString literal string
     * @return validated string
     */
    public static String validateLiteralString(final String literalString) {
        if (literalString.contains("'")) {
            throw getInstallerException(literalString, List.of('\''));
        } else {
            return literalString;
        }
    }

    /**
     * Validate a virtual schema property key.
     *
     * @param key property key
     * @return validated string
     */
    public static String validatePropertyKey(final String key) {
        final Set<Character> allowedSpecialCharacters = Set.of('_');
        return validateString(key, allowedSpecialCharacters);
    }

    private static String validateString(final String value, final Set<Character> allowedSpecialCharacters) {
        final List<Character> illegalCharacters = getIllegalCharacters(value, allowedSpecialCharacters);
        if (illegalCharacters.isEmpty()) {
            return value;
        } else {
            throw getInstallerException(value, illegalCharacters);
        }
    }

    private static InstallerException getInstallerException(final String value,
            final List<Character> illegalCharacters) {
        return new InstallerException(ExaError.messageBuilder("E-VS-INSTL-7")
                .message("Value {{value}} has illegal characters: {{illegalChars|uq}}", value,
                        illegalCharacters.toString())
                .toString());
    }

    private static List<Character> getIllegalCharacters(final String value,
            final Set<Character> allowedSpecialCharacters) {
        final List<Character> illegalCharacters = new ArrayList<>();
        for (final char ch : value.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch) && !allowedSpecialCharacters.contains(ch)) {
                illegalCharacters.add(ch);
            }
        }
        return illegalCharacters;
    }
}