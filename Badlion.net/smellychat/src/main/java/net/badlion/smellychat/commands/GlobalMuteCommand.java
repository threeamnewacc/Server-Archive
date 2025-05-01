package net.badlion.smellychat.commands;

import net.badlion.banmanager.BanManager;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.events.GlobalMuteEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class GlobalMuteCommand implements CommandExecutor {

	private boolean recentlyToggled = false;

	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (BanManager.isLobby && !sender.hasPermission("badlion.manager")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to global mute in the lobby.");
			return true;
		}

		GlobalMuteEvent globalMuteEvent = new GlobalMuteEvent();
		SmellyChat.getInstance().getServer().getPluginManager().callEvent(globalMuteEvent);

		if (globalMuteEvent.isCancelled()) {
			sender.sendMessage(ChatColor.RED + "Global mute cancelled");
			return true;
		}

		if (this.recentlyToggled) {
			sender.sendMessage(ChatColor.RED + "Global mute was recently toggled, please try again in a few seconds.");
			return true;
		}

		if (!SmellyChat.GLOBAL_MUTE) {
			Gberry.broadcastMessage(ChatColor.AQUA + "Global mute is now enabled!");
		} else {
			Gberry.broadcastMessage(ChatColor.AQUA + "Global mute is now disabled!");
		}

		SmellyChat.GLOBAL_MUTE = !SmellyChat.GLOBAL_MUTE;

		// GSync for lobbies
		if (BanManager.isLobby) {
			// Post this message to the REST API
			new BukkitRunnable() {
				@Override
				public void run() {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("global_mute", SmellyChat.GLOBAL_MUTE);

					try {
						HTTPCommon.executePOSTRequest("http://" + GetCommon.getIpForDB() + ":10123/ChatSync/" + SmellyChat.getInstance().getServerUUID().toString() + "/uTHbNCWEMhCSRagbgF724UMX2kQUJGAM", jsonObject);
					} catch (HTTPRequestFailException e) {
						SmellyChat.getInstance().getServer().getLogger().info("Failed to sync lobby chat message with error " + e.getResponseCode());
					}
				}
			}.runTaskAsynchronously(SmellyChat.getInstance());
		}

		this.recentlyToggled = true;

		// Untoggle boolean in 3 seconds
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				GlobalMuteCommand.this.recentlyToggled = false;
			}
		}, 60L);
		return true;
	}

}
