package org.alfresco.events.handler;

import jakarta.annotation.PostConstruct;
import org.alfresco.ai.AIClient;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.handling.handler.OnNodeCreatedEventHandler;
import org.alfresco.event.sdk.handling.handler.OnNodeDeletedEventHandler;
import org.alfresco.event.sdk.handling.handler.OnNodeUpdatedEventHandler;
import org.alfresco.events.filter.ParentFolderFilter;
import org.alfresco.repo.event.v1.model.*;
import org.alfresco.service.AlfrescoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Handler for content-related repository events that manages synchronization with an AI service.
 * Handles node creation, update, and deletion events within a specified folder.
 */
@Component
public class ContentHandler implements OnNodeCreatedEventHandler, OnNodeUpdatedEventHandler, OnNodeDeletedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentHandler.class);

    public static final String CREATED = "org.alfresco.event.node.Created";
    public static final String UPDATED = "org.alfresco.event.node.Updated";
    public static final String DELETED = "org.alfresco.event.node.Deleted";

    private static final String INITIALIZATION_ERROR = "Failed to initialize ContentHandler: {}";

    private List<String> folderIdsList;

    @Autowired
    private AIClient aiClient;

    @Autowired
    private AlfrescoClient alfrescoClient;

    @Autowired
    private BlockingQueue<RepoEvent<DataAttributes<Resource>>> eventQueue;

    @Autowired
    private AtomicBoolean isInitialSyncComplete;

    protected void addFolder(String folder) {
        folderIdsList.add(folder);
    }

    protected void removeFolder(String folder) {
        folderIdsList.add(folder);
    }

    /**
     * Initializes the handler by resolving the folder ID from the configured folder path.
     * Executed after dependency injection is complete.
     */
    @PostConstruct
    public void initialize() {
        LOGGER.info("Initializing ContentHandler");
        try {
            folderIdsList = alfrescoClient.getSyncFolders();
            LOGGER.info("Successfully initialized with folder ID: {}", folderIdsList);
        } catch (Exception e) {
            LOGGER.error(INITIALIZATION_ERROR, e.getMessage(), e);
            throw new IllegalStateException("Failed to resolve folder ID", e);
        }
    }

    /**
     * Handles repository events based on their type and current sync status.
     *
     * @param event The repository event to handle
     */
    @Override
    public void handleEvent(RepoEvent<DataAttributes<Resource>> event) {
        NodeResource nodeResource = extractNodeResource(event);
        String uuid = nodeResource.getId();

        LOGGER.info("Processing {} event for node ID: {}", event.getType(), uuid);

        try {
            if (isInitialSyncComplete.get()) {
                processEvent(event, nodeResource, uuid);

                int index = IntStream.range(0, nodeResource.getPrimaryHierarchy().size())
                        .filter(i -> folderIdsList.contains(nodeResource.getPrimaryHierarchy().get(i)))
                        .findFirst()
                        .orElse(-1);
                alfrescoClient.updateTime(folderIdsList.get(index), false);
            } else {
                LOGGER.warn("Initial sync pending. Queueing event for node ID: {}", uuid);
                eventQueue.add(event);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process {} event for node ID {}: {}",
                    event.getType(), uuid, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<EventType> getHandledEventTypes() {
        Set<EventType> handledEventTypes = Stream.of(
                        OnNodeCreatedEventHandler.super.getHandledEventTypes(),
                        OnNodeUpdatedEventHandler.super.getHandledEventTypes(),
                        OnNodeDeletedEventHandler.super.getHandledEventTypes()
                )
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        LOGGER.debug("Handling event types: {}", handledEventTypes);
        return handledEventTypes;
    }

    @Override
    public EventFilter getEventFilter() {
        return Optional.ofNullable(folderIdsList)
                .filter(ids -> !ids.isEmpty())
                .map(ParentFolderFilter::of)
                .orElseThrow(() -> new IllegalStateException("Folder IDs not initialized or list is empty"));
    }

    /**
     * Processes a repository event based on its type.
     */
    private void processEvent(RepoEvent<DataAttributes<Resource>> event, NodeResource nodeResource, String uuid)
            throws IOException {
        switch (event.getType()) {
            case CREATED:
                alfrescoClient.processDocument(uuid, getSyncFolderId(nodeResource), nodeResource.getName());
                break;
            case UPDATED:
                handleUpdateEvent(event, uuid, nodeResource);
                break;
            case DELETED:
                handleDeleteEvent(uuid);
                break;
            default:
                LOGGER.warn("Unhandled event type: {} for node ID: {}", event.getType(), uuid);
        }
    }

    public String getSyncFolderId(NodeResource nodeResource) {
        return  nodeResource.getPrimaryHierarchy().stream()
                .filter(folderIdsList::contains)
                .findFirst().orElse("");
    }

    /**
     * Handles update events by checking if content has changed.
     */
    private void handleUpdateEvent(RepoEvent<DataAttributes<Resource>> event, String uuid, NodeResource nodeResource) throws IOException {
        NodeResource nodeResourceBefore = (NodeResource) event.getData().getResourceBefore();
        if (nodeResourceBefore != null && nodeResourceBefore.getContent() != null) {
            alfrescoClient.processDocument(uuid, getSyncFolderId(nodeResource), nodeResource.getName());
        } else {
            LOGGER.info("Skipping update for node ID {} ({}): content unchanged",
                    uuid, nodeResource.getName());
        }
    }

    /**
     * Handles delete events by removing the document from the AI service.
     */
    private void handleDeleteEvent(String uuid) throws IOException {
        LOGGER.info("Processing deletion for node ID: {}", uuid);
        String response = aiClient.deleteDocument(uuid);
        LOGGER.info("Deletion completed for node ID {}: {}", uuid, response);
    }

    /**
     * Extracts and validates the NodeResource from an event.
     */
    private NodeResource extractNodeResource(RepoEvent<DataAttributes<Resource>> event) {
        return Optional.ofNullable(event)
                .map(RepoEvent::getData)
                .map(DataAttributes::getResource)
                .filter(resource -> resource instanceof NodeResource)
                .map(resource -> (NodeResource) resource)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event resource type"));
    }
}