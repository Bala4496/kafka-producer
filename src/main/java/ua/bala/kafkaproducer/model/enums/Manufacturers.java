package ua.bala.kafkaproducer.model.enums;

import lombok.Getter;

@Getter
public enum Manufacturers {
    APPLE("Apple"),
    SAMSUNG("Samsung"),
    WINDOWS("Windows"),
    XIAOMI("Xiaomi");

    private final String value;

    Manufacturers(String value) {
        this.value = value;
    }
}
