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
    private AgentServiceImpl agentService;
    @Mock
    private AgentRepository agentRepository;
    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Test
    void testCreateAgentAndSave() {
        var agent = agentService.createAgent();

        assertNotNull(agent);
        assertNotNull(agent.getId());
        assertNotNull(agent.getManufacturer());
        assertNotNull(agent.getOs());
    }

    @Test
    void testSaveAllAgents() {
        var agents = List.of(
                getTestAgent(),
                getTestAgent(),
                getTestAgent()
        );
        when(agentRepository.saveAll(any(Flux.class))).thenReturn(Flux.fromIterable(agents));

        var result = agentService.saveAll(agents);

        StepVerifier.create(result)
                .expectNextCount(agents.size())
                .verifyComplete();
    }

//    @Test
//    void testGetAllAgents() {
//        var agents = List.of(
//                getTestAgent(),
//                getTestAgent(),
//                getTestAgent()
//        );
//        when(agentRepository.findAll()).thenReturn(Flux.fromIterable(agents));
//
//        var result = agentService.getAll();
//
//        StepVerifier.create(result)
//                .expectNextCount(agents.size())
//                .verifyComplete();
//    }

    @Test
    void testRemoveAllAgents() {
        when(agentRepository.deleteAll()).thenReturn(Mono.empty());

        var result = agentService.removeAll();

        StepVerifier.create(result).verifyComplete();
    }

    private Agent getTestAgent() {
        return new Agent()
                .setManufacturer(Manufacturers.WINDOWS.getValue())
                .setOs(OperationSystems.WINDOWS.getValue());
    }
}