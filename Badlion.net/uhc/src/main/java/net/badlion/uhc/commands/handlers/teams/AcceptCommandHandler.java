package net.badlion.uhc.commands.handlers.teams;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import net.badlion.uhc.util.MessageHelperUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AcceptCommandHandler {

    public static void handleAcceptCommand(Player player, String[] args) {
        AcceptCommandHandler.handleAcceptCommand(player, args, true);
    }

    public static void handleAcceptCommand(Player player, String[] args, boolean verbose) {
        // Don't check to see if they're already in a team, InviteCommand handles that
        UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
        if (uhcp.getTeamRequest() != null) {
	        // Plus 1 to players size because of the leader
            if (uhcp.getTeamRequest().getSize() >= (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue()) {
                uhcp.setTeamRequest(null);
                player.sendMessage(ChatColor.RED + "That team is already full!");
                return;
            }

            UHCTeamManager.removeUHCTeam(uhcp.getTeam());
            uhcp.setTeam(uhcp.getTeamRequest());
	        uhcp.setTeamRequest(null);

            for (UUID uuid : uhcp.getTeam().getUuids()) {
                if (verbose) {
                    MessageHelperUtil.messagePlayerIfOnline(uuid, ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has joined the team!");
                }
            }

            uhcp.setSolo(false); // Always on a team now
            uhcp.getTeam().addPlayer(player.getUniqueId());
            if (verbose) {
                player.sendMessage(ChatColor.AQUA + "You have joined the team!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a pending team invitation!");
        }
    }

}
