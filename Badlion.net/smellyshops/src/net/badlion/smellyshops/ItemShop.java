package net.badlion.smellyshops;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemShop {

    private Location location;

    private int amount;
    private ItemStack item;

	private int price;
	private Map<ItemStack, Integer> itemPrice;

    public ItemShop(int x, int y, int z, String world, ItemStack item, int amount, int price, Map<ItemStack, Integer> itemPrice) {
        this.location = new Location(SmellyShops.getInstance().getServer().getWorld(world), x, y, z);
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.itemPrice = itemPrice;
    }

    public ItemShop(Location location, ItemStack item, int amount, int price, Map<ItemStack, Integer> itemPrice) {
        this.location = location;
        this.item = item;
        this.amount = amount;
        this.price = price;
	    this.itemPrice = itemPrice;
    }

    public Location getLocation() {
        return location;
    }

    public int getAmount() {
        return amount;
    }

	public ItemStack getItem() {
		return item;
	}

    public int getPrice() {
        return price;
    }

	public Map<ItemStack, Integer> getItemPrice() {
		return itemPrice;
	}

}
