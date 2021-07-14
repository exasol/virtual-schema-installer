package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.FILE_SEPARATOR;
import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.TEMP_DIRECTORY;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.exasol.adapter.installer.dialect.Dialect;
import com.exasol.errorreporting.ExaError;

/**
 * Downloads a Virtual Schema JAR file.
 */
public class VirtualSchemaJarDownloader implements FileProvider {
    private final Dialect dialect;

    /**
     * Instantiates a new {@link VirtualSchemaJarDownloader}.
     *
     * @param dialect the dialect
     */
    public VirtualSchemaJarDownloader(final Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public File getFile() {
        final List<GHAsset> latestReleaseAssets = getGHAssets(this.dialect);
        final GHAsset jarAsset = getJarAsset(latestReleaseAssets);
        final String jarName = jarAsset.getName();
        final String filePath = TEMP_DIRECTORY + FILE_SEPARATOR + jarName;
        if (!Files.exists(Paths.get(filePath))) {
            downloadJarFile(jarAsset, jarName, filePath);
        }
        return new File(TEMP_DIRECTORY, jarName);
    }

    private void downloadJarFile(final GHAsset jarAsset, final String jarName, final String filePath) {
        try (final InputStream in = new URL(jarAsset.getBrowserDownloadUrl()).openStream()) {
            writeToDisk(filePath, in);
        } catch (final IOException exception) {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-5")
                    .message("Cannot download and save the file {{file}}.", jarName).toString(), exception);
        }
    }

    private List<GHAsset> getGHAssets(final Dialect dialect) {
        final String repositoryName = "exasol/" + dialect.name().toLowerCase() + "-virtual-schema";
        final GHRepository repository = getRepository(repositoryName);
        final Optional<List<GHAsset>> assets = getLatestReleaseAssets(repository);
        if (assets.isEmpty()) {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-3")
                    .message("Cannot find an available release in the repository {{repo}}.", repositoryName)
                    .toString());
        }
        return assets.get();
    }

    private GHAsset getJarAsset(final List<GHAsset> assets) {
        for (final GHAsset ghAsset : assets) {
            if (ghAsset.getName().contains("dist")) {
                return ghAsset;
            }
        }
        throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-4")
                .message("Cannot find an available JAR file in the repository.").toString());
    }

    private GHRepository getRepository(final String repositoryName) {
        try {
            final GitHub gitHub = GitHub.connect();
            return gitHub.getRepository(repositoryName);
        } catch (final IOException exception) {
            throw new InstallerException(
                    ExaError.messageBuilder("E-VS-INSTL-1")
                            .message("Cannot access the GitHub repository {{repo}}.", repositoryName).toString(),
                    exception);
        }
    }

    private Optional<List<GHAsset>> getLatestReleaseAssets(final GHRepository repository) {
        try {
            final GHRelease release = repository.getLatestRelease();
            return (release == null) ? Optional.empty() : Optional.of(release.listAssets().toList());
        } catch (final IOException exception) {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-2")
                    .message("GitHub connection problem happened during retrieving the latest release.").toString(),
                    exception);
        }
    }

    private void writeToDisk(final String filePath, final InputStream in) throws IOException {
        Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
    }
}