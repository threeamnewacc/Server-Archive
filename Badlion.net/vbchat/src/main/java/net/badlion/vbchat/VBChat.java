package net.badlion.vbchat;

import net.badlion.smellychat.SmellyChat;
import org.bukkit.plugin.java.JavaPlugin;

public class VBChat extends JavaPlugin {

    private static VBChat plugin;

    public static VBChat getInstance() {
        return VBChat.plugin;
    }

    @Override
    public void onEnable() {
        VBChat.plugin = this;

        // Create command executors
        this.getCommand("tc").setExecutor(new AliasCommand());

        // Set custom channel handler
        SmellyChat.getInstance().setChannelHandler(new VBChannelHandler());

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }

    @Override
    public void onDisable() {
    }

}
