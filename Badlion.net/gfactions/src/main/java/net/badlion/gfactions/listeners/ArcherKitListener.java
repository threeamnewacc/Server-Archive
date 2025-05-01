package net.badlion.gfactions.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArcherKitListener implements Listener {

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
			Player player = ((Player) ((Arrow) event.getDamager()).getShooter());
			PlayerInventory inventory = player.getInventory();

			// Are they wearing full leather?
			if (inventory.getHelmet().getType() == Material.LEATHER_HELMET
					&& inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE
					&& inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS
					&& inventory.getBoots().getType() == Material.LEATHER_BOOTS) {
				// Extra damage
				event.setDamage(event.getDamage() * 2.0D);
			}
		}
	}

	@EventHandler
	public void onPlayerCloseInventoryEvent(InventoryCloseEvent event) {
		if (event.getInventory().getType() == InventoryType.CRAFTING) {
			Player player = ((Player) event.getPlayer());
			PlayerInventory inventory = player.getInventory();

			// Are they wearing full leather?
			if (inventory.getHelmet() != null && inventory.getHelmet().getType() == Material.LEATHER_HELMET
					&& inventory.getChestplate() != null && inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE
					&& inventory.getLeggings() != null && inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS
					&& inventory.getBoots() != null && inventory.getBoots().getType() == Material.LEATHER_BOOTS) {

				// Send message only if they just equipped it
				boolean send = true;
				for (PotionEffect potionEffect : player.getActivePotionEffects()) {
					// Speed 3?
					if (potionEffect.getType().getName().equals(PotionEffectType.SPEED.getName()) && potionEffect.getAmplifier() == 2) {
						send = false;
					}
				}

				if (send) {
					player.sendMessage(ChatColor.GOLD + "Archer kit equipped. (2.0x bow damage)");
				}

				// Speed 3 bonus
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
			} else {
				// Speed potion effect?
				if (player.hasPotionEffect(PotionEffectType.SPEED)) {
					for (PotionEffect potionEffect : player.getActivePotionEffects()) {
						// Speed 3?
						if (potionEffect.getType().getName().equals(PotionEffectType.SPEED.getName()) && potionEffect.getAmplifier() == 2) {
							// Remove the potion effect
							player.removePotionEffect(PotionEffectType.SPEED);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getItem() != null) {
			Material type = event.getItem().getType();
				PlayerInventory inventory = player.getInventory();

				if (type == Material.LEATHER_HELMET) {
					// Are they wearing the three other pieces of leather?
					if (inventory.getChestplate() != null && inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE
							&& inventory.getLeggings() != null && inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS
							&& inventory.getBoots() != null && inventory.getBoots().getType() == Material.LEATHER_BOOTS) {
						// Speed 3 bonus
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
					}
				} else if (type == Material.LEATHER_CHESTPLATE) {
					// Are they wearing the three other pieces of leather?
					if (inventory.getHelmet() != null && inventory.getHelmet().getType() == Material.LEATHER_HELMET
							&& inventory.getLeggings() != null && inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS
							&& inventory.getBoots() != null && inventory.getBoots().getType() == Material.LEATHER_BOOTS) {
						// Speed 3 bonus
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
					}
				} else if (type == Material.LEATHER_LEGGINGS) {
					// Are they wearing the three other pieces of leather?
					if (inventory.getHelmet() != null && inventory.getHelmet().getType() == Material.LEATHER_HELMET
							&& inventory.getChestplate() != null && inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE
							&& inventory.getBoots() != null && inventory.getBoots().getType() == Material.LEATHER_BOOTS) {
						// Speed 3 bonus
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
					}
				} else if (type == Material.LEATHER_BOOTS) {
					// Are they wearing the three other pieces of leather?
					if (inventory.getHelmet() != null && inventory.getHelmet().getType() == Material.LEATHER_HELMET
							&& inventory.getChestplate() != null && inventory.getChestplate().getType() == Material.LEATHER_CHESTPLATE
							&& inventory.getLeggings() != null && inventory.getLeggings().getType() == Material.LEATHER_LEGGINGS) {
						// Speed 3 bonus
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
					}
				}
		}
	}
}
