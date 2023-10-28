package ua.bala.kafkaproducer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Agent;

import java.util.List;
import java.util.UUID;

public interface AgentService {

    Agent createAgent();
    Flux<Agent> saveAll(List<Agent> agents);
    Flux<UUID> getAvailableIds();
    Mono<Void> removeAll();
}
