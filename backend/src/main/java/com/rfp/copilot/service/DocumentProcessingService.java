package com.rfp.copilot.service;

import com.rfp.copilot.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentProcessingService {

    private final VectorStore vectorStore;

    public DocumentProcessingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String extractTimelineInformation(String sourceFilter) {
        String prompt = PromptTemplate.getTimeLineInfoPrompt();
        return extractRelevantContent(sourceFilter, prompt);
    }

    public String extractTechnicalRequirements(String sourceFilter) {
        String prompt = PromptTemplate.getTechnicalInfoPrompt();
        return extractRelevantContent(sourceFilter, prompt);
    }

    public String extractRequirementGatherPrompt(String sourceFilter) {
        String prompt = PromptTemplate.generateRequirementGatherPrompt();
        return extractRelevantContent(sourceFilter, prompt);
    }

    public String extractRelevantContent(String sourceFilter, String prompt) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(5)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        return Optional.ofNullable(documents)
                .orElse(List.of())
                .stream()
                .filter(doc -> sourceFilter.equals(doc.getMetadata().get("source")))
                .map(Document::getFormattedContent)
                .collect(Collectors.joining());
    }
}

