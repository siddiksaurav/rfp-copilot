package com.rfp.copilot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessService.class);
    private final VectorStoreService vectorStoreService;
    private final TorFilteringService torFilteringService;

    @Value("${file.process.path}")
    private String fileProcessPath;

    public ProcessService(VectorStoreService vectorStoreService, TorFilteringService torFilteringService) {
        this.vectorStoreService = vectorStoreService;
        this.torFilteringService = torFilteringService;
    }

    public List<String> torProcessQueue() {
        String projectRoot = System.getProperty("user.dir");
        File downloadsDir = new File(projectRoot, fileProcessPath);

        if (!downloadsDir.exists() || !downloadsDir.isDirectory()) {
            logger.error("Downloads folder not found.");
            return new ArrayList<>();
        }

        File[] files = downloadsDir.listFiles();
        if (files == null || files.length == 0) {
            logger.info("No files found in Downloads folder.");
            return new ArrayList<>();
        }

        List<String> sourceNames = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".pdf")) {
                String sourceName = file.getName().substring(0, file.getName().lastIndexOf("."));
                sourceNames.add(sourceName);
            }
        }

        return sourceNames;
    }

    public String processPdf(String sourceName) {
        String projectRoot = System.getProperty("user.dir");
        File downloadsDir = new File(projectRoot, fileProcessPath);

        if (!downloadsDir.exists() || !downloadsDir.isDirectory()) {
            logger.error("Downloads folder not found.");
            return "Downloads folder not found.";
        }

        File[] files = downloadsDir.listFiles();
        if (files == null || files.length == 0) {
            logger.info("No files found in Downloads folder.");
            return "No files to process.";
        }

        for (File file : files) {
            String fileSourceName = file.getName().substring(0, file.getName().lastIndexOf("."));

            if (file.isFile() && file.getName().endsWith(".pdf") && sourceName.equals(fileSourceName)) {
                try {
                    // Read file content
                    FileInputStream input = new FileInputStream(file);
                    MultipartFile multipartFile = new MockMultipartFile(
                            file.getName(),
                            file.getName(),
                            "application/pdf",
                            input
                    );

                    vectorStoreService.processFile(multipartFile, sourceName);

                    input.close();
                } catch (IOException e) {
                    logger.error("Error processing file: {}", file.getName(), e);
                }
            }
        }

        String result = torFilteringService.analyzeTorSuitability(sourceName);
        logger.info("final tor filtering: {}", result);
        return result;
    }
}
