package ua.bala.kafkaproducer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.repository.AgentRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AgentServiceTest {

    @InjectMocks
    private AgentServiceImpl agentService;
    @Mock
    private AgentRepository agentRepository;
    @Mock
    private BatchService<Agent> batchService;

    @Test
    void testCreateAgentAndSave() {
        var agent = agentService.createAgent();

        assertNotNull(agent);
        assertNotNull(agent.getId());
        assertNotNull(agent.getManufacturer());
        assertNotNull(agent.getOs());
    }

    @Test
    void testSaveAll() {
        var agents = List.of(
                agentService.createAgent(),
                agentService.createAgent(),
                agentService.createAgent()
        );

        when(batchService.saveAllInBatch(eq(agents), anyString(), anyList()))
                .thenReturn(Flux.fromIterable(agents));

        var result = agentService.saveAll(agents);

        StepVerifier.create(result)
                .expectNextCount(agents.size())
                .verifyComplete();
    }

    @Test
    void testGetAvailableIds() {
        var agentIds = List.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        when(agentRepository.getAvailableIds())
                .thenReturn(Flux.fromIterable(agentIds));

        var result = agentService.getAvailableIds();

        StepVerifier.create(result)
                .expectNextCount(agentIds.size())
                .verifyComplete();
    }

    @Test
    void testRemoveAll() {
            when(agentRepository.deleteAll())
                    .thenReturn(Mono.empty());

            var result = agentService.removeAll();

            StepVerifier.create(result)
                    .expectNextCount(0L)
                    .verifyComplete();
    }

}
