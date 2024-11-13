package org.alfresco.ai_framework.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for ingesting documents into the vector store, utilizing document parsing and transformation.
 */
@Service
public class IngestionService {

    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);

    private final VectorStore vectorStore;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Ingests a document into the vector store by reading, transforming, and storing it.
     */
    public void ingest(String documentId, String folderId, String fileName, Resource file) {
        logger.info("Starting ingestion for document ID: {}, folder: {}", documentId, folderId);

        List<Document> documents = transformDocument(file);
        addMetadata(documents, documentId, folderId, fileName);

        deleteByDocumentId(documentId);  // Remove any existing documents with the same ID
        vectorStore.add(documents);

        logger.info("Ingestion complete for document ID: {}", documentId);
    }

    /**
     * Deletes documents from the vector store matching the specified document ID.
     */
    public void deleteByDocumentId(String documentId) {
        deleteDocuments("documentId", documentId);
    }

    /**
     * Deletes documents from the vector store matching the specified folder ID.
     */
    public void deleteByFolderId(String folderId) {
        deleteDocuments("folderId", folderId);
    }

    /**
     * Reads and transforms a document from the provided file resource.
     */
    private List<Document> transformDocument(Resource file) {
        List<Document> documentText = new TikaDocumentReader(file).get();
        return TokenTextSplitter.builder().build().apply(documentText);
    }

    /**
     * Adds metadata to each document.
     */
    private void addMetadata(List<Document> documents, String documentId, String folderId, String fileName) {
        documents.forEach(doc -> {
            doc.getMetadata().put("documentId", documentId);
            doc.getMetadata().put("folderId", folderId);
            doc.getMetadata().put("fileName", fileName);
        });
    }

    /**
     * Deletes documents from the vector store that match the specified metadata key and value.
     */
    private void deleteDocuments(String key, String value) {
        logger.info("Deleting documents with {}: {}", key, value);

        try {
            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.defaults().withFilterExpression("'" + key + "' == '" + value + "'")
            );

            if (!documents.isEmpty()) {
                vectorStore.delete(documents.stream().map(Document::getId).collect(Collectors.toList()));
                logger.info("Deleted {} document(s) with {}: {}", documents.size(), key, value);
            } else {
                logger.info("No documents found with {}: {}", key, value);
            }
        } catch (RuntimeException e) {
            logger.error("Error deleting documents with {}: {}", key, value, e);
        }
    }
}
