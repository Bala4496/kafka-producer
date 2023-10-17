package ua.bala.kafkaproducer.model.enums;

import lombok.Getter;

@Getter
public enum ActiveServices {
    YOUTUBE("YouTube"),
    NETFLIX("Netflix"),
    FACEBOOK("Facebook"),
    INSTAGRAM("Instagram");

    private final String value;

    ActiveServices(String value) {
        this.value = value;
    }
}
