package com.exasol.adapter.installer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class reads configurable properties.
 */
public final class PropertyReader {
    private static final Logger LOGGER = Logger.getLogger(PropertyReader.class.getName());
    private final String pathToPropertyFile;

    /**
     * Create a new instance of {@link PropertyReader}.
     *
     * @param pathToPropertyFile path to a property file
     */
    public PropertyReader(final String pathToPropertyFile) {
        this.pathToPropertyFile = pathToPropertyFile;
    }

    /**
     * Read property by property name.
     *
     * @param propertyName propertyName
     * @return value
     */
    public String readProperty(final String propertyName) {
        final Optional<String> property = readFromFile(propertyName);
        if (property.isPresent()) {
            LOGGER.fine(() -> "Using property '" + propertyName + "' from the '" + this.pathToPropertyFile + "' file.");
            return property.get();
        } else {
            LOGGER.fine(() -> "Property '" + propertyName + "' is not found in the '" + this.pathToPropertyFile
                    + "' file.");
            return readPropertyFromConsole(propertyName);
        }
    }

    private Optional<String> readFromFile(final String propertyName) {
        try (final InputStream stream = new FileInputStream(this.pathToPropertyFile)) {
            final var properties = new Properties();
            properties.load(stream);
            final String value = properties.getProperty(propertyName);
            if (value == null) {
                return Optional.empty();
            } else {
                return Optional.of(value);
            }
        } catch (final IOException exception) {
            return Optional.empty();
        }
    }

    private String readPropertyFromConsole(final String propertyName) {
        return System.console().readLine("Enter " + propertyName.replace("_", " ") + ": ");
    }
}