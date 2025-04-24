package me.devkevin.practice.kit.listener;

import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;

/**
 * Copyright 18/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class KitEditorError32Listener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

            if (profile.isEditing()) {
                if (event.getClickedInventory() != null && event.getClickedInventory() instanceof CraftingInventory) {
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                }
            } else if (profile.isFighting()) {
                if (event.getClickedInventory() != null && event.getClickedInventory() instanceof CraftingInventory) {
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
