package ua.bala.kafkaproducer.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ua.bala.kafkaproducer.producer.TelemetryProducer;
import ua.bala.kafkaproducer.service.AgentService;
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
    private final TelemetryProducer telemetryProducer;


    @EventListener(ApplicationReadyEvent.class)
    public void initAgents() {
        cleanUpAgents();
        log.info("Initiating agents");
        Flux.range(0, agentCount)
                .flatMap(i -> agentService.createAgentAndSave())
                .then()
                .doOnSuccess(success -> log.info("Agents initialized: {}", agentCount))
                .subscribe();
    }

    public void cleanUpAgents() {
        agentService.removeAllAgents()
                .doOnSuccess(success -> log.info("Agents removed"))
                .block();
    }

    @Scheduled(fixedRateString = "#{60000 / ${app.message.ratePerMinute}}")
    public void sendMessageToKafka() {
        log.info("Sending telemetry messages");
        agentService.getAllAgents()
                .flatMap(agent -> telemetryService.buildAndSaveTelemetryMessage(agent)
                        .doOnNext(telemetryProducer::sendMessage)
                )
                .subscribe();
    }
}
