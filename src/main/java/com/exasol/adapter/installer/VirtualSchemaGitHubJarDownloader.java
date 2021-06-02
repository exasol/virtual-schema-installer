package com.exasol.adapter.installer;

import static com.exasol.adapter.installer.VirtualSchemaInstallerConstants.FILE_SEPARATOR;

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

import com.exasol.errorreporting.ExaError;

/**
 * Downloads a Virtual Schema JAR file.
 */
public class VirtualSchemaGitHubJarDownloader implements VirtualSchemaJarProvider {
    private final Dialect dialect;

    /**
     * Instantiates a new {@link VirtualSchemaGitHubJarDownloader}.
     *
     * @param dialect the dialect
     */
    public VirtualSchemaGitHubJarDownloader(final Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public VirtualSchemaJar provideJar() {
        final List<GHAsset> assets = getGHAssets(this.dialect);
        final GHAsset ghAsset = getGHAsset(assets);
        final String tempDirectory = System.getProperty("java.io.tmpdir");
        final String assetName = ghAsset.getName();
        final String filePath = tempDirectory + FILE_SEPARATOR + assetName;
        try (final InputStream in = new URL(ghAsset.getBrowserDownloadUrl()).openStream()) {
            writeToDisk(filePath, in);
        } catch (final IOException exception) {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-5")
                    .message("Cannot download and save the file {{file}}.", assetName).toString(), exception);
        }
        return new VirtualSchemaJar(tempDirectory, assetName);
    }

    private List<GHAsset> getGHAssets(final Dialect dialect) {
        final String repositoryName = "exasol/" + dialect.name().toLowerCase() + "-virtual-schema";
        final GHRepository repository = getRepository(repositoryName);
        final Optional<List<GHAsset>> assets = getAssets(repository);
        if (assets.isEmpty()) {
            throw new InstallerException(ExaError.messageBuilder("E-VS-INSTL-3")
                    .message("Cannot find an available release in the repository {{repo}}.", repositoryName)
                    .toString());
        }
        return assets.get();
    }

    private GHAsset getGHAsset(final List<GHAsset> assets) {
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

    private Optional<List<GHAsset>> getAssets(final GHRepository repository) {
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