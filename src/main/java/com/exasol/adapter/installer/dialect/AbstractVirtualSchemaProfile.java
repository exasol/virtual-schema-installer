package com.exasol.adapter.installer.dialect;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.exasol.adapter.installer.File;
import com.exasol.adapter.installer.UserInput;
import com.exasol.adapter.installer.VirtualSchemaProfile;

public abstract class AbstractVirtualSchemaProfile implements VirtualSchemaProfile {
    private static final String EXA_IP_DEFAULT = "localhost";
    private static final String EXA_PORT_DEFAULT = "8563";
    private static final String EXA_BUCKET_FS_PORT_DEFAULT = "2580";
    private static final String EXA_BUCKET_NAME_DEFAULT = "default";
    private static final String EXA_SCHEMA_NAME_DEFAULT = "ADAPTER";
    private static final String SOURCE_IP_DEFAULT = "localhost";

    private final ConfigCreator configCreator = new ConfigCreator();
    protected final UserInput userInput;

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
                    getNoSecurity());
        } else {
            return null;
        }
    }

    @Override
    public String getConnectionString() {
        return getDriverPrefix() + "//" + getIp() + ":" + getPort() + "/";
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
    public String getBucketName() {
        return EXA_BUCKET_NAME_DEFAULT;
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
    public String getExaIp() {
        return getOrDefault(this.userInput.getParameters(), EXA_IP_KEY, EXA_IP_DEFAULT);
    }

    @Override
    public String getExaPort() {
        return getOrDefault(this.userInput.getParameters(), EXA_PORT_KEY, EXA_PORT_DEFAULT);
    }

    @Override
    public List<String> getDialectSpecificProperties() {
        return Arrays.stream(this.userInput.getAdditionalProperties()).collect(Collectors.toList());
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

    private String getIp() {
        return getOrDefault(this.userInput.getParameters(), SOURCE_IP_KEY, SOURCE_IP_DEFAULT);
    }

    private String getPort() {
        return getOrDefault(this.userInput.getParameters(), SOURCE_PORT_KEY, getDefaultPort());
    }

    protected abstract String getDefaultPort();

    protected abstract String getDefaultDriverName();

    protected abstract String getDriverMain();

    protected abstract String getDriverPrefix();

    protected abstract String getNoSecurity();

    protected abstract boolean isConfigRequired();

    protected String getOrDefault(final Map<String, String> userInput, final String key, final String defaultValue) {
        if (!userInput.containsKey(key) || userInput.get(key) == null || userInput.get(key).isEmpty()) {
            return defaultValue;
        } else {
            return userInput.get(key);
        }
    }
}