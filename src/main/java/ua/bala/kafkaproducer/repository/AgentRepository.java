package ua.bala.kafkaproducer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ua.bala.kafkaproducer.model.entity.Agent;

import java.util.UUID;

public interface AgentRepository extends ReactiveCrudRepository<Agent, UUID> {
}
