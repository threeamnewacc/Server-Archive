package net.badlion.common.libraries;

import java.util.UUID;

public class StringCommon {

    public static String cleanEnum(String e) {
        String[] pieces = e.split("_");
        StringBuilder builder = new StringBuilder();
        for (String p : pieces) {
            String lower = p.toLowerCase();
            builder.append(lower.substring(0, 1).toUpperCase());
            builder.append(lower.substring(1).toLowerCase());

            builder.append(" ");
        }

        builder.setLength(builder.length() - 1);

        return builder.toString();
    }

    public static UUID uuidFromStringWithoutDashes(String digits) {
        String uuid = digits.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
        return UUID.fromString(uuid);
    }

    public static String niceTime(int seconds) {
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return niceTime(hours, minutes, seconds);
    }

    public static String niceTime(int seconds, boolean showEmptyHours) {
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return niceTime(hours, minutes, seconds, showEmptyHours);
    }

    public static String niceTime(int hours, int minutes, int seconds) {
        return niceTime(hours, minutes, seconds, true);
    }

    public static String niceTime(int hours, int minutes, int seconds, boolean showEmptyHours) {
        StringBuilder builder = new StringBuilder();

        // Skip hours
        if (hours > 0) {
            if (hours < 10) {
                builder.append('0');
            }
            builder.append(hours);
            builder.append(':');
        } else if (showEmptyHours) {
            builder.append("00:");
        }

        if (minutes < 10 && hours != -1) {
            builder.append('0');
        }
        builder.append(minutes);
        builder.append(':');

        if (seconds < 10) {
            builder.append('0');
        }
        builder.append(seconds);

        return builder.toString();
    }

	public static String niceUpperCase(String s) {
		String newString = "";
		int i2 = 0;
		for (char c : s.replace('_', ' ').toCharArray()) {
			if (i2 == 0) {
				newString += Character.toUpperCase(c);
			} else {
				newString += c;
			}
			if (c == ' ')
				i2 = -1;
			i2++;
		}
		return newString;
	}
}
