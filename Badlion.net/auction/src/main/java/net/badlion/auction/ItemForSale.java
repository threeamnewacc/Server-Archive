package net.badlion.auction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemForSale {

	private ItemStack item;
	private Player player;
	private int price;
	private int increment;
	private int currentBid;
	private ArrayList<Integer> bids;
	private ArrayList<Timestamp> timestamps;
	private ArrayList<Player> players;
	private Map<String, Integer> enchantments;
	
	public ItemForSale(ItemStack item, Player player, int price, int increment) {
		this.item = item;
		this.player = player;
		this.price = price;
		this.increment = increment;
		this.currentBid = price - increment; // hack for easy min bid value
		this.bids = new ArrayList<Integer>();
		this.timestamps = new ArrayList<Timestamp>();
		this.players = new ArrayList<Player>();
		this.enchantments = new HashMap<String, Integer>();
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public int getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(int currentBid) {
		this.currentBid = currentBid;
	}

	public ArrayList<Integer> getBids() {
		return bids;
	}

	public void setBids(ArrayList<Integer> bids) {
		this.bids = bids;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public ArrayList<Timestamp> getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(ArrayList<Timestamp> timestamps) {
		this.timestamps = timestamps;
	}

	public Map<String, Integer> getEnchantments() {
		return enchantments;
	}

	public void setEnchantments(Map<String, Integer> enchantments) {
		this.enchantments = enchantments;
	}
}
