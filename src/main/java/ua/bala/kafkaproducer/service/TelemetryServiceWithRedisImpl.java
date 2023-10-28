package ua.bala.kafkaproducer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.repository.TelemetryRepository;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static ua.bala.kafkaproducer.config.RedisConfig.AGENT_KEY_PREFIX;

@Service
@Slf4j
@Primary
@ConditionalOnProperty(name = "redis-cache.enabled", havingValue = "true")
public class TelemetryServiceWithRedisImpl extends TelemetryServiceImpl {

    private final ReactiveRedisTemplate<String, Telemetry> reactiveRedisTemplate;
    private static final UnaryOperator<String> PREFIX_APPLIER = agentId -> String.join("#", AGENT_KEY_PREFIX, agentId);

    public TelemetryServiceWithRedisImpl(TelemetryRepository telemetryRepository,
                                         BatchService<Telemetry> batchService,
                                         ReactiveRedisTemplate<String, Telemetry> reactiveRedisTemplate) {
        super(telemetryRepository, batchService);
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Telemetry> getLastTelemetryByAgentId(UUID agentId) {
        return reactiveRedisTemplate.opsForValue().get(getKey(agentId))
                .switchIfEmpty(super.getLastTelemetryByAgentId(agentId)
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(this::saveToRedis)
                );
    }

    @Override
    public Flux<Telemetry> saveAll(List<Telemetry> telemetries) {
        return super.saveAll(telemetries)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(this::saveToRedis);
    }

    private String getKey(UUID agentId) {
        return PREFIX_APPLIER.apply(String.valueOf(agentId));
    }

    private void saveToRedis(Telemetry telemetry) {
        reactiveRedisTemplate.opsForValue()
                .set(getKey(telemetry.getAgentId()), telemetry)
                .subscribe();
    }
}
