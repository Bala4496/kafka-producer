package ua.bala.kafkaproducer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ua.bala.kafkaproducer.model.entity.Agent;
import ua.bala.kafkaproducer.repository.AgentRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BatchServiceTest {

    @InjectMocks
    private BatchService<Agent> agentBatchService;
    @Mock
    private DatabaseClient databaseClient;

    @InjectMocks
    private AgentServiceImpl agentService;
    @Mock
    private AgentRepository agentRepository;
    @Mock
    private BatchService<Agent> batchService;

    @Test
    void saveAllInBatch() {
        ReflectionTestUtils.setField(agentBatchService, "batchSize", 10);
        var agents = List.of(
                agentService.createAgent(),
                agentService.createAgent(),
                agentService.createAgent()
        );

        when(databaseClient.inConnectionMany(any()))
                .thenReturn(Flux.fromIterable(agents));

        var result = agentBatchService.saveAllInBatch(agents, "query", Collections.emptyList());

        StepVerifier.create(result)
                .expectNextCount(agents.size())
                .verifyComplete();
    }

    @Test
    void saveAllInBatchIfEmptyList() {
        List<Agent> agents = Collections.emptyList();

        var result = agentBatchService.saveAllInBatch(agents, "insertQuery", Collections.emptyList());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
