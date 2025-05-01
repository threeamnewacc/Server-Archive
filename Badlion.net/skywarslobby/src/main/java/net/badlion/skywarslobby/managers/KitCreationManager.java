package net.badlion.skywarslobby.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.skywarslobby.SkyWarsLobby;
import net.badlion.skywarslobby.inventories.KitSelectionInventory;
import net.badlion.skywarslobby.kits.PlayerKits;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitCreationManager extends net.badlion.gberry.utils.BukkitUtil.Listener {

	private static int MAX_TOTAL_WEIGHT = 10;

	private static KitCreationScreenHandler kitCreationScreenHandler;

	private static Location kitSpawnLocation;

	private static Location kitChestLocation;

	private static Location spawnSignLocation;
	private static Location saveKitSignLocation;
	private static Location loadKitSignLocation;

	private static ItemStack clearInventoryItem;

	private static Map<ItemStack, List<ItemStack>> kitChestItems = new HashMap<>();

	private static Set<UUID> wasNotHidingPlayer = new HashSet<>();

	public static void initialize() {
		new KitCreationManager();

		KitCreationManager.kitCreationScreenHandler = new KitCreationScreenHandler();

		KitCreationManager.kitSpawnLocation = new Location(Bukkit.getWorld("world"), -0.5, 252, 0.5, 90, 0);

		KitCreationManager.kitChestLocation = new Location(Bukkit.getWorld("world"), -4, 252, 0);

		KitCreationManager.spawnSignLocation = new Location(Bukkit.getWorld("world"), -3, 254, 0);
		KitCreationManager.saveKitSignLocation = new Location(Bukkit.getWorld("world"), -3, 253, 1);
		KitCreationManager.loadKitSignLocation = new Location(Bukkit.getWorld("world"), -3, 253, -1);

		// Create all the unlockedItems for the kit chest
		KitCreationManager.clearInventoryItem = ItemStackUtil.createItem(Material.PAINTING, ChatColor.AQUA + "Clear Inventory");

		List<ItemStack> weaponItems = new ArrayList<>();
		ItemStack weaponItem = ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Weapons");
		KitCreationManager.kitChestItems.put(weaponItem, weaponItems);

		weaponItems.add(KitCreationManager.createWeightedItem(0, 3, Material.STONE_SWORD));
		weaponItems.add(KitCreationManager.createWeightedItem(1, 6, Material.BOW));
		weaponItems.add(KitCreationManager.createWeightedItem(2, 1, Material.ARROW));

		List<ItemStack> armorItems = new ArrayList<>();
		ItemStack armorItem = ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Armor");
		KitCreationManager.kitChestItems.put(armorItem, armorItems);

		armorItems.add(KitCreationManager.createWeightedItem(3, 2, Material.LEATHER_HELMET));
		armorItems.add(KitCreationManager.createWeightedItem(4, 4, Material.LEATHER_CHESTPLATE));
		armorItems.add(KitCreationManager.createWeightedItem(5, 3, Material.CHAINMAIL_BOOTS));

		List<ItemStack> blockItems = new ArrayList<>();
		ItemStack blockItem = ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Blocks");
		KitCreationManager.kitChestItems.put(blockItem, blockItems);

		blockItems.add(KitCreationManager.createWeightedItem(6, 3, Material.COBBLESTONE, 13));
		blockItems.add(KitCreationManager.createWeightedItem(7, 4, Material.WOOD, 18));
		blockItems.add(KitCreationManager.createWeightedItem(8, 8, Material.DIAMOND_ORE, 3));

		List<ItemStack> miscItems = new ArrayList<>();
		ItemStack miscItem = ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Miscellaneous");
		KitCreationManager.kitChestItems.put(miscItem, miscItems);

		miscItems.add(KitCreationManager.createWeightedItem(9, 3, Material.EGG, 6));
		miscItems.add(KitCreationManager.createWeightedItem(10, 6, Material.SNOW_BALL, 13));
		miscItems.add(KitCreationManager.createWeightedItem(11, 1, Material.WATER_BUCKET));
		miscItems.add(KitCreationManager.createWeightedItem(12, 1, Material.EXP_BOTTLE, 3));
	}

	@EventHandler
	public void onPlayerInteractEvent(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.WALL_SIGN
					|| event.getClickedBlock().getType() == Material.CHEST) {
				Location location = event.getClickedBlock().getLocation();
				if (KitCreationManager.isKitChest(location)) {
				 	KitCreationManager.openKitChestInventory(player);
				} else if (KitCreationManager.isSpawnSign(location)) {
					if (KitCreationManager.wasNotHidingPlayer.remove(player.getUniqueId())) {
						SkyWarsLobby.getInstance().toggleHidingPlayers(player, false);
					}

					SkyWarsKitManager.getPlayerKits(player).setKitEditing(null);

					player.sendMessage(ChatColor.AQUA + "Teleporting to spawn...");

					SkyWarsLobby.getInstance().teleportToSpawnAndGiveItems(player);
				} else if (KitCreationManager.isSaveKitSign(location)) {
					SkyWarsKitManager.saveKit(player, SkyWarsKitManager.getPlayerKits(player).getKitEditing());
				} else if (KitCreationManager.isLoadKitSign(location)) {
					KitSelectionInventory.openKitInventory(player);
				} else {
					return;
				}

				event.setCancelled(true);
				event.setUseInteractedBlock(Event.Result.DENY);
			}
		}
	}

	public static void teleportToKitCreation(Player player) {
		// Hide all players
		if (!SkyWarsLobby.getInstance().isHidingPlayers(player)) {
			SkyWarsLobby.getInstance().toggleHidingPlayers(player, false);

			KitCreationManager.wasNotHidingPlayer.add(player.getUniqueId());
		}

		player.teleport(KitCreationManager.kitSpawnLocation);
	}

	public static void openKitChestInventory(Player player) {
		SmellyInventory smellyInventory = new SmellyInventory(KitCreationManager.kitCreationScreenHandler,
				54, ChatColor.AQUA + ChatColor.BOLD.toString() + "Kit Items");

		// Add the kit unlockedItems
		int counter = 0;
		for (ItemStack itemStack : KitCreationManager.kitChestItems.keySet()) {
			smellyInventory.getMainInventory().addItem(itemStack);

			// Create sub-inventoryContents
			Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), KitCreationManager.kitCreationScreenHandler,
					counter, 54, ChatColor.GREEN + itemStack.getItemMeta().getDisplayName());
			for (ItemStack itemStack2 : KitCreationManager.kitChestItems.get(itemStack)) {
				// Check if player has this item unlocked
				PlayerKits playerKits = SkyWarsKitManager.getPlayerKits(player);
				if (playerKits.hasUnlockedItem(itemStack2)) {
					inventory.addItem(itemStack2);
				} else {
					inventory.addItem(ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (short) 7,
							KitCreationManager.getFriendlyName(itemStack2.getType()), "",
							ChatColor.RED + "This item is not available to you", "",
							ChatColor.GOLD + "You can unlock this item", ChatColor.GOLD + "by opening crates."));
				}
			}

			counter++;
		}

		smellyInventory.getMainInventory().setItem(45, KitCreationManager.clearInventoryItem);

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	public static String getFriendlyName(Material mat) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.RESET);
		for (String str : mat.name().split("_"))
			sb.append(" ").append(Character.toUpperCase(str.charAt(0))).append(str.substring(1).toLowerCase());
		return sb.toString().trim().replace("Diode", "Redstone Repeater").replace("Thin Glass", "Glass Pane").replace("Wood ", "Wooden ");
	}

	public static boolean isKitChest(Location location) {
		return location.equals(KitCreationManager.kitChestLocation);
	}

	public static boolean isSpawnSign(Location location) {
		return location.equals(KitCreationManager.spawnSignLocation);
	}

	public static boolean isSaveKitSign(Location location) {
		return location.equals(KitCreationManager.saveKitSignLocation);
	}

	public static boolean isLoadKitSign(Location location) {
		return location.equals(KitCreationManager.loadKitSignLocation);
	}

	private static ItemStack createWeightedItem(int id, int weight, Material material) {
		return KitCreationManager.createWeightedItem(id, weight, material, (short) 0, 1);
	}


	private static ItemStack createWeightedItem(int id, int weight, Material material, int amount) {
		return KitCreationManager.createWeightedItem(id, weight, material, (short) 0, amount);
	}

	private static ItemStack createWeightedItem(int id, int weight, Material material, short data) {
		return KitCreationManager.createWeightedItem(id, weight, material, data, 1);
	}


	private static ItemStack createWeightedItem(int id, int weight, Material material, short data, int amount) {
		// Don't use Gberry methods since we want no display name
		ItemStack itemStack = new ItemStack(material, amount, data);

		// Add weight in lore
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.LIGHT_PURPLE + "Weight: " + weight);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);

		// Convert to NMS and store our ID
		net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		nmsItem.getTag().setInt("id", id);

		return CraftItemStack.asBukkitCopy(nmsItem);
	}

	public static int getItemID(ItemStack itemStack) {
		return CraftItemStack.asNMSCopy(itemStack).getTag().getInt("id");
	}

	public static int getItemWeight(ItemStack itemStack) {
		return Integer.valueOf(itemStack.getItemMeta().getLore().get(0).split(" ")[1]);
	}


	public static class KitCreationScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			// Main inventoryContents?
			if (fakeHolder.getParentInventory() == null) {
				// Clear inventoryContents item?
				if (slot == 45) {
					player.getInventory().clear();
					player.getInventory().setArmorContents(null);

					player.sendMessage(ChatColor.YELLOW + "Inventory cleared");

					BukkitUtil.closeInventory(player);
				} else {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				}
			} else { // They clicked on an item
				// Is this an unlocked item?
				PlayerKits playerKits = SkyWarsKitManager.getPlayerKits(player);
				if (playerKits.hasUnlockedItem(item)) {
					int itemWeight = KitCreationManager.getItemWeight(item);

					// Check total weight of their kit
					if (playerKits.getKitEditing().getTotalWeight() + itemWeight > KitCreationManager.MAX_TOTAL_WEIGHT) {
						player.sendMessage(ChatColor.RED + "Cannot add item. Your kit's weight is "
								+ playerKits.getKitEditing().getTotalWeight() + "/" + KitCreationManager.MAX_TOTAL_WEIGHT + ".");
						return;
					}

					// Add this item's weight to their kit's total weight
					playerKits.getKitEditing().addWeight(item);

					// Add to their inventory
					player.getInventory().addItem(item);
				} else {
					player.sendMessage(ChatColor.RED + "You don't have this item unlocked!");
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
