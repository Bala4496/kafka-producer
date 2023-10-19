package ua.bala.kafkaproducer.service;

import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.enums.ActiveServices;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;
import ua.bala.kafkaproducer.utils.EnumUtils;

import java.util.Random;

public interface TelemetryService {
    Mono<TelemetryMessage> buildAndSaveTelemetryMessage(Agent agent);

    default Telemetry createTelemetry(Agent agent) {
        return new Telemetry()
                .setAgentId(agent.getId())
                .setActiveService(EnumUtils.getRandomEnum(ActiveServices.class).getValue())
                .setQualityScore(getRandomQualityScore());
    }

    default short getRandomQualityScore() {
        return (short) new Random().nextInt(100);
    }
}
