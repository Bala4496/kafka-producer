package ua.bala.kafkaproducer.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TelemetryProducer {

    private final KafkaTemplate<String, TelemetryMessage> kafkaTemplate;

    @Async("telemetryTaskExecutor")
    public CompletableFuture<SendResult<String, TelemetryMessage>> sendMessage(TelemetryMessage message) {
        return kafkaTemplate.sendDefault(message);
    }

}
