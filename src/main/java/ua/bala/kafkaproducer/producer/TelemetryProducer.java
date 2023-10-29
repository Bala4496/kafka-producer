package ua.bala.kafkaproducer.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

@Component
@RequiredArgsConstructor
public class TelemetryProducer {

    private final KafkaTemplate<String, TelemetryMessage> kafkaTemplate;

    public void sendMessage(TelemetryMessage message) {
        kafkaTemplate.sendDefault(message);
    }

}
