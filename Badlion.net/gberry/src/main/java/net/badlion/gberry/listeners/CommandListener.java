package net.badlion.gberry.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.HelpCommandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPostprocessEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class CommandListener implements Listener {

	@EventHandler(priority = EventPriority.LAST)
	public void playerCommandWhileDeadEvent(PlayerCommandPreprocessEvent event) {
		// Fix Bukkit race condition
		if (event.getPlayer().isDead()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage();
		if (!event.getPlayer().isOp() && (msg.equalsIgnoreCase("/me") || msg.toLowerCase().startsWith("/me ")
				|| msg.equalsIgnoreCase("/buy") || msg.toLowerCase().startsWith("/buy ")
				|| msg.equalsIgnoreCase("/reload") || msg.equalsIgnoreCase("/restart")
				|| msg.toLowerCase().equalsIgnoreCase("/version")
				|| msg.toLowerCase().startsWith("/bukkit:") || msg.equalsIgnoreCase("/bukkit")
				|| msg.toLowerCase().startsWith("/minecraft:") || msg.equalsIgnoreCase("/minecraft")
				|| msg.equalsIgnoreCase("/version") || msg.startsWith("/bukkit:ver"))
				|| msg.equalsIgnoreCase("/kill") || msg.toLowerCase().startsWith("/kill ")) {
			event.getPlayer().sendMessage("Unknown command. Type \"/help\" for help.");
			event.setCancelled(true);
		} else if ((msg.toLowerCase().startsWith("/help") && !msg.toLowerCase().startsWith("/helpop"))
				|| msg.toLowerCase().startsWith("/?")) {
			event.setCancelled(true);

			// Split arguments up
			String[] args = event.getMessage().split(" ");
			if (args.length >= 2) {
				args = Arrays.copyOfRange(args, 1, args.length);
			} else {
				args = new String[0];
			}

			// Fire off help cmd
			Gberry.plugin.getServer().getPluginManager().callEvent(new HelpCommandEvent(event.getPlayer(), args));
		} else if (event.getMessage().equalsIgnoreCase("/list")) {
			event.setCancelled(true);

			// Update the strings for /list
			Gberry.plugin.getListCommandHandler().updateListCommandStrings();

			// Send messages
			Gberry.plugin.getListCommandHandler().handleListCommand(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onPlayerCommandPostprocessEvent(final PlayerCommandPostprocessEvent event) {
		String command = event.getMessage().split(" ")[0].toLowerCase();
		if (command.equals("ad") || command.equals("mc") || command.equals("report") || command.equals("msg")) return;

		Gberry.recordCommandUsage(event.getPlayer(), event.getMessage());
	}

}
