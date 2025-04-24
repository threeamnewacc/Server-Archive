// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import club.mineman.core.util.Duration;
import java.sql.Timestamp;

public final class TimeUtil
{
    private TimeUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }
    
    public static Timestamp addDuration(final long duration) {
        return new Timestamp(System.currentTimeMillis() + duration);
    }
    
    public static Timestamp addDuration(final Timestamp timestamp) {
        return new Timestamp(System.currentTimeMillis() + timestamp.getTime());
    }
    
    public static Timestamp fromMillis(final long millis) {
        return new Timestamp(millis);
    }
    
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    public static long toMillis(final String time) {
        long outTime = 0L;
        final String[] split;
        final String[] timesArray = split = time.split(" ");
        for (String timeChars : split) {
            final String aTimesArray = timeChars;
            final Duration[] values;
            final Duration[] timeTypes = values = Duration.values();
            for (final Duration duration : values) {
                if (timeChars.contains(duration.getName())) {
                    timeChars = timeChars.replaceAll(duration.getName(), "");
                    final long rawTime = Integer.parseInt(timeChars);
                    outTime += rawTime * duration.getDuration();
                }
            }
        }
        return outTime;
    }
    
    public static String millisToRoundedTime(long millis) {
        ++millis;
        final long seconds = millis / 1000L;
        final long minutes = seconds / 60L;
        final long hours = minutes / 60L;
        final long days = hours / 24L;
        final long weeks = days / 7L;
        final long months = weeks / 4L;
        final long years = months / 12L;
        if (years > 0L) {
            return years + " year" + ((years == 1L) ? "" : "s");
        }
        if (months > 0L) {
            return months + " month" + ((months == 1L) ? "" : "s");
        }
        if (weeks > 0L) {
            return weeks + " week" + ((weeks == 1L) ? "" : "s");
        }
        if (days > 0L) {
            return days + " day" + ((days == 1L) ? "" : "s");
        }
        if (hours > 0L) {
            return hours + " hour" + ((hours == 1L) ? "" : "s");
        }
        if (minutes > 0L) {
            return minutes + " minute" + ((minutes == 1L) ? "" : "s");
        }
        return seconds + " second" + ((seconds == 1L) ? "" : "s");
    }
    
    public static long parseTime(final String time) {
        long totalTime = 0L;
        boolean found = false;
        final Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);
        while (matcher.find()) {
            final String s = matcher.group();
            final Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            final String s2;
            final String type = s2 = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
            switch (s2) {
                case "s": {
                    totalTime += value;
                    found = true;
                    continue;
                }
                case "m": {
                    totalTime += value * 60L;
                    found = true;
                    continue;
                }
                case "h": {
                    totalTime += value * 60L * 60L;
                    found = true;
                    continue;
                }
                case "d": {
                    totalTime += value * 60L * 60L * 24L;
                    found = true;
                    continue;
                }
                case "w": {
                    totalTime += value * 60L * 60L * 24L * 7L;
                    found = true;
                    continue;
                }
                case "M": {
                    totalTime += value * 60L * 60L * 24L * 30L;
                    found = true;
                    continue;
                }
                case "y": {
                    totalTime += value * 60L * 60L * 24L * 365L;
                    found = true;
                    continue;
                }
            }
        }
        return found ? (totalTime * 1000L) : -1L;
    }
}
