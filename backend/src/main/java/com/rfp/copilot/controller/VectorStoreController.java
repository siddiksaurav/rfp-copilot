package com.rfp.copilot.controller;

import com.rfp.copilot.component.DataLoader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Controller
public class VectorStoreController {

    private final DataLoader dataLoader;

    public VectorStoreController(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @PostMapping("/upload-pdf-from-url")
    public ResponseEntity<String> uploadPdfFromUrl(@RequestParam String url, @RequestParam String name) {
        addPdfFromApi(url, name);
        return ResponseEntity.ok("PDF added to vector store");
    }

    @PostMapping("/upload-pdf-file")
    public ResponseEntity<String> uploadPdfFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String sourceName
    ) {
        try {
            byte[] pdfBytes = file.getBytes();
            Resource pdfResource = new ByteArrayResource(pdfBytes);

            dataLoader.loadPdfToVectorStore(pdfResource, sourceName);

            return ResponseEntity.ok("PDF uploaded and added to vector store");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload PDF");
        }
    }


    public void addPdfFromApi(String pdfUrl, String sourceName) {
        try {
            Resource apiPdf = downloadPdfFromApi(pdfUrl);
            dataLoader.loadPdfToVectorStore(apiPdf, sourceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Resource downloadPdfFromApi(String pdfUrl) {
        RestTemplate restTemplate = new RestTemplate();
        byte[] pdfBytes = restTemplate.getForObject(pdfUrl, byte[].class);
        return new ByteArrayResource(Objects.requireNonNull(pdfBytes));
    }

}
