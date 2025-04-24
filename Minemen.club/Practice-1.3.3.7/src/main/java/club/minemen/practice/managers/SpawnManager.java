package club.minemen.practice.managers;

import club.minemen.core.util.CustomLocation;
import club.minemen.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class SpawnManager {
	private final Practice plugin = Practice.getInstance();

	private CustomLocation spawnLocation;
	private CustomLocation spawnMin;
	private CustomLocation spawnMax;

	private CustomLocation editorLocation;
	private CustomLocation editorMin;
	private CustomLocation editorMax;

	public SpawnManager() {
		this.loadConfig();
	}

	private void loadConfig() {
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		if (config.contains("spawnLocation")) {
			this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
			this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
			this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
			this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
			this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
			this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));
		}
	}

	public void saveConfig() {
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		config.set("spawnLocation", CustomLocation.locationToString(this.spawnLocation));
		config.set("spawnMin", CustomLocation.locationToString(this.spawnMin));
		config.set("spawnMax", CustomLocation.locationToString(this.spawnMax));
		config.set("editorLocation", CustomLocation.locationToString(this.editorLocation));
		config.set("editorMin", CustomLocation.locationToString(this.editorMin));
		config.set("editorMax", CustomLocation.locationToString(this.editorMax));
		this.plugin.getMainConfig().save();
	}
}
