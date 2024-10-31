package org.alfresco.ai_framework.chat;

import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
class ChatController {

    @Autowired
    ChatService chatService;

    @PostMapping("/chat")
    ChatResponseDTO chat(@RequestBody String query) {
        ChatResponse response = chatService.chat(query);
        String answer = response.getResult().getOutput().getContent();
        List<Content> contextDocuments = response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);

        List<Map<String, Object>> documentMetadata = contextDocuments.stream()
                .map(Content::getMetadata)
                .collect(Collectors.toList());

        return new ChatResponseDTO(answer, documentMetadata);
    }

}