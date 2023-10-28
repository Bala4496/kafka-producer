package ua.bala.kafkaproducer.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ua.bala.kafkaproducer.model.entity.Agent;

import java.util.UUID;

public interface AgentRepository extends ReactiveCrudRepository<Agent, UUID> {

    @Query("select id from agents;")
    Flux<UUID> getAvailableIds();
}
