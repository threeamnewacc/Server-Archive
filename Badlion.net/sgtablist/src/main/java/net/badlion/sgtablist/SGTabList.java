package net.badlion.sgtablist;

import net.badlion.gberry.Gberry;
import net.badlion.sgtablist.listeners.CustomEventListener;
import net.badlion.sgtablist.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SGTabList extends JavaPlugin {

	private static SGTabList plugin;

	private SGTabListManager sgTabListManager;

	public SGTabList() {
		Gberry.enableProtocol = true;
	}

	@Override
	public void onEnable() {
		SGTabList.plugin = this;

		this.sgTabListManager = new SGTabListManager();

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new CustomEventListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public static SGTabList getInstance() {
		return SGTabList.plugin;
	}

	public SGTabListManager getSGTabListManager() {
		return this.sgTabListManager;
	}

}
