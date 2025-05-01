package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.event.KitLoadEvent;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.arenas.BuildUHCArena;
import net.badlion.arenapvp.manager.ArenaManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.GCheatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComboBuildUHCListener implements Listener {

	private List<Material> whitelistedBlocks = new ArrayList<>();

	@EventHandler(priority = EventPriority.FIRST)
	public void onTypeC(GCheatEvent event) {

		if (MatchState.playerIsInMatchAndUsingRuleSet(event.getPlayer(), KitRuleSet.comboBuildUHCRuleSet)) {
			if (event.getType() == GCheatEvent.Type.KILL_AURA && event.getMsg().contains("Type C")) {
				int lvl = Integer.parseInt(event.getMsg().substring(event.getMsg().length() - 1));
				if (lvl == 2 || lvl == 3) {
					event.setCancelled(true);
				}
			}
		}
	}


	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (KitRuleSet.comboBuildUHCRuleSet == event.getKitRuleSet()) {
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
		if (event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			if (MatchState.playerIsInMatchAndUsingRuleSet(damaged, KitRuleSet.comboBuildUHCRuleSet)) {
				if (event.getDamager() instanceof Player) {
					Player damager = (Player) event.getDamager();
					// Repair sword
					int slot = damager.getInventory().first(Material.GOLD_SWORD);
					if (slot != -1) {
						damager.getInventory().getItem(slot).setDurability((short) 1);
					}

					// Did they hit with fishing rod?
					if (damager.getItemInHand().getType() == Material.FISHING_ROD) {
						// Reduce durability
						short maxDurability = damager.getItemInHand().getType().getMaxDurability();
						short newDurability = (short) (damager.getItemInHand().getDurability() + 4);

						if (newDurability > maxDurability) {
							// Break
							damager.setItemInHand(new ItemStack(Material.AIR));
							damager.playSound(damager.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.0f, 1.0f);
						} else {
							damager.getItemInHand().setDurability(newDurability);
						}
					}

					damager.updateInventory();
				} else if (event.getDamager() instanceof Arrow) {
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
	}

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.comboBuildUHCRuleSet)) {
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
		} else if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.comboBuildUHCRuleSet)) {
			if (!KitRuleSet.comboBuildUHCRuleSet.canBreakBlock(block)) return;

			event.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerUseBucketEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getType() == Material.BUCKET) {

					if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.comboBuildUHCRuleSet)) {
						if (!KitRuleSet.comboBuildUHCRuleSet.canBreakBlock(event.getClickedBlock())) return;

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

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.comboBuildUHCRuleSet)) {
			if (!KitRuleSet.comboBuildUHCRuleSet.canBreakBlock(event.getBlock())) return;
			Match match = MatchState.getPlayerMatch(player);
			// Cancel if block is not whitelisted or if it's the map's obsidian
			if (!this.whitelistedBlocks.contains(event.getBlock().getType())
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

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.comboBuildUHCRuleSet)) {
			if (!KitRuleSet.comboBuildUHCRuleSet.canBreakBlock(event.getBlock())) return;

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
						player.sendFormattedMessage("{0}Block glitching is not allowed!", ChatColor.RED);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.comboBuildUHCRuleSet)) {
			// Check whichever block they click on for this
			if (!KitRuleSet.comboBuildUHCRuleSet.canBreakBlock(event.getBlockClicked())) return;

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

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
	    /*protected double knockbackFriction = 1.9;
        protected double knockbackHorizontal = 0.50;
		protected double knockbackVertical = 0.34;
		protected double knockbackVerticalLimit = 0.4;
		protected double knockbackExtraHorizontal = 0.6;
		protected double knockbackExtraVertical = 0.125;


		/*this.knockbackFriction = 1.9;
		this.knockbackHorizontal = 0.50;
		this.knockbackVertical = 0.34;
		this.knockbackVerticalLimit = 0.4;
		this.knockbackExtraHorizontal = 0.6;
		this.knockbackExtraVertical = 0.125;

		Player player = ((Player) event.getPlayer());
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			for (Player pl : GameState.getGroupGame(group).getPlayers()) {
				this.applyKnockbackToPlayer(pl);
			}
		}*/
	}


}
