package net.badlion.practicechat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class PracticeChat extends JavaPlugin {

    private static PracticeChat plugin;

    @Override
    public void onEnable() {
        PracticeChat.plugin = this;

	    // Create command executors
	    this.getCommand("pc").setExecutor(new AliasCommand());

	    // Set custom channel handler
	    SmellyChat.getInstance().setChannelHandler(new PracticeChannelHandler());

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }

    @Override
    public void onDisable() {
    }

	public static PracticeChat getInstance() {
		return PracticeChat.plugin;
	}

}
