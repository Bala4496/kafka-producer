package ua.bala.kafkaproducer.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.kafkaproducer.model.entity.Telemetry;

import java.util.UUID;
import java.util.function.UnaryOperator;

import static ua.bala.kafkaproducer.config.RedisConfig.AGENT_KEY_PREFIX;

@Repository
@RequiredArgsConstructor
public class TelemetryRepositoryRedis {

    private final TelemetryRepository telemetryRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ReactiveRedisTemplate<String, Telemetry> reactiveRedisTemplate;
    private final UnaryOperator<String> prefixApplier = agentId -> String.join("#", AGENT_KEY_PREFIX, agentId);

    public Mono<Telemetry> findByAgentId(UUID agentId) {
        return reactiveRedisTemplate.opsForValue().get(getKey(agentId))
                .switchIfEmpty(telemetryRepository.findFirstByAgentIdOrderByCreatedAtDesc(agentId));
    }

    public Mono<Telemetry> save(Telemetry telemetry) {
        return r2dbcEntityTemplate.insert(telemetry)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(savedTelemetry -> reactiveRedisTemplate.opsForValue()
                        .set(getKey(savedTelemetry.getAgentId()), savedTelemetry)
                        .subscribe()
                );
    }

    private String getKey(UUID agentId) {
        return prefixApplier.apply(String.valueOf(agentId));
    }
}



