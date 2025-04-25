package cc.patrone.practice.managers;

import zone.potion.storage.flatfile.Config;
import cc.patrone.practice.PracticePlugin;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Setter
public class LocationManager {
	private final Config config;
	@Getter
	private Location spawn, kitEditor;

	public LocationManager(PracticePlugin plugin) {
		this.config = new Config(plugin, "practice");

		Location spawn = plugin.getServer().getWorlds().get(0).getSpawnLocation();

		config.addDefaults(ImmutableMap.<String, Object>builder()
				.put("locations.spawn", spawn)
				.put("locations.kit-editor", spawn)
				.build());
		config.copyDefaults();

		this.spawn = config.getLocation("locations.spawn");
		this.kitEditor = config.getLocation("locations.kit-editor");
	}

	public void saveLocations() {
		config.set("locations.spawn", spawn);
		config.set("locations.kit-editor", kitEditor);
		config.save();
	}
}
