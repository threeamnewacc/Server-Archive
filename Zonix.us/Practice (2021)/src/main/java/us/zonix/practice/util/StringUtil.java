package us.zonix.practice.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public final class StringUtil {
    public static final String NO_PERMISSION = ChatColor.RED + "You don't enough permissions.";
    public static final String PLAYER_ONLY = ChatColor.RED + "Only players can use this command.";
    public static final String PLAYER_NOT_FOUND = ChatColor.RED + "%s not found.";
    public static final String LOAD_ERROR = ChatColor.RED + "An error occured, please contact an administrator.";
    public static final String SPLIT_PATTERN = Pattern.compile("\\s").pattern();
    private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";
    private static final List<String> VOWELS = Arrays.asList("a", "e", "u", "i", "o");

    private StringUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static String toNiceString(String string) {
        string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.toCharArray().length; i++) {
            char c = string.toCharArray()[i];
            if (i > 0) {
                char prev = string.toCharArray()[i - 1];
                if ((prev == ' ' || prev == '[' || prev == '(')
                    && (i == string.toCharArray().length - 1 || c != 'x' || !Character.isDigit(string.toCharArray()[i + 1]))) {
                    c = Character.toUpperCase(c);
                }
            } else if (c != 'x' || !Character.isDigit(string.toCharArray()[i + 1])) {
                c = Character.toUpperCase(c);
            }

            sb.append(c);
        }

        return sb.toString();
    }

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }

    public static String getFirstSplit(String s) {
        return s.split(SPLIT_PATTERN)[0];
    }

    public static String getAOrAn(String input) {
        return VOWELS.contains(input.substring(0, 1).toLowerCase()) ? "an" : "a";
    }
}
