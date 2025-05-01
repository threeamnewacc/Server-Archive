package net.badlion.smellycases;

import net.badlion.smellycases.commands.GiveCaseCommand;
import net.badlion.smellycases.listeners.InventoryListener;
import net.badlion.smellycases.managers.CaseManager;
import net.badlion.smellycases.managers.CaseDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SmellyCases extends JavaPlugin {

	private static SmellyCases plugin;

	public static SmellyCases getInstance() {
		return SmellyCases.plugin;
	}

	@Override
	public void onEnable() {
		SmellyCases.plugin = this;

		// Initialize our classes
		CaseManager.initialize();

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new CaseDataManager(), this);

		this.getCommand("givecases").setExecutor(new GiveCaseCommand());
	}

	@Override
	public void onDisable() {

	}

}
