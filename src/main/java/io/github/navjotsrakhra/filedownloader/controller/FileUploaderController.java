package io.github.navjotsrakhra.filedownloader.controller;

import io.github.navjotsrakhra.filedownloader.exceptions.IllegalPathException;
import io.github.navjotsrakhra.filedownloader.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static io.github.navjotsrakhra.filedownloader.logging.LoggingController.LOG;

@RestController
@RequestMapping("/upload")
public class FileUploaderController {
    private final StorageService storageService;

    public FileUploaderController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<?> uploadFiles(MultipartFile file) throws IOException {
        LOG.info("fileName: '{}', fileSize: '{}'", file.getOriginalFilename(), file.getSize());
        return storageService.save(file);
    }

    @PostMapping("/setDownloadRoot")
    public ResponseEntity<?> setDownloadRoot(@RequestBody String newDownloadRootPath) throws IllegalPathException {
        LOG.info("request new root: {}", newDownloadRootPath);
        storageService.setRootPath(newDownloadRootPath);
        return ResponseEntity
                .ok()
                .build();
    }
}
