package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MCPCommand implements CommandExecutor {

	public static Map<UUID, Long> cooldowns = new HashMap<>();

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, String s, final String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		if (command.getName().equalsIgnoreCase("sp")
				|| command.getName().equalsIgnoreCase("spectate")
				|| command.getName().equalsIgnoreCase("spec")
				|| command.getName().equalsIgnoreCase("unfollow")
				|| command.getName().equalsIgnoreCase("follow")
				|| command.getName().equalsIgnoreCase("party")
				|| command.getName().equalsIgnoreCase("mvote")) {
			final Player player = (Player) sender;
			if (cooldowns.containsKey(player.getUniqueId())) {
				Long time = cooldowns.get(player.getUniqueId());
				if ((System.currentTimeMillis() - 1000) < time) {
					player.sendFormattedMessage("{0}Do not spam this command.", ChatColor.RED);
					return false;
				}
			}
			cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

			new BukkitRunnable() {
				@Override
				public void run() {
					List<String> argsList = new ArrayList<>();
					for (String string : args) {
						argsList.add(string);
					}
					JSONObject data = new JSONObject();
					data.put("uuid", player.getUniqueId().toString());
					data.put("username", player.getName());
					data.put("command", command.getName());
					data.put("args", argsList);
					try {
						JSONObject response = Gberry.contactMCP("command", data);
						ArenaLobby.getInstance().getLogger().log(Level.INFO, "[sending command]: " + data);
						ArenaLobby.getInstance().getLogger().log(Level.INFO, "Getting command response " + response);
					} catch (HTTPRequestFailException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(ArenaLobby.getInstance());
		}
		return false;
	}
}
