package me.devkevin.landcore.utils;

import com.google.common.base.Joiner;
import lombok.experimental.UtilityClass;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.ChatColor;
import org.omg.CORBA.NO_PERMISSION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtil {
    public static final Pattern URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
    public static final Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    public static String buildString(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    private final List COLORS;

    static {
        COLORS = new ArrayList(Arrays.asList(new ChatColor[]{ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.BLACK, ChatColor.DARK_GREEN, ChatColor.RED}));
    }

    public static int convertChatColorToWoolData(ChatColor color) {
        return color != ChatColor.DARK_RED && color != ChatColor.RED ? (color == ChatColor.DARK_GREEN ? 13 : (color == ChatColor.BLUE ? 11 : (color == ChatColor.DARK_PURPLE ? 10 : (color == ChatColor.DARK_AQUA ? 9 : (color == ChatColor.DARK_GRAY ? 7 : COLORS.indexOf(color)))))) : 14;
    }

    public static String joinListGrammaticallyWithGuava(final List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.size() > 1
                ? Joiner.on(", ").join(list.subList(0, list.size() - 1))
                .concat(String.format("%s and ", list.size() > 2 ? "," : ""))
                .concat(list.get(list.size() - 1))
                : list.get(0);
    }

    private static final ArrayList<ChatColor> woolColors = new ArrayList<>(Arrays.asList(
            ChatColor.WHITE,
            ChatColor.GOLD,
            ChatColor.LIGHT_PURPLE,
            ChatColor.AQUA,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.LIGHT_PURPLE,
            ChatColor.DARK_GRAY,
            ChatColor.GRAY,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_PURPLE,
            ChatColor.BLUE,
            ChatColor.BLACK,
            ChatColor.DARK_GREEN,
            ChatColor.RED,
            ChatColor.BLACK
    ));

    private static final ArrayList<String> woolCCs = new ArrayList<>(Arrays.asList(
            CC.WHITE,
            CC.GOLD,
            CC.PINK,
            CC.AQUA,
            CC.YELLOW,
            CC.GREEN,
            CC.D_GRAY,
            CC.GRAY,
            CC.D_AQUA,
            CC.PURPLE,
            CC.BLUE,
            CC.BLACK,
            CC.DARK_GREEN,
            CC.RED,
            CC.BLACK
    ));

    public static int convertCCToWoolData(String color) {
        if (Objects.equals(color, CC.D_RED)) {
            color = CC.RED;
        }

        return woolCCs.indexOf(color);
    }
}
