package ua.bala.kafkaproducer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Telemetry;

import java.util.List;
import java.util.UUID;

public interface TelemetryService {

    Telemetry createTelemetryByAgentId(UUID agentId);
    Mono<Telemetry> getLastTelemetryByAgentId(UUID agentId);
    Flux<Telemetry> saveAll(List<Telemetry> telemetries);
}
