package net.badlion.arenapvp.listener;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.inventory.SpectatorInventory;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SpectatorListener implements Listener {

	// Used for blocking interacts when they first go into spectator
	public static Map<UUID, Long> spectatorWarmup = new HashMap<>();

	private static Map<UUID, Long> spectatorMessageCooldown = new HashMap<>();

	private static Map<UUID, Long> spectatorItemSpamCooldown = new HashMap<>();


	private static final int COMPASS_DISTANCE = 100;


	public static void cleanupCooldownMaps(Player player) {
		if (SpectatorListener.spectatorWarmup.containsKey(player.getUniqueId())) {
			SpectatorListener.spectatorWarmup.remove(player.getUniqueId());
		}
		if (SpectatorListener.spectatorMessageCooldown.containsKey(player.getUniqueId())) {
			SpectatorListener.spectatorMessageCooldown.remove(player.getUniqueId());
		}
		if (SpectatorListener.spectatorItemSpamCooldown.containsKey(player.getUniqueId())) {
			SpectatorListener.spectatorItemSpamCooldown.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.spigot().getCollidesWithEntities()) {
			ItemStack item = event.getItem();

			// Cancel this if not op
			if (!player.isOp()) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Event.Result.DENY);
			}

			// Do this check after we cancel the event
			if (item == null || item.getType().equals(Material.AIR)) return;

			if (spectatorWarmup.containsKey(player.getUniqueId())) {
				Long warmup = spectatorWarmup.get(player.getUniqueId());
				if ((System.currentTimeMillis() - warmup) < 5000) {
					if (spectatorMessageCooldown.containsKey(player.getUniqueId())) {
						Long messageCooldown = spectatorMessageCooldown.get(player.getUniqueId());
						if ((System.currentTimeMillis() - messageCooldown) < 2500) {
							// Don't send msg if they are on cooldown
							return;
						}
					}
					player.sendFormattedMessage("{0}Please wait to use these items.", ChatColor.RED);
					spectatorMessageCooldown.put(player.getUniqueId(), System.currentTimeMillis());
					return;
				}
			}


			if (TeamStateMachine.spectatorState.contains(player)) {

				// Teleport compass since there is no way for us to block gguard
				if (event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
					event.setCancelled(true);
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						Location loc = traceThroughBlocks(center(event.getClickedBlock().getLocation()), player.getLocation().getDirection());

						if (loc != null) {
							loc.setDirection(player.getLocation().getDirection());
							player.teleport(loc);
						} else {
							player.sendFormattedMessage("{0}No free space found", ChatColor.RED);
						}

					} else if (event.getAction() == Action.LEFT_CLICK_AIR) {
						Location loc = traceThroughAir(player.getEyeLocation(), player.getLocation().getDirection());

						if (loc != null) {
							loc.setDirection(player.getLocation().getDirection());
							player.teleport(loc);
						} else {
							player.sendFormattedMessage("{0}No block found in sight", ChatColor.RED);
						}

					} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
						Location loc = traceThroughBlocks(event.getClickedBlock().getLocation(), new Vector(0, 1, 0));
						if (loc != null) {
							loc.setDirection(player.getLocation().getDirection());
							player.teleport(loc);
						}
					}
				}

				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (event.getItem() != null && event.getItem().getType() == Material.WATCH) {
						if (TeamStateMachine.spectatorState.getSpectatorMatch(player) != null) {
							Match match = TeamStateMachine.spectatorState.getSpectatorMatch(player);
							SpectatorInventory.openSelectPlayersInventory(player, match);
							return;
						}
					}


					if (this.spectatorItemSpamCooldown.containsKey(player.getUniqueId())) {
						Long cooldown = this.spectatorItemSpamCooldown.get(player.getUniqueId());
						if ((System.currentTimeMillis() - cooldown) < 3000) {
							player.sendFormattedMessage("{0}Please wait {1} seconds to use this again.", ChatColor.RED, (Math.round(Math.ceil((3000 - (System.currentTimeMillis() - cooldown)) / 1000)) + 1));
							return;
						} else {
							this.spectatorItemSpamCooldown.remove(player.getUniqueId());
						}
					}


					if (player.getInventory().getHeldItemSlot() == 3) {
						// TODO: This needs cooldown
						if (TeamStateMachine.spectatorState.getSpectatorMatch(player) != null) {
							if (TeamStateMachine.spectatorState.isColorArmorEnabled(player)) {
								TeamStateMachine.spectatorState.setColoredArmorDisabled(player);
								player.sendFormattedMessage("{0}Colored team armor is now disabled.", ChatColor.GREEN);
								player.getInventory().setItem(3, SpectatorHelper.getSpectatorLeatherHelmetColorsOff());
							} else {
								TeamStateMachine.spectatorState.setColoredArmorEnabled(player);
								player.sendFormattedMessage("{0}Colored team armor is now enabled.", ChatColor.GREEN);
								player.getInventory().setItem(3, SpectatorHelper.getSpectatorLeatherHelmetColorsOn());
							}
							this.spectatorItemSpamCooldown.put(player.getUniqueId(), System.currentTimeMillis());
							player.updateInventory();
							return;
						}
						return;
					}


					if (player.getInventory().getHeldItemSlot() == 8) {
						player.sendFormattedMessage("{0}Leaving spectator mode.", ChatColor.GREEN);
						this.spectatorItemSpamCooldown.put(player.getUniqueId(), System.currentTimeMillis());
						new BukkitRunnable() {
							@Override
							public void run() {
								try {
									JSONObject data = new JSONObject();
									data.put("uuid", player.getUniqueId().toString());
									data.put("region", Gberry.serverRegion.toString().toLowerCase());
									Gberry.contactMCP("leave-spectator", data);
									Bukkit.getLogger().log(Level.INFO, "LEAVE Spec: " + data);
								} catch (HTTPRequestFailException e) {
									e.printStackTrace();
								}
							}
						}.runTaskAsynchronously(ArenaPvP.getInstance());
					}
				}
			}

		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHorseInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMoveItem(InventoryMoveItemEvent event) {
		if (event.getDestination().getType() == InventoryType.PLAYER) {
			List<HumanEntity> entities = event.getSource().getViewers();
			if (entities.get(0) instanceof Player) {
				Player player = (Player) entities.get(0);
				if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();

			if (player.isOp()) {
				return;
			}

			// Only allow them to pot themselves up and not debuff nearby players
			if (!player.spigot().getCollidesWithEntities()) {
				for (Entity entity : event.getAffectedEntities()) {
					// Allow them to affect themselves.
					if (entity == player) {
						continue;
					}

					event.setIntensity((LivingEntity) entity, 0.0);
				}
			} else {
				// Someone who is not a specator threw a potion down, don't let spectators get affected
				for (LivingEntity entity : event.getAffectedEntities()) {
					if (entity instanceof Player) {
						Player p = (Player) entity;
						if (!p.spigot().getCollidesWithEntities() && !p.isOp()) {
							event.setIntensity(entity, 0D);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void onSpectatorDamage(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Player player = null;
		if (damager instanceof Projectile) {
			player = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
		} else if (damager instanceof Player) {
			player = (Player) damager;
		}

		if (player == null || player.isOp()) {
			return;
		}

		// Fail safety checks
		if (!player.spigot().getCollidesWithEntities() || player.getGameMode() == GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerTakesDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			// If they are invisible cancel damage
			if (!player.spigot().getCollidesWithEntities()) {
				event.setCancelled(true);
			}
		}
	}

	// Compass junk since there is no way to block that in gguard

	private Location traceThroughBlocks(Location loc, Vector direction) {
		for (int i = 0; i < COMPASS_DISTANCE; i++) {
			loc.add(direction);
			// dont go under the world
			if (loc.getY() < 0) {
				return null;
			}
			Block block = loc.getBlock();
			// try to find a free block plus another above it
			if (!isSolid(block) && !isSolid(block.getRelative(BlockFace.UP))) {
				return center(loc);
			}
		}
		return null;
	}

	private Location traceThroughAir(Location loc, Vector direction) {
		for (int i = 0; i < COMPASS_DISTANCE; i++) {
			loc.add(direction);
			// went out of the world?
			if (loc.getY() < 0 || loc.getY() > 255) {
				return null;
			}
			Block block = loc.getBlock();
			if (isSolid(block)) {
				// loc stepped into a solid block, so step back one
				loc.subtract(direction);
				return center(loc);
			}
		}
		return null;
	}

	// return a copy of the location, but in the center of the block
	private Location center(Location loc) {
		return new Location(
				loc.getWorld(),
				loc.getBlockX() + 0.5,
				loc.getBlockY(),
				loc.getBlockZ() + 0.5,
				loc.getYaw(),
				loc.getPitch());
	}

	private boolean isSolid(Block block) {
		// put this in a method incase we need to code exceptions to Material.isSolid
		return block.getType().isSolid();
	}

}
