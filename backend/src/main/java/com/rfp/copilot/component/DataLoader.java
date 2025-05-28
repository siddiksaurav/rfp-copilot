package com.rfp.copilot.component;

import com.rfp.copilot.constants.Constants;
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
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    @Value("classpath:/rfp_document_banbeis.pdf")
    private Resource dataPdf;

    public DataLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }


    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql(Constants.EMPTY_VECTOR_DB_CHECK_QUERY).query(Integer.class).single();
        if (count == 0) {
            logger.info("No vector store found");
            loadPdfToVectorStore(dataPdf, "rfp_document_banbeis.pdf");
            logger.info("Loaded all PDFs into vector store");
        }
    }

    public void loadPdfToVectorStore(Resource pdfResource, String sourceName) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build();
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource, config);
        List<Document> pages = reader.get();

        var textSplitter = new TokenTextSplitter();
        List<Document> splitDocs = textSplitter.apply(pages);
        List<Document> docsWithMetadata = splitDocs.stream()
                .map(doc -> new Document(doc.getFormattedContent(), Map.of("source", sourceName)))
                .toList();

        vectorStore.accept(docsWithMetadata);
        logger.info("Loaded PDF: {}", sourceName);
    }
}
