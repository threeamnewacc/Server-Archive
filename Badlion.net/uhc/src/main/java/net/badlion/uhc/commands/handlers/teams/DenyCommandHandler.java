package net.badlion.uhc.commands.handlers.teams;

import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.MessageHelperUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DenyCommandHandler {

    public static void handleDenyCommand(Player player, String[] args) {
        // Don't check to see if they're already in a team, InviteCommand handles that
        UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
        if (uhcp.getTeamRequest() != null) {
	        // Message rest of team members
            for (UUID uuid : uhcp.getTeamRequest().getUuids()) {
                MessageHelperUtil.messagePlayerIfOnline(uuid, ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has denied the team invitation!");
            }

            uhcp.setTeamRequest(null);
            player.sendMessage(ChatColor.AQUA + "You have denied the team invitation!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a pending team invitation!");
        }
    }

}
