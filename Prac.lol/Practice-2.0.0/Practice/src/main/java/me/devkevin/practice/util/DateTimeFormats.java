package me.devkevin.practice.util;

import org.apache.commons.lang.time.FastDateFormat;

import java.time.*;
import java.text.*;
import java.util.*;

/**
 * Copyright 16/11/2019 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class DateTimeFormats
{
    public static final TimeZone SERVER_TIME_ZONE;
    public static final ZoneId SERVER_ZONE_ID;
    public static final FastDateFormat DAY_MTH_HR_MIN_SECS;
    public static final FastDateFormat DAY_MTH_YR_HR_MIN_AMPM;
    public static final FastDateFormat DAY_MTH_HR_MIN_AMPM;
    public static final FastDateFormat HR_MIN_AMPM;
    public static final FastDateFormat HR_MIN_AMPM_TIMEZONE;
    public static final FastDateFormat HR_MIN;
    public static final FastDateFormat MIN_SECS;
    public static final FastDateFormat KOTH_FORMAT;
    public static final FastDateFormat PALACE_FORMAT;
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS;
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING;

    static {
        SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
        SERVER_ZONE_ID = DateTimeFormats.SERVER_TIME_ZONE.toZoneId();
        DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance("MM/dd/yy hh:mma", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        HR_MIN = FastDateFormat.getInstance("hh:mm", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        MIN_SECS = FastDateFormat.getInstance("mm:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        KOTH_FORMAT = FastDateFormat.getInstance("m:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        PALACE_FORMAT = FastDateFormat.getInstance("m:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.ENGLISH);
        REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
            @Override
            protected DecimalFormat initialValue() {
                return new DecimalFormat("0.#");
            }
        };
        REMAINING_SECONDS_TRAILING = new ThreadLocal<DecimalFormat>() {
            @Override
            protected DecimalFormat initialValue() {
                return new DecimalFormat("0.0");
            }
        };
    }
}

