package net.badlion.common.libraries;

public class EnumCommon {

    public static <T extends Enum<T>> T getEnumValueOf(Class<T> e, String... keys) {
        for (String key : keys) {
            try {
                return Enum.valueOf(e, key);
            } catch (IllegalArgumentException ex) {
                continue;
            }
        }

        throw new IllegalArgumentException("Could not find enum we were looking for " + e.getName());
    }

}
