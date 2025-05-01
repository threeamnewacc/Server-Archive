package net.badlion.smellyshops;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemShopInfo {

    private int amount;
	private String itemDescription;

    private int price;
	private Map<ItemStack, Integer> itemPrice;

    public ItemShopInfo(int amount, String itemDescription, int price) {
        this.amount = amount;
	    this.itemDescription = itemDescription;

        this.price = price;
    }

	public int getAmount() {
        return amount;
    }

	public String getItemDescription() {
		return itemDescription;
	}

    public int getPrice() {
        return price;
    }

	public Map<ItemStack, Integer> getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Map<ItemStack, Integer> itemPrice) {
		this.itemPrice = itemPrice;
	}

}
