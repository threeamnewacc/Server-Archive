package me.devkevin.practice.util.menu;

import me.devkevin.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Elb1to
 * Project: Frost
 * Date: 6/9/2021 @ 10:12 PM
 */
public class MenuListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onButtonPress(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Menu openInventory = Menu.currentlyOpenedMenus.get(player.getName());

		if (openInventory != null) {
			if (event.getSlot() != event.getRawSlot()) {
				if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
					event.setCancelled(true);
				}

				return;
			}

			if (openInventory.getButtons().containsKey(event.getSlot())) {
				Button button = openInventory.getButtons().get(event.getSlot());
				boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());

				if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
					event.setCancelled(true);
					if (event.getCurrentItem() != null) {
						player.getInventory().addItem(event.getCurrentItem());
					}
				} else {
					event.setCancelled(cancel);
				}

				button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());
				if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
					Menu newInventory = Menu.currentlyOpenedMenus.get(player.getName());
					if (newInventory == openInventory) {
						boolean buttonUpdate = button.shouldUpdate(player, event.getSlot(), event.getClick());
						if (buttonUpdate) {
							openInventory.setClosedByMenu(true);
							newInventory.openMenu(player);
						}
					}
				} else if (button.shouldUpdate(player, event.getSlot(), event.getClick())) {
					openInventory.setClosedByMenu(true);
					openInventory.openMenu(player);
				}

				if (event.isCancelled()) {
					Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
				}
			} else {
				if (event.getCurrentItem() != null) {
					event.setCancelled(true);
				}
				if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
					event.setCancelled(true);
				}
			}
		}
	}
}
