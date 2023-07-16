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
public class ExplorerService {
    @Value(value = "${rootPath}")
    private String rootPath;

    public ResponseEntity<?> handleFilesGetRequest(final String path, final String filePath, final String requestUriLocation) throws IOException, IllegalPathException, NotADirectoryException {
        checkValidPath(path);
        checkValidPath(filePath);

        LOG.info("request path: '{}', request filePath: '{}'", path, filePath);

        if (filePath != null)
            return downloadFile(filePath);
        else
            return getRecordOfFilesAndDirectoriesAt(path, requestUriLocation);
    }

    private ResponseEntity<FilesAndDirectories> getRecordOfFilesAndDirectoriesAt(String pathToReadFrom, String requestUriLocation) throws IllegalPathException, NotADirectoryException {

        LOG.info("request uri is '{}'", requestUriLocation);

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
        if (!isPathAllowed(pathToReadFrom)) {
            LOG.warn("root path is '{}' and requested path is '{}'", rootPath, pathToReadFrom);
            LOG.error("Requested path '{}' is not present/not allowed", pathToReadFrom);
            throw new IllegalPathException();
        }
    }

    private ResponseEntity<Resource> downloadFile(String downloadFilePath) throws IOException, IllegalPathException {
        Objects.requireNonNull(downloadFilePath);
        LOG.info("requested file: '{}'", downloadFilePath);
        if (new java.io.File(downloadFilePath).isDirectory())
            throw new IllegalPathException();
        FileSystemResource resource = new FileSystemResource(downloadFilePath);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resource.getFilename())
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private boolean isPathAllowed(String path) {
        return PathUtils.isChildOfPath(rootPath, path);
    }


}
