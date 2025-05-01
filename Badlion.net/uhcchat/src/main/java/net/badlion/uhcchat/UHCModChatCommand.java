package net.badlion.uhcchat;

import net.badlion.smellychat.SmellyChat;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Queue;

public class UHCModChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            if (uhcPlayer.getState() != UHCPlayer.State.MOD && uhcPlayer.getState() != UHCPlayer.State.HOST) {
                sender.sendMessage("You do not have permission to use this command");
                return true;
            }
        }

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /uhcmc <message>");
			return true;
		}

		// Generate the message
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(" ");
			sb.append(arg);
		}

		String message = sb.toString();

		Queue<UHCPlayer> moderators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD);
        for (UHCPlayer uhcPlayer : moderators) {
            Player pl = uhcPlayer.getPlayer();
            if (pl != null) {
				pl.sendMessage(ChatColor.AQUA + "[UHC-MC] " + sender.getName() + ":" + message);
			}
		}

        UHCPlayer host = BadlionUHC.getInstance().getHost();
        if (host != null) {
            Player pl = BadlionUHC.getInstance().getServer().getPlayer(host.getUUID());
            if (pl != null) {
                pl.sendMessage(ChatColor.AQUA + "[UHC-MC] " + sender.getName() + ":" + message);
            }
        }

		// Log
		if (sender instanceof Player) {
			SmellyChat.getInstance().logMessage("UHC-MC", (Player) sender, message);
		}
		return true;
	}

}
