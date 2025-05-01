package net.badlion.auction;

import org.bukkit.inventory.ItemStack;

public class Trade {
	
	private ItemStack hostItem;
	private ItemStack guestItem;
	private double guestMoney;
	private boolean hostAccepted;
	private boolean guestAccepted;
	
	public Trade(ItemStack item) {
		this.hostItem = item;
	}

	public ItemStack getHostItem() {
		return hostItem;
	}

	public void setHostItem(ItemStack hostItem) {
		this.hostItem = hostItem;
	}

	public ItemStack getGuestItem() {
		return guestItem;
	}

	public void setGuestItem(ItemStack guestItem) {
		this.guestItem = guestItem;
	}

	public double getGuestMoney() {
		return guestMoney;
	}

	public void setGuestMoney(double guestMoney) {
		this.guestMoney = guestMoney;
	}

	public boolean isHostAccepted() {
		return hostAccepted;
	}

	public void setHostAccepted(boolean hostAccepted) {
		this.hostAccepted = hostAccepted;
	}

	public boolean isGuestAccepted() {
		return guestAccepted;
	}

	public void setGuestAccepted(boolean guestAccepted) {
		this.guestAccepted = guestAccepted;
	}

}
