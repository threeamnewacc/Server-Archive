package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.arenas.BuildUHCArena;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.state.MatchState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CustomListener implements Listener {

	@EventHandler
	public void onPlayerDrinkSoupEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.customRuleSet)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
					if (!player.isDead()) {
						if (player.getHealth() < 20) {
							if (player.getHealth() + 7 < 20) {
								player.setHealth(player.getHealth() + 7);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							} else {
								player.setHealth(20);
								player.getItemInHand().setType(Material.BOWL);
								player.getItemInHand().setItemMeta(null);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());

		// Don't let them take liquids that were already in the map
		if (!ArenaManager.containsLiquidBlock(block)) {
			event.setCancelled(true);

			event.getPlayer().sendMessage(ChatColor.RED + "You can't take that!");
		} else if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.customRuleSet)) {
			if (!KitRuleSet.buildUHCRuleSet.canBreakBlock(block)) return;

			event.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerUseBucketEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getType() == Material.BUCKET) {

					if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.customRuleSet)) {
						if (!KitRuleSet.buildUHCRuleSet.canBreakBlock(event.getClickedBlock())) return;

						event.setUseInteractedBlock(Event.Result.ALLOW);
						event.setCancelled(false);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.customRuleSet)) {
			if (!KitRuleSet.buildUHCRuleSet.canBreakBlock(event.getBlock())) return;

			// Cancel if block is not whitelisted or if it's the map's obsidian
			if (!KitRuleSet.buildUHCRuleSet.whitelistedBlocks.contains(event.getBlock().getType())
					|| (event.getBlock().getType() == Material.OBSIDIAN
					&& !MatchState.getPlayerMatch(player).getArena().containsBlockPlaced(event.getBlock()))
					&& !MatchState.getPlayerMatch(player).getArena().containsBlockRemoved(event.getBlock())) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You're not allowed to break this block!");
			} else {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.customRuleSet)) {
			if (!KitRuleSet.buildUHCRuleSet.canBreakBlock(event.getBlock())) return;

			event.setCancelled(false);

			Match game = MatchState.getPlayerMatch(player);
			if (game.getArena() instanceof BuildUHCArena) {
				int max = ((BuildUHCArena) game.getArena()).getMaxBlockYLevel(event.getBlock().getLocation());
				if (event.getBlock().getY() > max) {
					event.setCancelled(true);

					// Prevent block glitching
					if (player.getLocation().getY() > max + 2) {
						Location gotchaBitch = player.getLocation();
						gotchaBitch.setY(max + 1);

						// Temporary fix for broken area scans
						if (gotchaBitch.getBlock().getRelative(0, 1, 0).isEmpty()) {
							player.teleport(gotchaBitch);
						}
						player.sendMessage(ChatColor.RED + "Block glitching is not allowed!");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.customRuleSet)) {
			// Check whichever block they click on for this
			if (!KitRuleSet.buildUHCRuleSet.canBreakBlock(event.getBlockClicked())) return;

			event.setCancelled(false);

			Match game = MatchState.getPlayerMatch(player);
			if (game.getArena() instanceof BuildUHCArena) {
				Block block = event.getBlockClicked().getRelative(event.getBlockFace());
				int max = ((BuildUHCArena) game.getArena()).getMaxBlockYLevel(block.getLocation());
				if (block.getY() > max) {
					event.setCancelled(true);

					// Prevent water bucket and lava bucket glitching
					if (event.getBucket() == Material.WATER_BUCKET || event.getBucket() == Material.LAVA_BUCKET) {
						if (player.getLocation().getY() > max + 2) {
							Location gotchaBitch = player.getLocation();
							gotchaBitch.setY(max + 1);

							// Temporary fix for broken area scans
							if (gotchaBitch.getBlock().getRelative(0, 1, 0).isEmpty()) {
								player.teleport(gotchaBitch);
							}
							player.sendMessage(ChatColor.RED + "Water and lava glitching is not allowed!");
						}
					}
				}
			}
		}
	}

}
