package net.badlion.survivalgames;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGWorld;
import net.badlion.worldrotator.GWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SGWorld extends MPGWorld {

	private boolean hasDeathmatchArena;

	private int deathMatchRadiusLimit;
	private Location deathMatchCenterLocation;
	private Location deathMatchSpectatorLocation;
	private List<Location> deathMatchLocations = new ArrayList<>();

	private List<Location> supplyDropLocations = new ArrayList<>();
	private List<Location> availableSupplyDropLocations = new ArrayList<>();

    public SGWorld(GWorld gWorld) {
        super(gWorld);
    }

    @Override
    public void load() {
	    super.load();

	    this.hasDeathmatchArena = this.gWorld.getYml().getBoolean("deathmatch_arena");

	    // Grab deathmatch information depending on if there's a deathmatch arena or not
	    if (this.hasDeathmatchArena) {
		    this.deathMatchLocations = this.getLocationsFromYml("deathmatch_locations", false);
		    this.deathMatchSpectatorLocation = Gberry.parseLocation(this.gWorld.getYml().getString("deathmatch_spectator_location"));
		    this.deathMatchSpectatorLocation.getChunk().load();
	    } else {
		    // DM spectator location is the same as the normal one
		    this.deathMatchSpectatorLocation = this.spectatorLocation;

		    this.deathMatchRadiusLimit = this.gWorld.getYml().getInt("deathmatch_radius");
		    this.deathMatchCenterLocation = Gberry.parseLocation(this.gWorld.getYml().getString("deathmatch_center"));
	    }

	    // Load Tier 1 and Tier 2 chests
	    this.loadChests(this.getLocationsFromYml("tier_1_chests"), this.getLocationsFromYml("tier_2_chests"));

	    // Load supply drop locations
	    this.supplyDropLocations = this.getLocationsFromYml("supply_drop_locations", true);

	    // Change the locations grabbed to block locations
	    for (Location supplyDropLocation : this.supplyDropLocations) {
		    supplyDropLocation.getChunk().load();

		    this.availableSupplyDropLocations.add(supplyDropLocation.getBlock().getLocation());
	    }
    }

	private void loadChests(List<Location> tier1Locations, List<Location> tier2Locations) {
		SGGame sgGame = SurvivalGames.getInstance().getSGGame();

		// Create map in tier chests map for this tier
		Map<Location, Inventory> tier1Chests = new HashMap<>();
		sgGame.getTierChests().put(1, tier1Chests);
		for (Location location : tier1Locations) {
			if (location.getBlock().getType() != Material.CHEST) {
				Bukkit.getLogger().info("Unable to find tier 1 chest at " + location.toString());
				Bukkit.getLogger().info("Unable to find tier 1 chest at " + location.toString());
				Bukkit.getLogger().info("Unable to find tier 1 chest at " + location.toString());
			}

			Chest chest = (Chest) location.getBlock().getState();
			chest.getInventory().clear();

			// Create a new inventory
			tier1Chests.put(location, SurvivalGames.getInstance().getServer().
					createInventory(null, 27, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Tier 1 Chest"));
		}

		// Create map in tier chests map for this tier
		Map<Location, Inventory> tier2Chests = new HashMap<>();
		sgGame.getTierChests().put(2, tier2Chests);
		for (Location location : tier2Locations) {
			if (location.getBlock().getType() != Material.ENDER_CHEST) {
				Bukkit.getLogger().info("Unable to find tier 2 enderchest at " + location.toString());
				Bukkit.getLogger().info("Unable to find tier 2 enderchest at " + location.toString());
				Bukkit.getLogger().info("Unable to find tier 2 enderchest at " + location.toString());
			}

			// Set ender chest to normal chest
			location.getBlock().setType(Material.CHEST);

			// Create a new inventory
			tier2Chests.put(location, SurvivalGames.getInstance().getServer().
					createInventory(null, 27, ChatColor.GOLD + ChatColor.BOLD.toString() + "Tier 2 Chest"));
		}

		// Clean the map
		this.cleanMap();

		// Fill chests
		sgGame.fillChests();
	}

	public boolean hasDeathmatchArena() {
		return this.hasDeathmatchArena;
	}

	public int getDeathMatchRadiusLimit() {
		return this.deathMatchRadiusLimit;
	}

	@Override
	public Location getSpectatorLocation() {
		// Has deathmatch started?
		if (MPG.getInstance().getMPGGame().getGameState().ordinal() >= MPGGame.GameState.DEATH_MATCH_COUNTDOWN.ordinal()) {
			return this.deathMatchSpectatorLocation;
		}

		return this.spectatorLocation;
	}

	public Location getDeathMatchLocation(int index) {
		Location location;

		if (this.hasDeathmatchArena) {
			location = this.deathMatchLocations.get(index);
		} else {
			// Deathmatch locations are the same as the spawn locations
			location = this.spawnLocations.get(index);
		}

		return location.clone().add(0, 1, 0);
	}

	public List<Location> getDeathMatchLocations() {
		return this.deathMatchLocations;
	}

	public Location getDeathMatchCenterLocation() {
		return this.deathMatchCenterLocation;
	}

	public Location getRandomSupplyDropLocation() {
		return this.availableSupplyDropLocations.remove(0);
	}

	public List<Location> getAvailableSupplyDropLocations() {
		return this.availableSupplyDropLocations;
	}

	public List<Location> getAllSupplyDropLocations() {
		return this.supplyDropLocations;
	}

}
