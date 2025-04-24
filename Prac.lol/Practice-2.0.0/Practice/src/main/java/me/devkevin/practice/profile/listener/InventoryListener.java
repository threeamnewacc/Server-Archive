package me.devkevin.practice.profile.listener;

import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;

/**
 * Copyright 15/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class InventoryListener implements Listener {

    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            event.setCancelled(true);
            return;
        }

        if (!clicked.equals(player.getInventory())) {
            if (clicked instanceof CraftingInventory) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
            return;
        }

        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (profile.isFighting()) return;

        if (profile.isInSpawn() || profile.isInQueue()
                || profile.isSpectating()
                || profile.isInParty()
                || profile.isInEvent() && player.getItemInHand() != null && player.getItemInHand().getType() == Material.COMPASS) {
            event.setCancelled(true);
        }
    }
}

