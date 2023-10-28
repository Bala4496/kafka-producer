package ua.bala.kafkaproducer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryProducer {

    private final KafkaTemplate<String, TelemetryMessage> kafkaTemplate;

//    @Async("basicTaskExecutor")
    public void sendMessage(TelemetryMessage message) {
        kafkaTemplate.sendDefault(message);
    }

}
