package com.rfp.copilot.controller;

import com.rfp.copilot.service.VectorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VectorStoreController {
    private static final Logger logger = LoggerFactory.getLogger(VectorStoreController.class);
    private final VectorStoreService vectorStoreService;

    public VectorStoreController(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/upload-pdf-from-url")
    public ResponseEntity<String> uploadPdfFromUrl(@RequestParam String url, @RequestParam String name) {
        vectorStoreService.addPdfFromApi(url, name);
        return ResponseEntity.ok("PDF added to vector store");
    }

    @PostMapping("/upload-pdf-file")
    public ResponseEntity<String> uploadPdfFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String sourceName
    ) {
        return vectorStoreService.processFile(file, sourceName);
    }

}
