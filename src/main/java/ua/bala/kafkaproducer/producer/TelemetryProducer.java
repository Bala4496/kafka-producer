package ua.bala.kafkaproducer.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

import java.util.function.BiConsumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryProducer {

    private final KafkaTemplate<String, TelemetryMessage> kafkaTemplate;
    private final BiConsumer<SendResult<String, TelemetryMessage>, Throwable> completeAction = (messageSendResult, throwable) -> {
        var message = messageSendResult.getProducerRecord().value();

        if (throwable != null) {
            log.error("Error while sending message: " + message, throwable);
        } else {
            log.info("Message sent successfully: " + message);
        }
    };

    @Async
    public void sendMessage(TelemetryMessage message) {
        log.info("Telemetry message is being send: {}", message);
        kafkaTemplate.sendDefault(message)
                .whenComplete(completeAction);
    }

}
