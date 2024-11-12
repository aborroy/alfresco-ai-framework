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
 * Service responsible for handling the ingestion of documents into the vector store.
 * Utilizes a document reader and text splitter to parse and transform document content.
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
     *
     * @param documentId Unique identifier for the document.
     * @param fileName   Public name of the file.
     * @param file       The file resource to ingest.
     */
    public void ingest(String documentId, String fileName, Resource file) {
        logger.info("Starting ingestion for document ID: {}", documentId);

        // Read and split document into text chunks
        var transformedDocuments = transformDocument(file);

        // Add document ID to each document's metadata
        transformedDocuments.forEach(document -> {
            document.getMetadata().put("documentId", documentId);
            document.getMetadata().put("fileName", fileName);
        });

        // Remove existing documents with the same document ID before adding new ones
        deleteByDocumentId(documentId);

        // Add updated documents to the vector store
        vectorStore.add(transformedDocuments);

        logger.info("Successfully ingested document ID: {}", documentId);
    }

    /**
     * Deletes documents from the vector store that match the given document ID.
     *
     * @param documentId Unique identifier for the document to delete.
     */
    public void deleteByDocumentId(String documentId) {
        logger.info("Deleting existing documents with document ID: {}", documentId);

        // Perform a similarity search to find documents with the specified document ID
        try {
            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.defaults().withFilterExpression("'documentId' == '" + documentId + "'")
            );

            // Delete the found documents from the vector store
            if (!documents.isEmpty()) {
                vectorStore.delete(documents.stream()
                        .map(Document::getId)
                        .collect(Collectors.toList())
                );
                logger.info("Deleted {} document(s) with document ID: {}", documents.size(), documentId);
            } else {
                logger.info("No documents found with document ID: {}", documentId);
            }
        } catch (RuntimeException e) {
            logger.error("Failed to delete documents with ID: {}", documentId, e);
        }
    }

    /**
     * Reads and transforms a document from the provided file resource.
     *
     * @param file The file resource to read and transform.
     * @return A list of transformed document chunks.
     */
    private List<Document> transformDocument(Resource file) {
        var tikaDocumentReader = new TikaDocumentReader(file);
        var documentText = tikaDocumentReader.get();
        var textSplitter = TokenTextSplitter.builder().build();
        return textSplitter.apply(documentText);
    }
}
