package ua.bala.kafkaproducer.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.producer.TelemetryProducer;
import ua.bala.kafkaproducer.service.AgentService;
import ua.bala.kafkaproducer.service.TelemetryMessageService;
import ua.bala.kafkaproducer.service.TelemetryService;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.agent.load", havingValue = "true")
public class AgentInitializer {

    @Value("${app.agent.count}")
    private int agentCount;
    private final AgentService agentService;
    private final TelemetryService telemetryService;
    private final TelemetryMessageService telemetryMessageService;
    private final TelemetryProducer telemetryProducer;
    private final ReactiveRedisTemplate<String, Telemetry> reactiveRedisTemplate;
    private boolean isReady = true;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        isReady = false;
        reactiveRedisTemplate.keys("*")
                .flatMap(reactiveRedisTemplate.opsForValue()::delete)
                .then()
                .block();
        agentService.removeAll()
                .doOnSuccess(success -> log.info("Agents removed"))
                .block();
        log.info("Initiating agents");
        long start = System.currentTimeMillis();
        Flux.range(0, agentCount)
                .map(i -> agentService.createAgent())
                .collectList()
                .doOnSuccess(agents -> log.info("Agents created - {}", agents.size()))
                .flatMapMany(agentService::saveAll)
                .count()
                .doOnSuccess(count -> log.info("Agents saved - {}", count))
                .doFinally(signal -> log.info("Agents saved for {} ms", System.currentTimeMillis() - start))
                .block();
        isReady = true;
    }

    @Scheduled(initialDelay = 5000, fixedRateString = "#{60000 / ${app.message.ratePerMinute}}")
    public void sendMessageToKafka() {
        if (!isReady) {
            return;
        }
        log.info("Initiating sending messages to Kafka");
        long start = System.currentTimeMillis();
        var availableIds = agentService.getAvailableIds();
        var previousTransactionFlux = availableIds.flatMap(telemetryService::getLastTelemetryByAgentId);

        availableIds.map(telemetryService::createTelemetryByAgentId)
                .collectList()
                .doOnNext(ids -> log.info("Preparing {} messages", ids.size()))
                .flatMapMany(telemetryService::saveAll)
                .zipWith(previousTransactionFlux)
                .parallel()
                .map(tuple -> telemetryMessageService.buildTelemetryMessage(tuple.getT1(), tuple.getT2()))
                .doOnNext(telemetryProducer::sendMessage)
                .sequential()
                .count()
                .doOnSuccess(count -> log.info("Telemetries sent - {}", count))
                .doFinally(signal -> log.info("Telemetries sent for {} ms", System.currentTimeMillis() - start))
                .subscribe();
    }

}
