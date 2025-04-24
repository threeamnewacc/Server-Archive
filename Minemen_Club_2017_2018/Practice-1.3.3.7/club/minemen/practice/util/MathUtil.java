// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.util;

public final class MathUtil
{
    public static String convertTicksToMinutes(final int ticks) {
        final long minute = ticks / 1200L;
        final long second = ticks / 20L - minute * 60L;
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
    
    public static String convertToRomanNumeral(final int number) {
        switch (number) {
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            default: {
                return null;
            }
        }
    }
    
    public static double roundToHalves(final double d) {
        return Math.round(d * 2.0) / 2.0;
    }
}
