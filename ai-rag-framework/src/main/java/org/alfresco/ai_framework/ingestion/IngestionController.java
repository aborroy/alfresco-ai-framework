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
 * REST controller for handling document ingestion requests.
 * Provides an endpoint to upload documents by accepting a document ID and file.
 */
@RestController
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    /**
     * Endpoint for uploading a document to the system.
     * Accepts a document ID and file to be ingested by the IngestionService.
     *
     * @param documentId Unique identifier for the document.
     * @param fileName   Public name of the file.
     * @param file       The file to be ingested.
     */
    @PostMapping("/documents")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("documentId") String documentId,
            @RequestParam("fileName") String fileName,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            Resource resource = createFileResource(file);
            ingestionService.ingest(documentId, fileName, resource);
            return ResponseEntity.ok("Document uploaded successfully with ID: " + documentId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to process file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to ingest document: " + e.getMessage());
        }
    }

    /**
     * Endpoint for deleting a document from the system.
     * Accepts a document ID to delete the corresponding document from the system.
     *
     * @param documentId Unique identifier for the document.
     * @return ResponseEntity with the status of the deletion.
     */
    @DeleteMapping("/documents")
    public ResponseEntity<?> deleteDocument(@RequestParam("documentId") String documentId) {
        try {
            ingestionService.deleteByDocumentId(documentId);
            return ResponseEntity.ok("Document deleted successfully with ID: " + documentId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete document: " + e.getMessage());
        }
    }


    /**
     * Creates a Resource from the MultipartFile to be ingested.
     *
     * @param file The file uploaded by the user.
     * @return A Resource representation of the file.
     * @throws IOException If an error occurs while accessing the file input stream.
     */
    private Resource createFileResource(MultipartFile file) throws IOException {
        return new InputStreamResource(file.getInputStream()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }
}
