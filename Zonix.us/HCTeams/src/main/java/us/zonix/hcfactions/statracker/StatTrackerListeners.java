package us.zonix.hcfactions.statracker;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class StatTrackerListeners implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer != null) {
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    new StatTracker(itemStack, StatTrackerType.ARMOR).add(killer.getDisplayName(), player.getDisplayName());
                }
            }

            if (killer.getItemInHand() != null) {
                ItemStack itemStack = killer.getItemInHand();
                if (itemStack.getType().name().contains("SWORD")) {
                    new StatTracker(itemStack, StatTrackerType.WEAPON).add(killer.getDisplayName(), player.getDisplayName());
                }
            }
        }
    }

}
