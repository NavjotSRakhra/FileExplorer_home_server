package io.github.navjotsrakhra.filedownloader.controller;

import io.github.navjotsrakhra.filedownloader.exceptions.IllegalPathException;
import io.github.navjotsrakhra.filedownloader.fileSys.NotADirectoryException;
import io.github.navjotsrakhra.filedownloader.services.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.github.navjotsrakhra.filedownloader.logging.LoggingController.LOG;

@RestController()
@RequestMapping("/files")
public class DownloadPathController {


    private final DownloadService downloadService;

    @Autowired
    public DownloadPathController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @ExceptionHandler({IllegalPathException.class, NotADirectoryException.class})
    public ResponseEntity<?> handle(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @GetMapping
    public ResponseEntity<?> getDownloadableFiles(@RequestParam(required = false) String path, @RequestParam(required = false) String filePath) throws NotADirectoryException, IllegalPathException, IOException {
        LOG.info("[path: {}, filePath: {}], [binaryPath: {}, binaryFilePath: {}]", path, filePath, String.valueOf(path).getBytes(StandardCharsets.UTF_8), String.valueOf(filePath).getBytes(StandardCharsets.UTF_8));
        return downloadService.handleFilesGetRequest(path, filePath, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/files");
    }

    @PostMapping("/setDownloadRoot")
    public ResponseEntity<?> setDownloadRoot(@RequestBody String newDownloadRootPath) throws IllegalPathException {
        LOG.info("request new root: {}", newDownloadRootPath);
        downloadService.setRootPath(newDownloadRootPath);
        return ResponseEntity
                .ok()
                .build();
    }
}
