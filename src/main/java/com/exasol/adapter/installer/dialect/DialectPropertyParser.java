package com.exasol.adapter.installer.dialect;

import com.exasol.adapter.installer.InstallerException;
import com.exasol.errorreporting.ExaError;

/**
 * Parser for dialect properties.
 */
class DialectPropertyParser {
    private DialectPropertyParser() {
    }

    /**
     * Parse dialect property from a string.
     *
     * @param property property as string
     * @return dialect property
     */
    public static DialectProperty parseProperty(final String property) {
        final int firstEqualityMark = property.indexOf('=');
        if (firstEqualityMark < 1) {
            throw createException(property);
        } else {
            return createDialectProperty(property, property.substring(0, firstEqualityMark),
                    property.substring(firstEqualityMark + 1));
        }
    }

    private static InstallerException createException(final String property) {
        return new InstallerException(ExaError.messageBuilder("E-VS-INSTL-10")
                .message("Can't parse dialect property: {{property}}", property)
                .mitigation("Dialect property must have the following format: <PROPERTY>:'<VALUE>'").toString());
    }

    private static DialectProperty createDialectProperty(final String property, final String key, final String value) {
        if (value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\'') {
            throw createException(property);
        } else {
            return new DialectProperty(key, value.substring(1, value.length() - 1));
        }
    }
}