package net.badlion.uhcmeetup.listeners;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.uhcmeetup.UHCMeetup;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
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
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlivePlayerListener implements Listener {

	public List<Material> whitelistedBlocks = new ArrayList<>();

	public AlivePlayerListener() {
		// Add all whitelisted blocks
		this.whitelistedBlocks.add(Material.LOG);
		this.whitelistedBlocks.add(Material.LOG_2);
		this.whitelistedBlocks.add(Material.WOOD);
		this.whitelistedBlocks.add(Material.LEAVES);
		this.whitelistedBlocks.add(Material.LEAVES_2);
		this.whitelistedBlocks.add(Material.WATER);
		this.whitelistedBlocks.add(Material.STATIONARY_WATER);
		this.whitelistedBlocks.add(Material.LAVA);
		this.whitelistedBlocks.add(Material.STATIONARY_LAVA);
		this.whitelistedBlocks.add(Material.LONG_GRASS);
		this.whitelistedBlocks.add(Material.YELLOW_FLOWER);
		this.whitelistedBlocks.add(Material.COBBLESTONE);
		this.whitelistedBlocks.add(Material.CACTUS);
		this.whitelistedBlocks.add(Material.SUGAR_CANE_BLOCK);
		this.whitelistedBlocks.add(Material.DOUBLE_PLANT);
		this.whitelistedBlocks.add(Material.OBSIDIAN);
		this.whitelistedBlocks.add(Material.SNOW);
		this.whitelistedBlocks.add(Material.YELLOW_FLOWER);
		this.whitelistedBlocks.add(Material.RED_ROSE);
		this.whitelistedBlocks.add(Material.BROWN_MUSHROOM);
		this.whitelistedBlocks.add(Material.RED_MUSHROOM);
		this.whitelistedBlocks.add(Material.HUGE_MUSHROOM_1);
		this.whitelistedBlocks.add(Material.HUGE_MUSHROOM_2);
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerItemsDroppedFromDeathEvent(PlayerItemsDroppedFromDeathEvent event) {
		// Don't drop the load kit items
		for (Item item : event.getItemsDroppedOnDeath()) {
			Material type = item.getItemStack().getType();
			if (type == Material.ENCHANTED_BOOK) {
				item.remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onCombatTagDropInventoryEvent(CombatTagDropInventoryEvent event) {
		// Don't drop the load kit items
		for (int i = 0; i < event.getInventory().length; i++) {
			ItemStack item = event.getInventory()[i];
			if (item != null && item.getType() == Material.ENCHANTED_BOOK) {
				event.getInventory()[i] = null;
			}
		}
	}

	@EventHandler
	public void onPlayerCraftItemEvent(CraftItemEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;

		Player player = (Player) event.getWhoClicked();
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) return;

		event.setCancelled(true);
		event.setResult(Event.Result.DENY);

		player.sendMessage(ChatColor.RED + "Crafting items is not allowed!");
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) return;

		// Disable workbenches
		if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WORKBENCH) {
			event.setCancelled(false);
			event.setUseInteractedBlock(Event.Result.DENY);
			player.sendMessage(ChatColor.RED + "Crafting tables are disabled!");
			return;
		}

		// Kit selection
		if (event.getItem() != null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
			if (event.getItem().getType() == Material.ENCHANTED_BOOK) {
				event.setCancelled(true);

				final int slot = player.getInventory().getHeldItemSlot();

				player.getInventory().setHeldItemSlot(0);

				if (slot == 8) {
					KitCommon.loadDefaultKit(player, KitRuleSet.buildUHCRuleSet, true);
				} else if (slot >= 0 && slot < 5) {
					int kitId = slot;
					if (event.hasItem()) {
						KitType kitType = new KitType(player.getUniqueId().toString(), KitRuleSet.buildUHCRuleSet.getName());
						Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());
						if (kitTypeListMap != null) {
							List<Kit> kits = kitTypeListMap.get(kitType);
							if (kits != null) {
								KitCommon.loadKit(player, KitRuleSet.buildUHCRuleSet, kitId);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (player.isOp()) return;

		if (MPG.getInstance().getMPGGame().getGameState().ordinal() < MPGGame.GameState.GAME_COUNTDOWN.ordinal()) return;

		event.setCancelled(true);

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			// Allow if block is whitelisted
			if (this.whitelistedBlocks.contains(event.getBlock().getType())) {
				event.setCancelled(false);
			} else {
				player.sendMessage(ChatColor.RED + "You're not allowed to break this block!");
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (player.isOp()) return;

		if (MPG.getInstance().getMPGGame().getGameState().ordinal() != MPGGame.GameState.GAME.ordinal()) return;

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			int max = UHCMeetup.getInstance().getUHCMeetupGame().getWorld().getMaxBlockYLevel(event.getBlock().getLocation());
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
					player.sendMessage(ChatColor.RED + "Sky basing is not allowed!");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();

		if (player.isOp()) return;

		if (MPG.getInstance().getMPGGame().getGameState().ordinal() != MPGGame.GameState.GAME.ordinal()) return;

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			// Check whichever block they click on for this
			if (!KitRuleSet.buildUHCRuleSet.canBreakBlock(event.getBlockClicked())) return;

			event.setCancelled(false);

			Block block = event.getBlockClicked().getRelative(event.getBlockFace());
			int max = UHCMeetup.getInstance().getUHCMeetupGame().getWorld().getMaxBlockYLevel(block.getLocation());
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

	@EventHandler
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
		// Cancel natural regeneration
		if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();
			if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
				Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

				if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
					damager.sendMessage(ChatColor.GOLD + damaged.getName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
							Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item.getType().equals(Material.GOLDEN_APPLE)) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
				// Add potion effect
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + 4/*Number of 1/2 hearts to heal*/ * 25, 1), true);
			}
		}
	}

}