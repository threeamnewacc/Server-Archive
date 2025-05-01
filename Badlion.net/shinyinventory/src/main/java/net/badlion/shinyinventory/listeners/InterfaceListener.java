package net.badlion.shinyinventory.listeners;

import net.badlion.shinyinventory.ShinyInventory;
import net.badlion.shinyinventory.events.InterfaceOpenEvent;
import net.badlion.shinyinventory.gui.Interface;
import net.badlion.shinyinventory.gui.InterfaceManager;
import net.badlion.shinyinventory.gui.buttons.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by ShinyDialga45 on 6/20/2015.
 */
public class InterfaceListener implements Listener {

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        Interface gui = InterfaceManager.getInterface(event.getView());
        Player player = ((Player) event.getWhoClicked());
        try {
            Interface playergui = InterfaceManager.getInterface(player.getInventory());
            if (playergui != null) {
                for (Button button : InterfaceManager.getButtons(playergui, event.getSlot())) {
                    if (button != null) {
                        event.setCancelled(true);
                        button.function(player);
                        return;
                    }
                }
            }
        } catch (Exception e) {

        }
        if (gui != null) {
            event.setCancelled(true);
            for (Button button : InterfaceManager.getButtons(gui, event.getRawSlot())) {
                if (button != null) {
                    button.function(player);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Interface gui = InterfaceManager.getInterface(event.getInventory().getHolder());
        if (gui != null) {
            ShinyInventory.getInstance().callEvent(new InterfaceOpenEvent(gui, (Player) event.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Interface gui = InterfaceManager.getInterface(event.getPlayer().getInventory());
            if (gui != null) {
                Button button = InterfaceManager.getButton(gui, event.getPlayer().getItemInHand());
                if (button != null) {
                    event.setCancelled(true);
                    button.function(event.getPlayer());
                }
            }
        }
    }

}
