package com.ecom.controller;

import com.ecom.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RequiredArgsConstructor
@RestController
@RequestMapping("/static")
public class FileController {
    private final FileService fileService;
    @GetMapping("/view/{filename}")
    public ResponseEntity<byte[]> viewImage(@PathVariable String filename) {
        byte[] imageData = fileService.readFile(filename);
        String contentType = resolveContentType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(imageData);
    }
    private String resolveContentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}
