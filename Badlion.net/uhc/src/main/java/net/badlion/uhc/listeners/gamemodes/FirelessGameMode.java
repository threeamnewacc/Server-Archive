package net.badlion.uhc.listeners.gamemodes;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FirelessGameMode implements GameMode {

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getEntity().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
				return;
			}

			// Remove fire damage
			if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
					|| event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
				event.setCancelled(true);
			}
		}
	}


	public ItemStack getExplanationItem() {
		ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Fireless");

		List<String> lore = new ArrayList<>();

		lore.add(ChatColor.AQUA + "- No fire damage");
		lore.add(ChatColor.AQUA + "- No lava damage");
		lore.add(ChatColor.AQUA + "- NOTE: Only works in Overworld");

		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

	public String getAuthor() {
		return "Badlion";
	}

	@Override
	public void unregister() {
		EntityDamageEvent.getHandlerList().unregister(this);
	}

}
