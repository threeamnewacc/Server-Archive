package net.badlion.survivalgames.gamemodes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class UHCGameMode implements GameMode, Listener {

    /**
     * Get a Tier 1 Item
     */
    public ItemStack getTier1Item() {
        return null;
    }

    /**
     * Get a Tier 2 Item
     */
    public ItemStack getTier2Item() {
        return null;
    }

    /**
     * Do anything special for a death
     */
    public void handleDeath(Player died) {
        died.getLocation().getWorld().dropItemNaturally(died.getLocation(), new ItemStack(Material.GOLDEN_APPLE));
    }

    @EventHandler
    public void onPlayerNaturallyRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Return name
     */
    public String name() {
        return "UHC";
    }

}
