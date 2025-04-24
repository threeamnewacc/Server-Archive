package me.devkevin.landcore.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtil {
    public static Integer getInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
