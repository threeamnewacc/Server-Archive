package net.badlion.arenapvpchat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaPvPChat extends JavaPlugin {

    private static ArenaPvPChat plugin;

    @Override
    public void onEnable() {
        ArenaPvPChat.plugin = this;

	    // Set custom channel handler
	    SmellyChat.getInstance().setChannelHandler(new ArenaPvPChannelHandler());
    }

    @Override
    public void onDisable() {
    }

	public static ArenaPvPChat getInstance() {
		return ArenaPvPChat.plugin;
	}

}
