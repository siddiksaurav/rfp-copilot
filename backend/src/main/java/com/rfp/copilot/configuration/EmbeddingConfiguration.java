
package com.rfp.copilot.configuration;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingConfiguration {

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(@Qualifier("ollamaEmbeddingModel") OllamaEmbeddingModel ollamaModel) {
        return ollamaModel;
    }

    @Bean
    @Primary
    public ChatModel primaryChatModel(@Qualifier("openAiChatModel") OpenAiChatModel openAiModel) {
        return openAiModel;
    }
}
