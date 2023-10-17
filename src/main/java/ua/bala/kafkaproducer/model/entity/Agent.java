package ua.bala.kafkaproducer.model.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("agents")
@Data
@Accessors(chain = true)
public class Agent {

    @Id
    @Setter(AccessLevel.NONE)
    private UUID id;
    private String manufacturer;
    private String os;

    public Agent() {
        this.id = UUID.randomUUID();
    }
}
