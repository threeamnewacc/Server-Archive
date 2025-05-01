package net.badlion.gfactions;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Config {

	private static Map<String, Config> loadedConfigs = new HashMap<>();

	public YamlConfiguration config;

	private File file;

	public Config(String fileName) {
		this.file = new File(GFactions.plugin.getDataFolder(), fileName + ".yml");

		if (this.file.exists()) {
			this.config = YamlConfiguration.loadConfiguration(new File(GFactions.plugin.getDataFolder(), fileName + ".yml"));

			// Add config to our static map
			Config.loadedConfigs.put(fileName, this);
		} else {
			this.loadConfigFile(fileName);

			GFactions.plugin.getServer().getLogger().severe(fileName + " configuration file not found, stopping server!");
			GFactions.plugin.getServer().getLogger().severe(fileName + " configuration file not found, stopping server!");
			GFactions.plugin.getServer().getLogger().severe(fileName + " configuration file not found, stopping server!");

			GFactions.plugin.getServer().shutdown();
		}
	}

	private void loadConfigFile(String fileName) {
		InputStream configStream = GFactions.plugin.getResource(fileName + ".yml");
		if (configStream == null) {
			System.out.println("ERROR NULL");
			return;
		}
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(configStream);

		try {
			defConfig.save(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {
		this.config = YamlConfiguration.loadConfiguration(this.file);

		this.load();
	}

	public abstract void load();

	public void setValue(String path, Object value) {
		this.config.set(path, value);
	}

	public static void reloadConfig(String fileName) throws ConfigNotFoundException {
		Config config = Config.loadedConfigs.get(fileName);
		if (config != null) {
			config.loadConfig();
		} else {
			throw new ConfigNotFoundException();
		}
	}

	public static class ConfigNotFoundException extends Exception {

	}

}
