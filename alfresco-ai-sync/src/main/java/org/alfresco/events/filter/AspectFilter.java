package org.alfresco.events.filter;

import org.alfresco.event.sdk.handling.filter.AbstractEventFilter;
import org.alfresco.repo.event.v1.model.DataAttributes;
import org.alfresco.repo.event.v1.model.NodeResource;
import org.alfresco.repo.event.v1.model.RepoEvent;
import org.alfresco.repo.event.v1.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AspectFilter extends AbstractEventFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectFilter.class);

    private final String acceptedAspect;

    private AspectFilter(final String acceptedAspect) {
        this.acceptedAspect = Objects.requireNonNull(acceptedAspect);
    }

    public static AspectFilter of(final String acceptedAspect) {
        return new AspectFilter(acceptedAspect);
    }

    @Override
    public boolean test(RepoEvent<DataAttributes<Resource>> event) {
        LOGGER.debug("Checking filter for aspect {} and event {}", acceptedAspect, event);
        final NodeResource nodeResourceBefore = (NodeResource) event.getData().getResourceBefore();
        final NodeResource nodeResource = (NodeResource) event.getData().getResource();
        final boolean aspectExistedBefore = (nodeResourceBefore != null && nodeResourceBefore.getAspectNames().contains(acceptedAspect));
        final boolean aspectExists = (nodeResource != null && nodeResource.getAspectNames().contains(acceptedAspect));
        LOGGER.debug("The aspect {} has been added? {}", acceptedAspect, aspectExists && !aspectExistedBefore);
        return aspectExists && !aspectExistedBefore;
    }

}