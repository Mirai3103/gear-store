package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    private final Path storagePath;

    public FileService() {
        this.storagePath = Paths.get("uploads"); // default folder
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }
    public String saveFile(byte[] data, String filename) {
        try {
            Path target = storagePath.resolve(filename);
            Files.write(target, data);
            return "/static/view/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file from bytes: " + filename, e);
        }
    }

    public String   saveFile(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            Path target = storagePath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL để frontend có thể load được
            return "/static/view/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }


    public byte[] readFile(String filename) {
        try {
            Path filePath = storagePath.resolve(filename);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filename, e);
        }
    }

    public void deleteFile(String fileUrl) {
        // Ví dụ: "/static/view/abc.jpg" -> "abc.jpg"
        String filename = Paths.get(fileUrl).getFileName().toString();
        try {
            Path filePath = storagePath.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
