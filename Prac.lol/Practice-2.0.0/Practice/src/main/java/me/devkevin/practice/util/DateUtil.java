package me.devkevin.practice.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Copyright 06/06/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class DateUtil {

    static String stringDateFormat = "MMMM d, yyyy";
    private static DateFormat dateFormat = new SimpleDateFormat(stringDateFormat);

    public static String getFormattedDate(long value) {
        Date date = new Date(value);
        return dateFormat.format(date).replaceAll(",", "th,");
    }

    public static String getTodayDate() {
        Date todayDate = new Date();
        DateFormat todayDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        todayDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        String strTodayDate = todayDateFormat.format(todayDate);
        return strTodayDate;
    }

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("US/Eastern"));
        dateFormat.setTimeZone(cal.getTimeZone());
        String currentTime = dateFormat.format(cal.getTime());
        return currentTime;
    }
}
