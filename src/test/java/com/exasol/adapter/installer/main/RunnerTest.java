package com.exasol.adapter.installer.main;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.elasticsearch.core.Map;
import org.junit.jupiter.api.Test;

class RunnerTest {

    @Test
    void testGetOrDefaultEmptyInputReturnsDefault() {
        assertThat(Runner.getOrDefault(emptyMap(), "key", "default"), equalTo("default"));
    }

    @Test
    void testGetOrDefaultUnknownKeyReturnsDefault() {
        assertThat(Runner.getOrDefault(Map.of("key", "value"), "wrongkey", "default"), equalTo("default"));
    }

    @Test
    void testGetOrDefaultKeyReturnsValue() {
        assertThat(Runner.getOrDefault(Map.of("key", "value"), "key", "default"), equalTo("value"));
    }

    @Test
    void testGetOrDefaultEmptyValueReturnsDefault() {
        assertThat(Runner.getOrDefault(Map.of("key", ""), "key", "default"), equalTo("default"));
    }

    @Test
    void testGetOrDefaultBlankValueReturnsValue() {
        assertThat(Runner.getOrDefault(Map.of("key", " \t\n"), "key", "default"), equalTo(" \t\n"));
    }
}
