package net.badlion.ffa.listeners;

import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlListener implements Listener {

	private static EnderPearlListener instance;

	private Map<UUID, Boolean> hasTakenFallDamage = new HashMap<>();

	public EnderPearlListener() {
		EnderPearlListener.instance = this;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		this.hasTakenFallDamage.put(event.getPlayer().getUniqueId(), false);
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		this.hasTakenFallDamage.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		this.hasTakenFallDamage.put(event.getEntity().getUniqueId(), false);
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		if (MPGPlayerManager.getMPGPlayer(event.getPlayer()).getState() != MPGPlayer.PlayerState.PLAYER) return;

		// Did they drop an enderpearl?
		if (event.getItemDrop().getItemStack().getType() == Material.ENDER_PEARL) {
			event.getItemDrop().remove();
		}
	}

	// Priority needs to be higher than the one in AlivePlayerListener
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (MPGPlayerManager.getMPGPlayer(player).getState() != MPGPlayer.PlayerState.PLAYER) return;

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			event.setCancelled(true);
			if (!this.hasTakenFallDamage.get(player.getUniqueId())) {
				this.hasTakenFallDamage.put(player.getUniqueId(), true);

				player.teleport(event.getTo());

				// TODO: FIX FOR NODEBUFF
				// Remove redstone torch spectator item
				player.getInventory().remove(Material.REDSTONE_TORCH_ON);
			}
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player) event.getEntity();

		if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (MPGPlayerManager.getMPGPlayer(event.getEntity().getUniqueId()).getState() != MPGPlayer.PlayerState.PLAYER) return;

			// Check if they haven't taken damage yet since last death
			if (!this.hasTakenFallDamage.get(player.getUniqueId())) {
				this.hasTakenFallDamage.put(player.getUniqueId(), true);

				// Remove their enderpearl from their inventory and clear their crafting screen
				if (player.getOpenInventory() != null) {
					Inventory inventory = player.getOpenInventory().getTopInventory();
					if (inventory != null && inventory.getType() == InventoryType.CRAFTING) {
						inventory.clear();
					}
				}

				// TODO: FIX FOR NODEBUFF
				// Remove redstone torch spectator item
				player.getInventory().remove(Material.REDSTONE_TORCH_ON);

				player.getInventory().remove(Material.ENDER_PEARL);
				player.updateInventory();

				event.setCancelled(true);
			}
		}
	}

	public static EnderPearlListener getInstance() {
		return EnderPearlListener.instance;
	}

	public void setHasTakenFallDamage(UUID uuid, boolean b) {
		this.hasTakenFallDamage.put(uuid, b);
	}

}
