package me.devkevin.landcore.utils.time;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFormatUtils {

    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static String getDetailedTime(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? "y" : "y").append(day == 0 ? "" : ",");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? "d" : "d").append(hours == 0 ? "" : ",");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? "h" : "h").append(minutes == 0 ? "" : ",");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? "m" : "m").append(seconds == 0 ? "" : ",");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? "s" : "s");
        }

        return time.toString().trim();
    }

    public static String getSimpleDetailedTime(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? "y" : "y");
        }

        else if (day != 0) {
            time.append(day).append(day == 1 ? "d" : "d");
        }

        else if (hours != 0) {
            time.append(hours).append(hours == 1 ? "h" : "h");
        }

        else if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? "m" : "m");
        }

        else if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? "s" : "s");
        }

        return time.toString().trim();
    }

    public static String getMoreDetailedTime(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? " year" : " years").append(day == 0 ? "" : ",");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? " day" : " days").append(hours == 0 ? "" : ",");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? " hour" : " hours").append(minutes == 0 ? "" : ",");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? " minute" : " minutes").append(seconds == 0 ? "" : ",");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }

        return time.toString().trim();
    }

    public static String getMoreDetailedTime(long millis, boolean milis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        if (!milis) {
            seconds = seconds % 60;
        }
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? " year" : " years").append(day == 0 ? "" : ",");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? " day" : " days").append(hours == 0 ? "" : ",");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? " hour" : " hours").append(minutes == 0 ? "" : ",");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? " minute" : " minutes").append(seconds == 0 ? "" : ",");
        }

        if (!milis) {
            time.append(seconds).append(seconds == 1 ? " second" : " seconds");
        } else {
            time.append(DECIMAL_FORMAT.format(millis / 1000D)).append(seconds <= 1 ? " second" : " seconds");
        }

        return time.toString().trim();
    }

    public static String formatTime(int timer) {
        int hours = timer / 3600;
        int secondsLeft = timer - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";

        if (hours > 0) {
            if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";
        }

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
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
                case "mo": {
                    totalTime += value * 60L * 60L * 24L * 30L;
                    found = true;
                    continue;
                }
                case "y": {
                    totalTime += value * 60L * 60L * 24L * 365L;
                    found = true;
                }
            }
        }

        return found ? (totalTime * 1000L) + 1000L : -1;
    }
}