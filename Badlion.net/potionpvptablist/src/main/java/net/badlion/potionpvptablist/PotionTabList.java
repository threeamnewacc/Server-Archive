package net.badlion.potionpvptablist;

import net.badlion.gberry.Gberry;
import net.badlion.potionpvptablist.listeners.CustomEventListener;
import net.badlion.potionpvptablist.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PotionTabList extends JavaPlugin {

	private static PotionTabList plugin;

	private PotionTabListManager potionTabListManager;

	public PotionTabList() {
		Gberry.enableProtocol = true;
	}

	@Override
	public void onEnable() {
		PotionTabList.plugin = this;

		this.potionTabListManager = new PotionTabListManager();

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		// Task to update the player count every 30 seconds, no need to track updated players anymore
		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				Map<Integer, String> tabChanges = new HashMap<>();
				tabChanges.put(22, "§9Players - " + Bukkit.getOnlinePlayers().size());
				PotionTabList.this.potionTabListManager.setAllTabListPositions(tabChanges);
			}
		}, 100L, 600L);
	}

	public static PotionTabList getInstance() {
		return PotionTabList.plugin;
	}

	public PotionTabListManager getPotionTabListManager() {
		return potionTabListManager;
	}

}
