package org.alfresco.service;

import org.alfresco.ai.AIClient;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.NodeBodyUpdate;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service responsible for synchronizing documents between Alfresco and an AI service.
 * Handles initial document synchronization and subsequent event processing.
 */
@Service
public class AlfrescoClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoClient.class);

    private static final String PATH_QUERY_TEMPLATE =
            "ANCESTOR:\"workspace://SpacesStore/%s\" AND TYPE:\"cm:content\" AND cm:modified:[%s TO *]";
    private static final String FIELD_MODIFIED = "cm:modified";

    @Value("${alfresco.ai.sync.maxItems}")
    private int maxItems;

    @Value("${alfresco.ai.sync.aspect}")
    private String syncAspect;

    @Value("${alfresco.ai.sync.aspect.published}")
    private String propPublished;

    @Value("${alfresco.ai.sync.aspect.updated}")
    private String propUpdated;

    @Autowired
    private SearchApi searchApi;

    @Autowired
    private NodesApi nodesApi;

    @Autowired
    private AIClient aiClient;

    /**
     * Retrieves a list of folder IDs that are marked for synchronization.
     *
     * @return List of folder IDs
     */
    public List<String> getSyncFolders() {
        SearchRequest request = new SearchRequest()
                .query(new RequestQuery()
                        .language(RequestQuery.LanguageEnum.AFTS)
                        .query("ASPECT:\"" + syncAspect + "\" AND TYPE:\"cm:folder\""));
        return searchApi.search(request).getBody().getList().getEntries().stream()
                .map(ResultSetRowEntry::getEntry)
                .map(ResultNode::getId)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of folders that need to be synchronized based on latest document updated.
     *
     * @return List of folders that need synchronization
     */
    public List<AlfrescoSyncFolder> getFoldersToSync() {
        RequestInclude include = new RequestInclude();
        include.add("properties");

        SearchRequest baseRequest = new SearchRequest()
                .query(new RequestQuery()
                        .language(RequestQuery.LanguageEnum.AFTS)
                        .query("ASPECT:\"" + syncAspect + "\" AND TYPE:\"cm:folder\""))
                .include(include);

        List<ResultSetRowEntry> folders = searchApi.search(baseRequest).getBody().getList().getEntries();
        List<AlfrescoSyncFolder> syncFolders = new ArrayList<>();

        RequestSortDefinition sort = new RequestSortDefinition();
        sort.add(new RequestSortDefinitionInner()
                .type(RequestSortDefinitionInner.TypeEnum.FIELD)
                .field("cm:modified")
                .ascending(false));

        for (ResultSetRowEntry folder : folders) {
            SearchRequest folderRequest = new SearchRequest()
                    .query(new RequestQuery()
                            .language(RequestQuery.LanguageEnum.AFTS)
                            .query("ANCESTOR:\"workspace://SpacesStore/" + folder.getEntry().getId() + "\" AND TYPE:\"cm:content\""))
                    .sort(sort)
                    .paging(new RequestPagination().maxItems(1))
                    .include(include);

            List<ResultSetRowEntry> documents = searchApi.search(folderRequest).getBody().getList().getEntries();

            if (!documents.isEmpty()) {
                OffsetDateTime published = getDateTime(folder, propPublished);
                OffsetDateTime updated = getDateTime(folder, propUpdated);
                OffsetDateTime modified = documents.get(0).getEntry().getModifiedAt();

                if (updated != null && updated.isBefore(modified)) {
                    syncFolders.add(new AlfrescoSyncFolder(folder.getEntry().getId(), published, updated, modified));
                }
            }
        }
        return syncFolders;
    }

    /**
     * Extracts a date-time value from the folder entry properties.
     *
     * @param entry   Folder entry to extract the date-time from
     * @param property Property key for the date-time value
     * @return OffsetDateTime value if present, otherwise null
     */
    private static OffsetDateTime getDateTime(ResultSetRowEntry entry, String property) {
        return Optional.ofNullable(entry.getEntry().getProperties())
                .map(props -> (String) ((Map<?, ?>) props).get(property))
                .map(AlfrescoClient::parseDateTime)
                .orElse(null);
    }

    /**
     * Parses a string date-time representation to an OffsetDateTime object.
     *
     * @param dateTimeString The string representation of the date-time
     * @return Parsed OffsetDateTime object
     */
    private static OffsetDateTime parseDateTime(String dateTimeString) {
        return OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
    }

    /**
     * Synchronizes documents in the given folder, processing them in batches.
     *
     * @param processedCount Atomic integer to keep track of processed documents
     * @param folder         The folder to synchronize
     */
    public void synchronizeDocuments(AtomicInteger processedCount, AlfrescoSyncFolder folder) {
        RequestSortDefinition sortDefinition = createSortDefinition();
        boolean hasMoreItems;

        do {
            ResultSetPaging results = fetchAndProcessBatch(sortDefinition, processedCount, folder);
            hasMoreItems = Optional.ofNullable(results)
                    .map(ResultSetPaging::getList)
                    .map(ResultSetPagingList::getPagination)
                    .map(Pagination::isHasMoreItems)
                    .orElse(false);

            LOGGER.debug("Batch processing complete. More items available: {}", hasMoreItems);
        } while (hasMoreItems);
    }

    /**
     * Fetches a batch of documents to process and handles their processing.
     *
     * @param sortDefinition Sort definition to apply during the fetch
     * @param processedCount Atomic integer to track processed documents
     * @param folder         Folder to synchronize
     * @return ResultSetPaging containing the fetched documents
     */
    private ResultSetPaging fetchAndProcessBatch(RequestSortDefinition sortDefinition, AtomicInteger processedCount, AlfrescoSyncFolder folder) {
        LOGGER.debug("Fetching batch of documents (max: {})", maxItems);

        ResponseEntity<ResultSetPaging> searchResponse = executeSearch(sortDefinition, folder);
        List<ResultSetRowEntry> entries = searchResponse.getBody().getList().getEntries();

        processDocumentBatch(entries, processedCount);

        return searchResponse.getBody();
    }

    /**
     * Processes a batch of documents in parallel.
     *
     * @param entries Documents to process
     * @param processedCount Counter for processed documents
     */
    private void processDocumentBatch(List<ResultSetRowEntry> entries, AtomicInteger processedCount) {
        entries.parallelStream().forEach(entry -> {
            String uuid = entry.getEntry().getId();
            String name = entry.getEntry().getName();

            try {
                processDocument(uuid, name);
                processedCount.incrementAndGet();
                LOGGER.debug("Processed document: {} ({})", name, uuid);
            } catch (Exception e) {
                LOGGER.error("Failed to process document: {} ({})", name, uuid, e);
            }
        });
    }

    /**
     * Processes a single document by fetching its content and uploading it to the AI service.
     *
     * @param uuid Document identifier
     * @param documentName Document name
     * @throws IOException If processing fails
     */
    public void processDocument(String uuid, String documentName) throws IOException {
        try (InputStream content = nodesApi.getNodeContent(uuid, true, null, null)
                .getBody()
                .getInputStream()) {

            String response = aiClient.uploadDocument(uuid, documentName, content);
            LOGGER.debug("Document uploaded: {} - Response: {}", documentName, response);
        }
    }

    /**
     * Creates the sort definition used for sorting document queries.
     *
     * @return A RequestSortDefinition configured for sorting by modification date
     */
    private RequestSortDefinition createSortDefinition() {
        RequestSortDefinition sortDefinition = new RequestSortDefinition();
        sortDefinition.add(new RequestSortDefinitionInner()
                .type(RequestSortDefinitionInner.TypeEnum.FIELD)
                .field(FIELD_MODIFIED)
                .ascending(true));
        return sortDefinition;
    }

    /**
     * Executes a search query to fetch documents for synchronization.
     *
     * @param sortDefinition Sort definition to apply during the search
     * @param folder         Folder to search within
     * @return ResponseEntity containing the search results
     */
    private ResponseEntity<ResultSetPaging> executeSearch(RequestSortDefinition sortDefinition, AlfrescoSyncFolder folder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        SearchRequest request = new SearchRequest()
                .query(new RequestQuery()
                        .language(RequestQuery.LanguageEnum.AFTS)
                        .query(String.format(PATH_QUERY_TEMPLATE, folder.id(), folder.updatedDate().format(formatter))))
                .sort(sortDefinition)
                .paging(new RequestPagination().maxItems(maxItems).skipCount(0));

        return searchApi.search(request);
    }

    /**
     * Updates the modification time of a folder. Optionally updates the published time.
     *
     * @param folder   The folder to update
     * @param published If true, the published time is also updated
     */
    public void updateTime(String folder, boolean published) {
        String currentTime = Instant.now()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Map<String, Object> properties = new HashMap<>(Map.of(propUpdated, currentTime));
        if (published) {
            properties.put(propPublished, currentTime);
        }

        nodesApi.updateNode(folder, new NodeBodyUpdate().properties(properties), null, null);
    }
}
