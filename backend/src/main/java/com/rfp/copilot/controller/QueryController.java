package com.rfp.copilot.controller;

import com.rfp.copilot.prompt.PromptTemplates;
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
        String document = documentService.extractRelevantContent(sourceFilter, question);
        PromptTemplate template = new PromptTemplate(PromptTemplates.getRequirementChatPrompt());

        Map<String, Object> promptsParameters = new HashMap<>();
        promptsParameters.put("input", question);
        promptsParameters.put("documents", document);

        String response = chatModel
                .call(template.create(promptsParameters))
                .getResult()
                .getOutput().toString();

        return response;
    }
}
