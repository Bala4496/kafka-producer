package ua.bala.kafkaproducer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.repository.TelemetryRepository;

import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TelemetryMessageServiceTest {

    @Spy
    private TelemetryMessageServiceImpl telemetryMessageService;
    @InjectMocks
    private TelemetryServiceImpl telemetryService;
    @Mock
    private TelemetryRepository telemetryRepository;
    @Mock
    private BatchService<Telemetry> batchService;

    @Test
    void testBuildTelemetryMessage() throws InterruptedException {
        var agentId = UUID.randomUUID();
        var prevTelemetry = telemetryService.createTelemetryByAgentId(agentId);
        TimeUnit.SECONDS.sleep(1L);
        var nextTelemetry = telemetryService.createTelemetryByAgentId(agentId);

        var telemetryMessage = telemetryMessageService.buildTelemetryMessage(nextTelemetry, prevTelemetry);

        assertNotNull(telemetryMessage);
        assertNotNull(telemetryMessage.getUuid());
        assertEquals(telemetryMessage.getUuid(), nextTelemetry.getId().toString());
        assertNotNull(telemetryMessage.getAgentId());
        assertEquals(telemetryMessage.getAgentId(), nextTelemetry.getAgentId().toString());
        assertTrue(telemetryMessage.getPreviousMessageTime() >= 0);
        assertEquals(telemetryMessage.getPreviousMessageTime(), prevTelemetry.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
        assertTrue(prevTelemetry.getCreatedAt().isBefore(nextTelemetry.getCreatedAt()));
        assertNotNull(telemetryMessage.getActiveService());
        assertEquals(telemetryMessage.getActiveService(), nextTelemetry.getActiveService());
        assertEquals(telemetryMessage.getQualityScore(), nextTelemetry.getQualityScore());
        assertTrue(telemetryMessage.getQualityScore() >= 0 &&
                   telemetryMessage.getQualityScore() <= 100);
    }

}
