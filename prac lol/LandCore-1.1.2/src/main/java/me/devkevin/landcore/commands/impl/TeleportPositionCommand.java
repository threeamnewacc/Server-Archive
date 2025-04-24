package me.devkevin.landcore.commands.impl;

import com.google.common.primitives.Ints;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 07/02/2023 @ 18:05
 * TeleportPositionCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class TeleportPositionCommand extends PlayerCommand {
    private final LandCore plugin;

    public TeleportPositionCommand(LandCore plugin) {
        super("tppos", Rank.DEVELOPER);
        this.plugin = plugin;
        setAliases("teleportposition");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(CC.RED + "Usage: /tppos <x> <y> <z>");
            return;
        }

        Integer x = Ints.tryParse(args[0]);
        Integer y = Ints.tryParse(args[1]);
        Integer z = Ints.tryParse(args[2]);

        if (x == null || y == null || z == null) {
            player.sendMessage(CC.RED + "Location not found.");
            return;
        }

        player.sendMessage(CC.GRAY + "Teleporting...");
        player.teleport(new Location(player.getWorld(), (double) x, (double) y, (double) z));
    }
}
