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

import java.util.List;
import java.util.Map;

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
//        jdbcClient.sql("DELETE FROM vector_store").update();
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


        var textSplitter = new TokenTextSplitter();

        List<Document> pages = reader.get();

        List<Document> splitDocs = textSplitter.apply(pages);
        List<Document> docsWithMetadata = splitDocs.stream()
                .map(doc -> new Document(doc.getFormattedContent(), Map.of("source", sourceName)))
                .toList();

        vectorStore.accept(docsWithMetadata);

        logger.info("Loaded PDF: {}", sourceName);
    }
}
