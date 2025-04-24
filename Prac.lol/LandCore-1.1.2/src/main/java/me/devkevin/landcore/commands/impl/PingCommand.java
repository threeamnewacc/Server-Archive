package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCoreAPI;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand extends PlayerCommand {
    public PingCommand() {
        super("ping");
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = args.length < 1 || Bukkit.getPlayer(args[0]) == null ? player : Bukkit.getPlayer(args[0]);
        int targetPing = target.spigot().getPing();

        if (target == player) {
            player.sendMessage(CC.PRIMARY + "Your ping is " + CC.SECONDARY + LandCoreAPI.getColorPing(targetPing) + CC.PRIMARY + " ms.");
        } else {
            int difference = targetPing - player.spigot().getPing();
            String name = target.getDisplayName();

            player.sendMessage(name + CC.PRIMARY + "'s ping is " + CC.SECONDARY + targetPing + CC.PRIMARY + " ms "
                    + CC.ACCENT + "(" + (difference > 0 ? "+" : "") + LandCoreAPI.getColorPing(difference) + " difference)" + CC.PRIMARY + ".");
        }
    }
}
