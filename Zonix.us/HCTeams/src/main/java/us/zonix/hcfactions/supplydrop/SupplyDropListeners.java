package us.zonix.hcfactions.supplydrop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SupplyDropListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack != null && itemStack.getType() == Material.FIREWORK && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            new SupplyDrop(event.getClickedBlock().getLocation());
        }

    }

}
