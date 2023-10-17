package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.utils.EnumUtils;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.enums.Manufacturers;
import ua.bala.kafkaproducer.model.enums.OperationSystems;
import ua.bala.kafkaproducer.repository.AgentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Agent> createAgentAndSave() {
        var agent = new Agent()
                .setManufacturer(EnumUtils.getRandomEnum(Manufacturers.class).getValue())
                .setOs(EnumUtils.getRandomEnum(OperationSystems.class).getValue());
        return r2dbcEntityTemplate.insert(agent)
                .doOnSuccess(savedAgent -> log.info("Agent saved: {}", savedAgent.getId()));
    }

    public Flux<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Mono<Void> removeAllAgents() {
        return agentRepository.deleteAll();
    }
}
