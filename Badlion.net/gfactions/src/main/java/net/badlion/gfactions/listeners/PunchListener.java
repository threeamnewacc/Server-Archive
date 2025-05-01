package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PunchListener implements Listener {

	private GFactions plugin;

	public PunchListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerHitByArrow(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Location location = event.getEntity().getLocation();
			// The target is in the water
			if (location.getBlock().getType() == Material.STATIONARY_WATER || location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.LAVA
					|| location.getBlock().getType() == Material.STATIONARY_LAVA) {
				// Shot by a player
				if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
					// Easy way to cancel the knockback
					Arrow arrow = (Arrow) event.getDamager();
					arrow.setKnockbackStrength(0);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerEnchantPunchTwoBows(EnchantItemEvent event) {
		if (event.getEnchantsToAdd().containsKey(Enchantment.ARROW_KNOCKBACK)) {
			int lvl = event.getEnchantsToAdd().get(Enchantment.ARROW_KNOCKBACK);
			if (lvl >= 2) {
				event.getEnchantsToAdd().put(Enchantment.ARROW_KNOCKBACK, 1);
				event.getEnchanter().sendMessage(ChatColor.RED + "Punch 2 is disabled on the server. Punch 1 applied instead.");
			}
		}
	}

	@EventHandler
	public void onPlayerUsePunchTwoBow(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getPlayer().getItemInHand();
			if (item != null && item.getType() == Material.BOW) {
				if (item.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK) >= 2) {
					item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
					event.getPlayer().setItemInHand(item);
					event.getPlayer().sendMessage(ChatColor.RED + "Punch 2 is disabled on the server. Punch 1 applied instead.");
				}
			}
		}
	}

}
