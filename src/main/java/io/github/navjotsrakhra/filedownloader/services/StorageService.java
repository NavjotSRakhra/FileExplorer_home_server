package io.github.navjotsrakhra.filedownloader.services;

import io.github.navjotsrakhra.filedownloader.exceptions.IllegalPathException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static io.github.navjotsrakhra.filedownloader.logging.LoggingController.LOG;

@Service
public class StorageService {
    @Value("${rootPathUpload}")
    private String rootPathUpload;

    private static HttpHeaders redirectToHeader(CharSequence path) {
        HttpHeaders header = new HttpHeaders();
        header.setLocation(URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + path));
        return header;
    }

    public ResponseEntity<?> save(MultipartFile file) throws IOException {

        if (file == null)
            throw new IllegalArgumentException("No file supplied");

        if (file.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, Path.of(rootPathUpload).toAbsolutePath().normalize().resolve(Objects.requireNonNull(file.getOriginalFilename())), StandardCopyOption.REPLACE_EXISTING);
        }

        HttpHeaders header = redirectToHeader("/");

        return new ResponseEntity<>(header, HttpStatus.SEE_OTHER);
    }

    public void setRootPath(String newPath) throws IllegalPathException {
        if (newPath == null)
            throw new IllegalPathException("Path cannot be null.");

        newPath = newPath.trim();

        var file = new java.io.File(newPath);
        if (!file.exists() || !file.isDirectory())
            throw new IllegalPathException("Path must point to an existing directory.");

        rootPathUpload = newPath;
        LOG.info(rootPathUpload);
    }
}
