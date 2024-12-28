package com.ktds.krater.update.consumer;

import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.krater.common.dto.UsageUpdateRequest;
import com.ktds.krater.update.service.UsageUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsageUpdateConsumer {

    private final UsageUpdateService usageUpdateService;
    private final ObjectMapper objectMapper;

    public void processEvent(EventContext eventContext) {
        try {
            String eventData = eventContext.getEventData().getBodyAsString();
            UsageUpdateRequest request = objectMapper.readValue(eventData, UsageUpdateRequest.class);
            
            log.info("Received usage update event - userId: {}, type: {}, amount: {}", 
                    request.getUserId(), request.getType(), request.getAmount());
            
            usageUpdateService.updateUsage(request);
            eventContext.updateCheckpoint();
            
        } catch (Exception e) {
            log.error("Failed to process usage update event", e);
        }
    }

    public void processError(ErrorContext errorContext) {
        log.error("Error occurred in EventProcessor - {}", errorContext.getThrowable().getMessage());
    }
}
