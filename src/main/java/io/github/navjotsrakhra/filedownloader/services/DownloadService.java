package io.github.navjotsrakhra.filedownloader.services;

import io.github.navjotsrakhra.filedownloader.exceptions.IllegalPathException;
import io.github.navjotsrakhra.filedownloader.fileSys.FileSystem;
import io.github.navjotsrakhra.filedownloader.fileSys.NotADirectoryException;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.Directory;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.File;
import io.github.navjotsrakhra.filedownloader.fileSys.util.OrganizableUtils;
import io.github.navjotsrakhra.filedownloader.fileSys.util.PathUtils;
import io.github.navjotsrakhra.filedownloader.output.FilesAndDirectories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static io.github.navjotsrakhra.filedownloader.logging.LoggingController.LOG;

@Service
public class DownloadService {
    @Value(value = "${rootPath}")
    private String rootPath;

    public ResponseEntity<?> handleFilesGetRequest(String path, String filePath, final String requestUriLocation) throws IOException, IllegalPathException, NotADirectoryException {
        LOG.info("Root path: {}", rootPath);
        if (path != null) {
            path = path.trim();
        }
        if (filePath != null) {
            filePath = filePath.trim();
        }

        checkValidPath(path);
        checkValidPath(filePath);

        if (filePath != null)
            return downloadFile(filePath);
        else
            return getRecordOfFilesAndDirectoriesAt(path, requestUriLocation);
    }

    private ResponseEntity<FilesAndDirectories> getRecordOfFilesAndDirectoriesAt(String pathToReadFrom, String requestUriLocation) throws NotADirectoryException {

        if (pathToReadFrom == null)
            pathToReadFrom = rootPath;

        FileSystem fileSystem = new FileSystem(pathToReadFrom);

        FilesAndDirectories responseOfFilesAndDirectories = new FilesAndDirectories(
                fileSystem.getAllFiles(false)
                        .parallelStream()
                        .map(e -> (File) OrganizableUtils.encodeToURIValue(e))
                        .map(e -> new File(e.fileName(), requestUriLocation + "?filePath=" + e.filePath()))
                        .toList(),

                fileSystem.getAllDirectories(false)
                        .parallelStream()
                        .map(e -> (Directory) OrganizableUtils.encodeToURIValue(e))
                        .map(e -> new Directory(e.directoryName(), requestUriLocation + "?path=" + e.directoryPath()))
                        .toList()
        );

        return ResponseEntity.ok(responseOfFilesAndDirectories);
    }

    private void checkValidPath(String pathToReadFrom) throws IllegalPathException {
        if (pathToReadFrom == null)
            return;
        if (!PathUtils.isChildOfPath(rootPath, pathToReadFrom)) {
            LOG.warn("root path is '{}' and requested path is '{}'", rootPath, pathToReadFrom);
            LOG.error("Requested path '{}' is not present/not allowed", pathToReadFrom);
            throw new IllegalPathException("The requested path is forbidden");
        }
    }

    private ResponseEntity<Resource> downloadFile(String downloadFilePath) throws IOException, IllegalPathException {
        Objects.requireNonNull(downloadFilePath);
        var file = new java.io.File(downloadFilePath);
        if (!file.exists())
            throw new IllegalPathException("The requested path/file doesn't exist");
        if (file.isDirectory())
            throw new IllegalPathException("The requested path is not a File");

        FileSystemResource resource = new FileSystemResource(downloadFilePath);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resource.getFilename())
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public void setRootPath(String newPath) throws IllegalPathException {
        if (newPath == null)
            throw new IllegalPathException("Path cannot be null.");

        newPath = newPath.trim();

        var file = new java.io.File(newPath);
        if (!file.exists() || !file.isDirectory())
            throw new IllegalPathException("Path must point to an existing directory.");

        rootPath = newPath;
        LOG.info(rootPath);
    }
}
