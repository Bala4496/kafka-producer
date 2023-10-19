package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;
import ua.bala.kafkaproducer.repository.TelemetryRepositoryRedis;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryServiceWithRedis implements TelemetryService {

    private final TelemetryRepositoryRedis telemetryRepository;

    public Mono<TelemetryMessage> buildAndSaveTelemetryMessage(Agent agent) {
        log.info("Building TelemetryMessage");
        return Mono.just(createTelemetry(agent))
                .flatMap(telemetry -> telemetryRepository.findByAgentId(agent.getId())
                        .map(Telemetry::getCreatedAt)
                        .defaultIfEmpty(LocalDateTime.now())
                        .flatMap(prevTelemetry -> telemetryRepository.save(telemetry)
                                .thenReturn(new TelemetryMessage()
                                        .setUuid(telemetry.getId().toString())
                                        .setAgentId(agent.getId().toString())
                                        .setPreviousMessageTime(prevTelemetry.toEpochSecond(ZoneOffset.UTC))
                                        .setActiveService(telemetry.getActiveService())
                                        .setQualityScore(telemetry.getQualityScore())
                                )
                        )
                );
    }

}
