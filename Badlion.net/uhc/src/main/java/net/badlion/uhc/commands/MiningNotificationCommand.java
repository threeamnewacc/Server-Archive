package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

public class MiningNotificationCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Generate the message
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.LIGHT_PURPLE);
        sb.append(ChatColor.BOLD);

        boolean flag = false;
        for (String arg : args) {
            if (flag) {
                sb.append(" ");
            }

            flag = true;
            sb.append(arg);
        }

        String message = sb.toString();

        Queue<UHCPlayer> moderators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD);
        for (UHCPlayer uhcPlayer : moderators) {
            Player pl = uhcPlayer.getPlayer();
            if (pl != null) {
                pl.sendMessage(message);
            }
        }

        UHCPlayer host = BadlionUHC.getInstance().getHost();
        if (host != null) {
            Player pl = host.getPlayer();
            if (pl != null) {
                pl.sendMessage(message);
            }
        }

        return true;
    }
}
