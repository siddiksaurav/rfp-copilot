package com.rfp.copilot.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class QueryController {

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    private String prompt = """
            Your task is to answer the questions about Indian Constitution. Use the information from the DOCUMENTS
            section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
            simply state that you don't know the answer.
                        
            QUESTION:
            {input}
                        
            DOCUMENTS:
            {documents}
                        
            """;

    public QueryController(@Qualifier("openAiChatModel") ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask")
    public String simplify(
            @RequestParam(value = "question", defaultValue = "Give a welcome message") String question,
            @RequestParam(value = "source", required = false) String sourceFilter
    ) {

        PromptTemplate template
                = new PromptTemplate(prompt);
        Map<String, Object> promptsParameters = new HashMap<>();
        promptsParameters.put("input", question);
        promptsParameters.put("documents", findSimilarData(question, sourceFilter != null ? sourceFilter : "EMPDATA.pdf"));

        String response =  chatModel
                .call(template.create(promptsParameters))
                .getResult()
                .getOutput().toString();

        System.out.println("response:"+response);
        return response;
    }

    private String findSimilarData(String question, String sourceFilter) {
        List<Document> documents;
        SearchRequest searchRequest = SearchRequest.builder().query(question).topK(5).build();

        documents = vectorStore.similaritySearch(searchRequest);

        return Objects.requireNonNull(documents)
                .stream()
                .filter(doc -> {
                    Map<String, Object> metadata = doc.getMetadata();
                    return metadata.get("source") != null && metadata.get("source").equals(sourceFilter);
                })
                .map(Document::getFormattedContent)
                .collect(Collectors.joining());
    }
}
