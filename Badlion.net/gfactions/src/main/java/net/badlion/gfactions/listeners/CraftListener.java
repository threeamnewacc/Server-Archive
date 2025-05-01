package net.badlion.gfactions.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftListener implements Listener {

    @EventHandler
    public void craftItemEvent(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.sendMessage(ChatColor.RED + "You cannot craft enderchests.");
        } else if (event.getRecipe().getResult().getType() == Material.HOPPER || event.getRecipe().getResult().getType() == Material.HOPPER_MINECART) {
			event.setCancelled(true);
			if (event.getWhoClicked() instanceof Player) {
                    ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You cannot craft hoppers, buy them from spawn.");
			}
		}
    }

}
