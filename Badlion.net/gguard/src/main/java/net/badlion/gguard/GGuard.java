package net.badlion.gguard;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.gguard.commands.RegionCommand;
import net.badlion.gguard.listeners.BlockListener;
import net.badlion.gguard.listeners.CreatureListener;
import net.badlion.gguard.listeners.DamageListener;
import net.badlion.gguard.listeners.ExplodeListener;
import net.badlion.gguard.listeners.HangingItemListener;
import net.badlion.gguard.listeners.IceListener;
import net.badlion.gguard.listeners.PVPListener;
import net.badlion.gguard.listeners.PlayerListener;
import net.badlion.gguard.listeners.ProjectileListener;
import net.badlion.gguard.listeners.RegionListener;
import net.badlion.gguard.listeners.ToolListener;
import net.badlion.gguard.listeners.WorldListener;
import net.badlion.gguard.tasks.CheckLocationsTask;
import net.badlion.gguard.tasks.FeedHealTask;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GGuard extends JavaPlugin {

	public static boolean VERBOSE = true;

	public static Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

	private static GGuard plugin;

	private Map<String, ProtectedRegion> protectedRegionMap = new HashMap<>();
	private List<ProtectedRegion> protectedRegions = new ArrayList<>();
	private List<PolygonRegion> polygonRegions = new ArrayList<>();
	private CheckLocationsTask locationTask;
	private ProtectedRegion defaultRegion = null;
	
	@Override
	public void onEnable() {
		GGuard.plugin = this;

		this.load();

		// Attach listeners
		this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		this.getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
		this.getServer().getPluginManager().registerEvents(new IceListener(), this);
		this.getServer().getPluginManager().registerEvents(new DamageListener(), this);
		this.getServer().getPluginManager().registerEvents(new CreatureListener(), this);
		this.getServer().getPluginManager().registerEvents(new ProjectileListener(), this);
		this.getServer().getPluginManager().registerEvents(new HangingItemListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new PVPListener(), this);
		this.getServer().getPluginManager().registerEvents(new RegionListener(), this);
		this.getServer().getPluginManager().registerEvents(new WorldListener(), this);

		new FeedHealTask(this).runTaskTimer(this, 100, 100); // Every 5 seconds

		// This never worked...comment out for now
		if (this.getConfig().getBoolean("gguard.show-regions", false)) {
			this.locationTask = new CheckLocationsTask(this);
			this.locationTask.runTaskTimer(this, 100, 100); // Every 5 seconds
		}

		if (this.getConfig().getBoolean("gguard.use_compass", true)) {
			this.getServer().getPluginManager().registerEvents(new ToolListener(), this);
		}

		this.getCommand("rg").setExecutor(new RegionCommand());
	}
	
	@Override
	public void onDisable() {
	}
	
	public void addProtectedRegion(ProtectedRegion toAdd) {
		if (toAdd.getRegionName().equalsIgnoreCase("default")) {
			this.defaultRegion = toAdd;
			return;
		}

		this.protectedRegionMap.put(toAdd.getRegionName(), toAdd);
        //this.getLogger().info("Adding region " + toAdd.getRegionName());

		// Otherwise start seeing if we are in another zone
		for (ProtectedRegion pr2 : this.protectedRegions) {
			// Bottom left corner is in one of the other protected regions, keep going until we hit the bottom
			if (pr2.isLocationInProtectedRegion(toAdd.getLocation1()) && pr2.isLocationInProtectedRegion(toAdd.getLocation2())) {
                //this.getLogger().info("Added " + toAdd.getRegionName() + " to region " + pr2.getRegionName() + " non-recursively.");
				this.addProtectedRegionRecursiveStep(pr2, toAdd);
				return;
			}
		}
		
		// Not in any other region, add it to the top level
		this.protectedRegions.add(toAdd);
        //this.getLogger().info("Added " + toAdd.getRegionName() + " to region " + " base");
	}
	
	public void addProtectedRegionRecursiveStep(ProtectedRegion inside, ProtectedRegion toAdd) {
		if (inside.getProtectedRegions().size() != 0) {
			// Iterate and go another level deeper
			for (ProtectedRegion pr : inside.getProtectedRegions()) {
				if (pr.isLocationInProtectedRegion(toAdd.getLocation1()) && pr.isLocationInProtectedRegion(toAdd.getLocation2())) {
                    //this.getLogger().info("Added " + toAdd.getRegionName() + " to region " + pr.getRegionName() + " recursively.");
					this.addProtectedRegionRecursiveStep(pr, toAdd);
					return;
				}
			}
			
			// Base case, no children that we fit into
			inside.getProtectedRegions().add(toAdd);
            //this.getLogger().info("Added " + toAdd.getRegionName() + " to region " + inside.getRegionName() + " base case 1");
		} else {
			// Base case, no children
			inside.getProtectedRegions().add(toAdd);
            //this.getLogger().info("Added " + toAdd.getRegionName() + " to region " + inside.getRegionName() + " base case 2");
		}
	}
	
	public void deleteProtectedRegion(ProtectedRegion toRemove) {
		// Otherwise start seeing if we are in another zone
		if (toRemove.getRegionName().equalsIgnoreCase("default")) {
			this.defaultRegion = null;
			return;
		}
		for (ProtectedRegion pr2 : this.protectedRegions) {
			// Bottom left corner is in one of the other protected regions, keep going until we hit the bottom
			if (pr2.isLocationInProtectedRegion(toRemove.getLocation1()) && pr2.isLocationInProtectedRegion(toRemove.getLocation2())) {
                //this.getLogger().info("Removed " + toRemove.getRegionName() + " from " + pr2.getRegionName() + " non-recursively.");
				this.deleteProtectedRegionRecursiveStep(this.protectedRegions, pr2, toRemove);
				return;
			}
		}

        // Base case, the region is in the top level
        if (this.protectedRegions.contains(toRemove)) {
            this.protectedRegions.remove(toRemove);
            //this.getLogger().info("Removed " + toRemove.getRegionName() + " from base.");
        }
	}
	
	public void deleteProtectedRegionRecursiveStep(List<ProtectedRegion> parent, ProtectedRegion inside, ProtectedRegion toRemove) {
		if (inside.getProtectedRegions().size() != 0) {
			// Iterate and go another level deeper
			for (ProtectedRegion pr : inside.getProtectedRegions()) {
				if (pr.isLocationInProtectedRegion(toRemove.getLocation1()) && pr.isLocationInProtectedRegion(toRemove.getLocation2())) {
                    //this.getLogger().info("Removed " + toRemove.getRegionName() + " from region " + pr.getRegionName() + " recursively.");
					this.deleteProtectedRegionRecursiveStep(inside.getProtectedRegions(), pr, toRemove);
					return;
				}
			}
			
			// Base case, no children that we fit into
            if (inside.getProtectedRegions().contains(toRemove)) {
                //this.getLogger().info("Removed " + toRemove.getRegionName() + " from region " + inside.getRegionName() + " base case 1");
			    inside.getProtectedRegions().remove(toRemove);
            }
		} else {
            // Base case, no children at all, this is what we fit into
            //this.getLogger().info("Removed " + toRemove.getRegionName() + " from region " + inside.getRegionName() + " base case 2");
            parent.remove(toRemove);
        }
	}
	
	public String getProtectedRegionName(Location location, List<ProtectedRegion> protectedRegions) {
		UnsafeLocation unsafeLocation = new UnsafeLocation(location);
		for (ProtectedRegion pr : protectedRegions) {
			// Recursively step through
			if (pr.getProtectedRegions().size() != 0) {
				String tmp = getProtectedRegionName(location, pr.getProtectedRegions());
				if (tmp != null) {
					return tmp;
				}
			}
			
			// Ok check this section ourselves
			if (pr.isLocationInProtectedRegion(unsafeLocation)) {
				return pr.getRegionName();
			}
		}

		// Nothing found, return default region
		return "default";
	}
	
	public ProtectedRegion getProtectedRegion(Location location, List<ProtectedRegion> protectedRegions) {
		UnsafeLocation unsafeLocation = new UnsafeLocation(location);
		for (ProtectedRegion pr : protectedRegions) {
            //this.getLogger().info("get loop " + pr.getRegionName());
			// Recursively step through
			if (pr.getProtectedRegions().size() != 0) {
                //this.getLogger().info("recursive " + pr.getRegionName());
				ProtectedRegion region = getProtectedRegion(location, pr.getProtectedRegions());
				if (region != null) {
                    //this.getLogger().info("returning " + region.getRegionName());
					return region;
				}
			}
			
			// Ok check this section ourselves
			if (pr.isLocationInProtectedRegion(unsafeLocation)) {
                //this.getLogger().info("returning 1 " + pr.getRegionName());
				return pr;
			}
		}
		
		// Nothing found, return default region
		return defaultRegion;
	}

    public ProtectedRegion getPVPProtectedRegion(Location location, List<ProtectedRegion> protectedRegions) {
		UnsafeLocation unsafeLocation = new UnsafeLocation(location);
        for (ProtectedRegion pr : protectedRegions) {
            //this.getLogger().info("get loop " + pr.getRegionName());
            // Recursively step through
            if (pr.getProtectedRegions().size() != 0) {
                //this.getLogger().info("recursive " + pr.getRegionName());
                ProtectedRegion region = getPVPProtectedRegion(location, pr.getProtectedRegions());
	            if (region != null) {
                    //this.getLogger().info("returning " + region.getRegionName());
                    return region;
                }
            }

            // Ok check this section ourselves
            if (pr.isLocationPvPInProtectedRegion(unsafeLocation)) {
	            //this.getLogger().info("returning 1 " + pr.getRegionName());
                return pr;
            }
        }

        // Nothing found, return default region
        return defaultRegion;
    }

	public PolygonRegion getPolygonRegion(String regionName) {
		for (PolygonRegion region : this.polygonRegions) {
			if (regionName.equalsIgnoreCase(region.getRegionName())) {
				return region;
			}
		}

		return null;
	}

	public PolygonRegion getPolygonRegion(Location location) {
		UnsafeLocation unsafeLocation = new UnsafeLocation(location);
		for (PolygonRegion region : this.polygonRegions) {
			if (region.isLocationInProtectedRegion(unsafeLocation)) {
				return region;
			}
		}

		return null;
	}

	public ProtectedRegion getDefaultRegion() {
		return defaultRegion;
	}

	public void setDefaultRegion(ProtectedRegion toSet) {
		defaultRegion = toSet;
		defaultRegion.setRegionName("default");
	}
	
	public void getProtectedRegionFromConfig(String region) {
		if (this.getConfig().contains("gguard.regions." + region)) {
			// CRUCIAL, gotta have the region defined
			String configRegion = "gguard.regions." + region;
			this.getLogger().info("Discovered region " + configRegion);
			if (!this.getConfig().contains(configRegion + ".min_x")) {
				this.getLogger().info("min_x");
			}
			if (!this.getConfig().contains(configRegion + ".min_y")) {
				this.getLogger().info("min_y");
			}
			if (!this.getConfig().contains(configRegion + ".min_z")) {
				this.getLogger().info("min_z");
			}
			if (!this.getConfig().contains(configRegion + ".max_x")) {
				this.getLogger().info("max_x");
			}
			if (!this.getConfig().contains(configRegion + ".max_y")) {
				this.getLogger().info("max_y");
			}
			if (!this.getConfig().contains(configRegion + ".max_z")) {
				this.getLogger().info("max_z");
			}
			if (!this.getConfig().contains(configRegion + ".world")) {
				this.getLogger().info("world");
			}
			
			if (this.getConfig().contains(configRegion + ".min_x") && this.getConfig().contains(configRegion + ".max_x")
					&& this.getConfig().contains(configRegion + ".min_y") && this.getConfig().contains(configRegion + ".max_y")
					&& this.getConfig().contains(configRegion + ".min_z") && this.getConfig().contains(configRegion + ".max_z")
					&& this.getConfig().contains(configRegion + ".world")) {
				this.getLogger().info("Successfully located necessary info for " + configRegion);
				String worldName = this.getConfig().getString(configRegion + ".world");
				int min_x = this.getConfig().getInt(configRegion + ".min_x");
				int min_y = this.getConfig().getInt(configRegion + ".min_y");
				int min_z = this.getConfig().getInt(configRegion + ".min_z");
				int max_x = this.getConfig().getInt(configRegion + ".max_x");
				int max_y = this.getConfig().getInt(configRegion + ".max_y");
				int max_z = this.getConfig().getInt(configRegion + ".max_z");
				
				// More sanity checks with coordinates
				if (min_x > max_x) {
					int tmp = min_x;
					min_x = max_x;
					max_x = tmp;
				}
				
				if (min_y > max_y) {
					int tmp = min_y;
					min_y = max_y;
					max_y = tmp;
				}
				
				if (min_z > max_z) {
					int tmp = min_z;
					min_z = max_z;
					max_z = tmp;
				}
				
				// Create the region
				UnsafeLocation bottomLeftLocation = new UnsafeLocation(worldName, min_x, min_y, min_z);
				UnsafeLocation topRightLocation = new UnsafeLocation(worldName, max_x, max_y, max_z);
				ProtectedRegion pr = new ProtectedRegion(region, bottomLeftLocation, topRightLocation);
				
				// Now start checking for flags and such
				pr.setAllowBlockMovement(this.getConfig().getBoolean(configRegion + ".allow_block_movement"));
				pr.setAllowBrokenBlocks(this.getConfig().getBoolean(configRegion + ".allow_broken_blocks"));
				pr.setAllowCreeperBlockDamage(this.getConfig().getBoolean(configRegion + ".allow_creeper_block_damage"));
				pr.setAllowCreeperExplosions(this.getConfig().getBoolean(configRegion + ".allow_creeper_explosions"));
				pr.setAllowedBucketPlacements(this.getConfig().getBoolean(configRegion + ".allow_bucket_placement"));
				pr.setAllowFire(this.getConfig().getBoolean(configRegion + ".allow_fire"));
				pr.setAllowIceMelt(this.getConfig().getBoolean(configRegion + ".allow_ice_melt"));
				pr.setAllowPistonUsage(this.getConfig().getBoolean(configRegion + ".allow_piston_usage"));
				pr.setAllowPlacedBlocks(this.getConfig().getBoolean(configRegion + ".allow_placed_blocks"));
				pr.setAllowTNTBlockDamage(this.getConfig().getBoolean(configRegion + ".allow_tnt_block_damage"));
				pr.setAllowTNTExplosions(this.getConfig().getBoolean(configRegion + ".allow_tnt_explosions"));
				pr.setChangeMobDamageToPlayer(this.getConfig().getBoolean(configRegion + ".change_mob_damage_to_player"));
				pr.setDamageMultiplier(this.getConfig().getDouble(configRegion + ".damage_multiplier"));
				pr.setAllowCreatureSpawn(this.getConfig().getBoolean(configRegion + ".allow_creature_spawn"));
				pr.setHealPlayers(this.getConfig().getBoolean(configRegion + ".heal_players"));
				pr.setFeedPlayers(this.getConfig().getBoolean(configRegion + ".feed_players"));
				pr.setAllowEnderPearls(this.getConfig().getBoolean(configRegion + ".allow_ender_pearls"));
				pr.setAllowMobDamage(this.getConfig().getBoolean(configRegion + ".allow_mob_damage"));
				pr.setAllowEndermanMoveBlocks(this.getConfig().getBoolean(configRegion + ".allow_enderman_move_blocks"));
				pr.setAllowPotionEffects(this.getConfig().getBoolean(configRegion + ".allow_potion_effects"));
				pr.setAllowChestInteraction(this.getConfig().getBoolean(configRegion + ".allow_chest_interaction"));
				pr.setAllowHangingItems(this.getConfig().getBoolean(configRegion + ".allow_hanging_items"));
				pr.setAllowPVP(this.getConfig().getBoolean(configRegion + ".allow_pvp"));
                pr.setAllowItemInteraction(this.getConfig().getBoolean(configRegion + ".allow_item_interaction"));
                pr.setAllowPlantGrowth(this.getConfig().getBoolean(configRegion + ".allow_plant_growth"));
                pr.setAllowPlantSpread(this.getConfig().getBoolean(configRegion + ".allow_plant_spread"));
                pr.setAllowFireSpread(this.getConfig().getBoolean(configRegion + ".allow_fire_spread"));
				pr.setAllowPlayerSleep(this.getConfig().getBoolean(configRegion + ".allow_player_sleep"));
				pr.setAllowPlayerPickupItems(this.getConfig().getBoolean(configRegion + ".allow_player_pickup_items", true)); // Default true
				pr.setAllowBlockChangesByEntities(this.getConfig().getBoolean(configRegion + ".allow_block_changes_by_entities"));
				pr.setAllowLeafDecay(this.getConfig().getBoolean(configRegion + ".allow_leaf_decay"));
				pr.setAllowBoneMealUsage(this.getConfig().getBoolean(configRegion + ".allow_bone_meal_usage"));
				pr.setOverrideChestUsage(this.getConfig().getBoolean(configRegion + ".override_chest_usage"));
                pr.setAllowEnderPearlIn(this.getConfig().getBoolean(configRegion + ".allow_ender_pearl_in"));
                pr.setAllowFireIgniteByPlayer(this.getConfig().getBoolean(configRegion + ".allow_fire_ignite_by_player"));
				pr.setAllowPlantTrampling(this.getConfig().getBoolean(configRegion + ".allow_plant_trampling"));
				pr.setDisallowedPlaceBlocks(this.getConfig().getString(configRegion + ".disallowed_place_blocks"));
				pr.setDisallowedBreakBlocks(this.getConfig().getString(configRegion + ".disallowed_break_blocks"));

				this.getLogger().info("Adding region " + region + " to protected regions.");
				
				this.addProtectedRegion(pr);
			} else {
				this.getLogger().info("Missing some info required for " + configRegion);
			}
		} else {
			this.getLogger().info("Missing region " + region + " in gguard.");
		}
	}

	private void loadPolygonRegions() {
		File directory = new File(this.getDataFolder(), "polygonregions");

		// Create polygonregions directory if it doesn't exist
		directory.mkdir();

		for (File file : directory.listFiles()) {
			if (file.getName().endsWith(".json")) {
				try (FileReader reader = new FileReader(file)) {
					PolygonRegion region = GGuard.GSON.fromJson(reader, PolygonRegion.class);

					this.polygonRegions.add(region);
				} catch (FileNotFoundException ex) {
					// Ignore
				} catch (Exception ex) {
					this.getLogger().warning("Failed to load " + file.getName());
					ex.printStackTrace();
				}
			}
		}
	}

	public void getProtectedRegionsFromConfig() {
		@SuppressWarnings("unchecked")
		List<String> regions = (List<String>) this.getConfig().getList("gguard.active_regions");

		if (regions != null) {
			for (String region : regions) {
				if (this.getConfig().contains("gguard.regions." + region)) {
					// CRUCIAL, gotta have the region defined
					String configRegion = "gguard.regions." + region;
					this.getLogger().info("Discovered region " + configRegion);
					if (!this.getConfig().contains(configRegion + ".min_x")) {
						this.getLogger().info("min_x");
					}
					if (!this.getConfig().contains(configRegion + ".min_y")) {
						this.getLogger().info("min_y");
					}
					if (!this.getConfig().contains(configRegion + ".min_z")) {
						this.getLogger().info("min_z");
					}
					if (!this.getConfig().contains(configRegion + ".max_x")) {
						this.getLogger().info("max_x");
					}
					if (!this.getConfig().contains(configRegion + ".max_y")) {
						this.getLogger().info("max_y");
					}
					if (!this.getConfig().contains(configRegion + ".max_z")) {
						this.getLogger().info("max_z");
					}
					if (!this.getConfig().contains(configRegion + ".world")) {
						this.getLogger().info("world");
					}

					if (this.getConfig().contains(configRegion + ".min_x") && this.getConfig().contains(configRegion + ".max_x")
							&& this.getConfig().contains(configRegion + ".min_y") && this.getConfig().contains(configRegion + ".max_y")
							&& this.getConfig().contains(configRegion + ".min_z") && this.getConfig().contains(configRegion + ".max_z")
							&& this.getConfig().contains(configRegion + ".world")) {
						this.getLogger().info("Successfully located necessary info for " + configRegion);
						String worldName = this.getConfig().getString(configRegion + ".world");
						int min_x = this.getConfig().getInt(configRegion + ".min_x");
						int min_y = this.getConfig().getInt(configRegion + ".min_y");
						int min_z = this.getConfig().getInt(configRegion + ".min_z");
						int max_x = this.getConfig().getInt(configRegion + ".max_x");
						int max_y = this.getConfig().getInt(configRegion + ".max_y");
						int max_z = this.getConfig().getInt(configRegion + ".max_z");

						// More sanity checks with coordinates
						if (min_x > max_x) {
							int tmp = min_x;
							min_x = max_x;
							max_x = tmp;
						}

						if (min_y > max_y) {
							int tmp = min_y;
							min_y = max_y;
							max_y = tmp;
						}

						if (min_z > max_z) {
							int tmp = min_z;
							min_z = max_z;
							max_z = tmp;
						}

						// Create the region
						UnsafeLocation bottomLeftLocation = new UnsafeLocation(worldName, min_x, min_y, min_z);
						UnsafeLocation topRightLocation = new UnsafeLocation(worldName, max_x, max_y, max_z);
						ProtectedRegion pr = new ProtectedRegion(region, bottomLeftLocation, topRightLocation);

						// Now start checking for flags and such
						pr.setAllowBlockMovement(this.getConfig().getBoolean(configRegion + ".allow_block_movement"));
						pr.setAllowBrokenBlocks(this.getConfig().getBoolean(configRegion + ".allow_broken_blocks"));
						pr.setAllowCreeperBlockDamage(this.getConfig().getBoolean(configRegion + ".allow_creeper_block_damage"));
						pr.setAllowCreeperExplosions(this.getConfig().getBoolean(configRegion + ".allow_creeper_explosions"));
						pr.setAllowedBucketPlacements(this.getConfig().getBoolean(configRegion + ".allow_bucket_placement"));
						pr.setAllowFire(this.getConfig().getBoolean(configRegion + ".allow_fire"));
						pr.setAllowIceMelt(this.getConfig().getBoolean(configRegion + ".allow_ice_melt"));
						pr.setAllowPistonUsage(this.getConfig().getBoolean(configRegion + ".allow_piston_usage"));
						pr.setAllowPlacedBlocks(this.getConfig().getBoolean(configRegion + ".allow_placed_blocks"));
						pr.setAllowTNTBlockDamage(this.getConfig().getBoolean(configRegion + ".allow_tnt_block_damage"));
						pr.setAllowTNTExplosions(this.getConfig().getBoolean(configRegion + ".allow_tnt_explosions"));
						pr.setChangeMobDamageToPlayer(this.getConfig().getBoolean(configRegion + ".change_mob_damage_to_player"));
						pr.setDamageMultiplier(this.getConfig().getDouble(configRegion + ".damage_multiplier"));
						pr.setAllowCreatureSpawn(this.getConfig().getBoolean(configRegion + ".allow_creature_spawn"));
						pr.setHealPlayers(this.getConfig().getBoolean(configRegion + ".heal_players"));
						pr.setFeedPlayers(this.getConfig().getBoolean(configRegion + ".feed_players"));
						pr.setAllowEnderPearls(this.getConfig().getBoolean(configRegion + ".allow_ender_pearls"));
						pr.setAllowMobDamage(this.getConfig().getBoolean(configRegion + ".allow_mob_damage"));
						pr.setAllowEndermanMoveBlocks(this.getConfig().getBoolean(configRegion + ".allow_enderman_move_blocks"));
						pr.setAllowPotionEffects(this.getConfig().getBoolean(configRegion + ".allow_potion_effects"));
						pr.setAllowChestInteraction(this.getConfig().getBoolean(configRegion + ".allow_chest_interaction"));
						pr.setAllowHangingItems(this.getConfig().getBoolean(configRegion + ".allow_hanging_items"));
						pr.setAllowPVP(this.getConfig().getBoolean(configRegion + ".allow_pvp"));
						pr.setAllowItemInteraction(this.getConfig().getBoolean(configRegion + ".allow_item_interaction"));
						pr.setAllowPlantGrowth(this.getConfig().getBoolean(configRegion + ".allow_plant_growth"));
						pr.setAllowPlantSpread(this.getConfig().getBoolean(configRegion + ".allow_plant_spread"));
						pr.setAllowFireSpread(this.getConfig().getBoolean(configRegion + ".allow_fire_spread"));
						pr.setAllowPlayerSleep(this.getConfig().getBoolean(configRegion + ".allow_player_sleep"));
						pr.setAllowPlayerPickupItems(this.getConfig().getBoolean(configRegion + ".allow_player_pickup_items", true)); // Default true
						pr.setAllowBlockChangesByEntities(this.getConfig().getBoolean(configRegion + ".allow_block_changes_by_entities"));
						pr.setAllowLeafDecay(this.getConfig().getBoolean(configRegion + ".allow_leaf_decay"));
						pr.setAllowBoneMealUsage(this.getConfig().getBoolean(configRegion + ".allow_bone_meal_usage"));
						pr.setOverrideChestUsage(this.getConfig().getBoolean(configRegion + ".override_chest_usage"));
                        pr.setAllowEnderPearlIn(this.getConfig().getBoolean(configRegion + ".allow_ender_pearl_in"));
                        pr.setAllowFireIgniteByPlayer(this.getConfig().getBoolean(configRegion + ".allow_fire_ignite_by_player"));
						pr.setAllowPlantTrampling(this.getConfig().getBoolean(configRegion + ".allow_plant_trampling"));
						pr.setDisallowedPlaceBlocks(this.getConfig().getString(configRegion + ".disallowed_place_blocks"));
						pr.setDisallowedBreakBlocks(this.getConfig().getString(configRegion + ".disallowed_break_blocks"));

						this.getLogger().info("Adding region " + region + " to protected regions.");

						this.addProtectedRegion(pr);
					} else {
						this.getLogger().info("Missing some info required for " + configRegion);
					}
				} else {
					this.getLogger().info("Missing region " + region + " in gguard.");
				}
			}
		}
	}

	public void load() {

		// Save default config if doesn't exist
		this.saveDefaultConfig();

		GGuard.VERBOSE = this.getConfig().getBoolean("gguard.verbose");

		// Load config regions
		this.getProtectedRegionsFromConfig();

		// Load polygon regions
		this.loadPolygonRegions();
	}

	public void unload() {
		for (ProtectedRegion pr : this.protectedRegions) {
			this.deleteProtectedRegion(pr);
		}

	}

	public static GGuard getInstance() {
		return GGuard.plugin;
	}

	public List<PolygonRegion> getPolygonRegions() {
		return this.polygonRegions;
	}

	public List<ProtectedRegion> getProtectedRegions() {
		return this.protectedRegions;
	}

	public CheckLocationsTask getLocationTask() {
		return this.locationTask;
	}

}