package org.alfresco.ai_framework.ingestion;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

    /**
     * Constructs an IngestionController with the specified IngestionService.
     *
     * @param ingestionService The service responsible for processing document ingestion.
     */
    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    /**
     * Endpoint for uploading a document to the system.
     * Accepts a document ID and file to be ingested by the IngestionService.
     *
     * @param documentId Unique identifier for the document.
     * @param file       The file to be ingested.
     * @throws IOException If an error occurs while reading the file input stream.
     */
    @PostMapping("/documents")
    public void uploadDocument(
            @RequestParam("documentId") String documentId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Resource resource = createFileResource(file);
        ingestionService.ingest(documentId, resource);
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
