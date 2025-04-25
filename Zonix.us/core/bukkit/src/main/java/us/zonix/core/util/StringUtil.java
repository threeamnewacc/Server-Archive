package us.zonix.core.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public final class StringUtil {

    public static final String NO_PERMISSION = CC.RED + "You don't have permission to use this command.";
    public static final String SLOW_CHAT = CC.RED + "Chat is currently slowed down. Please wait before another chat message.";
    public static final String CHAT_COOLDOWN = CC.RED + "You can't chat that fast.";
    public static final String PLAYER_ONLY = CC.RED + "Only players can use this command.";
    public static final String PLAYER_NOT_FOUND = CC.RED + "%s not found.";
    private static final FontRenderer FONT_RENDERER = new FontRenderer();
    private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";
    private static final List<String> VOWELS = Arrays.asList("a", "e", "u", "i", "o");

    private StringUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static String getBorderLine(String prefix) {
        int chatWidth = FONT_RENDERER.getWidth(MAX_LENGTH) / 10 * 9;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            sb.append("-");

            if (FONT_RENDERER.getWidth(sb.toString()) >= chatWidth) {
                break;
            }
        }

        return prefix + sb.toString();
    }

    public static String center(String string) {
        StringBuilder preColors = new StringBuilder();

        while (string.startsWith(ChatColor.COLOR_CHAR + "")) {
            preColors.append(string.substring(0, 2));
            string = string.substring(2, string.length());
        }

        int width = FONT_RENDERER.getWidth(string);
        int chatWidth = FONT_RENDERER.getWidth(MAX_LENGTH);

        if (width == chatWidth) {
            return string;
        } else if (width > chatWidth) {
            String[] words = string.split(" ");

            if (words.length == 1) {
                return string;
            }

            StringBuilder sb = new StringBuilder();

            int total = 0;

            for (String word : words) {
                int wordWidth = FONT_RENDERER.getWidth(word + " ");

                if (total + wordWidth > chatWidth) {
                    sb.append("\n");
                    total = 0;
                }

                total += wordWidth;
                sb.append(word).append(" ");
            }

            return center(preColors + sb.toString().trim());
        }

        StringBuilder sb = new StringBuilder();

        int diff = (chatWidth) - (width);
        diff /= 3;

        for (int i = 0; i < 100; i++) {
            sb.append(" ");

            if (FONT_RENDERER.getWidth(sb.toString()) >= diff) {
                break;
            }
        }

        sb.append(string);

        return preColors + sb.toString();
    }

    public static String buildMessage(String[] args, int start) {
        if (start >= args.length) {
            return "";
        }

        return ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }

    public static String getAOrAn(String input) {
        return ((VOWELS.contains(input.substring(0, 1).toLowerCase())) ? "an" : "a");
    }

    public static String formatScoreboardTitle(String sub) {
        return CC.DARK_RED + CC.BOLD + "Zonix" + CC.GRAY + " ┃ " + sub;
    }

}
