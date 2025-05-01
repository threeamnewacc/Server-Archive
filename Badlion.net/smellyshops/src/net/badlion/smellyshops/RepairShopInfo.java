package net.badlion.smellyshops;

public class RepairShopInfo {

	private int durability;
	private int price;

	public RepairShopInfo(int durability, int price) {
		this.durability = durability;
		this.price = price;
	}

	public int getDurability() {
		return durability;
	}

	public int getPrice() {
		return price;
	}

}
