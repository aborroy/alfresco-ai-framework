package org.alfresco.events.filter;

import org.alfresco.event.sdk.handling.filter.AbstractEventFilter;
import org.alfresco.repo.event.v1.model.DataAttributes;
import org.alfresco.repo.event.v1.model.NodeResource;
import org.alfresco.repo.event.v1.model.RepoEvent;
import org.alfresco.repo.event.v1.model.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Event filter that checks if a repository event is related to a node within a specific parent folder hierarchy.
 * This filter extends AbstractEventFilter and specifically handles node-based repository events.
 */
public class ParentFolderFilter extends AbstractEventFilter {

    private final List<String> parentId;

    /**
     * Private constructor to enforce the use of factory method.
     *
     * @param parentId The uuid of the parent folder to filter against (must not be null)
     */
    private ParentFolderFilter(final List<String> parentId) {
        this.parentId = Objects.requireNonNull(parentId);
    }

    /**
     * Factory method to create a new instance of ParentFolderFilter.
     *
     * @param parentId The ID of the parent folder to filter against
     * @return A new ParentFolderFilter instance
     */
    public static ParentFolderFilter of(final List<String> parentId) {
        return new ParentFolderFilter(parentId);
    }

    /**
     * Tests if the given repository event matches the filter criteria.
     * The event must be a node event and the node must be within the specified parent folder hierarchy.
     *
     * @param event The repository event to test
     * @return true if the event matches the filter criteria, false otherwise
     */
    @Override
    public boolean test(RepoEvent<DataAttributes<Resource>> event) {
        if (!isNodeEvent(event)) {
            return false;
        }
        return Optional.ofNullable(event)
                .map(RepoEvent::getData)
                .map(DataAttributes::getResource)
                .filter(resource -> resource instanceof NodeResource)
                .map(resource -> (NodeResource) resource)
                .map(this::isParentInHierarchy)
                .orElse(false);
    }

    /**
     * Checks if the given node resource has the specified parent in its hierarchy.
     *
     * @param nodeResource The node resource to check
     * @return true if the parent is found in the hierarchy, false otherwise
     */
    private boolean isParentInHierarchy(NodeResource nodeResource) {
        return Optional.ofNullable(nodeResource)
                .map(NodeResource::getPrimaryHierarchy)
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(parentId::contains);
    }


}