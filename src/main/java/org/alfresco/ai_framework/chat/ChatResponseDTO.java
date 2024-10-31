package org.alfresco.ai_framework.chat;

import java.util.List;
import java.util.Map;

public class ChatResponseDTO {

    private String answer;
    private List<Map<String, Object>> documentMetadata;

    public ChatResponseDTO(String answer, List<Map<String, Object>> documentMetadata) {
        this.answer = answer;
        this.documentMetadata = documentMetadata;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Map<String, Object>> getDocumentMetadata() {
        return documentMetadata;
    }

    public void setDocumentMetadata(List<Map<String, Object>> documentMetadata) {
        this.documentMetadata = documentMetadata;
    }
}