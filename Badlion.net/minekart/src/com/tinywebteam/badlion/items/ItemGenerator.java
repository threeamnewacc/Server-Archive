package com.tinywebteam.badlion.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ItemGenerator {
	
	// Start off strings
	public static String SLOW_START = ChatColor.RED + "" + ChatColor.BLUE;
	public static String GRENADE_START = ChatColor.RED + "" + ChatColor.GREEN;
	public static String SPEED_BOOST_START = ChatColor.RED + "" + ChatColor.AQUA;
	public static String LAND_MINE_START = ChatColor.RED + "" + ChatColor.BLACK;
	public static String BLUE_SHELL_START = ChatColor.RED + "" + ChatColor.BOLD;
	public static String RED_SHELL_START = ChatColor.RED + "" + ChatColor.DARK_AQUA;
	public static String COBWEB_START = ChatColor.RED + "" + ChatColor.DARK_BLUE;
	public static String FAKE_ITEM_START = ChatColor.RED + "" + ChatColor.DARK_GRAY;
	public static String LIGHTNING_START = ChatColor.RED + "" + ChatColor.DARK_GREEN;
	public static String STAR_START = ChatColor.RED + "" + ChatColor.DARK_PURPLE;
	
	// Display names
	public static String SLOW_TITLE = SLOW_START + ChatColor.RED + "Slow Ball";
	public static String GRENADE_TITLE = GRENADE_START + ChatColor.RED + "Holy Hand Grenade";
	public static String SPEED_BOOST_TITLE = SPEED_BOOST_START + ChatColor.RED + "Speed Boost";
	public static String LAND_MINE_TITLE = LAND_MINE_START + ChatColor.RED + "TnT Trap";
	public static String BLUE_SHELL_TITLE = BLUE_SHELL_START + ChatColor.RED + "Blue Missile";
	public static String RED_SHELL_TITLE = RED_SHELL_START + ChatColor.RED + "Red Missile";
	public static String COBWEB_TITLE = COBWEB_START + ChatColor.RED + "Speed Trap";
	public static String FAKE_ITEM_TITLE = FAKE_ITEM_START + ChatColor.RED + "Fake Item Box";
	public static String LIGHTNING_TITLE = LIGHTNING_START + ChatColor.RED + "Global Slow Potion";
	public static String STAR_TITLE = STAR_START + ChatColor.RED + "Invincible Speed Potion";
	
	// Descriptions
	public static String SLOW_DESC = ChatColor.AQUA + "Slow your enemy by throwing this item at them!";
	public static String GRENADE_DESC = ChatColor.AQUA + "Throw thou holy hand grenade to stun enemies in an area!";
	public static String SPEED_BOOST_DESC = ChatColor.AQUA + "Use this item to speed yourself for a bit!";
	public static String LAND_MINE_DESC = ChatColor.AQUA + "Drop this item to place a nasty mine";
	public static String BLUE_SHELL_DESC = ChatColor.AQUA + "Shoot a missile at first place.";
	public static String RED_SHELL_DESC = ChatColor.AQUA + "Shoot a missile at the person in front of you.";
	public static String COBWEB_DESC = ChatColor.AQUA + "Place a Speed Trap to slow enemies down.";
	public static String FAKE_ITEM_DESC = ChatColor.AQUA + "Some Description.";
	public static String LIGHTNING_DESC = ChatColor.AQUA + "Slow all enemies in the race for 10 seconds.";
	public static String STAR_DESC = ChatColor.AQUA + "Speed potion and invincible for 10 seconds.";
	
	// Few private things for randomization
	private static ArrayList<Integer> place1Items;
	private static ArrayList<Integer> place2Items;
	private static ArrayList<Integer> place3Items;
	private static ArrayList<Integer> place4Items;
	private static ArrayList<Integer> place5Items;
	private static ArrayList<Integer> place6Items;
	private static ArrayList<Integer> place7Items;
	private static ArrayList<Integer> place8Items;
	
	private static Random randomGenerator;
	
	public ItemGenerator() {
	}
	
	public static void intialize() {
		place1Items = new ArrayList<Integer>();
		place2Items = new ArrayList<Integer>();
		place3Items = new ArrayList<Integer>();
		place4Items = new ArrayList<Integer>();
		place5Items = new ArrayList<Integer>();
		place6Items = new ArrayList<Integer>();
		place7Items = new ArrayList<Integer>();
		place8Items = new ArrayList<Integer>();
		
		// Add items
		for (int i = 0; i < 30; ++i) {
			place1Items.add(0);
		}
		for (int i = 0; i < 30; ++i) {
			place1Items.add(2);
		}
		for (int i = 0; i < 40; ++i) {
			place1Items.add(3);
		}
		
		for (int i = 0; i < 20; ++i) {
			place2Items.add(0);
		}
		for (int i = 0; i < 20; ++i) {
			place2Items.add(2);
		}
		for (int i = 0; i < 20; ++i) {
			place2Items.add(3);
		}
		for (int i = 0; i < 20; ++i) {
			place2Items.add(1);
		}
		for (int i = 0; i < 20; ++i) {
			place2Items.add(5);
		}
		
		for (int i = 0; i < 20; ++i) {
			place3Items.add(0);
		}
		for (int i = 0; i < 20; ++i) {
			place3Items.add(2);
		}
		for (int i = 0; i < 20; ++i) {
			place3Items.add(3);
		}
		for (int i = 0; i < 20; ++i) {
			place3Items.add(1);
		}
		for (int i = 0; i < 20; ++i) {
			place3Items.add(5);
		}
		
		for (int i = 0; i < 20; ++i) {
			place4Items.add(0);
		}
		for (int i = 0; i < 20; ++i) {
			place4Items.add(2);
		}
		for (int i = 0; i < 20; ++i) {
			place4Items.add(3);
		}
		for (int i = 0; i < 20; ++i) {
			place4Items.add(1);
		}
		for (int i = 0; i < 20; ++i) {
			place4Items.add(5);
		}
		
		for (int i = 0; i < 19; ++i) {
			place5Items.add(0);
		}
		for (int i = 0; i < 19; ++i) {
			place5Items.add(2);
		}
		for (int i = 0; i < 19; ++i) {
			place5Items.add(3);
		}
		for (int i = 0; i < 19; ++i) {
			place5Items.add(1);
		}
		for (int i = 0; i < 19; ++i) {
			place5Items.add(5);
		}
		for (int i = 0; i < 5; ++i) {
			place5Items.add(6);
		}
		
		for (int i = 0; i < 15; ++i) {
			place6Items.add(0);
		}
		for (int i = 0; i < 15; ++i) {
			place6Items.add(2);
		}
		for (int i = 0; i < 15; ++i) {
			place6Items.add(3);
		}
		for (int i = 0; i < 15; ++i) {
			place6Items.add(1);
		}
		for (int i = 0; i < 15; ++i) {
			place6Items.add(5);
		}
		for (int i = 0; i < 15; ++i) {
			place6Items.add(6);
		}
		for (int i = 0; i < 10; ++i) {
			place6Items.add(4);
		}
		
		for (int i = 0; i < 5; ++i) {
			place7Items.add(0);
		}
		for (int i = 0; i < 5; ++i) {
			place7Items.add(2);
		}
		for (int i = 0; i < 5; ++i) {
			place7Items.add(3);
		}
		for (int i = 0; i < 5; ++i) {
			place7Items.add(1);
		}
		for (int i = 0; i < 20; ++i) {
			place7Items.add(5);
		}
		for (int i = 0; i < 30; ++i) {
			place7Items.add(6);
		}
		for (int i = 0; i < 30; ++i) {
			place7Items.add(4);
		}
		
		for (int i = 0; i < 5; ++i) {
			place8Items.add(0);
		}
		for (int i = 0; i < 5; ++i) {
			place8Items.add(2);
		}
		for (int i = 0; i < 5; ++i) {
			place8Items.add(3);
		}
		for (int i = 0; i < 5; ++i) {
			place8Items.add(1);
		}
		for (int i = 0; i < 20; ++i) {
			place8Items.add(5);
		}
		for (int i = 0; i < 30; ++i) {
			place8Items.add(6);
		}
		for (int i = 0; i < 30; ++i) {
			place8Items.add(4);
		}
		
	}
	
	public static ItemStack getRandomItem(int position) {
		if (ItemGenerator.randomGenerator == null) {
			ItemGenerator.randomGenerator = new Random();
		}
		int randomInt = ItemGenerator.randomGenerator.nextInt(100);
		int itemInt = 0;
		switch(position) {
			case 0:
				itemInt = place1Items.get(randomInt);
				break;
			case 1:
				itemInt = place2Items.get(randomInt);
				break;
			case 2:
				itemInt = place3Items.get(randomInt);
				break;
			case 3:
				itemInt = place4Items.get(randomInt);
				break;
			case 4:
				itemInt = place5Items.get(randomInt);
				break;
			case 5:
				itemInt = place6Items.get(randomInt);
				break;
			case 6:
				itemInt = place7Items.get(randomInt);
				break;
			case 7:
				itemInt = place8Items.get(randomInt);
				break;
		}
		
		switch (itemInt) {
			case 0:
				return ItemGenerator.getSlowBall();
			case 1:
				return ItemGenerator.getSpeedBoost();
			case 2:
				return ItemGenerator.getCobweb();
			case 3:
				return ItemGenerator.getLandMine();
			case 4:
				return ItemGenerator.getBlueShell();
			case 5:
				return ItemGenerator.getRedShell();
			case 6:
				return ItemGenerator.getLightningItem();
			//case 7:
			//	return ItemGenerator.getStarItem();
		}
		return null; // should never hit
	}
	
	//Definitions of items
	public static ItemStack getSlowBall() {
		ItemStack item = new ItemStack(Material.SNOW_BALL);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(SLOW_TITLE);
		lore.add(SLOW_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getGrenade() {
		ItemStack item = new ItemStack(Material.ENDER_PEARL);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(GRENADE_TITLE);
		lore.add(GRENADE_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getSpeedBoost() {
		Potion potion = new Potion(PotionType.SPEED, 1);
		potion.setSplash(true);
		ItemStack item = potion.toItemStack(1);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(SPEED_BOOST_TITLE);
		lore.add(SPEED_BOOST_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	public static ItemStack getLandMine() {
		ItemStack item = new ItemStack(Material.TNT);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(LAND_MINE_TITLE);
		lore.add(LAND_MINE_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getBlueShell() {
		ItemStack item = new ItemStack(Material.DIAMOND);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(BLUE_SHELL_TITLE);
		lore.add(BLUE_SHELL_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getRedShell() {
		ItemStack item = new ItemStack(Material.REDSTONE);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(RED_SHELL_TITLE);
		lore.add(RED_SHELL_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getCobweb() {
		ItemStack item = new ItemStack(Material.WEB);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(COBWEB_TITLE);
		lore.add(COBWEB_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getFakeItem() {
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(FAKE_ITEM_TITLE);
		lore.add(FAKE_ITEM_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getStarItem() {
		ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(STAR_TITLE);
		lore.add(STAR_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getLightningItem() {
		Potion potion = new Potion(PotionType.FIRE_RESISTANCE, 1);
		potion.setSplash(true);
		ItemStack item = potion.toItemStack(1);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		meta.setDisplayName(LIGHTNING_TITLE);
		lore.add(LIGHTNING_DESC);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	//Item Checkers
	public static boolean isSlowBall(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(SLOW_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isGrenade(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(GRENADE_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isSpeedBoost(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(SPEED_BOOST_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isLandMine(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(LAND_MINE_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isBlueShell(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(BLUE_SHELL_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isRedShell(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(RED_SHELL_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isFakeItem(ItemStack item){
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(FAKE_ITEM_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isLightningItem(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(LIGHTNING_START)) {
			return true;
		}
		return false;
	}
	
	public static boolean isStarItem(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta.getDisplayName().startsWith(STAR_START)) {
			return true;
		}
		return false;
	}
}
	

