package ua.bala.kafkaproducer.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class EnumUtils {

    public  <T extends Enum<?>> T getRandomEnum(Class<T> clazz) {
        int index = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[index];
    }
}
