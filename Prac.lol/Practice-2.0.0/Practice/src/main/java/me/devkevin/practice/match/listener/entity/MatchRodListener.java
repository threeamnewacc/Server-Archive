package me.devkevin.practice.match.listener.entity;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

/**
 * Copyright 13/06/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchRodListener implements Listener {

    // Makes sure fishing rods don't do any damage to armor.

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        // dirty armor check
        if (!Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(event.getItem())) {
            return;
        }

        // if their last damage cause is by a fishing hook, don't allow any damage.
        if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof FishHook) {
                event.setCancelled(true);
            }
        }
    }
}
