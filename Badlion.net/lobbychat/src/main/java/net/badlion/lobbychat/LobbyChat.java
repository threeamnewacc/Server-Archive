package net.badlion.lobbychat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyChat extends JavaPlugin {

	private static LobbyChat plugin;

	@Override
	public void onEnable() {
		LobbyChat.plugin = this;

		// Set custom channel handler
		SmellyChat.getInstance().setChannelHandler(new LobbyChannelHandler());

		// Start chat sync task
		new ChatSyncTask().runTaskTimerAsynchronously(this, 10, 10);
	}

	@Override
	public void onDisable() {

	}

	public static LobbyChat getInstance() {
		return LobbyChat.plugin;
	}

}
