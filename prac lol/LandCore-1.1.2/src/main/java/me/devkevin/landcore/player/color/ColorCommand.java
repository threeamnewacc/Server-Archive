package me.devkevin.landcore.player.color;

import com.google.common.collect.ImmutableMap;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 06/02/2023 @ 4:28
 * ColorCommand / me.devkevin.landcore.player.color / LandCore
 */
public class ColorCommand extends PlayerCommand {
    private final LandCore plugin;

    private static final Map<String, String> COLORS = new ImmutableMap.Builder<String, String>()
            .put("purple", CC.PURPLE)
            .put("dark_aqua", CC.D_AQUA)
            .put("light_gray", CC.GRAY)
            .put("gray", CC.D_GRAY)
            .put("light_purple", CC.PINK)
            .put("green", CC.GREEN)
            .put("aqua", CC.AQUA)
            .put("gold", CC.GOLD)
            .put("red", CC.RED)
            .put("yellow", CC.YELLOW)
            .put("dark_green", CC.DARK_GREEN)
            .put("white", CC.WHITE)
            .put("black", CC.BLACK)
            .put("dark_red", CC.D_RED)
            .build();

    public ColorCommand(LandCore plugin) {
        super("setcolor");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = this.plugin.getProfileManager().getProfile(player.getUniqueId());

        String customColor = COLORS.get(args[0].toLowerCase());
        if (customColor == null && !args[0].equalsIgnoreCase("default")) {
            player.sendMessage(CC.RED + "Invalid color.");
            player.sendMessage(CC.RED + "Valid colors are: "
                    + COLORS.keySet().toString().replace("[", "").replace("]", ""));
            return;
        }

        profile.setCustomColor(customColor);
        player.sendMessage(CC.GREEN + "You changed your chat color to " + args[0] + ".");

        if (!args[0].equalsIgnoreCase("default")) {
            player.sendMessage(CC.GREEN + "Do '/color' to go back to default.");
        }
    }
}
