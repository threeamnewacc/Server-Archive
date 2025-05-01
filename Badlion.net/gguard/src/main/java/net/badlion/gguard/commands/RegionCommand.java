package net.badlion.gguard.commands;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.PolygonRegion;
import net.badlion.gguard.ProtectedRegion;
import net.badlion.gguard.UnsafeLocation;
import net.badlion.gguard.util.Vector2D;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionCommand implements CommandExecutor {

	private final Map<UUID, List<Vector2D>> polyRegions = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 0) {
			return false;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length < 8) {
				return false;
			}
			String regionName = args[1];
			String worldName = args[2];
			double x1, y1, z1, x2, y2, z2;
			try {
				x1 = Double.parseDouble(args[3]);
				y1 = Double.parseDouble(args[4]);
				z1 = Double.parseDouble(args[5]);
				x2 = Double.parseDouble(args[6]);
				y2 = Double.parseDouble(args[7]);
				z2 = Double.parseDouble(args[8]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Error: invalid coordinates");
				return false;
			}
			UnsafeLocation location1 = new UnsafeLocation(worldName, x1, y1, z1);
			UnsafeLocation location2 = new UnsafeLocation(worldName, x2, y2, z2);

			ProtectedRegion toAdd = new ProtectedRegion(regionName, location1, location2);

			List<String> regions = GGuard.getInstance().getConfig().getStringList("gguard.active_regions");
			regions.add(regionName);
			GGuard.getInstance().getConfig().set("gguard.active_regions", regions);

			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName);

			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".world");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".world", toAdd.getLocation1().getWorldName());

			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".min_x");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".min_x", toAdd.getLocation1().getX());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".min_y");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".min_y", toAdd.getLocation1().getY());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".min_z");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".min_z", toAdd.getLocation1().getZ());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".max_x");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".max_x", toAdd.getLocation2().getX());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".max_y");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".max_y", toAdd.getLocation2().getY());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".max_z");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".max_z", toAdd.getLocation2().getZ());

			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_broken_blocks");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_broken_blocks", toAdd.isAllowBrokenBlocks());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_placed_blocks");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_placed_blocks", toAdd.isAllowPlacedBlocks());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_fire");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_fire", toAdd.isAllowFire());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_ice_melt");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_ice_melt", toAdd.isAllowIceMelt());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_tnt_explosions");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_tnt_explosions", toAdd.isAllowTNTExplosions());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_tnt_block_damage");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_tnt_block_damage", toAdd.isAllowTNTBlockDamage());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_creeper_explosions");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_creeper_explosions", toAdd.isAllowCreeperExplosions());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_creeper_block_damage");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_creeper_block_damage", toAdd.isAllowCreeperBlockDamage());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_piston_usage");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_piston_usage", toAdd.isAllowPistonUsage());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_block_movement");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_block_movement", toAdd.isAllowBlockMovement());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_bucket_placement");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_bucket_placement", toAdd.isAllowedBucketPlacements());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".change_mob_damage_to_player");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".change_mob_damage_to_player", toAdd.isChangeMobDamageToPlayer());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".damage_multiplier");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".damage_multiplier", toAdd.getDamageMultiplier());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_creature_spawn");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_creature_spawn", toAdd.isAllowCreatureSpawn());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".heal_players");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".heal_players", toAdd.isHealPlayers());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".feed_players");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".feed_players", toAdd.isFeedPlayers());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_ender_pearls");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_ender_pearls", toAdd.isAllowEnderPearls());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_mob_damage");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_mob_damage", toAdd.isAllowMobDamage());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_enderman_move_blocks");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_enderman_move_blocks", toAdd.isAllowEndermanMoveBlocks());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_potion_effects");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_potion_effects", toAdd.isAllowPotionEffects());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_chest_interaction");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_chest_interaction", toAdd.isAllowChestInteraction());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_hanging_items");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_hanging_items", toAdd.isAllowHangingItems());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_pvp");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_pvp", toAdd.isAllowPVP());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_item_interaction");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_item_interaction", toAdd.isAllowItemInteraction());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_block_changes_by_entities");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_block_changes_by_entities", toAdd.isAllowBlockChangesByEntities());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_plant_growth");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_plant_growth", toAdd.isAllowPlantGrowth());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_plant_spread");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_plant_spread", toAdd.isAllowPlantSpread());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_fire_spread");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_fire_spread", toAdd.isAllowFireSpread());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_player_sleep");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_player_sleep", toAdd.isAllowPlayerSleep());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_player_pickup_items");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_player_pickup_items", toAdd.isAllowPlayerPickupItems());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_leaf_decay");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_leaf_decay", toAdd.isAllowLeafDecay());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_bone_meal_usage");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_bone_meal_usage", toAdd.isAllowBoneMealUsage());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".override_chest_usage");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".override_chest_usage", toAdd.isOverrideChestUsage());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_ender_pearl_in");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_ender_pearl_in", toAdd.isAllowEnderPearlIn());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".allow_fire_ignite_by_player");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".allow_fire_ignite_by_player", toAdd.isAllowFireIgniteByPlayer());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".disallowed_place_blocks");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".disallowed_place_blocks", toAdd.getDisallowedPlaceBlocks());
			GGuard.getInstance().getConfig().createSection("gguard.regions." + regionName + ".disallowed_break_blocks");
			GGuard.getInstance().getConfig().set("gguard.regions." + regionName + ".disallowed_break_blocks", toAdd.getDisallowedBreakBlocks());

			GGuard.getInstance().saveConfig();

			sender.sendMessage(ChatColor.RED + "Successfully creates a new region " + regionName);

			return true;
		} else if (args[0].equalsIgnoreCase("flag")) {
			if (args.length < 4) {
				return false;
			}

			String regionName = args[1];
			String flag = args[2].toLowerCase();
			String option = args[3];
			String configRegion = "gguard.regions." + regionName;

			if (GGuard.getInstance().getConfig().contains(configRegion + "." + flag)) {
				if (!flag.equals("damage_multiplier") &&
						!flag.equals("world") &&
						!flag.startsWith("min_") &&
						!flag.startsWith("max_")) {
					GGuard.getInstance().getConfig().set(configRegion + "." + flag, option.equalsIgnoreCase("true"));
					sender.sendMessage(ChatColor.RED + "Successfully changed " + flag + " to " + option.equalsIgnoreCase("true") + " for region " + regionName);
				} else {
					GGuard.getInstance().getConfig().set(configRegion + "." + flag, option); // Will this work without parsing the double?
					sender.sendMessage(ChatColor.RED + "Successfully changed " + flag + " to " + option + " for region " + regionName);
				}
				GGuard.getInstance().saveConfig();
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Error: unable to locate flag path " + configRegion + "." + flag);
			}

			return false;
		}/* TODO: This command does not work...
		 else if (args[0].equalsIgnoreCase("reload")) {

            GGuard.getInstance().unload();
            GGuard.getInstance().load();

            sender.sendMessage(ChatColor.RED + "Successfully reloaded config");

            return true;
        }*/ else if (args[0].equalsIgnoreCase("polytemplate")) {
			File file = new File(GGuard.getInstance().getDataFolder() + "/polygonregions", "default.json");

			try (FileWriter writer = new FileWriter(file)) {
				List<Vector2D> points = new ArrayList<>();
				points.add(new Vector2D(100, 100));
				points.add(new Vector2D(150, 150));

				GGuard.GSON.toJson(new PolygonRegion("default", "world", points), writer);
			} catch (Exception ex) {
				GGuard.getInstance().getLogger().warning("Failed to save default.json");
				ex.printStackTrace();
			}
		}

		if (sender instanceof Player) {
			Player player = ((Player) sender);
			if (args[0].equalsIgnoreCase("polytest")) {
				PolygonRegion region = GGuard.getInstance().getPolygonRegion(player.getLocation());

				if (region != null) {
					sender.sendMessage(ChatColor.GREEN + "You are in region " + region.getRegionName());
				} else {
					sender.sendMessage(ChatColor.RED + "You are not in a polygon region");
				}
			} else if (args[0].equalsIgnoreCase("polyadd")) {
				List<Vector2D> points = this.polyRegions.get(player.getUniqueId());

				if (points == null) {
					points = new ArrayList<>();
					this.polyRegions.put(player.getUniqueId(), points);
				}

				points.add(new Vector2D(player.getLocation().getX(), player.getLocation().getZ()));

				sender.sendMessage("Added point");
			} else if (args[0].equalsIgnoreCase("polysave")) {
				File file = new File(GGuard.getInstance().getDataFolder() + "/polygonregions", args[1] + ".json");

				try (FileWriter writer = new FileWriter(file)) {
					List<Vector2D> points = this.polyRegions.remove(player.getUniqueId());

					if (points == null) {
						sender.sendMessage(ChatColor.RED + "Use '/rg polysave' to save points first.");
						return true;
					}

					if (points.size() < 3) {
						sender.sendMessage(ChatColor.RED + "You need at least 3 points.");
						return true;
					}

					GGuard.GSON.toJson(new PolygonRegion(args[1], args[2], points), writer);

					sender.sendMessage("Saved region " + args[1] + " in world " + args[2]);
				} catch (Exception ex) {
					GGuard.getInstance().getLogger().warning("Failed to save " + args[1] + ".json");
					ex.printStackTrace();
				}
			} else if (args[0].equalsIgnoreCase("polyclear")) {
				this.polyRegions.remove(player.getUniqueId());

				sender.sendMessage("Cleared poly region");
			} else if (args[0].equalsIgnoreCase("polylist")) {
				List<Vector2D> points = this.polyRegions.get(player.getUniqueId());

				if (points == null) {
					sender.sendMessage("No points.");
					return true;
				}

				String lul = "Points:\n";

				for (Vector2D point : points) {
					lul += point.getBlockX() + ", " + point.getBlockZ() + "\n";
				}

				player.sendMessage(lul);
			} else if (args[0].equalsIgnoreCase("info")) {
				// Gives info for the regions the player is in
				int amount = 0;
				player.sendMessage(org.bukkit.ChatColor.GREEN + org.bukkit.ChatColor.STRIKETHROUGH.toString() + "---------");
				for (ProtectedRegion region : GGuard.getInstance().getProtectedRegions()) {
					if (region.isLocationInProtectedRegion(new UnsafeLocation(player.getLocation()))) {
						amount++;
						player.sendMessage("Region: " + region.getRegionName());
						player.sendMessage("Loc1: x=" + region.getLocation1().getBlockX() + " y=" + region.getLocation1().getBlockY() + " z=" + region.getLocation1().getBlockZ());
						player.sendMessage("Loc2: x=" + region.getLocation2().getBlockX() + " y=" + region.getLocation2().getBlockY() + " z=" + region.getLocation2().getBlockZ());
						player.sendMessage("PvP Enabled: " + region.isAllowPVP());
						player.sendMessage("Allow Breaking Blocks: " + region.isAllowBrokenBlocks());
						player.sendMessage("Allow Placing Blocks: " + region.isAllowPlacedBlocks());
						player.sendMessage("Allow Ender Pearls: " + region.isAllowEnderPearls());
						player.sendMessage("Allow EnderPearl in: " + region.isAllowEnderPearlIn());
						// TODO: Someone add more info for this command?
						player.sendMessage(org.bukkit.ChatColor.GREEN + org.bukkit.ChatColor.STRIKETHROUGH.toString() + "---------");
					}
				}
				if (amount == 0) {
					player.sendMessage(org.bukkit.ChatColor.RED + "You are not in a region.");
				}
			}
		}

		return false;
	}

}
