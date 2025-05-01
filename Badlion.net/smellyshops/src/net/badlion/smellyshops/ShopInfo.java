package net.badlion.smellyshops;

public class ShopInfo {

    private String buy;
    private int amount;
    private int price;
    private String itemDescription;

    public ShopInfo(String buy, int amount, int price, String itemDescription) {
        this.buy = buy;
        this.amount = amount;
        this.price = price;
        this.itemDescription = itemDescription;
    }

	public String getBuy() {
		return buy;
	}

	public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public String getItemDescription() {
        return itemDescription;
    }

}
