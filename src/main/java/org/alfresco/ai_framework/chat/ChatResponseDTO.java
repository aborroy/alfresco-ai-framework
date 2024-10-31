package org.alfresco.ai_framework.chat;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object representing the response from a chat request.
 * Contains the AI-generated answer and metadata about retrieved documents.
 */
public record ChatResponseDTO(String answer, List<Map<String, Object>> documentMetadata) {

    /**
     * Constructs a ChatResponseDTO with the specified answer and document metadata.
     *
     * @param answer           The answer generated by the chat service.
     * @param documentMetadata Metadata of documents used to contextualize the answer.
     */
    public ChatResponseDTO {
    }

    /**
     * Gets the answer generated by the chat service.
     *
     * @return The answer text.
     */
    @Override
    public String answer() {
        return answer;
    }

    /**
     * Gets the metadata of documents retrieved to support the answer.
     *
     * @return A list of metadata maps for each context document.
     */
    @Override
    public List<Map<String, Object>> documentMetadata() {
        return documentMetadata;
    }

    /**
     * Returns a string representation of the ChatResponseDTO.
     *
     * @return A string containing the answer and document metadata.
     */
    @Override
    public String toString() {
        return "ChatResponseDTO{" +
                "answer='" + answer + '\'' +
                ", documentMetadata=" + documentMetadata +
                '}';
    }

}
