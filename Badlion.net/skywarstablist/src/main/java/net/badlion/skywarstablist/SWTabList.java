package net.badlion.skywarstablist;

import net.badlion.skywarstablist.commands.HideCommand;
import net.badlion.skywarstablist.listeners.CustomEventListener;
import net.badlion.skywarstablist.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SWTabList extends JavaPlugin {

	private static SWTabList plugin;

	private SWTabListManager SWTabListManager;

	@Override
	public void onEnable() {
		SWTabList.plugin = this;

		this.SWTabListManager = new SWTabListManager();

		// Register command executors
		this.getCommand("hide").setExecutor(new HideCommand());

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public static SWTabList getInstance() {
		return SWTabList.plugin;
	}

	public SWTabListManager getSWTabListManager() {
		return SWTabListManager;
	}

}
