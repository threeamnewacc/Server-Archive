package us.zonix.core.util;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

	public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
		boolean future = false;

		if (toDate.equals(fromDate)) {
			return "now";
		}
		else {
			if (toDate.after(fromDate)) {
				future = true;
			}

			StringBuilder sb = new StringBuilder();
			int[] types = new int[]{1, 2, 5, 11, 12, 13};
			String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
			int accuracy = 0;

			for (int i = 0; i < types.length && accuracy <= 2; ++i) {
				int diff = dateDiff(types[i], fromDate, toDate, future);

				if (diff > 0) {
					++accuracy;
					sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
				}
			}

			return sb.length() == 0?"now":sb.toString().trim();
		}
	}

	private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
		int diff = 0;

		long savedDate;

		for(savedDate = fromDate.getTimeInMillis(); future && !fromDate.after(toDate) || !future && !fromDate.before(toDate); ++diff) {
			savedDate = fromDate.getTimeInMillis();
			fromDate.add(type, future?1:-1);
		}

		--diff;
		fromDate.setTimeInMillis(savedDate);
		return diff;
	}

	public static Long parseTime(String time) {
		if (time.equalsIgnoreCase("permanent") || time.equalsIgnoreCase("perm")) {
			return (long) -1;
		}

		long totalTime = 0L;
		boolean found = false;
		Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

		while (matcher.find()) {
			String s = matcher.group();
			Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
			String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

			switch (type) {
				case "s":
					totalTime += value;
					found = true;
					break;
				case "m":
					totalTime += value * 60;
					found = true;
					break;
				case "h":
					totalTime += value * 60 * 60;
					found = true;
					break;
				case "d":
					totalTime += value * 60 * 60 * 24;
					found = true;
					break;
				case "w":
					totalTime += value * 60 * 60 * 24 * 7;
					found = true;
					break;
				case "M":
					totalTime += value * 60 * 60 * 24 * 30;
					found = true;
					break;
				case "y":
					totalTime += value * 60 * 60 * 24 * 365;
					found = true;
					break;
			}
		}

		return !found ? null : totalTime * 1000;
	}

}
