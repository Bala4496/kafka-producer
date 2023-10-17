package ua.bala.kafkaproducer.model.enums;

import lombok.Getter;

@Getter
public enum OperationSystems {
    IOS("macOS Sonoma"),
    LINUX("Linux Ubuntu 23.10"),
    WINDOWS("Windows 11");

    private final String value;

    OperationSystems(String value) {
        this.value = value;
    }
}
