package ua.bala.kafkaproducer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.repository.TelemetryRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TelemetryServiceTest {

    @InjectMocks
    private TelemetryServiceImpl telemetryService;
    @Mock
    private TelemetryRepository telemetryRepository;
    @Mock
    private BatchService<Telemetry> batchService;

    @Test
    void testCreateTelemetryByAgentId() {
        var agentId = UUID.randomUUID();
        var telemetry = telemetryService.createTelemetryByAgentId(agentId);

        assertNotNull(telemetry);
        assertNotNull(telemetry.getId());
        assertNotNull(telemetry.getAgentId());
        assertNotNull(telemetry.getActiveService());
        assertTrue(telemetry.getQualityScore() >= 0 &&
                   telemetry.getQualityScore() <= 100);
        assertNotNull(telemetry.getCreatedAt());
    }

    @Test
    void testGetLastTelemetryByAgentId() {
        var agentId = UUID.randomUUID();
        var telemetry = telemetryService.createTelemetryByAgentId(agentId);

        when(telemetryRepository.findFirstByAgentIdOrderByCreatedAtDesc(agentId))
                .thenReturn(Mono.just(telemetry));

        var result = telemetryService.getLastTelemetryByAgentId(agentId);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    void testSaveAll() {
        var telemetries = List.of(
                telemetryService.createTelemetryByAgentId(UUID.randomUUID()),
                telemetryService.createTelemetryByAgentId(UUID.randomUUID()),
                telemetryService.createTelemetryByAgentId(UUID.randomUUID())
        );

        when(batchService.saveAllInBatch(eq(telemetries), anyString(), anyList()))
                .thenReturn(Flux.fromIterable(telemetries));

        var result = telemetryService.saveAll(telemetries);

        StepVerifier.create(result)
                .expectNextCount(telemetries.size())
                .verifyComplete();
    }

}
