package net.badlion.factionchat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionChat extends JavaPlugin {

	private static FactionChat plugin;

	@Override
	public void onEnable() {
		FactionChat.plugin = this;

		// Create command executors
		this.getCommand("fc").setExecutor(new AliasCommand());

		// Set custom channel handler
		SmellyChat.getInstance().setChannelHandler(new FactionChannelHandler());

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
	}

	@Override
	public void onDisable() {
	}

	public static FactionChat getInstance() {
		return FactionChat.plugin;
	}

}
