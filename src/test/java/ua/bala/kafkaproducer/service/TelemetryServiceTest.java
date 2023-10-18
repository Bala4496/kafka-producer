package ua.bala.kafkaproducer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.enums.Manufacturers;
import ua.bala.kafkaproducer.model.enums.OperationSystems;
import ua.bala.kafkaproducer.repository.TelemetryRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TelemetryServiceTest {

    @InjectMocks
    private TelemetryService telemetryService;
    @Mock
    private TelemetryRepository telemetryRepository;
    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Test
    void testBuildAndSaveTelemetryMessage() {
        var agent = getTestAgent();
        when(telemetryRepository.findFirstByAgentIdOrderByCreatedAtDesc(any(UUID.class))).thenReturn(Mono.empty());
        when(r2dbcEntityTemplate.insert(any(Telemetry.class))).thenAnswer(telemetry -> Mono.just(telemetry.getArgument(0, Telemetry.class)));

        var result = telemetryService.buildAndSaveTelemetryMessage(agent);

        StepVerifier.create(result)
                .consumeNextWith(message -> {
                    assertNotNull(message);
                    assertNotNull(message.getUuid());
                    assertEquals(message.getAgentId(), agent.getId().toString());
                    assertNotNull(message.getActiveService());
                    assertTrue(message.getQualityScore() > 0 && message.getQualityScore() < 100);
                    assertTrue(message.getPreviousMessageTime() != 0L);
                })
                .verifyComplete();

    }

    private Agent getTestAgent() {
        return new Agent()
                .setManufacturer(Manufacturers.WINDOWS.getValue())
                .setOs(OperationSystems.WINDOWS.getValue());
    }

}