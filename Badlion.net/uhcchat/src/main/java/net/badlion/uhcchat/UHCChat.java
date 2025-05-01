package net.badlion.uhcchat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class UHCChat extends JavaPlugin {

    private static UHCChat plugin;

    @Override
    public void onEnable() {
        UHCChat.plugin = this;

	    // Set custom channel handler
	    SmellyChat.getInstance().setChannelHandler(new UHCChannelHandler());

	    // Register command executers
        this.getCommand("sp").setExecutor(new SpectatorChatCommand());
        this.getCommand("tc").setExecutor(new AliasCommand());
	    this.getCommand("uhcmc").setExecutor(new UHCModChatCommand());

	    // Register listeners
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }

    @Override
    public void onDisable() {
    }

	public static UHCChat getInstance() {
		return UHCChat.plugin;
	}

}
