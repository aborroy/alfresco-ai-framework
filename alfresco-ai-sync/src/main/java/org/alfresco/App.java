package org.alfresco;

import org.alfresco.events.handler.ContentHandler;
import org.alfresco.repo.event.v1.model.DataAttributes;
import org.alfresco.repo.event.v1.model.RepoEvent;
import org.alfresco.repo.event.v1.model.Resource;
import org.alfresco.service.AlfrescoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main application class for initializing synchronization and processing events.
 * It performs an initial synchronization for folders and processes queued events using a thread pool.
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final long EXECUTOR_SHUTDOWN_TIMEOUT = 5L;

    @Value("${alfresco.ai.sync.parallel.threads}")
    private int parallelThreads;

    @Autowired
    private AtomicBoolean isInitialSyncComplete;

    @Autowired
    private BlockingQueue<RepoEvent<DataAttributes<Resource>>> eventQueue;

    @Autowired
    private AlfrescoClient alfrescoClient;

    @Autowired
    private ContentHandler contentHandler;

    public static void main(String... args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * Runs the application logic upon startup, performing an initial synchronization
     * and then processing any queued events.
     *
     * @param args Command-line arguments
     */
    @Override
    public void run(String... args) {
        LOGGER.info("Starting initial sync process.");
        performInitialSync();
        isInitialSyncComplete.set(true);
        LOGGER.info("Finished initial sync process.");
        processQueuedEvents();
    }

    /**
     * Performs the initial synchronization of documents from folders.
     * Updates the synchronization timestamp for each folder.
     */
    private void performInitialSync() {
        alfrescoClient.getFoldersToSync().forEach(folder -> {
            LOGGER.info("Starting initial synchronization for folder: {}", folder);
            var processedCount = new AtomicInteger(0);
            alfrescoClient.synchronizeDocuments(processedCount, folder);
            LOGGER.info("Initial synchronization for folder {} complete. Processed {} documents", folder, processedCount.get());
            alfrescoClient.updateTime(folder.id(), true);
        });
    }

    /**
     * Processes any events that were queued during the initial synchronization.
     * Events are processed using a fixed thread pool of the specified size.
     */
    private void processQueuedEvents() {
        if (eventQueue.isEmpty()) {
            LOGGER.info("No events to process in the queue.");
            return;
        }

        LOGGER.info("Processing {} queued events with {} threads", eventQueue.size(), parallelThreads);
        var executor = Executors.newFixedThreadPool(parallelThreads);

        try {
            processEventQueue(executor);
        } finally {
            shutdownExecutor(executor);
        }
    }

    /**
     * Processes each event in the queue using the provided executor service.
     *
     * @param executor ExecutorService responsible for parallel event processing
     */
    private void processEventQueue(ExecutorService executor) {
        RepoEvent<DataAttributes<Resource>> event;
        while ((event = eventQueue.poll()) != null) {
            RepoEvent<DataAttributes<Resource>> finalEvent = event;
            executor.submit(() -> handleEvent(finalEvent));
        }
    }

    /**
     * Handles a single event using the ContentHandler. Logs errors if event processing fails.
     *
     * @param event RepoEvent to be processed
     */
    private void handleEvent(RepoEvent<DataAttributes<Resource>> event) {
        try {
            contentHandler.handleEvent(event);
        } catch (Exception e) {
            LOGGER.error("Failed to process event: {}", event, e);
        }
    }

    /**
     * Shuts down the executor service gracefully. If termination times out,
     * forces a shutdown and interrupts any remaining tasks.
     *
     * @param executor ExecutorService to shut down
     */
    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
