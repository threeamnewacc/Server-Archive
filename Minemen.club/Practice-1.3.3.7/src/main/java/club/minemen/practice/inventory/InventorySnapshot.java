package club.minemen.practice.inventory;

import club.minemen.core.inventory.InventoryUI;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.util.MathUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;

@Getter
public class InventorySnapshot {

	private final InventoryUI inventoryUI;
	private final ItemStack[] originalInventory;
	private final ItemStack[] originalArmor;

	@Getter
	private final UUID snapshotId = UUID.randomUUID();

	public InventorySnapshot(Player player, Match match) {
		ItemStack[] contents = player.getInventory().getContents();
		ItemStack[] armor = player.getInventory().getArmorContents();

		this.originalInventory = contents;
		this.originalArmor = armor;

		PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

		double health = player.getHealth();
		double food = (double) player.getFoodLevel();

		List<String> potionEffectStrings = new ArrayList<>();

		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
			String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
			String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());

			potionEffectStrings.add(CC.PRIMARY + effectName + " " + romanNumeral + CC.SECONDARY + " (" + duration + ")");
		}

		this.inventoryUI = new InventoryUI(player.getName(), true, 6);

		for (int i = 0; i < 9; i++) {
			this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
			this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
			this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
		}

		boolean potionMatch = false;
		boolean soupMatch = false;

		for (ItemStack item : match.getKit().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.MUSHROOM_SOUP) {
				soupMatch = true;
				break;
			} else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
				potionMatch = true;
				break;
			}
		}

		if (potionMatch) {
			int potCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();

			this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
					ItemUtil.createItem(Material.POTION, CC.PRIMARY + "Health Potions: " + CC.SECONDARY + potCount, potCount, (short) 16421),
					CC.PRIMARY + "Missed Potions: " + CC.SECONDARY + playerData.getMissedPots())));
		} else if (soupMatch) {
			int soupCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

			this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(
					Material.MUSHROOM_SOUP, CC.PRIMARY + "Remaining Soups: " + CC.SECONDARY + soupCount, soupCount, (short) 16421)));
		}

		this.inventoryUI.setItem(48,
				new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, CC.PRIMARY + "Hearts: "
						+ CC.SECONDARY + MathUtil.roundToHalves(health / 2.0D) + " / 10 ❤", (int) Math.round(health / 2.0D))));

		this.inventoryUI.setItem(49,
				new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, CC.PRIMARY + "Hunger: "
						+ CC.SECONDARY + MathUtil.roundToHalves(food / 2.0D) + " / 10 ❤", (int) Math.round(food / 2.0D))));

		this.inventoryUI.setItem(50,
				new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
						ItemUtil.createItem(Material.BREWING_STAND_ITEM, CC.PRIMARY + "Potion Effects", potionEffectStrings.size())
						, potionEffectStrings.toArray(new String[]{}))));

		this.inventoryUI.setItem(51, new InventoryUI.EmptyClickableItem(
				ItemUtil.reloreItem(
						ItemUtil.createItem(Material.DIAMOND_SWORD, CC.PRIMARY + "Statistics"),
						CC.PRIMARY + "Longest Combo: " + CC.SECONDARY + playerData.getLongestCombo() + " Hit" +
								(playerData.getLongestCombo() > 1 ? "s" : ""),
						CC.PRIMARY + "Total Hits: " + CC.SECONDARY + playerData.getHits() + " Hit" + (playerData.getHits() > 1 ? "s" : ""))));

		if (!match.isParty()) {
			for (int i = 0; i < 2; i++) {
				this.inventoryUI.setItem(i == 0 ? 53 : 45, new InventoryUI.AbstractClickableItem(
						ItemUtil.reloreItem(ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "View Other Inventory"),
								CC.PRIMARY + "Click to view the other inventory")) {
					@Override
					public void onClick(InventoryClickEvent inventoryClickEvent) {
						Player clicker = (Player) inventoryClickEvent.getWhoClicked();

						if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
							clicker.closeInventory();
							Practice.getInstance().getServer().dispatchCommand(clicker, "inv " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
						}
					}
				});
			}
		}

		for (int i = 36; i < 40; i++) {
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
		}
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		JSONObject inventoryObject = new JSONObject();
		for (int i = 0; i < this.originalInventory.length; i++) {
			inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
		}
		object.put("inventory", inventoryObject);

		JSONObject armourObject = new JSONObject();
		for (int i = 0; i < this.originalArmor.length; i++) {
			armourObject.put(i, this.encodeItem(this.originalArmor[i]));
		}
		object.put("armour", armourObject);

		return object;
	}

	private JSONObject encodeItem(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return null;
		}

		JSONObject object = new JSONObject();
		object.put("material", itemStack.getType().name());
		object.put("durability", itemStack.getDurability());
		object.put("amount", itemStack.getAmount());

		JSONObject enchants = new JSONObject();
		for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
		}
		object.put("enchants", enchants);

		return object;
	}

}
