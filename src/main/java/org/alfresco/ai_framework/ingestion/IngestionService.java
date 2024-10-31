package org.alfresco.ai_framework.ingestion;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    private final VectorStore vectorStore;

    IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    void ingest(String documentId, Resource file) {
        var tikaDocumentReader = new TikaDocumentReader(file);
        var textSplitter = TokenTextSplitter.builder().build();
        var transformedDocuments = textSplitter.apply(tikaDocumentReader.get());
        transformedDocuments.forEach(document -> {
            document.getMetadata().put("documentId", documentId);
        });
        vectorStore.add(transformedDocuments);
    }

}