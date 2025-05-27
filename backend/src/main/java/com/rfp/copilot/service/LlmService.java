package com.rfp.copilot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class LlmService {
    private static final Logger logger = LoggerFactory.getLogger(LlmService.class);
    private final ChatModel chatModel;

    public LlmService(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generateResponse(String prompt) {
        logger.info("LLM prompt: {} " , prompt);

        PromptTemplate template = new PromptTemplate(prompt);
        String response = chatModel
                .call(template.create())
                .getResult()
                .getOutput().toString();

        logger.info("LLM response: {} " , response);
        return response;
    }
}
