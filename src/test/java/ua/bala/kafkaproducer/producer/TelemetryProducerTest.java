package ua.bala.kafkaproducer.producer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ua.bala.kafkaproducer.BaseIT;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class TelemetryProducerTest extends BaseIT {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withEmbeddedZookeeper();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("KAFKA_SERVERS", kafkaContainer::getBootstrapServers);
    }

    static {
        kafkaContainer.start();
    }

    @Autowired
    private TelemetryProducer producer;

    @Test
    void testSendMessage() throws InterruptedException, ExecutionException {
        var telemetryMessage = new TelemetryMessage().setUuid(UUID.randomUUID().toString());

        var producerRecord = producer.sendMessage(telemetryMessage).get().getProducerRecord();

        assertNotNull(producerRecord);
        var key = producerRecord.key();
        var value = producerRecord.value();

        assertNotNull(key);
        assertNotNull(value);
        assertEquals(value, telemetryMessage);
    }

}
