package net.badlion.uhc.commands.handlers.teams;

import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.MessageHelperUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LeaveCommandHandler {

    public static void handleLeaveCommand(Player player, String[] args) {
        UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
        if (uhcp.getTeam().getSize() == 1) {
            player.sendMessage(ChatColor.RED + "You cannot leave the team when you are the only one left.");
        } else {
            UHCTeam team = uhcp.getTeam();
            team.removePlayer(player.getUniqueId());

            uhcp.setTeam(new UHCTeam(player.getUniqueId()));

            for (UUID uuid : team.getUuids()) {
                MessageHelperUtil.messagePlayerIfOnline(uuid, ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has left the team!");
            }

            player.sendMessage(ChatColor.YELLOW + "You have left the team!");
        }
    }

}
