package net.badlion.uhc.commands;

import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HelpOpCommand implements CommandExecutor {

	private HashMap<String, Long> playerToTimestampMap = new HashMap<>();

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			// Check they added a message
			if (args.length == 0) {
				return false;
			}

			Player player = (Player) sender;

			// Check for cooldown
			Long ts = this.playerToTimestampMap.get(player.getDisguisedName());
			if (ts == null || ts + 5000 < System.currentTimeMillis()) { // Think this'll throw an exception? Think again :P
				StringBuilder sb = new StringBuilder();
				for (String str : args) {
					sb.append(str);
					sb.append(" ");
				}

				// Grab prefixes
				String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
				prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
				String message;

				// Check for disguised player
				if (player.isDisguised()) {
					message = ChatColor.YELLOW + "[HelpOp] " + player.getDisguisedName() + "(" + prefix + ChatColor.YELLOW + player.getName() + "): " + sb.toString();
				} else {
					message = ChatColor.YELLOW + "[HelpOp] " + prefix + ChatColor.YELLOW + player.getName() + ChatColor.YELLOW + ": " + sb.toString();
				}

				// Send mods the message
				ConcurrentLinkedQueue<UHCPlayer> moderators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD);
				for (UHCPlayer mod : moderators) {
					Player pl = BadlionUHC.getInstance().getServer().getPlayer(mod.getUUID());
					if (pl != null) {
						pl.sendMessage(message);
					}
				}

				// Send the host the message
				UHCPlayer host = BadlionUHC.getInstance().getHost();
				if (host != null) {
					Player pl = BadlionUHC.getInstance().getServer().getPlayer(host.getUUID());
					if (pl != null) {
						pl.sendMessage(message);
					}
				}

				// Log it
				SmellyChat.getInstance().logMessage("HelpOp", (Player) sender, message);

				player.sendMessage(ChatColor.YELLOW + "Your message has been sent.");

				// Reset cooldown
				this.playerToTimestampMap.put(player.getDisguisedName(), System.currentTimeMillis());
			} else {
				// Reset cooldown
				this.playerToTimestampMap.put(player.getDisguisedName(), System.currentTimeMillis());

				player.sendMessage(ChatColor.RED + "You can only use \"/helpop\" once every 5 seconds. Cooldown reset.");
			}
		}
		return true;
	}

}