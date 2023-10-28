package ua.bala.kafkaproducer.service;

import ua.bala.kafkaproducer.model.entity.Telemetry;
import ua.bala.kafkaproducer.model.message.TelemetryMessage;

public interface TelemetryMessageService {

    TelemetryMessage buildTelemetryMessage(Telemetry newTelemetry, Telemetry prevTelemetry);
}
