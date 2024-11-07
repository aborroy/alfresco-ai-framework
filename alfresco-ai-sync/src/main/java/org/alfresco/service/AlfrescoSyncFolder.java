package org.alfresco.service;

import java.time.OffsetDateTime;

/**
 * Represents a folder in Alfresco that is marked for synchronization.
 * Contains metadata about the folder and its documents, including published, updated, and last document update dates.
 */
public record AlfrescoSyncFolder(
        String id,                    // The unique identifier of the folder
        OffsetDateTime publishedDate,  // The published date of the folder
        OffsetDateTime updatedDate,    // The last updated date of the folder
        OffsetDateTime docLastUpdatedDate // The last updated date of the documents in the folder
) {
}

