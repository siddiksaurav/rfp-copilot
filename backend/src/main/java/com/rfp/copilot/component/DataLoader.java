package com.rfp.copilot.component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DataLoader {
    private  static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    // List multiple PDF resources
    @Value("classpath:/EMPDATA.pdf")
    private Resource empDataPdf;

    public DataLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }


    @PostConstruct
    public void init() {
        jdbcClient.sql("DELETE FROM vector_store").update();
        Integer count = jdbcClient.sql("select count(*) from vector_store").query(Integer.class).single();
        if (count == 0) {
            logger.info("No vector store found");
            loadPdfToVectorStore(empDataPdf, "EMPDATA.pdf");
            logger.info("Loaded all PDFs into vector store");
        }
    }

    public void loadPdfToVectorStore(Resource pdfResource, String sourceName) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build();
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource, config);

        List<Document> pages = reader.get();
        String fullText = pages.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"))
                .replace("\r", "")
                .replace("\u00A0", " ")
                .replaceAll(" +", " ")
                .trim();

        System.out.println("fullText:"+fullText);


        // Extract metadata
        Map<String, String> extractedMetadata = extractMetadata(fullText, sourceName);

        // Extract structured sections
        List<Document> structuredDocs = extractSectionsWithMetadata(fullText, extractedMetadata);

        // Store in vector store in batches
        final int batchSize = 100;
        for (int i = 0; i < structuredDocs.size(); i += batchSize) {
            int end = Math.min(i + batchSize, structuredDocs.size());
            System.out.println(i + " to " + end + " (" + (end - i) + " items)");
            vectorStore.accept(structuredDocs.subList(i, end));
        }

        logger.info("Successfully loaded PDF into vector store: {}", sourceName);
    }

    private Map<String, String> extractMetadata(String text, String sourceName) {
        String title = extractValue(text, "(?i)Project Title:\\s*(.+)");
        String duration = extractValue(text, "(?i)project duration is\\s*([\\w\\s\\-]+)");

        return Map.of(
                "source", sourceName,
                "title", title != null ? title : "Untitled",
                "duration", duration != null ? duration : "Unknown"
        );
    }

    private String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private List<Document> extractSectionsWithMetadata(String fullText, Map<String, String> baseMetadata) {
//        Pattern sectionPattern = Pattern.compile("(?m)^(\\d+\\.?\\d*)\\.\\s+(.*?)\\n");
        Pattern sectionPattern = Pattern.compile(
                "(?m)^\\s*(?:(Section|Module|Part)\\s*)?(\\d+(?:\\.\\d+)*)(?:\\.)\\s*(.+)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = sectionPattern.matcher(fullText);

        List<Integer> sectionStartIndexes = new ArrayList<>();
        List<String> sectionTitles = new ArrayList<>();

        while (matcher.find()) {
            sectionStartIndexes.add(matcher.start());
            sectionTitles.add(matcher.group(2).trim());
        }

        System.out.println("sectionStartIndexes:"+sectionStartIndexes);
        System.out.println("sectionTitles:"+sectionTitles);

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < sectionStartIndexes.size(); i++) {
            int start = sectionStartIndexes.get(i);
            int end = (i + 1 < sectionStartIndexes.size()) ? sectionStartIndexes.get(i + 1) : fullText.length();
            String sectionText = fullText.substring(start, end).trim();
            String sectionTitle = sectionTitles.get(i);
            String sectionLabel = "Section " + (i + 1) + " - " + sectionTitle;

//            Map<String, String> metadata = new HashMap<>(baseMetadata);
//            metadata.put("section", sectionTitle);

            Map<String, Object> metadata = baseMetadata.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
            metadata.put("section", sectionLabel);

            documents.add(new Document(sectionText, metadata));
        }

        return documents;
    }

}
