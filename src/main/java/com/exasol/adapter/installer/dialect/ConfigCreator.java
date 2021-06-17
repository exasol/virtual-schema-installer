package com.exasol.adapter.installer.dialect;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.FILE_SEPARATOR;
import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.TEMP_DIRECTORY;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.exasol.adapter.installer.File;
import com.exasol.adapter.installer.InstallerException;
import com.exasol.errorreporting.ExaError;

public class ConfigCreator {
    private static final String FILE_NAME = "settings.cfg";

    public File createConfig(final String dialectName, final String jarName, final String driverMain,
            final String driverPrefix, final String noSecurity) {
        final String configString = createConfigString(dialectName, jarName, driverMain, driverPrefix, noSecurity);
        createTemporaryFile(configString);
        return new File(TEMP_DIRECTORY, FILE_NAME);
    }

    private void createTemporaryFile(final String configString) {
        try (final BufferedWriter writer = new BufferedWriter(
                new FileWriter(TEMP_DIRECTORY + FILE_SEPARATOR + FILE_NAME))) {
            writer.write(configString);
        } catch (final IOException exception) {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-9") //
                    .message("Cannot create a " + FILE_NAME + " file.").toString(), exception);
        }
    }

    private String createConfigString(final String dialectName, final String jarName, final String driverMain,
            final String driverPrefix, final String noSecurity) {
        return "DRIVERNAME=" + dialectName + "\n" //
                + "JAR=" + jarName + "\n" //
                + "DRIVERMAIN=" + driverMain + "\n" //
                + "PREFIX=" + driverPrefix + "\n" //
                + "NOSECURITY=" + noSecurity + "\n" //
                + "FETCHSIZE=100000\n" //
                + "INSERTSIZE=-1\n\n";
    }
}
