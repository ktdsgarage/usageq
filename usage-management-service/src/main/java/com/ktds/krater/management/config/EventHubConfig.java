package com.ktds.krater.management.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubConfig {
    @Value("${eventhub.connectionString}")
    private String connectionString;

    @Value("${eventhub.name}")
    private String eventHubName;

    @Bean
    public EventHubProducerClient eventHubProducerClient() {
        return new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }
}
