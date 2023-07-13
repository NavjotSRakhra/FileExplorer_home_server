package io.github.navjotsrakhra.filedownloader.fileSys.util;

import io.github.navjotsrakhra.filedownloader.fileSys.organization.Directory;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.File;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.Organizable;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static io.github.navjotsrakhra.filedownloader.logging.LoggingController.LOG;

public class OrganizableUtils {
    public static Organizable encodeToURIValue(Organizable file) {
        Objects.requireNonNull(file);

        if (file instanceof Directory f) {
            return new Directory(f.directoryName(), UriUtils.encode(f.directoryPath(), StandardCharsets.UTF_8));
        } else if (file instanceof File f) {
            return new File(f.fileName(), UriUtils.encode(f.filePath(), StandardCharsets.UTF_8));
        }

        LOG.error("Organization not implemented");
        throw new IllegalArgumentException("Organizable implementation not supported");
    }
}
