package net.badlion.potpvp.helpers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.rulesets.CustomRuleSet;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.kits.KitCreationState;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class KitCreationHelper {

	// Info signs
	private static Location info1SignLocation;
	private static Location info2SignLocation;
	private static Location info3SignLocation;
	private static Location info4SignLocation;
	private static Location info5SignLocation;
	private static Location info6SignLocation;
	private static Location info7SignLocation;
	private static Location info8SignLocation;
	private static Location info9SignLocation;

	// Enchant signs
	private static Location knockbackSignLocation;
	private static Location featherFallingSignLocation;
	private static Location fireAspectSignLocation;
	private static Location flameSignLocation;
	private static Location infinitySignLocation;
	private static Location powerSignLocation;
	private static Location protectionSignLocation;
	private static Location punchSignLocation;
	private static Location sharpnessSignLocation;
	private static Location unbreakingSignLocation;

	private static Location spawnSignLocation; // Go to spawn
	private static Location saveKitSignLocation; // Save Kit
	private static Location loadKitSignLocation; // Load Kit
	private static Location loadDefaultKitSignLocation; // Load Default Kit
	private static Location clearInventorySignLocation; // Clear Inventory

	private static Location kitChestLocation;

	private static Map<String, Inventory> kitInventories = new HashMap<>();

	private static ItemStack kitWeaponsItem;
	private static ItemStack kitArmorItem;
	private static ItemStack kitFoodItem;
	private static ItemStack kitPotionsItem;
	private static ItemStack kitOtherItem;

	private static Inventory customKitDefaultInventory;
	private static Inventory customKitWeaponInventory;
	private static Inventory customKitArmorInventory;
	private static Inventory customKitFoodInventory;
	private static Inventory customKitPotionsInventory;
	private static Inventory customKitOtherInventory;

	private static Map<Enchantment, String> enchantmentNames = new HashMap<>();

	public static void initialize() {
		// Assign sign locations
		World world = PotPvP.getInstance().getServer().getWorld("world");
		KitCreationHelper.info1SignLocation = new Location(world, 485, 78, -5);
		KitCreationHelper.info2SignLocation = new Location(world, 485, 78, -6);
		KitCreationHelper.info3SignLocation = new Location(world, 485, 78, -7);
		KitCreationHelper.info4SignLocation = new Location(world, 485, 77, -5);
		KitCreationHelper.info5SignLocation = new Location(world, 485, 77, -6);
		KitCreationHelper.info6SignLocation = new Location(world, 485, 77, -7);
		KitCreationHelper.info7SignLocation = new Location(world, 485, 76, -5);
		KitCreationHelper.info8SignLocation = new Location(world, 485, 76, -6);
		KitCreationHelper.info9SignLocation = new Location(world, 485, 76, -7);

		KitCreationHelper.knockbackSignLocation = new Location(world, 493, 77, -5);
		KitCreationHelper.featherFallingSignLocation = new Location(world, 493, 76, -6);
		KitCreationHelper.fireAspectSignLocation = new Location(world, 493, 76, -5);
		KitCreationHelper.flameSignLocation = new Location(world, 493, 77, -7);
		KitCreationHelper.infinitySignLocation = new Location(world, 493, 79, -6);
		KitCreationHelper.powerSignLocation = new Location(world, 493, 78, -7);
		KitCreationHelper.protectionSignLocation = new Location(world, 493, 78, -6);
		KitCreationHelper.punchSignLocation = new Location(world, 493, 76, -7);
		KitCreationHelper.sharpnessSignLocation = new Location(world, 493, 78, -5);
		KitCreationHelper.unbreakingSignLocation = new Location(world, 493, 77, -6);

		KitCreationHelper.spawnSignLocation = new Location(world, 490, 77, -2);
		KitCreationHelper.saveKitSignLocation = new Location(world, 490, 78, -2);
		KitCreationHelper.loadKitSignLocation = new Location(world, 488, 78, -2);
		KitCreationHelper.loadDefaultKitSignLocation = new Location(world, 488, 77, -2);
		KitCreationHelper.clearInventorySignLocation = new Location(world, 489, 79, -2);

		// Assign chest location
		KitCreationHelper.kitChestLocation = new Location(world, 489, 76, -2);

		// Create customizable kit items
		KitCreationHelper.kitWeaponsItem = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Weapons");

		KitCreationHelper.kitArmorItem = ItemStackUtil.createItem(Material.DIAMOND_HELMET, ChatColor.GREEN + "Armor");

		KitCreationHelper.kitFoodItem = ItemStackUtil.createItem(Material.BREAD, ChatColor.GREEN + "Food");

		KitCreationHelper.kitPotionsItem = ItemStackUtil.createItem(Material.POTION, ChatColor.GREEN + "Potions");

		KitCreationHelper.kitOtherItem = ItemStackUtil.createItem(Material.ENDER_PEARL, ChatColor.GREEN + "Other");

		// Create default kit inventory
		Inventory customKitDefaultInventory = PotPvP.getInstance().getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");
		customKitDefaultInventory.addItem(KitCreationHelper.kitWeaponsItem, KitCreationHelper.kitArmorItem,
				KitCreationHelper.kitFoodItem, KitCreationHelper.kitPotionsItem,
				KitCreationHelper.kitOtherItem);
		customKitDefaultInventory.setItem(53, SmellyInventory.getCloseInventoryItem());
		KitCreationHelper.customKitDefaultInventory = customKitDefaultInventory;

		// Cache the kitruleset inventories
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			KitCreationHelper.kitInventories.put(kitRuleSet.getName(), kitRuleSet.getKitCreationInventory());
		}

		//Weapons
		Inventory customKitWeaponInventory = PotPvP.getInstance().getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

        customKitWeaponInventory.setItem(0, ItemStackUtil.WOOD_SWORD);
        customKitWeaponInventory.setItem(1, ItemStackUtil.STONE_SWORD);
        customKitWeaponInventory.setItem(2, ItemStackUtil.GOLD_SWORD);
        customKitWeaponInventory.setItem(3, ItemStackUtil.IRON_SWORD);
        customKitWeaponInventory.setItem(4, ItemStackUtil.DIAMOND_SWORD);
        customKitWeaponInventory.setItem(5, ItemStackUtil.FISHING_ROD);

        customKitWeaponInventory.setItem(9, ItemStackUtil.WOOD_AXE);
        customKitWeaponInventory.setItem(10, ItemStackUtil.STONE_AXE);
        customKitWeaponInventory.setItem(11, ItemStackUtil.GOLD_AXE);
        customKitWeaponInventory.setItem(12, ItemStackUtil.IRON_AXE);
        customKitWeaponInventory.setItem(13, ItemStackUtil.DIAMOND_AXE);

		customKitWeaponInventory.setItem(27, ItemStackUtil.BOW);
		customKitWeaponInventory.setItem(36, ItemStackUtil.ARROW);
		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			// TODO: MOJANG BUG
			//customKitWeaponInventory.setItem(37, ItemStackUtil.SPECTRAL_ARROW);
			//customKitWeaponInventory.setItem(38, ItemStackUtil.TIPPED_ARROW);
		}

        customKitWeaponInventory.setItem(53, SmellyInventory.getBackInventoryItem());

		KitCreationHelper.customKitWeaponInventory = customKitWeaponInventory;

        //Armor
		Inventory customKitArmorInventory = PotPvP.getInstance().getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

		customKitArmorInventory.setItem(0, ItemStackUtil.LEATHER_HELMET);
		customKitArmorInventory.setItem(1, ItemStackUtil.CHAINMAIL_HELMET);
		customKitArmorInventory.setItem(2, ItemStackUtil.GOLD_HELMET);
		customKitArmorInventory.setItem(3, ItemStackUtil.IRON_HELMET);
		customKitArmorInventory.setItem(4, ItemStackUtil.DIAMOND_HELMET);

        customKitArmorInventory.setItem(9, ItemStackUtil.LEATHER_CHESTPLATE);
        customKitArmorInventory.setItem(10, ItemStackUtil.CHAINMAIL_CHESTPLATE);
        customKitArmorInventory.setItem(11, ItemStackUtil.GOLD_CHESTPLATE);
        customKitArmorInventory.setItem(12, ItemStackUtil.IRON_CHESTPLATE);
        customKitArmorInventory.setItem(13, ItemStackUtil.DIAMOND_CHESTPLATE);

        customKitArmorInventory.setItem(18, ItemStackUtil.LEATHER_LEGGINGS);
        customKitArmorInventory.setItem(19, ItemStackUtil.CHAINMAIL_LEGGINGS);
        customKitArmorInventory.setItem(20, ItemStackUtil.GOLD_LEGGINGS);
        customKitArmorInventory.setItem(21, ItemStackUtil.IRON_LEGGINGS);
        customKitArmorInventory.setItem(22, ItemStackUtil.DIAMOND_LEGGINGS);

        customKitArmorInventory.setItem(27, ItemStackUtil.LEATHER_BOOTS);
        customKitArmorInventory.setItem(28, ItemStackUtil.CHAINMAIL_BOOTS);
        customKitArmorInventory.setItem(29, ItemStackUtil.GOLD_BOOTS);
        customKitArmorInventory.setItem(30, ItemStackUtil.IRON_BOOTS);
        customKitArmorInventory.setItem(31, ItemStackUtil.DIAMOND_BOOTS);

		if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			customKitArmorInventory.setItem(15, ItemStackUtil.ELYTRA);
			customKitArmorInventory.setItem(24, ItemStackUtil.SHIELD);
		}

		customKitArmorInventory.setItem(53, SmellyInventory.getBackInventoryItem());
		KitCreationHelper.customKitArmorInventory = customKitArmorInventory;

        //Food/Non-Potion Consumables
		Inventory customKitFoodInventory = PotPvP.getInstance().getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

		customKitFoodInventory.addItem(ItemStackUtil.ALL_FOOD());
        customKitFoodInventory.addItem(ItemStackUtil.GOLDEN_APPLE);
        customKitFoodInventory.addItem(ItemStackUtil.createGoldenHead());
        customKitFoodInventory.addItem(ItemStackUtil.GOD_APPLE);

		customKitFoodInventory.setItem(53, SmellyInventory.getBackInventoryItem());
		KitCreationHelper.customKitFoodInventory = customKitFoodInventory;

        //Potions
		Inventory customKitPotionsInventory = PotPvP.getInstance().getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

        customKitPotionsInventory.setItem(0, ItemStackUtil.SWIFTNESS_POTION);
        customKitPotionsInventory.setItem(1, ItemStackUtil.STRENGTH_POTION);
        customKitPotionsInventory.setItem(2, ItemStackUtil.REGENERATION_POTION);
        customKitPotionsInventory.setItem(3, ItemStackUtil.FIRE_RESISTANCE_POTION);
        customKitPotionsInventory.setItem(4, ItemStackUtil.HEALING_POTION);
        customKitPotionsInventory.setItem(5, ItemStackUtil.POISON_SPLASH);
        customKitPotionsInventory.setItem(6, ItemStackUtil.WEAKNESS_SPLASH);
        customKitPotionsInventory.setItem(7, ItemStackUtil.SLOWNESS_SPLASH);
        customKitPotionsInventory.setItem(8, ItemStackUtil.HARMING_SPLASH);

        customKitPotionsInventory.setItem(9, ItemStackUtil.SWIFTNESS_POTION_EXT);
        customKitPotionsInventory.setItem(10, ItemStackUtil.STRENGTH_POTION_EXT);
        customKitPotionsInventory.setItem(11, ItemStackUtil.REGENERATION_POTION_EXT);
        customKitPotionsInventory.setItem(12, ItemStackUtil.FIRE_RESISTANCE_POTION_EXT);
        customKitPotionsInventory.setItem(13, ItemStackUtil.HEALING_POTION_II);
        customKitPotionsInventory.setItem(14, ItemStackUtil.POISON_SPLASH_EXT);
        customKitPotionsInventory.setItem(15, ItemStackUtil.WEAKNESS_SPLASH_EXT);
        customKitPotionsInventory.setItem(16, ItemStackUtil.SLOWNESS_SPLASH_EXT);
        customKitPotionsInventory.setItem(17, ItemStackUtil.HARMING_SPLASH_II);

        customKitPotionsInventory.setItem(18, ItemStackUtil.SWIFTNESS_POTION_II);
        customKitPotionsInventory.setItem(19, ItemStackUtil.STRENGTH_POTION_II);
        customKitPotionsInventory.setItem(20, ItemStackUtil.REGENERATION_POTION_II);
        customKitPotionsInventory.setItem(21, ItemStackUtil.FIRE_RESISTANCE_SPLASH);
        customKitPotionsInventory.setItem(22, ItemStackUtil.HEALING_SPLASH);
        customKitPotionsInventory.setItem(23, ItemStackUtil.POISON_SPLASH_II);

        customKitPotionsInventory.setItem(27, ItemStackUtil.SWIFTNESS_SPLASH);
        customKitPotionsInventory.setItem(28, ItemStackUtil.STRENGTH_SPLASH);
        customKitPotionsInventory.setItem(29, ItemStackUtil.REGENERATION_SPLASH);
        customKitPotionsInventory.setItem(30, ItemStackUtil.FIRE_RESISTANCE_SPLASH_EXT);
        customKitPotionsInventory.setItem(31, ItemStackUtil.HEALING_SPLASH_II);

        customKitPotionsInventory.setItem(36, ItemStackUtil.SWIFTNESS_SPLASH_EXT);
        customKitPotionsInventory.setItem(37, ItemStackUtil.STRENGTH_SPLASH_EXT);
        customKitPotionsInventory.setItem(38, ItemStackUtil.REGENERATION_SPLASH_EXT);

        customKitPotionsInventory.setItem(45, ItemStackUtil.SWIFTNESS_SPLASH_II);
        customKitPotionsInventory.setItem(46, ItemStackUtil.STRENGTH_SPLASH_II);
        customKitPotionsInventory.setItem(47, ItemStackUtil.REGENERATION_SPLASH_II);
		customKitPotionsInventory.setItem(53, SmellyInventory.getBackInventoryItem());

		KitCreationHelper.customKitPotionsInventory = customKitPotionsInventory;

        //Other Items
		Inventory customKitOtherInventory = PotPvP.getInstance().getServer().createInventory(null, 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Customize Kit");

        customKitOtherInventory.addItem(ItemStackUtil.ENDER_PEARL);
        customKitOtherInventory.addItem(ItemStackUtil.MILK_BUCKET);

		customKitOtherInventory.setItem(53, SmellyInventory.getBackInventoryItem());

		KitCreationHelper.customKitOtherInventory = customKitOtherInventory;

		// Store enchantments for signs
		KitCreationHelper.enchantmentNames.put(Enchantment.KNOCKBACK, "Knockback");

		KitCreationHelper.enchantmentNames.put(Enchantment.FIRE_ASPECT, "Fire Aspect");

		KitCreationHelper.enchantmentNames.put(Enchantment.DAMAGE_ALL, "Sharpness");

		KitCreationHelper.enchantmentNames.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");

		KitCreationHelper.enchantmentNames.put(Enchantment.DURABILITY, "Unbreaking");

		KitCreationHelper.enchantmentNames.put(Enchantment.ARROW_KNOCKBACK, "Punch");

		KitCreationHelper.enchantmentNames.put(Enchantment.ARROW_INFINITE, "Infinity");

		KitCreationHelper.enchantmentNames.put(Enchantment.ARROW_FIRE, "Flame");

		KitCreationHelper.enchantmentNames.put(Enchantment.ARROW_DAMAGE, "Power");

		KitCreationHelper.enchantmentNames.put(Enchantment.PROTECTION_FALL, "Falling");
	}

	public static boolean isKitChest(Block block) {
		return block.getLocation().equals(KitCreationHelper.kitChestLocation);
	}

	public static boolean isKitInventory(Inventory inventory) {
		return inventory != null && ChatColor.stripColor(inventory.getName()).equals("Customize Kit");
	}

	public static boolean isWeaponsItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(KitCreationHelper.kitWeaponsItem.getItemMeta().getDisplayName());
	}

	public static boolean isArmorItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(KitCreationHelper.kitArmorItem.getItemMeta().getDisplayName());
	}

	public static boolean isFoodItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(KitCreationHelper.kitFoodItem.getItemMeta().getDisplayName());
	}

	public static boolean isPotionsItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(KitCreationHelper.kitPotionsItem.getItemMeta().getDisplayName());
	}

	public static boolean isOtherItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(KitCreationHelper.kitOtherItem.getItemMeta().getDisplayName());
	}

	public static void openKitInventory(Player player) {
		KitRuleSet kitCreating = GroupStateMachine.kitCreationState.getKitCreator(player).getKitRuleSet();
		if (kitCreating instanceof CustomRuleSet) {
			KitCreationHelper.openCustomKitDefaultInventory(player);
		} else {
			BukkitUtil.openInventory(player, KitCreationHelper.kitInventories.get(kitCreating.getName()));
		}
	}

	public static void openCustomKitDefaultInventory(Player player) {
		BukkitUtil.openInventory(player, KitCreationHelper.customKitDefaultInventory);
	}

	public static void openCustomKitWeaponInventory(Player player) {
		BukkitUtil.openInventory(player, KitCreationHelper.customKitWeaponInventory);
	}

	public static void openCustomKitArmorInventory(Player player) {
		BukkitUtil.openInventory(player, KitCreationHelper.customKitArmorInventory);
	}

	public static void openCustomKitFoodInventory(Player player) {
		BukkitUtil.openInventory(player, KitCreationHelper.customKitFoodInventory);
	}

	public static void openCustomKitPotionsInventory(Player player) {
		BukkitUtil.openInventory(player, KitCreationHelper.customKitPotionsInventory);
	}

	public static void openCustomKitOtherInventory(Player player) {
		BukkitUtil.openInventory(player, KitCreationHelper.customKitOtherInventory);
	}

	public static void handleEnchant(Player player, ItemStack enchantClicked) {
		ItemStack item = player.getItemInHand();
		KitCreator kitCreator = GroupStateMachine.kitCreationState.getKitCreator(player);

		// Fail-safe checks
		if (item == null) {
			player.sendMessage(ChatColor.RED + "Hold the item to enchant in your hand");
			return;
		}

		if (!(kitCreator.getKitRuleSet() instanceof EventRuleSet) && !kitCreator.getCurrentEnchantment().canEnchantItem(item)) {
			player.sendMessage(ChatColor.RED + "Cannot put enchant on this item");
			return;
		}

		// Get enchantment level
		String itemName = ChatColor.stripColor(enchantClicked.getItemMeta().getDisplayName());
		int enchantNameSize = KitCreationHelper.enchantmentNames.get(kitCreator.getCurrentEnchantment()).length();

		int enchantmentLevel = Gberry.fromRomanNumeral(itemName.substring(enchantNameSize + 1));

		item.addUnsafeEnchantment(kitCreator.getCurrentEnchantment(), enchantmentLevel);

		// For armor items
		player.updateInventory();

		player.sendMessage(ChatColor.GREEN + "Successfully applied enchantment");
	}

	public static boolean checkAndHandleEnchantSigns(Block block, Player player) {
		ItemStack item = player.getItemInHand();
		Enchantment enchantment = null;

		Location location = block.getLocation();
		if (location.equals(KitCreationHelper.knockbackSignLocation)) {
		 	enchantment = Enchantment.KNOCKBACK;
		} else if (location.equals(KitCreationHelper.featherFallingSignLocation)) {
			enchantment = Enchantment.PROTECTION_FALL;
		} else if (location.equals(KitCreationHelper.fireAspectSignLocation)) {
			enchantment = Enchantment.FIRE_ASPECT;
		} else if (location.equals(KitCreationHelper.flameSignLocation)) {
			enchantment = Enchantment.ARROW_FIRE;
		} else if (location.equals(KitCreationHelper.infinitySignLocation)) {
			enchantment = Enchantment.ARROW_INFINITE;
		} else if (location.equals(KitCreationHelper.powerSignLocation)) {
			enchantment = Enchantment.ARROW_DAMAGE;
		} else if (location.equals(KitCreationHelper.protectionSignLocation)) {
			enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
		} else if (location.equals(KitCreationHelper.punchSignLocation)) {
			enchantment = Enchantment.ARROW_KNOCKBACK;
		} else if (location.equals(KitCreationHelper.sharpnessSignLocation)) {
			enchantment = Enchantment.DAMAGE_ALL;
		} else if (location.equals(KitCreationHelper.unbreakingSignLocation)) {
			enchantment = Enchantment.DURABILITY;
		} else { // Not an enchant sign
			return false;
		}

		KitCreator kitCreator = GroupStateMachine.kitCreationState.getKitCreator(player);

		Integer maxLevel = kitCreator.getKitRuleSet().getValidEnchantments().get(enchantment);

		// Null check
		if (maxLevel == null) {
			// For armor items
			player.updateInventory();

			return true;
		}

		// Player has item in hand?
		if (item == null || item.getType().equals(Material.AIR)) {
			// For armor items
			player.updateInventory();

			player.sendMessage(ChatColor.RED + "Hold the item to enchant in your hand");
			return true;
		}

		// Can enchant item in hand?
		if (!(kitCreator.getKitRuleSet() instanceof EventRuleSet) && !enchantment.canEnchantItem(item)) {
			// For armor items
			player.updateInventory();

			player.sendMessage(ChatColor.RED + "Cannot put this enchant on the item in your hand");
			return true;
		}

		// Apply max enchant if not custom/event rule set
		if (kitCreator.getKitRuleSet() instanceof CustomRuleSet) {
			KitCreationHelper.openEnchantmentSelectionInventory(enchantment, kitCreator, player);
		} else {
			// Apply max enchant if not knockback
			if (enchantment == Enchantment.KNOCKBACK) {
				if (maxLevel == 1) {
					item.addUnsafeEnchantment(enchantment, maxLevel);
				} else {
					KitCreationHelper.openEnchantmentSelectionInventory(enchantment, kitCreator, player);
				}
			} else {
				item.addUnsafeEnchantment(enchantment, maxLevel);

				player.sendMessage(ChatColor.GREEN + "Successfully applied enchantment");
			}
		}

		// For armor items
		player.updateInventory();

		return true;
	}

	public static boolean isEnchantmentSelectionInventory(Inventory inventory) {
		return inventory != null && inventory.getName().equals(ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Enchant");
	}

	public static void openEnchantmentSelectionInventory(Enchantment enchantment, KitCreator kitCreator, Player player) {
		Integer maxLevel = kitCreator.getKitRuleSet().getValidEnchantments().get(enchantment);

		// Do nothing if enchantment isn't valid (sign is blank)
		if (maxLevel == null) return;

		String niceEnchantName = KitCreationHelper.enchantmentNames.get(enchantment);

		Inventory inventory = PotPvP.getInstance().getServer().createInventory(null, 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Enchant");
		for (int i = 1; i < maxLevel + 1; i++) {
			ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GREEN + niceEnchantName + " " + Gberry.toRomanNumeral(i));
			//itemMeta.addStoredEnchant(enchantment, i, true); // Already in the item's name
			item.setItemMeta(itemMeta);
			inventory.addItem(item);
		}

		// Store enchant
		kitCreator.setCurrentEnchantment(enchantment);

		// Add close inventory item
		inventory.setItem(17, SmellyInventory.getCloseInventoryItem());

		BukkitUtil.openInventory(player, inventory);
	}

	public static boolean isSaveKitSignLocation(Block block) {
		return block.getLocation().equals(KitCreationHelper.saveKitSignLocation);
	}

	public static boolean isLoadKitSignLocation(Block block) {
		return block.getLocation().equals(KitCreationHelper.loadKitSignLocation);
	}

	public static boolean isLoadDefaultKitSignLocation(Block block) {
		return block.getLocation().equals(KitCreationHelper.loadDefaultKitSignLocation);
	}

	public static Location getLoadDefaultKitSignLocation() {
		return KitCreationHelper.loadDefaultKitSignLocation;
	}

	public static boolean isClearInventorySignLocation(Block block) {
		return block.getLocation().equals(KitCreationHelper.clearInventorySignLocation);
	}

	public static Location getClearInventorySignLocation() {
		return KitCreationHelper.clearInventorySignLocation;
	}

	public static boolean isSpawnSignLocation(Block block) {
		return block.getLocation().equals(KitCreationHelper.spawnSignLocation);
	}

	public static class KitCreator {

		private Player player;
		private KitRuleSet kitRuleSet;
		private Enchantment currentEnchantment;

		private boolean updatedSigns = false;

		public KitCreator(Player player, KitRuleSet kitRuleSet) {
			this.player = player;
			this.kitRuleSet = kitRuleSet;

            // Update signs here first so they are in our queue
            this.updateSigns();

			// Teleport them (Group should only contain 1 player at any time within this state)
			KitCreationState.teleportToKitCreationArea(player);

			// Store in our map
			GroupStateMachine.kitCreationState.addKitCreator(player, this);

			// Load the kit for the group (Group should only contain 1 player at any time within this state)
			KitHelper.loadKit(player, kitRuleSet);
		}

		public KitCreator(Player player, KitRuleSet kitRuleSet, int kitNumber) {
			this.player = player;
			this.kitRuleSet = kitRuleSet;

            // Update signs here firsto so they are in our queue
            this.updateSigns();

			// Teleport them (Group should only contain 1 player at any time within this state)
			KitCreationState.teleportToKitCreationArea(player);

			// Store in our map
			GroupStateMachine.kitCreationState.addKitCreator(player, this);

			// Load the kit for the group (Group should only contain 1 player at any time within this state)
			KitHelper.loadKit(player, kitRuleSet, kitNumber);
		}

		public void handleKitInfoSigns() {
			PotPvP.sendSignChange(this.player, KitCreationHelper.info1SignLocation, this.kitRuleSet.getInfo1Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info2SignLocation, this.kitRuleSet.getInfo2Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info3SignLocation, this.kitRuleSet.getInfo3Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info4SignLocation, this.kitRuleSet.getInfo4Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info5SignLocation, this.kitRuleSet.getInfo5Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info6SignLocation, this.kitRuleSet.getInfo6Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info7SignLocation, this.kitRuleSet.getInfo7Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info8SignLocation, this.kitRuleSet.getInfo8Sign());
			PotPvP.sendSignChange(this.player, KitCreationHelper.info9SignLocation, this.kitRuleSet.getInfo9Sign());
		}

		public void handleEnchantSigns() {
			String[] lines = new String[4];

			// Knockback
			Integer maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.KNOCKBACK);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (maxLevel != 1 || this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Knockback";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.knockbackSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Knockback " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.knockbackSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.knockbackSignLocation, lines);
			}

			// Feather Falling
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.PROTECTION_FALL);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Feather";
					lines[2] = "Falling";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.featherFallingSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Feather";
					lines[2] = "Falling " + Gberry.toRomanNumeral(maxLevel);
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.featherFallingSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.featherFallingSignLocation, lines);
			}

			// Fire Aspect
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.FIRE_ASPECT);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Fire Aspect";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.fireAspectSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Fire Aspect " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.fireAspectSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.fireAspectSignLocation, lines);
			}

			// Flame
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.ARROW_FIRE);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Flame";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.flameSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Flame " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.flameSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.flameSignLocation, lines);
			}

			// Infinity
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.ARROW_INFINITE);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Infinity";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.infinitySignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Infinity " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.infinitySignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.infinitySignLocation, lines);
			}

			// Power
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.ARROW_DAMAGE);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Power";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.powerSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Power " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.powerSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.powerSignLocation, lines);
			}

			// Protection
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.PROTECTION_ENVIRONMENTAL);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Protection";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.protectionSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Protection " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.protectionSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.protectionSignLocation, lines);
			}

			// Punch
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.ARROW_KNOCKBACK);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Punch";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.punchSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Punch " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.punchSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.punchSignLocation, lines);
			}

			// Sharpness
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.DAMAGE_ALL);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Sharpness";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.sharpnessSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Sharpness " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.sharpnessSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.sharpnessSignLocation, lines);
			}

			// Unbreaking
			lines = new String[4];
			maxLevel = this.kitRuleSet.getValidEnchantments().get(Enchantment.DURABILITY);
			if (maxLevel != null) { // Is this even a valid enchant?
				if (this.kitRuleSet instanceof CustomRuleSet) {
					lines[0] = "";
					lines[1] = "Unbreaking";
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.unbreakingSignLocation, lines);
				} else {
					lines[0] = "";
					lines[1] = "Unbreaking " + Gberry.toRomanNumeral(maxLevel);
					lines[2] = "";
					lines[3] = "";
					PotPvP.sendSignChange(this.player, KitCreationHelper.unbreakingSignLocation, lines);
				}
			} else { // Leave sign blank
				lines[0] = "";
				lines[1] = "";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.unbreakingSignLocation, lines);
			}
		}

		public void handleKitManagementSigns() {
			String[] lines = new String[4];

			// Save kit sign
			lines[0] = "Save";
			lines[1] = this.kitRuleSet.getName();
			lines[2] = "Kit";
			lines[3] = "";
			PotPvP.sendSignChange(this.player, KitCreationHelper.saveKitSignLocation, lines);


			// Load kit sign
			lines = new String[4];
			lines[0] = "Load";
			lines[1] = this.kitRuleSet.getName();
			lines[2] = "Kit";
			lines[3] = "";
			PotPvP.sendSignChange(this.player, KitCreationHelper.loadKitSignLocation, lines);


			// Load default kit sign
			lines = new String[4];
			if (!(this.kitRuleSet instanceof CustomRuleSet)) {
				lines[0] = "Load Default";
				lines[1] = this.kitRuleSet.getName();
				lines[2] = "Kit";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.loadDefaultKitSignLocation, lines);

				lines = new String[4];
			} else {
				// Hide this sign for custom kits
				PotPvP.sendBlockChange(this.player, KitCreationHelper.loadDefaultKitSignLocation, Material.AIR);
			}

			// Clear inventory sign
			if (this.kitRuleSet.usesCustomChests()) {
				lines[0] = "Clear";
				lines[1] = "Inventory";
				lines[2] = "";
				lines[3] = "";
				PotPvP.sendSignChange(this.player, KitCreationHelper.clearInventorySignLocation, lines);
			} else {
				PotPvP.sendBlockChange(this.player, KitCreationHelper.clearInventorySignLocation, Material.AIR);
			}
		}

		public Player getPlayer() {
			return this.player;
		}

		public KitRuleSet getKitRuleSet() {
			return this.kitRuleSet;
		}

		public Enchantment getCurrentEnchantment() {
			return currentEnchantment;
		}

		public void setCurrentEnchantment(Enchantment currentEnchantment) {
			this.currentEnchantment = currentEnchantment;
		}

		public boolean areSignsUpdated() {
			return this.updatedSigns;
		}

		public void updateSigns() {
			this.updatedSigns = true;

			//BukkitUtil.runTaskLater(new Runnable() {
			//	@Override
			//	public void run() {
					KitCreator.this.handleKitInfoSigns();
					KitCreator.this.handleEnchantSigns();
					KitCreator.this.handleKitManagementSigns();
			//	}
			//}, 10L);
		}

	}

}
