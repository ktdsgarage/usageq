package com.ktds.krater.update.config;

import com.azure.messaging.eventhubs.CheckpointStore;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.ktds.krater.update.consumer.UsageUpdateConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import jakarta.annotation.PreDestroy;

@Configuration
public class EventHubConfig {

    @Value("${eventhub.connectionString}")
    private String eventHubConnectionString;

    @Value("${eventhub.name}")
    private String eventHubName;

    @Value("${eventhub.consumerGroup}")
    private String consumerGroup;

    @Value("${eventhub.checkpointStore.containerName}")
    private String containerName;

    @Value("${eventhub.checkpointStore.connectionString}")
    private String storageConnectionString;

    private EventProcessorClient eventProcessorClient;

    @Bean
    public BlobContainerAsyncClient blobContainerAsyncClient() {
        return new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName(containerName)
                .buildAsyncClient();
    }

    @Bean
    public CheckpointStore checkpointStore(BlobContainerAsyncClient blobContainerAsyncClient) {
        return new BlobCheckpointStore(blobContainerAsyncClient);
    }

    @Bean
    public EventProcessorClient eventProcessorClient(
            CheckpointStore checkpointStore,
            UsageUpdateConsumer usageUpdateConsumer) {

        eventProcessorClient = new EventProcessorClientBuilder()
                .connectionString(eventHubConnectionString, eventHubName)
                .consumerGroup(consumerGroup)
                .checkpointStore(checkpointStore)
                .processEvent(usageUpdateConsumer::processEvent)
                .processError(usageUpdateConsumer::processError)
                .buildEventProcessorClient();

        eventProcessorClient.start();
        return eventProcessorClient;
    }

    @PreDestroy
    public void cleanUp() {
        if (eventProcessorClient != null) {
            eventProcessorClient.stop();
        }
    }
}
