package io.github.navjotsrakhra.filedownloader.fileSys.organization;

import io.github.navjotsrakhra.filedownloader.fileSys.Organizable;

public record Directory(String directoryName, String directoryPath) implements Organizable {
}
