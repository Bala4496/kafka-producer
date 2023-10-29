package ua.bala.kafkaproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class KafkaProducerApplicationTests extends BaseIT {

    @Test
    void contextLoads() {
    }

}
