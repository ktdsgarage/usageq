package com.ktds.krater.update.consumer;

import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktds.krater.common.dto.UsageUpdateRequest;
import com.ktds.krater.update.service.UsageUpdateService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsageUpdateConsumer {

    private final UsageUpdateService usageUpdateService;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    public void processEvent(EventContext eventContext) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            String eventData = eventContext.getEventData().getBodyAsString();
            UsageUpdateRequest request = objectMapper.readValue(eventData, UsageUpdateRequest.class);
            
            log.info("Received usage update event - userId: {}, type: {}, amount: {}", 
                    request.getUserId(), request.getType(), request.getAmount());
            
            usageUpdateService.updateUsage(request);
            eventContext.updateCheckpoint();

            sample.stop(meterRegistry.timer("event.processing.time"));
            meterRegistry.counter("events.processed").increment();

        } catch (Exception e) {
            meterRegistry.counter("events.errors").increment();
            log.error("Failed to process usage update event", e);
        }
    }

    public void processError(ErrorContext errorContext) {
        meterRegistry.counter("events.fatal.errors").increment();
        log.error("Error occurred in EventProcessor - {}", errorContext.getThrowable().getMessage());
    }
}
