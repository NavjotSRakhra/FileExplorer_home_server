package io.github.navjotsrakhra.filedownloader.output;

import io.github.navjotsrakhra.filedownloader.fileSys.organization.Directory;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.File;

import java.util.List;

public record FilesAndDirectories(List<File> files, List<Directory> directories) {
}
