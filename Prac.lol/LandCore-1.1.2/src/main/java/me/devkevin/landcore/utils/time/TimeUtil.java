package me.devkevin.landcore.utils.time;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TimeUtil {
    public static String millisToRoundedTime(long millis) {
        millis += 1L;

        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;

        if (years > 0) {
            return years + " year" + (years == 1 ? "" : "s");
        } else if (months > 0) {
            return months + " month" + (months == 1 ? "" : "s");
        } else if (weeks > 0) {
            return weeks + " week" + (weeks == 1 ? "" : "s");
        } else if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s");
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
    }

    public static String dateToString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.getTime().toString();
    }

    public static String dateToString(Date date, String secondaryColor) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new SimpleDateFormat("MMM dd yyyy " + (secondaryColor == null ? "" : secondaryColor) +
                "(hh:mm aa zz)").format(date);
    }

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

    public static String formatTimeMillis(long millis) {
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
            time.append(years).append(years == 1 ? " year " : " years ");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? " day " : " days ");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? " hour " : " hours ");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? " second " : " seconds ");
        }

        return time.toString().trim();
    }

    public static String formatTimeSeconds(long seconds) {
        return formatTimeMillis(seconds * 1000);
    }

    public static String formatTimeMillisToClock(long millis) {
        return millis / 1000L <= 0 ? "0:00" : String.format("%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String formatTimeSecondsToClock(long seconds) {
        return formatTimeMillisToClock(seconds * 1000);
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
