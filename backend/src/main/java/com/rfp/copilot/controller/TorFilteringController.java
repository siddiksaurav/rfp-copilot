package com.rfp.copilot.controller;

import com.rfp.copilot.service.TorFilteringService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
