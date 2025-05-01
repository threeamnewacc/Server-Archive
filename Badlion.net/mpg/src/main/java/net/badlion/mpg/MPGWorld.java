package net.badlion.mpg;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.worldrotator.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MPGWorld {

    protected GWorld gWorld;

	protected int voidDeathYLevel;

    protected Location spectatorLocation;

	protected Map<Object, Location> spawnLocations = new HashMap<>();

    public MPGWorld(GWorld gWorld) {
        this.gWorld = gWorld;
    }

    public void load() {
	    // Check if there's no config file for worlds that don't use it
	    if (this.gWorld.getYml() == null) return;

	    // Load void death y level (default 0 if not set)
	    this.voidDeathYLevel = this.gWorld.getYml().getInt("void_death_y_level", 0);

	    this.spectatorLocation = Gberry.parseLocation(this.gWorld.getYml().getString("spectator_location"));

	    this.spectatorLocation.getChunk().load();

	    // TODO: Implementation might cause problems later if we have teams w/o spawn locations
	    // TODO: SG GOES TO THE ELSE SINCE ITS GAME TYPE IS ALWAYS FFA ON STARTUP AND THE MAP LOADS ON STARTUP
	    // Grab spawn locations for teams
	    if (/*MPG.ALLOW_RESPAWNING && */MPG.GAME_TYPE == MPG.GameType.PARTY) {
		    for (String color : this.gWorld.getYml().getStringList("team_colors")) {//for (MPGTeam team : MPGTeamManager.getAllMPGTeams()) {
			    Location location = Gberry.parseLocation(this.gWorld.getYml().getString(color + "_spawn_location"));
			    location.getChunk().load();

			    this.spawnLocations.put(MPGTeam.getTeamNameFromColor(ChatColor.valueOf(color.toUpperCase())), location);
		    }
	    } else {
		    int i = 0;
		    for (Location location : this.getLocationsFromYml("spawn_locations", false)) {
			    this.spawnLocations.put(i++, location);
		    }
	    }

	    // Update team respawn locations with new world's respawn locations
	    if (MPG.ALLOW_RESPAWNING) {
		    MPGTeamManager.updateTeamRespawnLocations();
	    }
    }

	public void cleanMap() {
		Chunk[] chunks = this.gWorld.getBukkitWorld().getLoadedChunks();
		for (Chunk chunk : chunks) {
			BlockState[] tileEntities = chunk.getTileEntities();
			for (BlockState i : tileEntities) {
				if (i instanceof Beacon) {
					Beacon blockState = ((Beacon) i);
					blockState.getInventory().clear();
				} else if (i instanceof BrewingStand) {
					BrewingStand blockState = ((BrewingStand) i);
					blockState.getInventory().clear();
				} else if (i instanceof Chest) {
					Chest blockState = ((Chest) i);
					blockState.getInventory().clear();
				} else if (i instanceof Dispenser) {
					Dispenser blockState = ((Dispenser) i);
					blockState.getInventory().clear();
				} else if (i instanceof Furnace) {
					Furnace blockState = ((Furnace) i);
					blockState.getInventory().clear();
				} else if (i instanceof Hopper) {
					Hopper blockState = ((Hopper) i);
					blockState.getInventory().clear();
				} else if (i instanceof Jukebox) {
					Jukebox blockState = ((Jukebox) i);
					blockState.eject();
				}
			}
		}

		for (Entity entity : this.gWorld.getBukkitWorld().getEntities()) {
			if (entity.getType() == EntityType.DROPPED_ITEM) {
				entity.remove();
			}
		}

		this.clearNonPlayerEntities();
	}

	public void clearNonPlayerEntities() {
		// Remove any lingering entities
		for (Entity entity : this.gWorld.getBukkitWorld().getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof Player) && !entity.hasMetadata("CombatLoggerNPC")) {
				entity.remove();
			}
		}
	}

	public List<Location> getLocationsFromYml(String key) {
		return this.getLocationsFromYml(key, false);
	}

	public List<Location> getLocationsFromYml(String key, boolean shuffle) {
		List<String> locationStrings = this.gWorld.getYml().getStringList(key);
		List<Location> tmpLocations = new ArrayList<>();

		for (String s : locationStrings) {
			Location location = Gberry.parseLocation(s);
			tmpLocations.add(location);

			// Load chunk
			location.getChunk().load();
		}

		if (shuffle) {
			Collections.shuffle(tmpLocations);
		}

		return Collections.unmodifiableList(tmpLocations);
	}

	public GWorld getGWorld() {
		return this.gWorld;
	}

	public int getVoidDeathYLevel() {
		return this.voidDeathYLevel;
	}

	public Location getSpawnLocation(int index) {
		return this.spawnLocations.get(index);
	}

	public Location getSpawnLocation(MPGTeam team) {
		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			return this.spawnLocations.get(team.getTeamName());
		} else {
			return this.spawnLocations.get("default");
		}
	}

	public Collection<Location> getSpawnLocations() {
		return this.spawnLocations.values();
	}

	public Location getSpectatorLocation() {
		return this.spectatorLocation;
	}

}
