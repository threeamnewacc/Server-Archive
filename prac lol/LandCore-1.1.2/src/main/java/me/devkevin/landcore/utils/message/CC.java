package me.devkevin.landcore.utils.message;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CC {
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String D_RED = ChatColor.DARK_RED.toString();
    public static final String D_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String D_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String D_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static final String PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String B = ChatColor.BOLD.toString();
    public static final String I = ChatColor.ITALIC.toString();
    public static final String U = ChatColor.UNDERLINE.toString();
    public static final String S = ChatColor.STRIKETHROUGH.toString();
    public static final String R = ChatColor.RESET.toString();
    public static final String PRIMARY = ChatColor.GOLD.toString();
    public static final String SECONDARY = ChatColor.YELLOW.toString();
    public static final String ACCENT = ChatColor.GOLD.toString();
    public static final String SPLITTER = "┃";
    public static final String BOARD_SEPARATOR = GRAY + S + "--------------------";
    public static final String SEPARATOR = GRAY + S + "-------------------------------------";
    public static char CIRCLE = '●';
    public static final String BD_RED = D_RED + B;
    public static final String COIN = CC.GOLD + " \u26C0";

    public static void logConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return toReturn;
    }
}
