package ua.bala.kafkaproducer.producer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import ua.bala.kafkaproducer.BaseIT;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = "default-test-topic", partitions = 1)
class TelemetryProducerTestV2 extends BaseIT {

    @Value("${spring.kafka.template.default-topic}")
    private String testTopic;

    @Autowired
    private TelemetryProducer producer;
    @Autowired
    private ConsumerFactory<String, TelemetryMessage> consumerFactory;

    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.0"))
            .withEmbeddedZookeeper()
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeAll
    static void beforeAll() {
        kafkaContainer.start();
    }

    @AfterAll
    static void afterAll() {
        kafkaContainer.stop();
    }

    @Test
    void test_send_sendsMessageToBroker() throws InterruptedException {
        producer.sendMessage(new TelemetryMessage().setUuid(UUID.randomUUID().toString()));

//        var consumedRecord = consumerRecords.poll(10, TimeUnit.SECONDS);
//
//        assertNotNull(consumedRecord);
//        assertEquals(consumedRecord.topic(), testTopic);
//        assertNull(consumedRecord.key());
//        assertNull(consumedRecord.value());
    }
}