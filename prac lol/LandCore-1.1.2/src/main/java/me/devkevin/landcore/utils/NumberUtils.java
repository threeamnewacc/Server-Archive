package me.devkevin.landcore.utils;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 12:25
 * NumberUtils / land.pvp.core.utils / LandCore
 */
public class NumberUtils {
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }
}

