package net.kohi.vaultbattle.listener;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Blaze || event.getEntity() instanceof Creeper) {
            for (ItemStack item : event.getDrops()) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
            }
        }
    }
}
