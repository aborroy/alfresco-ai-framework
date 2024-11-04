package org.alfresco.ai_framework.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling chat interactions with the AI system.
 * Configures chat advisors, such as QuestionAnswerAdvisor and SafeGuardAdvisor,
 * to enrich responses and ensure safe interactions.
 */
@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private static final int DEFAULT_TOP_K = 5;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    /**
     * Constructs the ChatService with a pre-configured ChatClient and VectorStore.
     *
     * @param chatClientBuilder Builder for creating a ChatClient instance.
     * @param vectorStore       Vector store for performing document searches.
     */
    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        logger.debug("ChatService initialized with ChatClient and VectorStore.");
    }

    /**
     * Processes a chat query by interacting with the AI through configured advisors.
     * Uses a QuestionAnswerAdvisor for document retrieval and a SafeGuardAdvisor
     * to filter inappropriate content.
     *
     * @param query The user input to process.
     * @return The AI-generated ChatResponse, containing the answer and metadata.
     */
    public ChatResponse chat(String query) {
        logger.info("Processing chat query: {}", query);

        // Configuring advisors to enhance the response quality
        ChatResponse response = chatClient.prompt()
                .advisors(createQuestionAnswerAdvisor())
                .user(query)
                .call()
                .chatResponse();

        logger.info("Received response from AI");
        return response;
    }

    /**
     * Creates a QuestionAnswerAdvisor for handling document retrieval.
     *
     * @return A configured QuestionAnswerAdvisor instance.
     */
    private QuestionAnswerAdvisor createQuestionAnswerAdvisor() {
        SearchRequest searchRequest = SearchRequest.defaults().withTopK(DEFAULT_TOP_K);
        return new QuestionAnswerAdvisor(vectorStore, searchRequest);
    }

}
