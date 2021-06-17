package com.exasol.adapter.installer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.exasol.errorreporting.ExaError;

/**
 * Validates user input.
 */
public class InputString {
    private static final Set<Character> ALLOWED_SPECIAL_CHARS = Set.of('_', '-', '.', '/', '=', '\'', ':');

    private InputString() {
    }

    /**
     * Validate an input string.
     *
     * @param value the value
     * @return validated string
     */
    public static String validate(final String value) {
        final List<Character> illegalCharacters = getIllegalCharacters(value);
        if (illegalCharacters.isEmpty()) {
            return value;
        } else {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-7")
                    .message("Value {{value}} has illegal characters: {{illegalChars|uq}}", value,
                            illegalCharacters.toString())
                    .toString());
        }
    }

    private static List<Character> getIllegalCharacters(final String value) {
        final List<Character> illegalCharacters = new ArrayList<>();
        for (final char ch : value.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch) && !ALLOWED_SPECIAL_CHARS.contains(ch)) {
                illegalCharacters.add(ch);
            }
        }
        return illegalCharacters;
    }
}