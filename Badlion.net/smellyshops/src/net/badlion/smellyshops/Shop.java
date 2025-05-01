package net.badlion.smellyshops;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Shop {

    private Location location;

    private int amount;
    private int price;
    private String buy;
    private ItemStack item;

    public Shop(int x, int y, int z, String world, ItemStack item, int amount, int price, String buy) {
        this.location = new Location(SmellyShops.getInstance().getServer().getWorld(world), x, y, z);
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.buy = buy;
    }

    public Shop(Location location, ItemStack item, int amount, int price, String buy) {
        this.location = location;
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.buy = buy;
    }

    public Location getLocation() {
        return location;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

	public String getBuy() {
		return buy;
	}

	public ItemStack getItem() {
        return item;
    }

}
