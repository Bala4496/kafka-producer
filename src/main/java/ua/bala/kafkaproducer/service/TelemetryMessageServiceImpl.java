package ua.bala.kafkaproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

import java.time.ZoneOffset;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryMessageServiceImpl implements TelemetryMessageService {

    @Override
    public TelemetryMessage buildTelemetryMessage(Telemetry nextTelemetry, Telemetry prevTelemetry) {
        return new TelemetryMessage()
                .setUuid(nextTelemetry.getId().toString())
                .setAgentId(String.valueOf(nextTelemetry.getAgentId()))
                .setPreviousMessageTime(prevTelemetry.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
                .setActiveService(nextTelemetry.getActiveService())
                .setQualityScore(nextTelemetry.getQualityScore());
    }
}
