package com.exasol.adapter.installer.dialect;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.adapter.installer.InstallerException;

class DialectPropertyParserTest {
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { //
            "TABLE_FILTER='Table_1, Table2';TABLE_FILTER;Table_1, Table2", //
            "POSTGRESQL_IDENTIFIER_MAPPING='PRESERVE_ORIGINAL_CASE';POSTGRESQL_IDENTIFIER_MAPPING;PRESERVE_ORIGINAL_CASE", //
    })
    void testParseProperty(final String property, final String expectedKey, final String expectedValue) {
        assertThat(DialectPropertyParser.parseProperty(property),
                equalTo(new DialectProperty(expectedKey, expectedValue)));
    }

    @ParameterizedTest
    @CsvSource(value = { //
            "TABLE_FILTER='Table_1, Table2';", //
            "TABLE_FILTER=Table_1, Table2", //
            "TABLE_FILTER", //
    })
    void testParseInvalidProperty(final String property) {
        final InstallerException exception = assertThrows(InstallerException.class,
                () -> DialectPropertyParser.parseProperty(property));
        assertThat(exception.getMessage(), containsString("E-VS-INSTL-10"));
    }
}