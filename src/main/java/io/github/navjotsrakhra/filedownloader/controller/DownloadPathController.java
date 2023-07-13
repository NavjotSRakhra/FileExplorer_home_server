package io.github.navjotsrakhra.filedownloader.controller;

import io.github.navjotsrakhra.filedownloader.exceptions.IllegalPathException;
import io.github.navjotsrakhra.filedownloader.fileSys.NotADirectoryException;
import io.github.navjotsrakhra.filedownloader.services.ExplorerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class DownloadPathController {


    private final ExplorerService explorerService;

    @Autowired
    public DownloadPathController(ExplorerService explorerService) {
        this.explorerService = explorerService;
    }

    @ExceptionHandler({IllegalPathException.class, NotADirectoryException.class})
    public ResponseEntity<?> handle() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Illegal path");
    }

    @GetMapping
    public ResponseEntity<?> getDownloadableFiles(@RequestParam(required = false) String path, @RequestParam(required = false) String filePath) throws NotADirectoryException, IllegalPathException, IOException {
        return explorerService.handleFilesGetRequest(path, filePath, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/files");
    }

}
