package com.exasol.adapter.installer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.exasol.errorreporting.ExaError;

/**
 * Contains validated user input.
 */
public class InputString {
    private static final Set<Character> ALLOWED_CHARS = Set.of('_', '-', '.', '/', '=', '\'');
    private final String value;

    private InputString(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Create an input string.
     *
     * @param value the value
     * @return the input string
     */
    public static InputString of(final String value) {
        final List<Character> illegalCharacters = getIllegalCharacters(value);
        if (illegalCharacters.isEmpty()) {
            return new InputString(value);
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
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch) && !ALLOWED_CHARS.contains(ch)) {
                illegalCharacters.add(ch);
            }
        }
        return illegalCharacters;
    }
}