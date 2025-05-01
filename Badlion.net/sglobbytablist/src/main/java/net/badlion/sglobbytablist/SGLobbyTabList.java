package net.badlion.sglobbytablist;

import net.badlion.gberry.Gberry;
import net.badlion.sglobbytablist.listeners.CustomEventListener;
import net.badlion.sglobbytablist.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SGLobbyTabList extends JavaPlugin {

	private static SGLobbyTabList plugin;

	private SGLobbyTabListManager sgLobbyTabListManager;

	public SGLobbyTabList() {
		Gberry.enableProtocol = true;
	}

	@Override
	public void onEnable() {
		SGLobbyTabList.plugin = this;

		this.sgLobbyTabListManager = new SGLobbyTabListManager();

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public static SGLobbyTabList getInstance() {
		return SGLobbyTabList.plugin;
	}

	public SGLobbyTabListManager getSGLobbyTabListManager() {
		return this.sgLobbyTabListManager;
	}

}
