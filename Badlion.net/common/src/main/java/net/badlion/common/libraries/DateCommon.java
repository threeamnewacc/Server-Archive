package net.badlion.common.libraries;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateCommon {

    private static final List<String> daysList = new ArrayList<>();

    static {
        DateCommon.daysList.add("Monday");
        DateCommon.daysList.add("Tuesday");
        DateCommon.daysList.add("Wednesday");
        DateCommon.daysList.add("Thursday");
        DateCommon.daysList.add("Friday");
        DateCommon.daysList.add("Saturday");
        DateCommon.daysList.add("Sunday");
    }

    public static DateTime parseDayTime(String dt) { // Monday 16:23
        String[] dt2 = dt.split(" ");

        if (dt2.length != 2) return null;

        // Get day of week
        int day = -1;
        for (int x = 0; x < 7; x++) {
            String day2 = DateCommon.daysList.get(x);
            if (day2.equalsIgnoreCase(dt2[0])) {
                day = x + 1;
            }
        }
        if (day == -1) return null;

        // Get time of day
        LocalTime localTime;
        try {
            DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm");
            localTime = format.parseLocalTime(dt2[1]);
        } catch (IllegalArgumentException e) {
            return null;
        }

        DateTime dateTime = DateTime.now();

		// Add a week if we are past this day already
        if (dateTime.getDayOfWeek() > day) {
            dateTime = dateTime.plusWeeks(1);
        } else if (dateTime.getMillisOfDay() > localTime.getMillisOfDay()) {
			// We are past the millis of the day from the parse time, add a week
			dateTime = dateTime.plusWeeks(1);
		}

        dateTime = dateTime.withDayOfWeek(day);
        dateTime = dateTime.withHourOfDay(localTime.getHourOfDay());
        dateTime = dateTime.withMinuteOfHour(localTime.getMinuteOfHour());
        dateTime = dateTime.withSecondOfMinute(0);
        dateTime = dateTime.withMillisOfSecond(0);
        return dateTime;
    }

	@Deprecated
    public static DateTime parseDateTime(String dt) { // 4-23-2014 16:23
        try {
            DateTimeFormatter format = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm");
            DateTime dateTime = format.parseDateTime(dt);
            return dateTime;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

	@Deprecated
	public static DateTime getTimeZoneConversion(String timeZone, DateTime dateTime) {
		SimpleDateFormat formatTimeZone = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		formatTimeZone.setTimeZone(TimeZone.getTimeZone(timeZone));

		Date date = null;
		try {
			date = formatTimeZone.parse(dateTime.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new DateTime(date);
	}
}
