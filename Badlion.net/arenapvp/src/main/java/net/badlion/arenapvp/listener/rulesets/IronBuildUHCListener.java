package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.arenas.BuildUHCArena;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class IronBuildUHCListener implements Listener {

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (KitRuleSet.ironBuildUHCRuleSet == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(damaged, KitRuleSet.ironBuildUHCRuleSet)) {
				if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

					if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
						damager.sendMessage(ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
								Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
					}
				}
			}
		}
	}

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironBuildUHCRuleSet)) {
				if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					event.setCancelled(true);
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

			event.getPlayer().sendFormattedMessage("{0}You can''t take that!", ChatColor.RED);
		} else if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironBuildUHCRuleSet)) {
			if (!KitRuleSet.ironBuildUHCRuleSet.canBreakBlock(block)) return;

			event.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerUseBucketEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getType() == Material.BUCKET) {

					if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironBuildUHCRuleSet)) {
						if (!KitRuleSet.ironBuildUHCRuleSet.canBreakBlock(event.getClickedBlock())) return;

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

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironBuildUHCRuleSet)) {
			if (!KitRuleSet.ironBuildUHCRuleSet.canBreakBlock(event.getBlock())) return;

			Match match = MatchState.getPlayerMatch(player);
			// Cancel if block is not whitelisted or if it's the map's obsidian
			if (!KitRuleSet.ironBuildUHCRuleSet.whitelistedBlocks.contains(event.getBlock().getType())
					|| (event.getBlock().getType() == Material.OBSIDIAN
					&& !match.getArena().containsBlockPlaced(event.getBlock()))
					&& !match.getArena().containsBlockRemoved(event.getBlock())) {
				event.setCancelled(true);
				player.sendFormattedMessage("{0}You''re not allowed to break this block!", ChatColor.RED);
			} else {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironBuildUHCRuleSet)) {
			if (!KitRuleSet.ironBuildUHCRuleSet.canBreakBlock(event.getBlock())) return;

			event.setCancelled(false);


			Match match = MatchState.getPlayerMatch(player);
			if (match.getArena() instanceof BuildUHCArena) {
				int max = ((BuildUHCArena) match.getArena()).getMaxBlockYLevel(event.getBlock().getLocation());
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
						player.sendFormattedMessage("{0}Block glitching is not allowed!", ChatColor.RED);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.ironBuildUHCRuleSet)) {
			// Check whichever block they click on for this
			if (!KitRuleSet.ironBuildUHCRuleSet.canBreakBlock(event.getBlockClicked())) return;

			event.setCancelled(false);

			Match game = MatchState.getPlayerMatch(player);
			if (game.getArena() instanceof BuildUHCArena) {
				Block block = event.getBlockClicked().getRelative(event.getBlockFace());
				int max = ((BuildUHCArena) game.getArena()).getMaxBlockYLevel(block.getLocation());
				if (block.getY() > max) {
					event.setCancelled(true);

					// Prevent water bucket glitching
					if (event.getBucket() == Material.WATER_BUCKET) {
						if (player.getLocation().getY() > max + 2) {
							Location gotchaBitch = player.getLocation();
							gotchaBitch.setY(max + 1);

							// Temporary fix for broken area scans
							if (gotchaBitch.getBlock().getRelative(0, 1, 0).isEmpty()) {
								player.teleport(gotchaBitch);
							}
							player.sendFormattedMessage("{0}Water glitching is not allowed!", ChatColor.RED);
						}
					}
				}
			}
		}
	}

}
