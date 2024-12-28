package com.ktds.krater.management.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.krater.common.dto.UsageUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageManagementService {
    private final EventHubProducerClient eventHubProducerClient;
    private final ObjectMapper objectMapper;

    public void updateUsage(UsageUpdateRequest request) {
        try {
            String jsonData = objectMapper.writeValueAsString(request);
            EventData eventData = new EventData(jsonData);
            // EventData를 List로 감싸서 전송
            eventHubProducerClient.send(Collections.singletonList(eventData));
            log.info("Successfully sent usage update event for userId: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Failed to send usage update event", e);
            throw new RuntimeException("Failed to process usage update", e);
        }
    }
}