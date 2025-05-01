package net.badlion.smellyshops.listeners;

import net.badlion.smellyshops.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {

	private ArrayList<Material> repairableMaterials;

    public PlayerListener() {
	    // Initialize tool materials list
	    this.repairableMaterials = new ArrayList<Material>();
	    this.repairableMaterials.add(Material.WOOD_SWORD);
	    this.repairableMaterials.add(Material.WOOD_SPADE);
	    this.repairableMaterials.add(Material.WOOD_PICKAXE);
	    this.repairableMaterials.add(Material.WOOD_AXE);
	    this.repairableMaterials.add(Material.WOOD_HOE);
	    this.repairableMaterials.add(Material.LEATHER_HELMET);
	    this.repairableMaterials.add(Material.LEATHER_CHESTPLATE);
	    this.repairableMaterials.add(Material.LEATHER_LEGGINGS);
	    this.repairableMaterials.add(Material.LEATHER_BOOTS);
	    this.repairableMaterials.add(Material.STONE_SWORD);
	    this.repairableMaterials.add(Material.STONE_SPADE);
	    this.repairableMaterials.add(Material.STONE_PICKAXE);
	    this.repairableMaterials.add(Material.STONE_AXE);
	    this.repairableMaterials.add(Material.STONE_HOE);
	    this.repairableMaterials.add(Material.CHAINMAIL_HELMET);
	    this.repairableMaterials.add(Material.CHAINMAIL_CHESTPLATE);
	    this.repairableMaterials.add(Material.CHAINMAIL_LEGGINGS);
	    this.repairableMaterials.add(Material.CHAINMAIL_BOOTS);
	    this.repairableMaterials.add(Material.GOLD_SWORD);
	    this.repairableMaterials.add(Material.GOLD_SPADE);
	    this.repairableMaterials.add(Material.GOLD_PICKAXE);
	    this.repairableMaterials.add(Material.GOLD_AXE);
	    this.repairableMaterials.add(Material.GOLD_HOE);
	    this.repairableMaterials.add(Material.GOLD_HELMET);
	    this.repairableMaterials.add(Material.GOLD_CHESTPLATE);
	    this.repairableMaterials.add(Material.GOLD_LEGGINGS);
	    this.repairableMaterials.add(Material.GOLD_BOOTS);
	    this.repairableMaterials.add(Material.IRON_SWORD);
	    this.repairableMaterials.add(Material.IRON_SPADE);
	    this.repairableMaterials.add(Material.IRON_PICKAXE);
	    this.repairableMaterials.add(Material.IRON_AXE);
	    this.repairableMaterials.add(Material.IRON_HOE);
	    this.repairableMaterials.add(Material.IRON_HELMET);
	    this.repairableMaterials.add(Material.IRON_CHESTPLATE);
	    this.repairableMaterials.add(Material.IRON_LEGGINGS);
	    this.repairableMaterials.add(Material.IRON_BOOTS);
	    this.repairableMaterials.add(Material.DIAMOND_SWORD);
	    this.repairableMaterials.add(Material.DIAMOND_SPADE);
	    this.repairableMaterials.add(Material.DIAMOND_PICKAXE);
	    this.repairableMaterials.add(Material.DIAMOND_AXE);
	    this.repairableMaterials.add(Material.DIAMOND_HOE);
	    this.repairableMaterials.add(Material.DIAMOND_HELMET);
	    this.repairableMaterials.add(Material.DIAMOND_CHESTPLATE);
	    this.repairableMaterials.add(Material.DIAMOND_LEGGINGS);
	    this.repairableMaterials.add(Material.DIAMOND_BOOTS);
    }

	@EventHandler
	public void itemPriceSetEvent(InventoryCloseEvent event) {
		ItemShopInfo itemShopInfo = SmellyShops.getInstance().getCreateItemShopAuthorization().get(event.getPlayer().getName());
		if (itemShopInfo != null && event.getInventory().getSize() == 54
				&& event.getInventory().getName().equals(ChatColor.AQUA + "Insert Item Costs & Close")) {
			// Calculate item prices
			Map<ItemStack, Integer> itemPrices = new HashMap<>();
			for (ItemStack item : event.getInventory().getContents()) {
				if (item != null && item.getType() != Material.AIR) {
					ItemStack key = new ItemStack(item.getType(), 1, item.getDurability());
					Integer pastAmount = itemPrices.get(key);
					if (pastAmount != null) {
						itemPrices.put(key, pastAmount + item.getAmount());
					} else {
						itemPrices.put(key, item.getAmount());
					}
				}
			}

			itemShopInfo.setItemPrice(itemPrices);
			((Player) event.getPlayer()).sendMessage(ChatColor.GREEN + "Click the sign with the item in hand that you want to add this item shop too.");
		}
	}

    @EventHandler
    public void createShopEvent(PlayerInteractEvent event) {
	    Player player = event.getPlayer();
        ShopInfo shopInfo = SmellyShops.getInstance().getCreateShopAuthorization().remove(player.getName());
        if (shopInfo != null) {  // Player is authorized
            event.setCancelled(true);

            // Did the player click a block?
            if (event.getClickedBlock() == null) {
                player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
                return;
            }

            // Did the player click a sign?
            if (!(event.getClickedBlock().getState() instanceof Sign)) {
                player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
                return;
            }

	        // Does any sort of shop sign already exist at that location?
	        if (SmellyShops.getInstance().getShops().containsKey(event.getClickedBlock().getLocation())
			        || SmellyShops.getInstance().getItemShops().containsKey(event.getClickedBlock().getLocation())
			        || SmellyShops.getInstance().getRepairShops().containsKey(event.getClickedBlock().getLocation())) {
                player.sendMessage(ChatColor.RED + "A shop sign already exists at that location, you have been deauthorized.");
                return;
            }

            // Player had an item in their hand
            if (event.getItem() == null) {
                player.sendMessage(ChatColor.RED + "You did not click with an item in your hand, you have been deauthorized.");
                return;
            }

            // Add the shop sign
	        SmellyShops.getInstance().createShopSign((Sign) event.getClickedBlock().getState(), event.getItem(), shopInfo.getItemDescription(), shopInfo.getAmount(),
		            shopInfo.getPrice(), shopInfo.getBuy());
            player.sendMessage(ChatColor.GREEN + "You have successfully added the shop sign!");
        } else {
	        ItemShopInfo itemShopInfo = SmellyShops.getInstance().getCreateItemShopAuthorization().remove(player.getName());
	        if (itemShopInfo != null) {  // Player is authorized
		        event.setCancelled(true);

		        // Did the player click a block?
		        if (event.getClickedBlock() == null) {
			        player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
			        return;
		        }

		        // Did the player click a sign?
		        if (!(event.getClickedBlock().getState() instanceof Sign)) {
			        player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
			        return;
		        }

		        // Does any sort of shop sign already exist at that location?
		        if (SmellyShops.getInstance().getShops().containsKey(event.getClickedBlock().getLocation())
				        || SmellyShops.getInstance().getItemShops().containsKey(event.getClickedBlock().getLocation())
				        || SmellyShops.getInstance().getRepairShops().containsKey(event.getClickedBlock().getLocation())) {
			        player.sendMessage(ChatColor.RED + "A shop sign already exists at that location, you have been deauthorized.");
			        return;
		        }

		        // Did the player have an item in their hand?
		        if (event.getItem() == null) {
			        player.sendMessage(ChatColor.RED + "You did not click with an item in your hand, you have been deauthorized.");
			        return;
		        }

		        // Add the shop sign
		        SmellyShops.getInstance().createItemShopSign((Sign) event.getClickedBlock().getState(), event.getItem(),
				        itemShopInfo.getItemDescription(), itemShopInfo.getAmount(), itemShopInfo.getPrice(), itemShopInfo.getItemPrice());
		        player.sendMessage(ChatColor.GREEN + "You have successfully added the item shop sign!");
	        } else {
		        RepairShopInfo repairShopInfo = SmellyShops.getInstance().getCreateRepairShopAuthorization().remove(player.getName());
		        if (repairShopInfo != null) { // Player is authorized
			        event.setCancelled(true);

			        // Did the player click a block?
			        if (event.getClickedBlock() == null) {
				        player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				        return;
			        }

			        // Did the player click a sign?
			        if (!(event.getClickedBlock().getState() instanceof Sign)) {
				        player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				        return;
			        }

			        // Does any sort of shop sign already exist at that location?
			        if (SmellyShops.getInstance().getShops().containsKey(event.getClickedBlock().getLocation())
					        || SmellyShops.getInstance().getItemShops().containsKey(event.getClickedBlock().getLocation())
					        || SmellyShops.getInstance().getRepairShops().containsKey(event.getClickedBlock().getLocation())) {
				        player.sendMessage(ChatColor.RED + "A shop sign already exists at that location, you have been deauthorized.");
				        return;
			        }

			        // Add the shop sign
			        SmellyShops.getInstance().createRepairShopSign((Sign) event.getClickedBlock().getState(), repairShopInfo.getDurability(), repairShopInfo.getPrice());
			        player.sendMessage(ChatColor.GREEN + "You have successfully added the repair shop sign!");
		        }
	        }
        }
    }

	@EventHandler
	public void removeShopEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (SmellyShops.getInstance().getRemoveShopAuthorization().remove(player.getName())) { // Player is authorized
			event.setCancelled(true);

			// Did the player click a block?
			if (event.getClickedBlock() == null) {
				player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				return;
			}

			// Did the player click a sign?
			if (!(event.getClickedBlock().getState() instanceof Sign)) {
				player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				return;
			}

			// Sign is a shop sign
			if (!SmellyShops.getInstance().getShops().containsKey(event.getClickedBlock().getLocation())) {
				player.sendMessage(ChatColor.RED + "The sign you clicked is not a shop sign, you have been deauthorized.");
				return;
			}

			// Remove the shop sign
			SmellyShops.getInstance().removeShopSign((Sign) event.getClickedBlock().getState());
			player.sendMessage(ChatColor.GREEN + "You have successfully removed the shop sign!");
		} else if (SmellyShops.getInstance().getRemoveItemShopAuthorization().remove(player.getName())) { // Player is authorized
			event.setCancelled(true);

			// Did the player click a block?
			if (event.getClickedBlock() == null) {
				player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				return;
			}

			// Did the player click a sign?
			if (!(event.getClickedBlock().getState() instanceof Sign)) {
				player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				return;
			}

			// Sign is a shop sign
			if (!SmellyShops.getInstance().getItemShops().containsKey(event.getClickedBlock().getLocation())) {
				player.sendMessage(ChatColor.RED + "The sign you clicked is not an item shop sign, you have been deauthorized.");
				return;
			}

			// Remove the item shop sign
			SmellyShops.getInstance().removeItemShopSign((Sign) event.getClickedBlock().getState());
			player.sendMessage(ChatColor.GREEN + "You have successfully removed the item shop sign!");
		} else if (SmellyShops.getInstance().getRemoveRepairShopAuthorization().remove(player.getName())) { // Player is authorized
			event.setCancelled(true);

			// Did the player click a block?
			if (event.getClickedBlock() == null) {
				player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				return;
			}

			// Did the player click a sign?
			if (!(event.getClickedBlock().getState() instanceof Sign)) {
				player.sendMessage(ChatColor.RED + "You did not click a sign, you have been deauthorized.");
				return;
			}

			// Sign is a repair shop sign
			if (!SmellyShops.getInstance().getRepairShops().containsKey(event.getClickedBlock().getLocation())) {
				player.sendMessage(ChatColor.RED + "The sign you clicked is not a repair shop sign, you have been deauthorized.");
				return;
			}

			// Remove the repair shop sign
			SmellyShops.getInstance().removeRepairShopSign((Sign) event.getClickedBlock().getState());
			player.sendMessage(ChatColor.GREEN + "You have successfully removed the repair shop sign!");
		}
	}

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void buyOrSellFromShopEvent(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
                Player player = event.getPlayer();
                Shop shop = SmellyShops.getInstance().getShops().get(b.getLocation());
                if (shop != null) { // Clicked sign is a shop sign
                    event.setCancelled(true);
					if (shop.getBuy().equals("buy")) { // Buy
						if (player.hasPermission("SmellyShops.buy")) { // Has permission to buy from a shop
							if (player.getInventory().firstEmpty() != -1) { // Has inventory space
								int balance = SmellyShops.getInstance().getArchMoney().checkBalance(player.getUniqueId().toString());
								if (balance >= shop.getPrice()) { // Can buy one "amount" of the item
									// Add item to player's inventory
									ItemStack item = shop.getItem().clone();
									item.setAmount(shop.getAmount());
									player.getInventory().addItem(item);

									// Update player inventory
									player.updateInventory();

									// Update balance
									SmellyShops.getInstance().getArchMoney().changeBalance(player.getUniqueId().toString(),
											(int) (SmellyShops.getInstance().getDiscount(player) * (-1) * shop.getPrice()),
											"[Shop] Bought " + shop.getAmount() + " " + shop.getItem().getType().toString() + " for " + shop.getPrice());

									player.sendMessage(ChatColor.GREEN + "You have bought " + shop.getAmount() + " "
											+ shop.getItem().getType().toString().toLowerCase() + " for $" +
											(int) (SmellyShops.getInstance().getDiscount(player) * shop.getPrice()) + "!");
								} else {
									player.sendMessage(ChatColor.RED + "You do not have enough money to buy this item!");
								}
							} else { // Has no inventory space
								player.sendMessage(ChatColor.RED + "You do not have enough inventory space to buy this item!");
							}
						} else { // Doesn't have permission to buy from a shop
							player.sendMessage(ChatColor.RED + "You do not have permission to buy from the shop!");
						}
					} else { // Sell
						if (player.hasPermission("SmellyShops.sell")) { // Has permission to sell to a shop
							// Check all the items for the proper amount
							ItemStack[] items = player.getInventory().getContents();
                            int first = -1;
							for (int i = 0; i < items.length; i++) {
								if (items[i] == null) {
									continue;
								}
								if (items[i].getType().equals(shop.getItem().getType())
										&& items[i].getDurability() == shop.getItem().getDurability() && items[i].getAmount() >= shop.getAmount()) {
									first = i;
									break;
								}
							}

                            if (first != -1) {
                                ItemStack item = player.getInventory().getItem(first);

                                if (item.getType().equals(shop.getItem().getType()) && item.getDurability() == shop.getItem().getDurability()) {
                                    if (item.getAmount() < shop.getAmount()) {
                                        player.sendMessage(ChatColor.RED + "You do not have enough of this item to sell.");
                                        return;
                                    } else {
                                        if (item.getAmount() == shop.getAmount()) {
                                            player.getInventory().setItem(first, null);
                                        } else {
                                            item.setAmount(item.getAmount() - shop.getAmount());
                                        }
                                    }

                                    // Update player inventory
                                    player.updateInventory();
                                } else {
                                    player.sendMessage(ChatColor.RED + "You cannot sell this item here.");
                                    return;
                                }

                                // Sell the item
                                SmellyShops.getInstance().getArchMoney().changeBalance(player.getUniqueId().toString(),
		                                shop.getPrice(), "Sold " + shop.getAmount() + " " + shop.getItem().getType().toString().toLowerCase());

                                player.sendMessage(ChatColor.GREEN + "You have sold " + shop.getAmount() + " "
		                                + shop.getItem().getType().toString().toLowerCase() + " for $" + shop.getPrice() + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You do not have enough of this item to sell!");
                            }
                        } else { // Doesn't have permission to sell to a shop
							player.sendMessage(ChatColor.RED + "You do not have permission to sell to the shop!");
						}
                    }
                } else {
	                ItemShop itemShop = SmellyShops.getInstance().getItemShops().get(b.getLocation());
	                if (itemShop != null) {
		                event.setCancelled(true);
			            if (player.hasPermission("SmellyShops.buy")) { // Has permission to buy from a shop
				            // Handle money
				            if (itemShop.getPrice() > 0) {
					            int available = SmellyShops.getInstance().getArchMoney().checkBalance(player.getUniqueId().toString());
					            if (available < itemShop.getPrice()) {
						            player.sendMessage(ChatColor.RED + "You do not have enough money to buy this item!");
						            return;
					            }
				            }

				            // Handle items
				            Map<ItemStack, Integer> payableItems = new HashMap(itemShop.getItemPrice());
				            Map<Integer, Integer> itemLocations = new HashMap<>();
				            for (ItemStack itemType : itemShop.getItemPrice().keySet()) {
					            Integer required = itemShop.getItemPrice().get(itemType);

					            if (required == null || required == 0) continue;

					            // Do they have this type of item?
					            for (int i = 0; i < player.getInventory().getSize(); i++) {
						            ItemStack item = player.getInventory().getItem(i);
						            if (item != null && item.getType() != Material.AIR
								            && item.getType() == itemType.getType()
								            && item.getDurability() == itemType.getDurability()) {
							            if (item.getAmount() >= required) {
								            if (item.getAmount() == required) {
									            itemLocations.put(i, 0);
								            } else {
									            itemLocations.put(i, item.getAmount() - required);
								            }

								            payableItems.remove(itemType);
								            break;
							            } else {
								            required = required - item.getAmount();

								            itemLocations.put(i, 0);
								            payableItems.put(itemType, required);
							            }
						            }
					            }
				            }

				            // Did player have all items required?
				            if (payableItems.isEmpty()) {
					            // Remove items from inventory
					            for (Integer slot : itemLocations.keySet()) {
						            Integer amount = itemLocations.get(slot);
						            if (amount != 0) {
							            player.getInventory().getItem(slot).setAmount(amount);
						            } else {
							            player.getInventory().setItem(slot, null);
						            }
					            }

					            // Add item to player's inventory
					            ItemStack item = itemShop.getItem().clone();
					            item.setAmount(itemShop.getAmount());
					            player.getInventory().addItem(item);

					            // Update player inventory
					            player.updateInventory();
					            // Remove money from their account
					            SmellyShops.getInstance().getArchMoney().changeBalance(player.getUniqueId().toString(),
							            -1 * itemShop.getPrice(), "[ItemShop] Bought " + itemShop.getAmount() + " "
									            + itemShop.getItem().getType().toString() + " for " + itemShop.getPrice());

					            player.sendMessage(ChatColor.GREEN + "You have bought " + itemShop.getAmount() + " "
							            + itemShop.getItem().getType().toString().toLowerCase() + " for $" +
							            (int) (SmellyShops.getInstance().getDiscount(player) * itemShop.getPrice()) + " and other items!");
				            } else {
					            player.sendMessage(ChatColor.RED + "You do not have the required items to buy this item!");
				            }
			            }
	                } else {
		                RepairShop repairShop = SmellyShops.getInstance().getRepairShops().get(b.getLocation());
		                if (repairShop != null) {
			                event.setCancelled(true);
			                if (player.hasPermission("SmellyShops.repair")) { // Has permission to buy from a repair shop
				                ItemStack item = player.getItemInHand();
				                if (item != null && this.repairableMaterials.contains(item.getType())) {
					                int balance = SmellyShops.getInstance().getArchMoney().checkBalance(player.getUniqueId().toString());
					                if (balance >= repairShop.getPrice()) {
						                if (item.getDurability() == 0) {
							                player.sendMessage(ChatColor.RED + "Item is already fully repaired!");
							                return;
						                }

						                int newDurability = item.getDurability() - repairShop.getDurability();

						                if (newDurability < 0) newDurability = 0;

						                item.setDurability((short) newDurability);

						                // Update balance
						                SmellyShops.getInstance().getArchMoney().changeBalance(player.getUniqueId().toString(),
								                (int) (SmellyShops.getInstance().getDiscount(player) * (-1) * repairShop.getPrice()),
								                "Bought an item repair for " + repairShop.getPrice());

						                player.sendMessage(ChatColor.GREEN + "You have repaired your item for " + repairShop.getDurability()
								                + " durability for $" + (int) (SmellyShops.getInstance().getDiscount(player) * repairShop.getPrice()) + "!");
					                }
				                } else {
					                player.sendMessage(ChatColor.RED + "You cannot repair this item!");
				                }
			                } else { // Doesn't have permission to buy from a shop
				                player.sendMessage(ChatColor.RED + "You must be duke or higher to buy from this repair shop!");
			                }
		                }
	                }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void breakShopSign(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.SIGN_POST) || event.getBlock().getType().equals(Material.WALL_SIGN)) {
	        if (SmellyShops.getInstance().getShops().containsKey(event.getBlock().getLocation())) {
		        event.setCancelled(true);
		        if (event.getPlayer().hasPermission("SmellyShops.remove")) {
			        event.getPlayer().sendMessage(ChatColor.RED + "You cannot break a shop sign, you must remove it using \"/removeshop\".");
		        }
	        } else if (SmellyShops.getInstance().getItemShops().containsKey(event.getBlock().getLocation())) {
		        event.setCancelled(true);
		        if (event.getPlayer().hasPermission("SmellyShops.remove")) {
			        event.getPlayer().sendMessage(ChatColor.RED + "You cannot break an item shop sign, you must remove it using \"/removeitemshop\".");
		        }
	        } else if (SmellyShops.getInstance().getRepairShops().containsKey(event.getBlock().getLocation())) {
	            event.setCancelled(true);
	            if (event.getPlayer().hasPermission("SmellyShops.remove")) {
		            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break a repair shop sign, you must remove it using \"/removerepairshop\".");
	            }
            }
        }
    }

}
