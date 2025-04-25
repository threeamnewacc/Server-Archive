package us.zonix.hcfactions.mode;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class ModeListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        for (Mode mode : Mode.getModes()) {
            if (mode.isSOTWActive()) {
                event.setDamage(0.0);
                event.setCancelled(true);
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation(), Effect.LAVA_POP, 5, 5);
            }
        }
    }

    @EventHandler
    public void onFoodChangeEvent(FoodLevelChangeEvent event) {
        for (Mode mode : Mode.getModes()) {
            if (mode.isSOTWActive()) {
                event.setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }

}
