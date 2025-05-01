package net.badlion.factiontablist;

import net.badlion.factiontablist.listeners.CommandListener;
import net.badlion.factiontablist.listeners.CustomEventListener;
import net.badlion.factiontablist.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class FactionTabList extends JavaPlugin {

	private static FactionTabList plugin;

	private FactionTabListManager factionTabListManager;

	@Override
	public void onEnable() {
		FactionTabList.plugin = this;

		this.factionTabListManager = new FactionTabListManager();

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
		this.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		// Task to update the player count every 30 seconds, no need to track updated players anymore
		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				Map<Integer, String> tabChanges = new HashMap<>();
				tabChanges.put(22, "§9Players - " + Bukkit.getOnlinePlayers().size());
				FactionTabList.this.factionTabListManager.setAllTabListPositions(tabChanges);
			}
		}, 100L, 600L);
	}

	public static FactionTabList getInstance() {
		return FactionTabList.plugin;
	}

	public FactionTabListManager getFactionTabListManager() {
		return factionTabListManager;
	}

}
