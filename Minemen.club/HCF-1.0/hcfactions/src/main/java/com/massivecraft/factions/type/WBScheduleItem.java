package com.massivecraft.factions.type;

import lombok.Getter;
import lombok.ToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Getter
@ToString(exclude = "calendar")
public class WBScheduleItem {

	private static final transient SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	private transient Calendar calendar;
	private Day day;
	private String time;
	private int distance;

	public WBScheduleItem(Day day, String time, int distance) {
		this.day = day;
		this.time = time;
		this.distance = distance;
	}


	private Calendar getCalendar() throws ParseException {
		if (calendar == null) {
			calendar = Calendar.getInstance();
			calendar.setTime(dateFormat.parse(time));
			calendar.set(Calendar.DAY_OF_WEEK, day.index());
		}
		return calendar;
	}

	public boolean matches(Calendar o) throws ParseException {
		calendar = getCalendar();
		return calendar.get(Calendar.MINUTE) == o.get(Calendar.MINUTE) &&
				calendar.get(Calendar.HOUR_OF_DAY) == o.get(Calendar.HOUR_OF_DAY) &&
				calendar.get(Calendar.DAY_OF_WEEK) == o.get(Calendar.DAY_OF_WEEK);
	}

	public long timeUntil(Calendar from) throws ParseException {
		Calendar calendar = (Calendar) getCalendar().clone();
		calendar.set(Calendar.DATE, from.get(Calendar.DATE));
		calendar.set(Calendar.MONTH, from.get(Calendar.MONTH));
		calendar.set(Calendar.YEAR, from.get(Calendar.YEAR));
		while (calendar.get(Calendar.DAY_OF_WEEK) != day.index() || calendar.before(from)) {
			calendar.add(Calendar.DATE, 1);
		}
		return calendar.getTimeInMillis() - from.getTimeInMillis();
	}

	public enum Day {
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

		public int index() {
			return ordinal() + 1;
		}
	}
}
