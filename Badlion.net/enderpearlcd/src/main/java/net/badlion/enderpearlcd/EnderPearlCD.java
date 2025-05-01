package net.badlion.enderpearlcd;

import org.bukkit.plugin.java.JavaPlugin;

public class EnderPearlCD extends JavaPlugin {

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new EnderPearlCDListener(), this);
	}

	@Override
	public void onDisable() {

	}

}
