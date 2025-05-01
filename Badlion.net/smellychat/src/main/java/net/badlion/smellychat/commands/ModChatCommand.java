package net.badlion.smellychat.commands;

import net.badlion.gberry.Gberry;
import net.badlion.smellychat.SmellyChat;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModChatCommand implements CommandExecutor, Listener {

	public static Set<UUID> modChatDisabled = new HashSet<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return false;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
			ModChatCommand.modChatDisabled.add(((Player) sender).getUniqueId());
			sender.sendMessage(ChatColor.GREEN + "You have disabled Mod Chat");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
			ModChatCommand.modChatDisabled.remove(((Player) sender).getUniqueId());
			sender.sendMessage(ChatColor.GREEN + "You have enabled Mod Chat");
			return true;
		}

		if (sender instanceof Player) {
			if (ModChatCommand.modChatDisabled.contains(((Player) sender).getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "You cannot talk in MC until you enable Mod Chat using /mc on");
				return true;
			}

			// Did they just get modded and aren't in our mods list?
			SmellyChat.getInstance().getMods().add((Player) sender);
		}

		// Generate the message
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(" ");
			sb.append(arg);
		}

		final String message = ChatColor.AQUA + "[MC] " + sender.getName() + " (" + Gberry.serverName.toUpperCase() + "):" + sb.toString();

		// Send to mods only
		if (sender instanceof Player) {
			for (Player mod : SmellyChat.getInstance().getMods()) {
				if (!ModChatCommand.modChatDisabled.contains(mod.getUniqueId())) {
					mod.sendMessage(message);
				}
			}

			// Send across network
			new BukkitRunnable() {
				public void run() {
					List<String> list = new ArrayList<>();
					list.add("ModChat");
					list.add(StringEscapeUtils.escapeJava(message));

					Gberry.sendGSyncEvent(list);
				}
			}.runTaskAsynchronously(SmellyChat.getInstance());


			// Log
			SmellyChat.getInstance().logMessage("MC", (Player) sender, message);
		} else {
			for (Player mod : SmellyChat.getInstance().getMods()) {
				mod.sendMessage(message);
			}
		}
		return true;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission("badlion.staff")) {
			SmellyChat.getInstance().getMods().add(event.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		SmellyChat.getInstance().getMods().remove(event.getPlayer());
	}

}
