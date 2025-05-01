package net.badlion.arenatablist;

import net.badlion.arenatablist.lobby.ArenaLobbyTabListManager;
import net.badlion.arenatablist.lobby.listeners.ArenaLobbyListener;
import net.badlion.arenatablist.lobby.listeners.ArenaLobbyPlayerListener;
import net.badlion.arenatablist.pvp.ArenaPvPTabListManager;
import net.badlion.arenatablist.pvp.listeners.ArenaPvPListener;
import net.badlion.arenatablist.pvp.listeners.ArenaPvPPlayerListener;
import net.badlion.gberry.Gberry;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaTabList extends JavaPlugin {

	private static ArenaTabList plugin;

	private ArenaLobbyTabListManager arenaLobbyTabListManager;
	private ArenaPvPTabListManager arenaPvPTabListManager;

	public ArenaTabList() {
		Gberry.enableProtocol = true;
	}

	@Override
	public void onEnable() {
		ArenaTabList.plugin = this;

		if (this.getServer().getPluginManager().getPlugin("ArenaLobby") != null) {
			// Load arena lobby tablist

			this.arenaLobbyTabListManager = new ArenaLobbyTabListManager();

			// Register listeners
			this.getServer().getPluginManager().registerEvents(new ArenaLobbyListener(), this);
			this.getServer().getPluginManager().registerEvents(new ArenaLobbyPlayerListener(), this);
		} else if (this.getServer().getPluginManager().getPlugin("ArenaPvP") != null) {
			// Load arena pvp tablist

			this.arenaPvPTabListManager = new ArenaPvPTabListManager();

			// Register listeners
			this.getServer().getPluginManager().registerEvents(new ArenaPvPListener(), this);
			this.getServer().getPluginManager().registerEvents(new ArenaPvPPlayerListener(), this);
		}
	}

	public static ArenaTabList getInstance() {
		return ArenaTabList.plugin;
	}

	public ArenaLobbyTabListManager getArenaLobbyTabListManager() {
		return this.arenaLobbyTabListManager;
	}

	public ArenaPvPTabListManager getArenaPvPTabListManager() {
		return this.arenaPvPTabListManager;
	}
}
