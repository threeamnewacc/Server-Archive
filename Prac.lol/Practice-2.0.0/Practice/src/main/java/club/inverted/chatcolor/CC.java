package club.inverted.chatcolor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Copyright 05/08/2019 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public final class CC {

    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String OBFUSCATED = MAGIC;
    public static final String B = BOLD;
    public static final String M = MAGIC;
    public static final String O = MAGIC;
    public static final String I = ITALIC;
    public static final String S = STRIKETHROUGH;
    public static final String R = RESET;
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String D_BLUE = DARK_BLUE;
    public static final String D_AQUA = DARK_AQUA;
    public static final String D_GRAY = DARK_GRAY;
    public static final String D_GREEN = DARK_GREEN;
    public static final String D_PURPLE = DARK_PURPLE;
    public static final String D_RED = DARK_RED;
    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static final String L_PURPLE = LIGHT_PURPLE;
    public static final String PINK = L_PURPLE;
    public static final String SCOREBAORD_SEPARATOR = CC.GRAY + CC.S + "----------------------";
    public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
    public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
    public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "----------------------";

    public static short translateToWool(ChatColor color) {
        switch (color) {
            case WHITE:
                return 0;
            case GOLD:
                return 1;
            case AQUA:
                return 3;
            case YELLOW:
                return 4;
            case GREEN:
                return 5;
            case LIGHT_PURPLE:
                return 6;
            case DARK_GRAY:
                return 7;
            case GRAY:
                return 8;
            case DARK_AQUA:
                return 9;
            case DARK_PURPLE:
                return 10;
            case DARK_BLUE:
            case BLUE:
                return 11;
            case DARK_GREEN:
                return 13;
            case DARK_RED:
            case RED:
                return 14;
            default:
                return 0;
        }
    }

    /**
     * Returns a coloured version of a string.
     *
     * @param string the string to be coloured.
     * @return coloured string.
     */
    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Returns a coloured version of a list.
     *
     * @param iterable the list to be coloured.
     * @return coloured list.
     */
    public static List<String> translate(Iterable<? extends String> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull).map(CC::translate).collect(Collectors.toList());
    }

    /**
     * Returns a coloured version of a list.
     *
     * @param list the list to be coloured.
     * @return coloured list.
     * */
    public static List<String> translate(List<String> list) {
        List<String> buffered = new ArrayList<String>();
        for (String string : list) buffered.add(translate(string));
        return buffered;
    }

    /**
     * Returns a coloured version of string list.
     *
     * @param strings the strings list to be coloured.
     * @return coloured strings list.
     * */
    public static String[] translate(String... strings) {
        return translate(Arrays.asList(strings)).stream().toArray(String[]::new);
    }

    /**
     * Convert strings to colors that are sent to the {@link Player}.
     *
     * @param receiver who is receiving the message.
     * @return strings what you're sending.
     * */
    public static void sendMessage(Player receiver, String... strings) {
        receiver.sendMessage(translate(strings));
    }

    /**
     * Convert strings to colors that are sent to the {@link CommandSender}.
     *
     * @param commandSender who is receiving the message.
     * @return strings what you're sending.
     * */
    public static void sendMessage(CommandSender commandSender, String... strings) {
        commandSender.sendMessage(translate(strings));
    }

    /**
     * Convert strings to colors that are sent to the {@link CommandSender}.
     *
     * @param commandSender who is receiving the message.
     * @return component text what you're sending.
     * */
    public static void sendMessage(CommandSender commandSender, BaseComponent[] value) {
        if(commandSender instanceof Player) {
            ((Player) commandSender).spigot().sendMessage(value);
        } else {
            commandSender.sendMessage(TextComponent.toLegacyText(value));
        }
    }

    /**
     * Convert strings to colors that are sent to the {@link ConsoleCommandSender}.
     *
     * @return strings what you're sending to the console.
     * */
    public static void logger(String... strings) {
        Bukkit.getConsoleSender().sendMessage(translate(strings));
    }
}
