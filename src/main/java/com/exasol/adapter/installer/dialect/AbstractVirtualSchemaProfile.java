package com.exasol.adapter.installer.dialect;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.exasol.adapter.installer.File;
import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;

/**
 * Abstract base of Virtual Schema Profile.
 */
public abstract class AbstractVirtualSchemaProfile implements VirtualSchemaProfile {
    private static final String EXA_HOST_DEFAULT = "localhost";
    private static final String EXA_PORT_DEFAULT = "8563";
    private static final String EXA_BUCKET_FS_PORT_DEFAULT = "2580";
    private static final String EXA_SCHEMA_NAME_DEFAULT = "ADAPTER";
    private static final String SOURCE_HOST_DEFAULT = "localhost";
    private final ConfigCreator configCreator = new ConfigCreator();
    protected final UserInput userInput;

    /**
     * Instantiate a new {@link AbstractVirtualSchemaProfile}.
     *
     * @param userInput the user input
     */
    protected AbstractVirtualSchemaProfile(final UserInput userInput) {
        this.userInput = userInput;
    }

    @Override
    public String getJdbcDriverName() {
        return getOrDefault(this.userInput.getParameters(), JDBC_DRIVER_NAME_KEY, getDefaultDriverName());
    }

    @Override
    public File getJdbcConfig() {
        if (isConfigRequired()) {
            return this.configCreator.createConfig(getDialectName(), //
                    getJdbcDriverName(), //
                    getDriverMain(), //
                    getDriverPrefix(), //
                    isSecurityManagerEnabled());
        } else {
            return null;
        }
    }

    @Override
    public String getConnectionName() {
        return getOrDefault(this.userInput.getParameters(), EXA_CONNECTION_NAME_KEY, getDefaultConnectionName());
    }

    @Override
    public String getAdapterSchemaName() {
        return getOrDefault(this.userInput.getParameters(), EXA_SCHEMA_NAME_KEY, EXA_SCHEMA_NAME_DEFAULT);
    }

    @Override
    public String getAdapterName() {
        return getOrDefault(this.userInput.getParameters(), EXA_ADAPTER_NAME_KEY, getDefaultAdapterName());
    }

    @Override
    public String getBucketFsPort() {
        return getOrDefault(this.userInput.getParameters(), EXA_BUCKET_FS_PORT_KEY, EXA_BUCKET_FS_PORT_DEFAULT);
    }

    @Override
    public String getVirtualSchemaName() {
        return getOrDefault(this.userInput.getParameters(), EXA_VIRTUAL_SCHEMA_NAME_KEY, getDefaultVirtualSchemaName());
    }

    @Override
    public String getExaHost() {
        return getOrDefault(this.userInput.getParameters(), EXA_HOST_KEY, EXA_HOST_DEFAULT);
    }

    @Override
    public String getExaPort() {
        return getOrDefault(this.userInput.getParameters(), EXA_PORT_KEY, EXA_PORT_DEFAULT);
    }

    @Override
    public List<DialectProperty> getDialectProperties() {
        return Arrays.stream(this.userInput.getAdditionalProperties()) //
                .map(DialectPropertyParser::parseProperty) //
                .collect(Collectors.toList());
    }

    private String getDialectName() {
        return this.userInput.getDialect().name();
    }

    private String getDefaultAdapterName() {
        return getDialectName() + "_ADAPTER_SCRIPT";
    }

    private String getDefaultConnectionName() {
        return getDialectName() + "_JDBC_CONNECTION";
    }

    private String getDefaultVirtualSchemaName() {
        return getDialectName() + "_VIRTUAL_SCHEMA";
    }

    /**
     * Get host.
     *
     * @return host
     */
    protected String getHost() {
        return getOrDefault(this.userInput.getParameters(), SOURCE_HOST_KEY, SOURCE_HOST_DEFAULT);
    }

    /**
     * Get port.
     *
     * @return port
     */
    protected String getPort() {
        return getOrDefault(this.userInput.getParameters(), SOURCE_PORT_KEY, getDefaultPort());
    }

    /**
     * Get default port.
     *
     * @return default port
     */
    protected abstract String getDefaultPort();

    /**
     * Get default driver name.
     *
     * @return default driver name
     */
    protected abstract String getDefaultDriverName();

    /**
     * Get driver main.
     *
     * @return driver main
     */
    protected abstract String getDriverMain();

    /**
     * Get driver prefix.
     *
     * @return driver prefix
     */
    protected abstract String getDriverPrefix();

    /**
     * Get true if a security manager is enabled
     *
     * @return true if a security manager is enabled
     */
    protected abstract boolean isSecurityManagerEnabled();

    /**
     * Check if config is required.
     *
     * @return true if config is required
     */
    protected abstract boolean isConfigRequired();

    /**
     * Get a value from user input or return default if missing.
     *
     * @param userInput    user input
     * @param key          key
     * @param defaultValue default value
     * @return value
     */
    protected String getOrDefault(final Map<String, String> userInput, final String key, final String defaultValue) {
        if (!userInput.containsKey(key) || userInput.get(key) == null || userInput.get(key).isEmpty()) {
            return defaultValue;
        } else {
            return userInput.get(key);
        }
    }
}