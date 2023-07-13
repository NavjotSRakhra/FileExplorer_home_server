package io.github.navjotsrakhra.filedownloader.fileSys.organization;

import io.github.navjotsrakhra.filedownloader.fileSys.Organizable;

public record File(String fileName, String filePath) implements Organizable {
}
