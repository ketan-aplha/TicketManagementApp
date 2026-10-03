package com.Library.Management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private final Path root = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(root);
            log.info("Initialized upload directory {}", root.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Could not initialize folder for upload at {}", root.toAbsolutePath());
            throw new RuntimeException("Could not initialize folder for upload");
        }
    }

    public String store(MultipartFile file) {
        try {
            // Create a unique file name to avoid overwriting
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            log.info("Storing file as {}", filename);
            Files.copy(file.getInputStream(), this.root.resolve(filename));
            log.info("Stored file {}", filename);
            return filename;
        } catch (IOException e) {
            log.warn("Could not store the file {}", file.getOriginalFilename());
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}
