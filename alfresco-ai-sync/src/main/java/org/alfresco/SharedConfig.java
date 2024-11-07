package org.alfresco;

import org.alfresco.repo.event.v1.model.DataAttributes;
import org.alfresco.repo.event.v1.model.RepoEvent;
import org.alfresco.repo.event.v1.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Configuration class for shared beans used across the application.
 * Provides a thread-safe queue for repository events and a flag to track
 * the initial synchronization status.
 */
@Configuration
public class SharedConfig {

    /**
     * Creates a {@link BlockingQueue} bean to hold repository events.
     * This queue is thread-safe and can be used to process events in a
     * producer-consumer model.
     *
     * @return a {@link BlockingQueue} for {@link RepoEvent} instances.
     */
    @Bean
    public BlockingQueue<RepoEvent<DataAttributes<Resource>>> eventQueue() {
        return new LinkedBlockingQueue<>();
    }

    /**
     * Creates an {@link AtomicBoolean} bean to indicate whether the initial
     * synchronization process is complete. This can be used to prevent
     * certain actions from taking place until the initial sync has finished.
     *
     * @return an {@link AtomicBoolean} representing the initial sync status.
     */
    @Bean
    public AtomicBoolean isInitialSyncComplete() {
        return new AtomicBoolean(false);
    }
}