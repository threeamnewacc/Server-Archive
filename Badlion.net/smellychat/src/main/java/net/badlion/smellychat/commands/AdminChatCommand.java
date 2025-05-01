package net.badlion.smellychat.commands;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AdminChatCommand implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return false;
		}

		if (args.length >= 1 && args[0].equalsIgnoreCase("off")) {
			SmellyChat.getInstance().getAdmins().remove((Player) sender);
			return true;
		} else if (args.length >= 1 && args[0].equalsIgnoreCase("on")) {
			SmellyChat.getInstance().getAdmins().add((Player) sender);
			return true;
		}

		if (sender instanceof Player) {
			// Did they just get admined and aren't in our admins list?
			SmellyChat.getInstance().getAdmins().add((Player) sender);
		}

		// Generate the message
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(" ");
			sb.append(arg);
		}

		String message = sb.toString();

		// Send to admins only
		for (Player search : SmellyChat.getInstance().getAdmins()) {
			search.sendMessage(ChatColor.DARK_RED + "[AC] " + sender.getName() + ":" + message);
		}

		// Log
		if (sender instanceof Player) {
			SmellyChat.getInstance().logMessage("AC", (Player) sender, message);
		}

		return true;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("badlion.admin")) {
			SmellyChat.getInstance().getAdmins().add(event.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		SmellyChat.getInstance().getAdmins().remove(event.getPlayer());
	}

}
