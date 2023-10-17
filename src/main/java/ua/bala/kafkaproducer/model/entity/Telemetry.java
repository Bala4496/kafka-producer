package ua.bala.kafkaproducer.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("telemetries")
@Data
@Accessors(chain = true)
public class Telemetry {

    @Id
    @Setter(AccessLevel.NONE)
    private UUID id;
    private UUID agentId;
    private String activeService;
    private short qualityScore;
    @CreatedDate
    private LocalDateTime createdAt;

    public Telemetry() {
        this.id = UUID.randomUUID();
    }
}
