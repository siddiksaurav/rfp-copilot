package com.rfp.copilot.service;

import com.rfp.copilot.component.DataLoader;
import com.rfp.copilot.controller.VectorStoreController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
public class VectorStoreService {

    private static final Logger logger = LoggerFactory.getLogger(VectorStoreController.class);

    private final DataLoader dataLoader;

    public VectorStoreService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public ResponseEntity<String> processFile(MultipartFile file, String sourceName) {
        logger.info("uploading pdf file: {}", sourceName);
        try {
            byte[] pdfBytes = file.getBytes();
            Resource pdfResource = new ByteArrayResource(pdfBytes);
            dataLoader.loadPdfToVectorStore(pdfResource, sourceName);
            logger.info("PDF uploaded and added to vector store");

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
