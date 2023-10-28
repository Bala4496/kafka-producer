package ua.bala.kafkaproducer.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ua.bala.kafkaproducer.BaseIT;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka
class TelemetryProducerTest extends BaseIT {

    @Value("${spring.kafka.template.default-topic}")
    private String testTopic;

    @Autowired
    private TelemetryProducer producer;
    @Autowired
    private ConsumerFactory<String, TelemetryMessage> consumerFactory;

    private final EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, false, 1, testTopic);

    private KafkaMessageListenerContainer<String, TelemetryMessage> container;
    private BlockingQueue<ConsumerRecord<String, TelemetryMessage>> consumerRecords;

    @BeforeEach
    void setup() {
            consumerRecords = new LinkedBlockingQueue<>();

            var containerProperties = new ContainerProperties(testTopic);
            container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
            container.setupMessageListener((MessageListener<String, TelemetryMessage>) record -> consumerRecords.add(record));
            container.start();

            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }

    @AfterEach
    void after() {
        container.stop();
    }

    @Test
    void test_send_sendsMessageToBroker() throws InterruptedException {
        producer.sendMessage(new TelemetryMessage().setUuid(UUID.randomUUID().toString()));

        var consumedRecord = consumerRecords.poll(10, TimeUnit.SECONDS);

        assertNotNull(consumedRecord);
        assertEquals(consumedRecord.topic(), testTopic);
        assertNull(consumedRecord.key());
        assertNull(consumedRecord.value());
    }
}