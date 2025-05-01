package net.badlion.bungeeprivate;

import net.badlion.bungeeprivate.commands.TestCommand;
import net.badlion.bungeeprivate.listeners.ForgeModListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePrivatePlugin extends Plugin {

    private static BungeePrivatePlugin instance;
    public static BungeePrivatePlugin getInstance() {
        return BungeePrivatePlugin.instance;
    }

    public BungeePrivatePlugin() {
        BungeePrivatePlugin.instance = this;
    }

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ForgeModListener());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TestCommand());
    }

}
