package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.enums.Manufacturers;
import ua.bala.kafkaproducer.model.enums.OperationSystems;
import ua.bala.kafkaproducer.repository.AgentRepository;
import ua.bala.kafkaproducer.utils.EnumUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Agent createAgent() {
        return new Agent()
                .setManufacturer(EnumUtils.getRandomEnum(Manufacturers.class).getValue())
                .setOs(EnumUtils.getRandomEnum(OperationSystems.class).getValue());
    }

    public Flux<Agent> saveAll(Flux<Agent> agents) {
        return agentRepository.saveAll(agents); //TODO rework error
    }

    public Flux<Agent> getAll() {
        return agentRepository.findAll();
    }

    public Mono<Void> removeAll() {
        return agentRepository.deleteAll();
    }
}
