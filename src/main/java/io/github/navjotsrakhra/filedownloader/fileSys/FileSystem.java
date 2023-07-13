package io.github.navjotsrakhra.filedownloader.fileSys;

import io.github.navjotsrakhra.filedownloader.fileSys.organization.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileSystem {
    private final String rootPath;
    private final List<io.github.navjotsrakhra.filedownloader.fileSys.organization.File> files;
    private final List<Directory> directories;

    public FileSystem(String rootPath) throws NotADirectoryException {
        this.rootPath = rootPath;

        files = new ArrayList<>();
        directories = new ArrayList<>();

        initializeFilesAndDirectories();
    }

    private void initializeFilesAndDirectories() throws NotADirectoryException {
        File rootFileObject = new File(rootPath);
        if (rootFileObject.isFile() || !rootFileObject.exists())
            throw new NotADirectoryException("The path \"" + rootFileObject.toPath().normalize() + "\" is not a path to a directory");
        for (File file : Objects.requireNonNull(rootFileObject.listFiles())) {
            if (file.isFile())
                files.add(new io.github.navjotsrakhra.filedownloader.fileSys.organization.File(file.getName(), file.toPath().normalize().toAbsolutePath().toString()));
            else directories.add(new Directory(file.getName(), file.toPath().normalize().toAbsolutePath().toString()));
        }
    }

    public List<io.github.navjotsrakhra.filedownloader.fileSys.organization.File> getAllFiles() {
        return files.subList(0, files.size());
    }

    public List<io.github.navjotsrakhra.filedownloader.fileSys.organization.File> getAllFiles(boolean includeHiddenFiles) {
        return files.parallelStream().filter(e -> !e.fileName().startsWith(".") || includeHiddenFiles).toList();
    }

    public List<Directory> getAllDirectories() {
        return directories.subList(0, directories.size());
    }

    public List<Directory> getAllDirectories(boolean includeHiddenFiles) {
        return directories.parallelStream().filter(e -> !e.directoryName().startsWith(".") || includeHiddenFiles).toList();
    }

}
