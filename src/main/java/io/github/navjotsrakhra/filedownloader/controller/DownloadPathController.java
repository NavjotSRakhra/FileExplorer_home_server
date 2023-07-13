package io.github.navjotsrakhra.filedownloader.controller;

import io.github.navjotsrakhra.filedownloader.exceptions.IllegalPathException;
import io.github.navjotsrakhra.filedownloader.fileSys.FileSystem;
import io.github.navjotsrakhra.filedownloader.fileSys.NotADirectoryException;
import io.github.navjotsrakhra.filedownloader.fileSys.Organizable;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.Directory;
import io.github.navjotsrakhra.filedownloader.fileSys.organization.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
public class DownloadPathController {


    @Value(value = "${rootPath}")
    private String rootPath;

    @ExceptionHandler(IllegalPathException.class)
    public ResponseEntity<?> handle() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .build();
    }

    private Organizable encodeOrganizationPath(Organizable organization, String urlWithPathParameter) {
        if (organization instanceof Directory d)
            return new Directory(d.directoryName(), urlWithPathParameter + UriUtils.encode(d.directoryPath(), Charset.defaultCharset()));
        else if (organization instanceof File f) {
            return new File(f.fileName(), urlWithPathParameter + UriUtils.encode(f.filePath(), Charset.defaultCharset()));
        }
        return null;
    }

    private Directory encodeDirectoryPath(Directory directory, String urlWithPathParameter) {
        return (Directory) encodeOrganizationPath(directory, urlWithPathParameter);
    }

    private File encodeFilePath(File file, String urlWithPathParameter) {
        return (File) encodeOrganizationPath(file, urlWithPathParameter);
    }

    @GetMapping
    public ResponseEntity<?> getDownloadableFiles(@RequestParam(required = false) String path, @RequestParam(required = false) String filePath) throws NotADirectoryException, IllegalPathException, IOException {

        if (filePath != null) {
            checkIfPathIsAChildOfRoot(filePath);
            FileSystemResource resource = new FileSystemResource(filePath);
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resource.getFilename())
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }


        final String urlWithPathParameter = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/files?path=";
        final String urlWithFilePathParameter = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/files?filePath=";


        FileSystem fileSystem;
        if (path != null) {
            checkIfPathIsAChildOfRoot(path);
            fileSystem = new FileSystem(path);
        } else
            fileSystem = new FileSystem(rootPath);
        return ResponseEntity
                .ok(new FilesAndDirectories(
                        fileSystem.getAllFiles(false)
                                .parallelStream()
                                .map(e -> this.encodeFilePath(e, urlWithFilePathParameter))
                                .toList(),
                        fileSystem.getAllDirectories(false)
                                .parallelStream()
                                .map(e -> this.encodeDirectoryPath(e, urlWithPathParameter)).toList()));
    }

    private void checkIfPathIsAChildOfRoot(String filePath) throws IllegalPathException {
        if (!Path.of(filePath).toAbsolutePath().normalize().startsWith(Path.of(rootPath).toAbsolutePath().normalize()))
            throw new IllegalPathException();
    }

    private record FilesAndDirectories(List<File> files, List<Directory> directories) {
    }
}
