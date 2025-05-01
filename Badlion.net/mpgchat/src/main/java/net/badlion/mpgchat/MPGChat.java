package net.badlion.mpgchat;

import net.badlion.mpgchat.commands.AliasCommand;
import net.badlion.mpgchat.commands.SpectatorChatCommand;
import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class MPGChat extends JavaPlugin {

	private static MPGChat plugin;

	@Override
	public void onEnable() {
		MPGChat.plugin = this;

		// Set custom channel handler
		SmellyChat.getInstance().setChannelHandler(new MPGChannelHandler());

		// Register command executers
		this.getCommand("sp").setExecutor(new SpectatorChatCommand());
		this.getCommand("tc").setExecutor(new AliasCommand());

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
	}

	@Override
	public void onDisable() {
	}

	public static MPGChat getInstance() {
		return MPGChat.plugin;
	}

}
