package net.badlion.arenalobby.inventories.duel;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DuelInventorySnapshotData {

	private final UUID uuid;
	private final String name;
	private final Inventory inventory;

	public DuelInventorySnapshotData(UUID playerId, String name, JSONArray potions, JSONArray armor, JSONArray inventory, Long food, Double health) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		Holder holder = new Holder();
		this.inventory = Bukkit.createInventory(holder, 9 * 6, name);
		holder.setInventory(this.inventory);

		int i = 0;
		for (Object object : inventory.toArray()) {
			int b = i < 9 ? i + 9 * 3 : i - 9;
			i++;
			JSONObject jsonObject = (JSONObject) object;
			String type = (String) jsonObject.get("t");
			if (type.equals("n")) {
				this.inventory.setItem(b, new ItemStack(Material.AIR));
			} else {
				Material material = Material.getMaterial(type);
				Long durability = (Long) jsonObject.get("d");
				Long amount = (Long) jsonObject.get("s");

				ItemStack item = new ItemStack(material, amount.intValue(), durability.shortValue());
				if (jsonObject.containsKey("e")) {
					Map<String, Long> enchants = (Map<String, Long>) jsonObject.get("e");
					for (Map.Entry<String, Long> entry : enchants.entrySet()) {
						item.addEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
					}
				}
				this.inventory.setItem(b, convert(item));
			}
		}

		i = 0;
		for (Object object : armor.toArray()) {
			JSONObject jsonObject = (JSONObject) object;
			String type = (String) jsonObject.get("t");
			if (type.equals("n")) {
				this.inventory.setItem(i + 9 * 4, new ItemStack(Material.AIR));
			} else {
				Material material = Material.getMaterial(type);
				Long durability = (Long) jsonObject.get("d");
				Long amount = (Long) jsonObject.get("s");
				ItemStack item = new ItemStack(material, amount.intValue(), durability.shortValue());
				if (jsonObject.containsKey("e")) {
					Map<String, Long> enchants = (Map<String, Long>) jsonObject.get("e");
					for (Map.Entry<String, Long> entry : enchants.entrySet()) {
						item.addEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
					}
				}
				this.inventory.setItem(i + 9 * 4, convert(item));
			}
			i++;
		}

		if (health > 0) {
			ItemStack healthItem = ItemStackUtil.createItem(Material.SPECKLED_MELON, ChatColor.RED + "Player health points");
			healthItem.setAmount(health.intValue());
			this.inventory.setItem(3 + 9 * 5, healthItem);
		} else {
			ItemStack healthItem = ItemStackUtil.createItem(Material.SKULL_ITEM, ChatColor.RED + "Player Died");
			healthItem.setAmount(1);
			this.inventory.setItem(3 + 9 * 5, healthItem);
		}
		ItemStack foodItem = ItemStackUtil.createItem(Material.COOKED_BEEF, ChatColor.RED + "Player food points");
		foodItem.setAmount(food.intValue());
		this.inventory.setItem(4 + 9 * 5, foodItem);
		List<String> potionEffects = new ArrayList<>();
		for (Object object : potions.toArray()) {
			JSONObject potionEffect = (JSONObject) object;
			String potionName = (String) potionEffect.get("name");
			Long amplifier = (Long) potionEffect.get("amplifier");
			potionEffects.add(String.format("%s %s", potionName, amplifier + 1));
		}
		if (potionEffects.isEmpty()) {
			potionEffects.add("No potion effects!");
		}
		ItemStack potion = ItemStackUtil.createItem(Material.BREWING_STAND_ITEM, ChatColor.GOLD + "Potion Effects", potionEffects);
		potion.setAmount((potionEffects.size() >= 1) ? potionEffects.size() : 1);
		this.inventory.setItem(5 + 9 * 5, potion);
	}

	public Inventory getInventory() {
		return inventory;
	}

	private ItemStack convert(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return null;
		}
		short max = item.getType().getMaxDurability();
		if (max > 0) {
			item = item.clone();
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			if (lore == null) {
				lore = new ArrayList<>();
			}
			lore.add(0, ChatColor.GRAY + "Durability: " + (max - item.getDurability()) + " / " + max);
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}
		return item;
	}

	public static class Holder implements InventoryHolder {

		private Inventory inventory;

		@Override
		public Inventory getInventory() {
			return inventory;
		}

		public void setInventory(Inventory inventory) {
			this.inventory = inventory;
		}
	}
}
