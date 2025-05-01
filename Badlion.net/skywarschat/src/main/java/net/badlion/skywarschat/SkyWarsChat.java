package net.badlion.skywarschat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyWarsChat extends JavaPlugin {

	private static SkyWarsChat plugin;
	@Override
	public void onEnable() {
		SkyWarsChat.plugin = this;

		// Set custom channel handler
		SmellyChat.getInstance().setChannelHandler(new SkyWarsChannelHandler());

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
	}

	@Override
	public void onDisable() {

	}

	public static SkyWarsChat getInstance() {
		return SkyWarsChat.plugin;
	}

}
