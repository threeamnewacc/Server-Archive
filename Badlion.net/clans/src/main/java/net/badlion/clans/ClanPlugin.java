package net.badlion.clans;

import org.bukkit.plugin.java.JavaPlugin;

public class ClanPlugin extends JavaPlugin {

    private static ClanPlugin plugin;

    public static ClanPlugin getInstance() {
        return ClanPlugin.plugin;
    }

    public ClanPlugin() {
        ClanPlugin.plugin = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
