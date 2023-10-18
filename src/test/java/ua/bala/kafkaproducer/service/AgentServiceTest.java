package ua.bala.kafkaproducer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.model.enums.Manufacturers;
import ua.bala.kafkaproducer.model.enums.OperationSystems;
import ua.bala.kafkaproducer.repository.AgentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AgentServiceTest {

    @InjectMocks
    private AgentService agentService;
    @Mock
    private AgentRepository agentRepository;
    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Test
    void testCreateAgentAndSave() {
        when(r2dbcEntityTemplate.insert(any(Agent.class))).thenAnswer(agent -> Mono.just(agent.getArgument(0, Agent.class)));

        var result = agentService.createAgentAndSave();

        StepVerifier.create(result)
                .consumeNextWith(agent -> {
                    assertNotNull(agent);
                    assertNotNull(agent.getId());
                    assertNotNull(agent.getManufacturer());
                    assertNotNull(agent.getOs());
                })
                .verifyComplete();
    }

    @Test
    void testGetAllAgents() {
        var agents = List.of(
                getTestAgent(),
                getTestAgent(),
                getTestAgent()
        );
        when(agentRepository.findAll()).thenReturn(Flux.fromIterable(agents));

        var result = agentRepository.findAll();

        StepVerifier.create(result)
                .expectNextCount(agents.size())
                .verifyComplete();
    }

    @Test
    void testRemoveAllAgents() {
        when(agentRepository.deleteAll()).thenReturn(Mono.empty());

        var result = agentRepository.deleteAll();

        StepVerifier.create(result).verifyComplete();
    }

    private Agent getTestAgent() {
        return new Agent()
                .setManufacturer(Manufacturers.WINDOWS.getValue())
                .setOs(OperationSystems.WINDOWS.getValue());
    }
}