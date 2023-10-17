package ua.bala.kafkaproducer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Telemetry;

import java.util.UUID;

public interface TelemetryRepository extends ReactiveCrudRepository<Telemetry, UUID> {
    Mono<Telemetry> findFirstByAgentIdOrderByCreatedAtDesc(UUID agentId);
}
