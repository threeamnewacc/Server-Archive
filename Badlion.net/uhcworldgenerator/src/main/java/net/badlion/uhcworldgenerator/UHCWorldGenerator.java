package net.badlion.uhcworldgenerator;

import net.badlion.uhcworldgenerator.commands.CheckBiomeCommand;
import net.badlion.uhcworldgenerator.commands.UHCWorldCommand;
import net.badlion.uhcworldgenerator.listeners.UHCWorldListener;
import net.badlion.worldborder.Config;
import net.badlion.worldborder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCWorldGenerator extends JavaPlugin {

    public static UHCWorldGenerator plugin;
    public static WorldBorder worldBorder;

	private int startTime;
	private int endTime;

    @Override
    public void onEnable() {
	    UHCWorldGenerator.plugin = this;

	    // Save default config if no config file exists
	    this.saveDefaultConfig();

	    this.startTime = this.getConfig().getInt("start_time");
	    this.endTime = this.getConfig().getInt("end_time");

	    UHCWorldGenerator.worldBorder = (WorldBorder) UHCWorldGenerator.plugin.getServer().getPluginManager().getPlugin("WorldBorder");

	    this.getCommand("tpworld").setExecutor(new UHCWorldCommand());
		this.getCommand("chkbiome").setExecutor(new CheckBiomeCommand());

	    this.getServer().getPluginManager().registerEvents(new UHCWorldListener(), this);

	    this.swapBiomes();

	    new UHCWorldCheckerTask().runTaskTimer(this, 20 * 5, 20 * 5);  // every 5 seconds
	    UHCWorldCheckerTask.isGenerating = Config.fillTask != null;

		// Force reboot after 24 hrs
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
			}
		}.runTaskLater(this, 20 * 60 * 60 * 4);

		// 1 minute later fail safe
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
			}
		}.runTaskLater(this, (20 * 60 * 60 * 4) + 1200);

		// Another fail safe if we took too long to generate a new world
		/*new BukkitRunnable() {
			@Override
			public void run() {
			 	if (!UHCWorldCheckerTask.isGenerating) {
					if (UHCWorldCheckerTask.lastAttemptTime + (120 * 1000) < System.currentTimeMillis()) {
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
					}
				}
			}
		}.runTaskTimer(this, 20 * 120, 20 * 120);*/
    }

    @Override
    public void onDisable() {
    }

	public void swapBiomes() {
		// Swap all biomes with other biomes
		Bukkit.getServer().setBiomeBase(Biome.OCEAN, Biome.FOREST, 0);
		Bukkit.getServer().setBiomeBase(Biome.BEACH, Biome.RIVER, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_HILLS, Biome.TAIGA, 0);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 0);
		Bukkit.getServer().setBiomeBase(Biome.DEEP_OCEAN, Biome.PLAINS, 0);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.FOREST, 0);

		// Weird sub-biomes
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE, Biome.PLAINS, 128);
		Bukkit.getServer().setBiomeBase(Biome.JUNGLE_EDGE, Biome.DESERT, 128);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA, Biome.SAVANNA, 128);
		Bukkit.getServer().setBiomeBase(Biome.SAVANNA_PLATEAU, Biome.RIVER, 128);
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

}
