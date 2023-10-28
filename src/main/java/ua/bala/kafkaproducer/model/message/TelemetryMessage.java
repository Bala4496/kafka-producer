package ua.bala.kafkaproducer.model.message;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TelemetryMessage {

    private String uuid;
    private String agentId;
    private long previousMessageTime;
    private String activeService;
    private short qualityScore;

}
