package me.devkevin.practice.util;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TimeUtil {

    public static String millisToTimer(long millis) {
        final long seconds = millis / 1000L;
        if (seconds > 3600L) {
            return String.format("%02d:%02d:%02d", seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        }
        return String.format("%02d:%02d", seconds / 60L, seconds % 60L);
    }

    public static String convertToFormat(long delay) {

        if (delay == 0L) {
            return "None";
        }

        return TimeUtil.formatTime(Math.abs((delay - System.currentTimeMillis())));
    }

    public static String formatTime(long millis) {
        int sec = (int) (millis / 1000 % 60);
        int min = (int) (millis / 60000 % 60);
        int hr = (int) (millis / 3600000 % 24);
        return ((hr > 0) ? String.format("%02d:", hr) : "") + String.format("%02d:%02d", min, sec);
    }

    public static String formatIntoDetailedString(int secs) {
        if (secs == 0)
            return "0 seconds";
        int remainder = secs % 86400;
        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = remainder / 60 - hours * 60;
        int seconds = remainder % 3600 - minutes * 60;
        String fDays = (days > 0) ? (" " + days + " day" + ((days > 1) ? "s" : "")) : "";
        String fHours = (hours > 0) ? (" " + hours + " hour" + ((hours > 1) ? "s" : "")) : "";
        String fMinutes = (minutes > 0) ? (" " + minutes + " minute" + ((minutes > 1) ? "s" : "")) : "";
        String fSeconds = (seconds > 0) ? (" " + seconds + " second" + ((seconds > 1) ? "s" : "")) : "";
        return (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static String formatLongIntoDetailedString(long secs) {
        int unconvertedSeconds = (int) secs;
        return formatIntoDetailedString(unconvertedSeconds);
    }
}
