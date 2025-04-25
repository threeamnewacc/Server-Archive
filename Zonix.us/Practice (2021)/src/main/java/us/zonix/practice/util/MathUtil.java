package us.zonix.practice.util;

public final class MathUtil {
    public static String convertTicksToMinutes(int ticks) {
        long minute = (long)ticks / 1200L;
        long second = (long)ticks / 20L - minute * 60L;
        String secondString = Math.round((float)second) + "";
        if (second < 10L) {
            secondString = 0 + secondString;
        }

        String minuteString = Math.round((float)minute) + "";
        if (minute == 0L) {
            minuteString = "0";
        }

        return minuteString + ":" + secondString;
    }

    public static String convertToRomanNumeral(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            default:
                return null;
        }
    }

    public static double roundToHalves(double d) {
        return (double)Math.round(d * 2.0) / 2.0;
    }
}
