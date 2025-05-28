package com.rfp.copilot.controller;

import com.rfp.copilot.service.DocumentProcessingService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueryController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(QueryController.class);
    private final ChatModel chatModel;
    private final DocumentProcessingService documentService;

    public QueryController(@Qualifier("openAiChatModel") ChatModel chatModel, DocumentProcessingService documentService) {
        this.chatModel = chatModel;
        this.documentService = documentService;
    }

    @GetMapping("/ask")
    public String simplify(
            @RequestParam(value = "question", defaultValue = "Give a welcome message") String question,
            @RequestParam(value = "source", required = false) String sourceFilter
    ) {
        logger.info("Starting query for source: {}", sourceFilter);

        String retrievedContent = documentService.extractRelevantContent(sourceFilter, question);

        logger.info("Retrieved content: {}", retrievedContent);

        String systemPrompt = """
                You are an RFP Co-Pilot assistant that answers questions about a specific Request for Proposal (RFP) document.
                
                Use only the information provided in the context below. 
                If the answer is not present in the context, respond with: 
                "I couldn't find that information in the RFP document."
                
                Be concise, factual, and specific.
                
                Context:
                {documents}
                
                User Question:
                {input}
                
                Answer:
                """;

        PromptTemplate template = new PromptTemplate(systemPrompt);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", question);
        promptParameters.put("documents", retrievedContent);

        String response = chatModel
                .call(template.create(promptParameters))
                .getResult()
                .getOutput()
                .toString();

        return response;
    }

}
