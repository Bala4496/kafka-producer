package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import ua.bala.kafkaproducer.model.entity.Identifiable;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchService<T extends Identifiable<UUID>> {

    @Value("${app.batch.size}")
    private int batchSize;
    private final DatabaseClient databaseClient;

    public Flux<T> saveAllInBatch(List<T> entities,
                                  String insertQuery,
                                  List<Function<T, Object>> functions) {
        if (entities.isEmpty()) {
            return Flux.empty();
        }
        return splitList(entities, batchSize)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(subList -> saveInBatch(subList, insertQuery, functions))
                .sequential();
    }

    private static <T> Flux<List<T>> splitList(List<T> list, int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Invalid batch size: " + batchSize);
        }

        int listSize = list.size();
        int numStreams = (int) Math.ceil((double) listSize / batchSize);

        return Flux.fromStream(IntStream.range(0, numStreams)
                .mapToObj(i -> list.subList(i * batchSize, Math.min(listSize, (i + 1) * batchSize))));
    }

    private Flux<T> saveInBatch(List<T> entities,
                                String insertQuery,
                                List<Function<T, Object>> functions) {
        var entitiesMap = entities.stream()
                .collect(Collectors.toMap(Identifiable::getId, Function.identity()));
        return databaseClient.inConnectionMany(connection -> {
            var statement = connection.createStatement(insertQuery);
            entities.forEach(entity -> {
                statement.add();
                IntStream.range(0, functions.size()).forEach(i ->
                        statement.bind(i, functions.get(i).apply(entity))
                );
            });
            return Flux.from(statement.execute())
                    .flatMap(result -> result.map(row -> row.get("id", UUID.class)))
                    .map(entitiesMap::get);
        });
    }

}
