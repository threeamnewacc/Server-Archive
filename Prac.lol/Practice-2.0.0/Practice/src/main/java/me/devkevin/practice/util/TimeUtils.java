package me.devkevin.practice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

/**
 * Copyright 03/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public final class TimeUtils {

    private static final ThreadLocal<StringBuilder> MMSS_BUILDER = ThreadLocal.withInitial((Supplier<? extends StringBuilder>) StringBuilder::new);

    public static String formatIntoMMSS(int secs) {
        final int seconds = secs % 60;
        secs -= seconds;
        long minutesCount = secs / 60;
        final long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        final long hours = minutesCount / 60L;
        final StringBuilder result = MMSS_BUILDER.get();
        result.setLength(0);
        if (hours > 0L) {
            if (hours < 10L) {
                result.append("0");
            }
            result.append(hours);
            result.append(":");
        }
        if (minutes < 10L) {
            result.append("0");
        }
        result.append(minutes);
        result.append(":");
        if (seconds < 10) {
            result.append("0");
        }
        result.append(seconds);
        return result.toString();
    }

    public static String nowDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String dateToString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.getTime().toString();
    }
}

