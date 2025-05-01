package net.badlion.tdm;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.MPGWorld;
import net.badlion.worldrotator.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class TDMWorld extends MPGWorld {

	private int minYLevel;

	private Map<ChatColor, Location> spawnLocations = new HashMap<>();

	public TDMWorld(GWorld gWorld) {
		super(gWorld);
	}

	@Override
	public void load() {
		super.load();

		this.minYLevel = this.gWorld.getYml().getInt("minytilldeath");

		// Load team spawn locations
		for (ChatColor color : MPGTeam.TEAM_COLORS) {
			String locationString = this.gWorld.getYml().getString("spawn_locations." + color.name().toLowerCase());

			if (locationString != null) {
				this.spawnLocations.put(color, Gberry.parseLocation(locationString));
			}
		}
	}

	public int getMinYLevel() {
		return minYLevel;
	}

	public Location getTeamSpawnLocation(ChatColor color) {
		Location spawnLocation = this.spawnLocations.get(color);

		if (spawnLocation == null) {
			throw new RuntimeException(color + " spawn location null for world " + this.getGWorld().getInternalName());
		}

		return spawnLocation;
	}

}
