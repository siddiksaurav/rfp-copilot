package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    private  static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;
    @Value("classpath:/EMPDATA.pdf")
    private Resource pdfResource;
    public DataLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql("select Count(*) from vector_store").query(Integer.class).single();
        if (count == 0) {
            logger.info("No vector store found");
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPagesPerDocument(1)
                    .build();
            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource, config);

            var textSpitter = new TokenTextSplitter();
            vectorStore.accept(textSpitter.apply(reader.get()));

            logger.info("Loaded vector store");
        }
    }
}
