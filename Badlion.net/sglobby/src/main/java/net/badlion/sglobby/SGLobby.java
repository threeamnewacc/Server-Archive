package net.badlion.sglobby;

import net.badlion.ministats.MiniStats;
import net.badlion.mpglobby.MPGLobby;
import net.badlion.mpglobby.QueueType;
import net.badlion.sglobby.commands.SettingsCommand;
import net.badlion.sglobby.commands.StatsCommand;
import net.badlion.sglobby.inventories.SGSettingsInventory;
import net.badlion.sglobby.managers.RatingManager;
import net.badlion.sglobby.managers.SGLobbySidebarManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class SGLobby extends MPGLobby {

	public static QueueType CLASSIC_FFA;

	public static QueueType CLASSIC_TEAM;

	public SGLobby() {
		// Configure ministats
		MiniStats.SEASON = 2;
		MiniStats.TAG = "SG";
		MiniStats.TABLE_NAME = "sg_s2_ministats";
	}

	@Override
	public void onEnable() {
		// Initialize queues first
		SGLobby.CLASSIC_FFA = new QueueType("SG FFA", QueueType.GameType.FFA, "classic",
				Material.FISHING_ROD, new Location(Bukkit.getWorld("world"), 4, 53, 0));

		SGLobby.CLASSIC_TEAM = new QueueType("SG Team of 2", QueueType.GameType.PARTY, "classic",
			    Material.BOW, new Location(Bukkit.getWorld("world"), 4, 53, 1));

		MPGLobby.QUEUE_INVENTORY_NAME = "Survival Games Queues";
		MPGLobby.MOTD_DESCRIPTION = "Badlion SG 2.0";

		super.onEnable();

		// Set ministats player creator
		MiniStats.getInstance().setMiniStatsPlayerCreator(new FakeSGMiniStatsPlayer.FakeSGMiniStatsPlayerCreator());

		// Initialize settings inventory
		new SGSettingsInventory();

		// Set lobby spawn location
		this.spawnLocation = new Location(this.getServer().getWorld("world"), -31.0, 51.0, 0.5, -90, 0);
		this.leaveQueueSignLocation = new Location(this.getServer().getWorld("world"), 4, 52, 1);

		// Make sure the spawn and leave queue sign chunks are loaded
		this.spawnLocation.getChunk().load();
		this.leaveQueueSignLocation.getChunk().load();

		this.getCommand("settings").setExecutor(new SettingsCommand());
		this.getCommand("stats").setExecutor(new StatsCommand());

		this.getServer().getPluginManager().registerEvents(new RatingManager(), this);
		this.getServer().getPluginManager().registerEvents(new SGLobbySidebarManager(), this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	public static SGLobby getInstance() {
		return (SGLobby) MPGLobby.getInstance();
	}

}
