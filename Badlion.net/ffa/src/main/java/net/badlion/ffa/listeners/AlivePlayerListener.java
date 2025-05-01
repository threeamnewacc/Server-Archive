package net.badlion.ffa.listeners;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.ffa.FFA;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class AlivePlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerItemsDroppedFromDeathEvent(PlayerItemsDroppedFromDeathEvent event) {
		// Don't drop the load kit items or spectate item
		for (Item item : event.getItemsDroppedOnDeath()) {
			Material type = item.getItemStack().getType();
			if (type == Material.REDSTONE_TORCH_ON || type == Material.ENCHANTED_BOOK) {
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
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
			Player player = ((Player) event.getEntity().getShooter());
			if (player.getLocation().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

		// Cancel if the pearl landed in spawn (check y level)
		if (event.getTo().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
			event.setCancelled(true);

			// Are they still on the spawn platform? Avoid exploits
			if (player.getLocation().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
				// Add enderpearl back to player's inventory
				player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
				player.updateInventory();
			}
		} else {
			// Add to last damage time to prevent them from doing /spawn right away
			MultiKillListener.getInstance().insertLastDamageTime(player.getUniqueId());

			// Do they still have the kit selection items?
			if (player.getInventory().first(Material.ENCHANTED_BOOK) != -1) {
				FFA.getInstance().loadKitAutomatically(player);
			}
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player player = ((Player) event.getEntity());

		// Cancel damage on the spawn platform
		if (player.getLocation().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) return;

		if (event.getItem() != null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
			if (event.getItem().getType() == Material.ENCHANTED_BOOK) {
				event.setCancelled(true);

				final int slot = player.getInventory().getHeldItemSlot();

				player.getInventory().setHeldItemSlot(0);

				if (slot == 8) {
					KitCommon.loadDefaultKit(player, FFA.FFA_KITRULESET, true);
				} else if (slot >= 0 && slot < 5) {
					int kitId = slot;
					if (event.hasItem()) {
						KitType kitType = new KitType(player.getUniqueId().toString(), FFA.FFA_KITRULESET.getName());
						Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());
						if (kitTypeListMap != null) {
							List<Kit> kits = kitTypeListMap.get(kitType);
							if (kits != null) {
								KitCommon.loadKit(player, FFA.FFA_KITRULESET, kitId);
							}
						}
					}
				}
			} else if (event.getItem().getType() == Material.REDSTONE_TORCH_ON) {
				event.setCancelled(true);

				// Change player's state to SPECTATOR
				mpgPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) return;

		// Don't let players drop items on spawn platform
		if (player.getLocation().getY() > FFA.getInstance().getFFAGame().getWorld().getSpawnPlatformYLimit()) {
			event.setCancelled(true);
		}
	}

}