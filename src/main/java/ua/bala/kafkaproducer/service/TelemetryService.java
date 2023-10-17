package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.utils.EnumUtils;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.enums.ActiveServices;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;
import ua.bala.kafkaproducer.repository.TelemetryRepository;

import java.time.LocalDateTime;
import java.util.Random;

import java.time.ZoneOffset;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryService {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TelemetryRepository telemetryRepository;

    public Mono<TelemetryMessage> buildAndSaveTelemetryMessage(Agent agent) {
        log.info("Building TelemetryMessage");
        return Mono.just(createTelemetry(agent))
                .flatMap(telemetry -> telemetryRepository.findFirstByAgentIdOrderByCreatedAtDesc(agent.getId())
                        .map(Telemetry::getCreatedAt)
                        .defaultIfEmpty(LocalDateTime.now())
                        .flatMap(prevTelemetry -> r2dbcEntityTemplate.insert(telemetry)
                                .map(savedTelemetry -> new TelemetryMessage()
                                        .setUuid(telemetry.getId().toString())
                                        .setAgentId(agent.getId().toString())
                                        .setPreviousMessageTime(prevTelemetry.toEpochSecond(ZoneOffset.UTC))
                                        .setActiveService(telemetry.getActiveService())
                                        .setQualityScore(telemetry.getQualityScore())))
                );
    }

    private Telemetry createTelemetry(Agent agent) {
        return new Telemetry()
                .setAgentId(agent.getId())
                .setActiveService(EnumUtils.getRandomEnum(ActiveServices.class).getValue())
                .setQualityScore(getRandomQualityScore());
    }

    private short getRandomQualityScore() {
        return (short) new Random().nextInt(100);
    }
}
