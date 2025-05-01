package net.badlion.smellyshops.listeners;

import net.badlion.smellyshops.Shop;
import net.badlion.smellyshops.SmellyShops;
import net.badlion.smellyshops.commands.SellAllCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SellAllInventoryListener implements Listener {

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		if (event.getInventory().getName().equals(ChatColor.AQUA + ChatColor.BOLD.toString()
				+ "Insert items to sell:")) { // Sell all inventory
			if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Sell Items")) {
					HashMap<Location, Integer> totalSellableItems = new HashMap<Location, Integer>();
					HashMap<ItemStack, Integer> totalUnsellableItems = new HashMap<ItemStack, Integer>();
					for (int x = 0; x < 54; x++) {
						ItemStack item = event.getInventory().getItem(x);
						if (item != null) {
							// Is it one of our wools?
							if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
									(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Sell Items")
											|| item.getItemMeta().getDisplayName().equals(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel"))) {
								continue;
							}

							int amount = item.getAmount();

							// Set amount to 1 so we can find it in our cache
							item.setAmount(1);

							// Is the item sellable?
                            boolean found = false;
							for (Shop shop : SmellyShops.getInstance().getShops().values()) {
                                // Sell prices
                                if (shop.getBuy().equals("buy")) {
                                    continue;
                                }

								if (item.getType().equals(shop.getItem().getType()) && item.getDurability() == shop.getItem().getDurability()) {
									Integer current = totalSellableItems.get(shop.getLocation());
									if (current != null) {
										totalSellableItems.put(shop.getLocation(), current + amount);
                                        found = true;
									} else {
										totalSellableItems.put(shop.getLocation(), amount);
                                        found = true;
									}
									break;
								}
							}

                            // Couldn't find this item, add it to return
                            if (!found) {
                                Integer current = totalUnsellableItems.get(item);
                                if (current != null) {
                                    totalUnsellableItems.put(item, current + amount);
                                } else {
                                    totalUnsellableItems.put(item, amount);
                                }
                            }
						}
					}

					// Calculate the price of the sellable items and how much the player can sell
					int totalPrice = 0;
					for (Location loc : totalSellableItems.keySet()) {
						int amount = totalSellableItems.get(loc);
						Shop shop = SmellyShops.getInstance().getShops().get(loc);
						ItemStack item = shop.getItem().clone();
						item.setAmount(1);

						if (amount < shop.getAmount()) {
							continue;
						} else if (amount == shop.getAmount()) {
							// Don't return any of this item since we sold all of it
							totalUnsellableItems.remove(item);

							// Sell the item
							SmellyShops.getInstance().getArchMoney().changeBalance(player.getUniqueId().toString(), shop.getPrice(),
									"Sold " + shop.getAmount() + " " + shop.getItem().getType().toString().toLowerCase());

							totalPrice += shop.getPrice();
						} else {
							int bunchesCanSell = (int) Math.floor((double) amount / shop.getAmount());
							for (int x = 0; x < bunchesCanSell; x++) {
								// Sell the item
								SmellyShops.getInstance().getArchMoney().changeBalance(player.getUniqueId().toString(), shop.getPrice(),
										"Sold " + shop.getAmount() + " " + shop.getItem().getType().toString().toLowerCase());

								totalPrice += shop.getPrice();
							}

							int itemsLeft = amount % shop.getAmount();
							totalUnsellableItems.put(item, itemsLeft);
						}
					}

					for (ItemStack item : totalUnsellableItems.keySet()) {
                        // Don't stack shit...
                        int amount = totalUnsellableItems.get(item);
                        for (int i = amount; i > 0; i -= item.getMaxStackSize()) {
                            ItemStack clone = item.clone();
                            clone.setAmount(i % item.getMaxStackSize());
                            player.getInventory().addItem(clone);
                        }

						player.getInventory().addItem(item);
					}

					/*for (ItemStack item : totalUnsellableItems.keySet()) {
						System.out.println(item + " " + totalUnsellableItems.get(item));
					}

					for (Location loc : totalSellableItems.keySet()) {
						System.out.println("S: " + loc + " " + totalSellableItems.get(loc));
					}*/

					player.sendMessage(ChatColor.YELLOW + "You have sold all sellable items in the chest for $" + totalPrice + ".");

					SellAllCommand.sellAllPlayers.remove(player);

					// Close inventory and cancel event
					event.setCancelled(true);
					SmellyShops.getInstance().getServer().getScheduler().runTaskLater(SmellyShops.getInstance(), new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
						}
					}, 1L);
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel")) {
					// Return items
					for (ItemStack item : event.getInventory().getContents()) {
						if (item != null) {
							// Is it one of our wools?
							if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
									(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Sell Items")
											|| item.getItemMeta().getDisplayName().equals(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel"))) {
								continue;
							}

							if (player.getInventory().firstEmpty() != -1) {
								player.getInventory().addItem(item);
							} else {
								player.getWorld().dropItemNaturally(player.getLocation(), item);
							}
						}
					}

					player.sendMessage(ChatColor.YELLOW + "You have cancelled the transaction, your items have been returned to your inventory " +
							"or dropped if you had no inventory space.");

					SellAllCommand.sellAllPlayers.remove(player);

					// Close inventory and cancel event
					event.setCancelled(true);
					SmellyShops.getInstance().getServer().getScheduler().runTaskLater(SmellyShops.getInstance(), new Runnable() {
						@Override
						public void run() {
							player.closeInventory();
						}
					}, 1L);
				}
			}
		}
	}

	@EventHandler
	public void inventoryCloseEvent(final InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (SellAllCommand.sellAllPlayers.remove(player) && event.getInventory().getName().equals(ChatColor.AQUA + ChatColor.BOLD.toString()
				+ "Insert items to sell:")) { // Sell all inventory
			// Return items
			for (ItemStack item : event.getInventory().getContents()) {
				if (item != null) {
					// Is it one of our wools?
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
							(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Sell Items")
									|| item.getItemMeta().getDisplayName().equals(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel"))) {
						continue;
					}

					if (player.getInventory().firstEmpty() != -1) {
						player.getInventory().addItem(item);
					} else {
						player.getWorld().dropItemNaturally(player.getLocation(), item);
					}
				}
			}

			player.sendMessage(ChatColor.YELLOW + "You have cancelled the transaction, your items have been returned to your inventory " +
					"or dropped if you had no inventory space.");
		}
	}

}
