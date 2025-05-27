package com.rfp.copilot.controller;

import com.rfp.copilot.dto.BidDecision;
import com.rfp.copilot.service.TorFilteringService;
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
public class TorFilteringController {
    private final TorFilteringService torFilteringService;

    public TorFilteringController(TorFilteringService torFilteringService, @Qualifier("openAiChatModel") ChatModel chatModel, VectorStore vectorStore) {
        this.torFilteringService = torFilteringService;
    }

    @GetMapping("/filter")
    public String simplify(
            @RequestParam(value = "question", defaultValue = "Give a welcome message") String question,
            @RequestParam(value = "source", required = false) String sourceFilter
    ) {
        String result = torFilteringService.analyzeTorSuitability(sourceFilter);
        System.out.println("response:"+result);
        return result;
    }

}
