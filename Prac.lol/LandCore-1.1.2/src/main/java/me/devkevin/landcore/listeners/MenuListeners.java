package me.devkevin.landcore.listeners;

import me.devkevin.landcore.utils.Menu;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/01/2023 @ 14:15
 * MenuListeners / me.devkevin.landcore.listeners / LandCore
 */
public class MenuListeners implements Listener {

    @EventHandler
    public void onInventoryGrantClickFix(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();

        if (inv.getTitle().equalsIgnoreCase(CC.translate("&aAre you sure?"))
                || inv.getTitle().equalsIgnoreCase(CC.PRIMARY + "Please select a duration")
                || inv.getTitle().equalsIgnoreCase(CC.PRIMARY + "Please select a reason")
                || inv.getTitle().equalsIgnoreCase(CC.PRIMARY + "Please select a rank")

                || inv.getTitle().equalsIgnoreCase(CC.PRIMARY + "Viewing grants..")
                || inv.getTitle().equalsIgnoreCase(CC.PRIMARY + "Please select a player")
                || inv.getTitle().equalsIgnoreCase(CC.PRIMARY + "Rank List")) {
            event.setCancelled(false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu menu = Menu.getByUuid(player.getUniqueId());

        if(!event.isCancelled() && menu != null) {
            event.setCancelled(true);

            if(event.getClickedInventory() != null && event.getClickedInventory().getTitle().equals(menu.getInventory().getTitle())) {
                if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                menu.onClickItem(player, event.getCurrentItem(), event.isRightClick());

                if (menu.isUpdateOnClick() && menu.getPlayer() != null && menu.getInventory() != null) {
                    menu.updateInventory(player);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu menu = Menu.getByUuid(player.getUniqueId());

        if (menu != null) {
            if(event.getInventory().getTitle().equals(menu.getInventory().getTitle())) {
                menu.onClose();
            }

            menu.destroy();
        }
    }
}
