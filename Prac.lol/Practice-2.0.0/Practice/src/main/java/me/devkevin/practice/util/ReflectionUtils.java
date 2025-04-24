package me.devkevin.practice.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

/**
 * Copyright 31/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@UtilityClass
public class ReflectionUtils {

    public static void setField(Object obj, String field, Object value) {
        try {
            Field fieldObject = obj.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
