package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.enums.ActiveServices;
import ua.bala.kafkaproducer.repository.TelemetryRepository;
import ua.bala.kafkaproducer.utils.EnumUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final BatchService<Telemetry> batchService;

    private static final String INSERT_TELEMETRY_QUERY = "INSERT INTO telemetries (id, agent_id, active_service, quality_score, created_at) " +
                                                         "VALUES ($1, $2, $3, $4, $5) " +
                                                         "RETURNING id;";
    private static final List<Function<Telemetry, Object>> TELEMETRY_FUNCTION = List.of(
            Telemetry::getId,
            Telemetry::getAgentId,
            Telemetry::getActiveService,
            Telemetry::getQualityScore,
            Telemetry::getCreatedAt
    );

    @Override
    public Flux<Telemetry> saveAll(List<Telemetry> telemetries) {
        return batchService.saveAllInBatch(telemetries,
                INSERT_TELEMETRY_QUERY,
                TELEMETRY_FUNCTION);
    }

    public Mono<Telemetry> getLastTelemetryByAgentId(UUID agentId) {
        return telemetryRepository.findFirstByAgentIdOrderByCreatedAtDesc(agentId);
    }

    @Override
    public Telemetry createTelemetryByAgentId(UUID agentId) {
        return new Telemetry()
                .setAgentId(agentId)
                .setActiveService(EnumUtils.getRandomEnum(ActiveServices.class).getValue())
                .setQualityScore(getRandomQualityScore());
    }

    private short getRandomQualityScore() {
        return (short) new Random().nextInt(100);
    }

}
