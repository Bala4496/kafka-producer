package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.enums.Manufacturers;
import ua.bala.kafkaproducer.model.enums.OperationSystems;
import ua.bala.kafkaproducer.repository.AgentRepository;
import ua.bala.kafkaproducer.utils.EnumUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final BatchService<Agent> batchService;

    private static final String INSERT_AGENT_QUERY = "INSERT INTO agents (id, manufacturer, os) " +
                                                     "VALUES ($1, $2, $3) " +
                                                     "RETURNING id";
    private static final List<Function<Agent, Object>> AGENT_FUNCTION = List.of(
            Agent::getId,
            Agent::getManufacturer,
            Agent::getOs
    );

    public Agent createAgent() {
        return new Agent()
                .setManufacturer(EnumUtils.getRandomEnum(Manufacturers.class).getValue())
                .setOs(EnumUtils.getRandomEnum(OperationSystems.class).getValue());
    }

    public Flux<Agent> saveAll(List<Agent> agents) {
        return batchService.saveAllInBatch(agents,
                INSERT_AGENT_QUERY,
                AGENT_FUNCTION
        );
    }

    public Flux<UUID> getAvailableIds() {
        return agentRepository.getAvailableIds();
    }

    public Mono<Void> removeAll() {
        return agentRepository.deleteAll();
    }
}
