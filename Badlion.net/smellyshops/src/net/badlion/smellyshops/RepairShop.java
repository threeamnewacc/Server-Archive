package net.badlion.smellyshops;

import org.bukkit.Location;

public class RepairShop {

	private Location location;

	private int durability;
	private int price;

	public RepairShop(int x, int y, int z, String world, int durability, int price) {
		this.location = new Location(SmellyShops.getInstance().getServer().getWorld(world), x, y, z);
		this.durability = durability;
		this.price = price;
	}

	public RepairShop(Location loc, int durability, int price) {
		this.location = loc;
		this.durability = durability;
		this.price = price;
	}

	public Location getLocation() {
		return location;
	}

	public int getDurability() {
		return durability;
	}

	public int getPrice() {
		return price;
	}

}
