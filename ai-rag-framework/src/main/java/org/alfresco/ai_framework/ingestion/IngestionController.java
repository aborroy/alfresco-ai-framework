package org.alfresco.ai_framework.ingestion;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for handling document ingestion requests, including upload and delete operations.
 */
@RestController
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    /**
     * Uploads a document to the system, ingesting it by the provided document and folder IDs.
     */
    @PostMapping("/documents")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("documentId") String documentId,
            @RequestParam("folderId") String folderId,
            @RequestParam("fileName") String fileName,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ingestionService.ingest(documentId, folderId, fileName, createFileResource(file));
            return ResponseEntity.ok("Document uploaded successfully with ID: " + documentId);
        } catch (IOException e) {
            return handleException("Failed to process file: ", e, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return handleException("Failed to ingest document: ", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a document by document ID.
     */
    @DeleteMapping("/documents")
    public ResponseEntity<String> deleteDocument(@RequestParam("documentId") String documentId) {
        return deleteById(() -> ingestionService.deleteByDocumentId(documentId), "document", documentId);
    }

    /**
     * Deletes documents in a folder by folder ID.
     */
    @DeleteMapping("/folders")
    public ResponseEntity<String> deleteDocumentsByFolder(@RequestParam("folderId") String folderId) {
        return deleteById(() -> ingestionService.deleteByFolderId(folderId), "folder", folderId);
    }

    /**
     * Creates a Resource from the MultipartFile to be ingested.
     */
    private Resource createFileResource(MultipartFile file) throws IOException {
        return new InputStreamResource(file.getInputStream()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

    /**
     * Deletes entities by ID, encapsulating the common deletion logic.
     */
    private ResponseEntity<String> deleteById(Runnable deleteAction, String entityType, String id) {
        try {
            deleteAction.run();
            return ResponseEntity.ok(entityType + " deleted successfully with ID: " + id);
        } catch (RuntimeException e) {
            return handleException("Failed to delete " + entityType + ": ", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles exceptions and creates a ResponseEntity with the given message and status.
     */
    private ResponseEntity<String> handleException(String message, Exception e, HttpStatus status) {
        return ResponseEntity.status(status).body(message + e.getMessage());
    }
}
