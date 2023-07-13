package io.github.navjotsrakhra.filedownloader.fileSys.util;

import java.nio.file.Path;

public class PathUtils {
    public static boolean isChildOfPath(String rootPath, String childCheckPath) {
        return Path.of(childCheckPath).normalize().toAbsolutePath().startsWith(Path.of(rootPath).normalize().toAbsolutePath());
    }
}
