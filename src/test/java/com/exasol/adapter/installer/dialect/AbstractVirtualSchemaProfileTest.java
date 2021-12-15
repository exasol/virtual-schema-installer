package com.exasol.adapter.installer.dialect;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.elasticsearch.core.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.installer.UserInput;

class AbstractVirtualSchemaProfileTest {

    private TestingVirtualSchemaProfile virtualSchemaProfile;

    @BeforeEach
    void setUp() {
        this.virtualSchemaProfile = new TestingVirtualSchemaProfile(null);
    }

    @Test
    void testGetOrDefaultEmptyInputReturnsDefault() {
        assertThat(this.virtualSchemaProfile.getOrDefault(emptyMap(), "key", "default"), equalTo("default"));
    }

    @Test
    void testGetOrDefaultUnknownKeyReturnsDefault() {
        assertThat(this.virtualSchemaProfile.getOrDefault(Map.of("key", "value"), "wrongkey", "default"),
                equalTo("default"));
    }

    @Test
    void testGetOrDefaultKeyReturnsValue() {
        assertThat(this.virtualSchemaProfile.getOrDefault(Map.of("key", "value"), "key", "default"), equalTo("value"));
    }

    @Test
    void testGetOrDefaultEmptyValueReturnsDefault() {
        assertThat(this.virtualSchemaProfile.getOrDefault(Map.of("key", ""), "key", "default"), equalTo("default"));
    }

    @Test
    void testGetOrDefaultBlankValueReturnsValue() {
        assertThat(this.virtualSchemaProfile.getOrDefault(Map.of("key", " \t\n"), "key", "default"), equalTo(" \t\n"));
    }

    private static class TestingVirtualSchemaProfile extends AbstractVirtualSchemaProfile {
        protected TestingVirtualSchemaProfile(final UserInput userInput) {
            super(userInput);
        }

        @Override
        public String getConnectionString() {
            return null;
        }

        @Override
        protected String getDefaultPort() {
            return null;
        }

        @Override
        protected String getDefaultDriverName() {
            return null;
        }

        @Override
        protected String getDriverMain() {
            return null;
        }

        @Override
        protected String getDriverPrefix() {
            return null;
        }

        @Override
        protected boolean isSecurityManagerEnabled() {
            return false;
        }

        @Override
        protected boolean isConfigRequired() {
            return false;
        }
    }
}
