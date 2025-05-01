package net.badlion.gedit.listeners;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.wands.SelectionManager;
import net.badlion.gedit.wands.WandSelection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class AxeListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_AXE) {

            if (!player.hasPermission("badlion.gedit")) {
                return;
            }

            WandSelection selection = SelectionManager.getSelection(player);
            event.setCancelled(true);

            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK:
                    selection.setPoint2(event.getClickedBlock().getLocation());
                    player.sendMessage(GEdit.PREFIX + ChatColor.YELLOW + "Point 2 set at " + SelectionManager.toStringLocation(event.getClickedBlock().getLocation()));
                    break;
                case LEFT_CLICK_BLOCK:
                    selection.setPoint1(event.getClickedBlock().getLocation());
                    player.sendMessage(GEdit.PREFIX + ChatColor.YELLOW + "Point 1 set at " + SelectionManager.toStringLocation(event.getClickedBlock().getLocation()));
                    break;
            }
        }
    }

}
