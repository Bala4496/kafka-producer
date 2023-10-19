package ua.bala.kafkaproducer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Agent;

public interface AgentService {

    Agent createAgent();
    Flux<Agent> saveAll(Flux<Agent> agents);
    Flux<Agent> getAll();
    Mono<Void> removeAll();
}
