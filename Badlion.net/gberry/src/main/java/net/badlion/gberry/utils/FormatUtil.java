package net.badlion.gberry.utils;

import java.util.regex.Pattern;

/**
 * Ripped from Essentials to save time
 */
public class FormatUtil {

	public static final transient Pattern VANILLA_PATTERN = Pattern.compile("§+[0-9A-FK-ORa-fk-or]?");
	public static final transient Pattern VANILLA_COLOR_PATTERN = Pattern.compile("§+[0-9A-Fa-f]");
	public static final transient Pattern VANILLA_MAGIC_PATTERN = Pattern.compile("§+[Kk]");
	public static final transient Pattern VANILLA_FORMAT_PATTERN = Pattern.compile("§+[L-ORl-or]");
	public static final transient Pattern REPLACE_ALL_PATTERN = Pattern.compile("(?<!&)&([0-9a-fk-orA-FK-OR])");
	public static final transient Pattern REPLACE_COLOR_PATTERN = Pattern.compile("(?<!&)&([0-9a-fA-F])");
	public static final transient Pattern REPLACE_MAGIC_PATTERN = Pattern.compile("(?<!&)&([Kk])");
	public static final transient Pattern REPLACE_FORMAT_PATTERN = Pattern.compile("(?<!&)&([l-orL-OR])");
	public static final transient Pattern REPLACE_PATTERN = Pattern.compile("&&(?=[0-9a-fk-orA-FK-OR])");
	public static final transient Pattern LOGCOLOR_PATTERN = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]");
	public static final transient Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
	public static final Pattern IPPATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static String replaceColor(String input, Pattern pattern)
	{
		return REPLACE_PATTERN.matcher(pattern.matcher(input).replaceAll("§$1")).replaceAll("&");
	}

	public static String stripColor(String input, Pattern pattern)
	{
		return pattern.matcher(input).replaceAll("");
	}
}
