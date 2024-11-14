package org.alfresco.events.handler;

import org.alfresco.ai.AIClient;
import org.alfresco.event.sdk.handling.filter.EventFilter;
import org.alfresco.event.sdk.handling.handler.OnNodeCreatedEventHandler;
import org.alfresco.event.sdk.handling.handler.OnNodeDeletedEventHandler;
import org.alfresco.event.sdk.handling.handler.OnNodeUpdatedEventHandler;
import org.alfresco.events.filter.AspectFilter;
import org.alfresco.repo.event.v1.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.alfresco.events.handler.ContentHandler.*;

@Component
public class FolderHandler implements OnNodeCreatedEventHandler, OnNodeUpdatedEventHandler, OnNodeDeletedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderHandler.class);

    @Autowired
    ContentHandler contentHandler;

    @Autowired
    private AIClient aiClient;

    @Value("${alfresco.ai.sync.aspect}")
    private String syncAspect;

    @Override
    public void handleEvent(RepoEvent<DataAttributes<Resource>> event) {
        NodeResource nodeResource = (NodeResource) event.getData().getResource();
        String uuid = nodeResource.getId();
        switch (event.getType()) {
            case CREATED:
            case UPDATED:
                LOGGER.debug("A new folder has been added for synchronization: {}", uuid);
                contentHandler.addFolder(uuid);
                break;
            case DELETED:
                LOGGER.debug("A folder has been removed from synchronization: {}", uuid);
                contentHandler.removeFolder(uuid);
                try {
                    String response = aiClient.deleteDocumentsFromFolder(uuid);
                    LOGGER.info("Deletion completed for folder ID {}: {}", uuid, response);
                } catch (IOException e) {
                    LOGGER.error("Failed to remove documents for folder: {}", uuid);
                }
                break;
            default:
                LOGGER.warn("Unhandled event type: {} for node ID: {}", event.getType(), uuid);
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
        return AspectFilter.of(syncAspect);
    }


}
