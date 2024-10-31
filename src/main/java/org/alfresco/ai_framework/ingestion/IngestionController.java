package org.alfresco.ai_framework.ingestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class IngestionController {

    @Autowired
    IngestionService ingestionService;

    @PostMapping("/documents")
    public void uploadDocument(
            @RequestParam("documentId") String documentId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Resource resource = new InputStreamResource(file.getInputStream()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        ingestionService.ingest(documentId, resource);
    }
}
