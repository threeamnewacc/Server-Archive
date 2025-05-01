package net.badlion.uhc.commands.handlers;

import net.badlion.gpermissions.GPermissions;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetHostCommandHandler {

    public static void handleSetHostCommand(CommandSender sender, String[] args) {
        if (BadlionUHC.getInstance().isMiniUHC()) {
            sender.sendMessage(ChatColor.RED + "You cannot set yourself as host in MiniUHC");
            return;
        }

        if (args.length == 1) {
	        Player player = Bukkit.getPlayerExact(args[0]);

	        // Is the player online?
	        if (player == null) {
		        sender.sendMessage(ChatColor.RED + "Player not found!");
		        return;
	        }

	        // Perm check
	        if (!SetHostCommandHandler.canBeSetAsHost(sender, player.getUniqueId())) {
		        return;
	        }

	        // Always undisguise the player if they are disguised
	        if (player.isDisguised()) {
		        player.performCommand("ud");
	        }

            if (BadlionUHC.getInstance().getHost() == null) {
	            UHCPlayerManager.updateUHCPlayerState(player.getUniqueId(), UHCPlayer.State.HOST);
	            BadlionUHC.getInstance().addMuteBanPerms(player);
	            sender.sendMessage(ChatColor.YELLOW + "Set the host to " + player.getName());
            } else {
	            if (BadlionUHC.getInstance().getHost().getUUID().equals(player.getUniqueId())) {
		            sender.sendMessage(ChatColor.RED + "You are already the host!");
		            return;
	            }

	            BadlionUHC.getInstance().addMuteBanPerms(player);
	            sender.sendMessage(ChatColor.YELLOW + "Changed hosts from " + BadlionUHC.getInstance().getHost().getUsername() + " to " + player.getName());
	            UHCPlayerManager.updateUHCPlayerState(BadlionUHC.getInstance().getHost().getUUID(), UHCPlayer.State.MOD);
	            UHCPlayerManager.updateUHCPlayerState(player.getUniqueId(), UHCPlayer.State.HOST);
            }
        } else {
            sender.sendMessage("Usage: /uhc sethost <player>");
        }
    }

    private static boolean canBeSetAsHost(CommandSender sender, UUID newHostUUID) {
        if (!sender.isOp() && !GPermissions.plugin.userHasPermission(newHostUUID.toString(), "badlion.uhctrial")) {
            sender.sendMessage(ChatColor.RED + "This user cannot be set as host.");
            return false;
        }

        return true;
    }
}
