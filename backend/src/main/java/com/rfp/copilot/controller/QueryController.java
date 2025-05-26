package com.rfp.copilot.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public QueryController(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask")
    public String simplify(@RequestParam(value = "question",
            defaultValue = "List all the Articles in the Indian Constitution")
                           String question) {

        PromptTemplate template
                = new PromptTemplate(prompt);
        Map<String, Object> promptsParameters = new HashMap<>();
        promptsParameters.put("input", question);
        promptsParameters.put("documents", findSimilarData(question));

        String response =  chatModel
                .call(template.create(promptsParameters))
                .getResult()
                .getOutput().toString();

        System.out.println("response:"+response);
        return response;
    }

    private String findSimilarData(String question) {
        List<Document> documents;
        SearchRequest searchRequest = SearchRequest.builder().query(question).topK(5).build();

        documents = vectorStore.similaritySearch(searchRequest);

        return documents
                .stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining());

    }
}
