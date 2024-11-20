package org.alfresco.ai_framework.ingestion;

import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Temporal fix for https://github.com/spring-projects/spring-ai/issues/1341
public class DocumentSplitter {
    private static final int MAX_LENGTH = 20000;

    public static List<Document> splitLargeDocuments(List<Document> documents) {
        List<Document> result = new ArrayList<>();

        for (Document doc : documents) {
            if (doc.getContent().length() <= MAX_LENGTH) {
                result.add(doc);
            } else {
                result.addAll(splitDocument(doc));
            }
        }

        return result;
    }

    private static List<Document> splitDocument(Document doc) {
        List<Document> splits = new ArrayList<>();
        String content = doc.getContent();
        int totalLength = content.length();
        int startIndex = 0;
        int partNumber = 1;

        while (startIndex < totalLength) {
            int endIndex = findSplitPoint(content, startIndex, MAX_LENGTH);
            String splitContent = content.substring(startIndex, endIndex);

            // Create new document using the builder pattern
            Document splitDoc = Document.builder()
                    .withId(doc.getId() + "_part" + partNumber)
                    .withContent(splitContent)
                    .withMedia(new ArrayList<>(doc.getMedia()))  // Copy media collection
                    .withMetadata(createSplitMetadata(doc, partNumber))
                    .build();

            splits.add(splitDoc);
            startIndex = endIndex;
            partNumber++;
        }

        return splits;
    }

    private static Map<String, Object> createSplitMetadata(Document originalDoc, int partNumber) {
        Map<String, Object> newMetadata = new HashMap<>(originalDoc.getMetadata());
        newMetadata.put("original_document_id", originalDoc.getId());
        newMetadata.put("part_number", partNumber);
        newMetadata.put("split_timestamp", System.currentTimeMillis());
        return newMetadata;
    }

    private static int findSplitPoint(String content, int startIndex, int maxLength) {
        int endIndex = Math.min(startIndex + maxLength, content.length());

        // Try to split at a paragraph boundary first
        if (endIndex < content.length()) {
            int paragraphEnd = endIndex;
            while (paragraphEnd > startIndex + maxLength * 0.8) {  // Look back up to 20% of max length
                if (isParagraphEnd(content, paragraphEnd)) {
                    return paragraphEnd + 1;  // Include the paragraph break
                }
                paragraphEnd--;
            }

            // If no paragraph break found, try sentence break
            int sentenceEnd = endIndex;
            while (sentenceEnd > startIndex + maxLength * 0.8) {
                if (isSentenceEnd(content, sentenceEnd)) {
                    return sentenceEnd + 1;
                }
                sentenceEnd--;
            }
        }

        return endIndex;
    }

    private static boolean isParagraphEnd(String content, int index) {
        if (index >= content.length() - 1) return false;
        return content.charAt(index) == '\n' &&
                (index + 1 >= content.length() || content.charAt(index + 1) == '\n');
    }

    private static boolean isSentenceEnd(String content, int index) {
        if (index >= content.length() - 1) return false;
        char c = content.charAt(index);
        char next = content.charAt(index + 1);
        return (c == '.' || c == '!' || c == '?') && Character.isWhitespace(next);
    }
}