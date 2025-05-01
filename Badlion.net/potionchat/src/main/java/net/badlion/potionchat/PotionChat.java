package net.badlion.potionchat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class PotionChat extends JavaPlugin {

    private static PotionChat plugin;

    @Override
    public void onEnable() {
        PotionChat.plugin = this;

	    // Create command executors
	    this.getCommand("pc").setExecutor(new AliasCommand());

	    // Set custom channel handler
	    SmellyChat.getInstance().setChannelHandler(new PotionChannelHandler());

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }

    @Override
    public void onDisable() {
    }

	public static PotionChat getInstance() {
		return PotionChat.plugin;
	}

}
