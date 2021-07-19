package com.exasol.adapter.installer.dialect;

import java.util.Objects;

import com.exasol.adapter.installer.UserInputValidator;

/**
 * Represents a single dialect property.
 */
public class DialectProperty {
    private final String key;
    private final String value;

    /**
     * Instantiate a new {@link DialectProperty}.
     *
     * @param key   key
     * @param value value
     */
    public DialectProperty(final String key, final String value) {
        this.key = UserInputValidator.validatePropertyKey(key);
        this.value = UserInputValidator.validateLiteralString(value);
    }

    /**
     * Get a key.
     *
     * @return key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Get a value.
     *
     * @return value
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DialectProperty)) {
            return false;
        }
        final DialectProperty that = (DialectProperty) o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    @Override
    public String toString() {
        return "DialectProperty{" + "key='" + this.key + '\'' + ", value='" + this.value + '\'' + '}';
    }
}