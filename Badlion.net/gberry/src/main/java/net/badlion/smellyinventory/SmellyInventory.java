package net.badlion.smellyinventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SmellyInventory implements Listener {

	public static JavaPlugin plugin;

	private static boolean allowChestInventoryActions;

	// Inventory navigation items
	private static ItemStack backInventoryItem;
	private static ItemStack closeInventoryItem;

	private static EmptySmellyInventoryHandler emptySmellyInventoryHandler;

    private static boolean initialized = false;

	public static void initialize(JavaPlugin plugin, boolean allowChestInventoryActions) {
        if (SmellyInventory.initialized) {
            return;
        }

		SmellyInventory.plugin = plugin;

		SmellyInventory.allowChestInventoryActions = allowChestInventoryActions;

		plugin.getServer().getPluginManager().registerEvents(new SmellyInventoryListener(), plugin);

		// Inventory navigation items
		ItemStack backInventoryItem = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta backInventoryItemMeta = backInventoryItem.getItemMeta();
		backInventoryItemMeta.setDisplayName(ChatColor.GREEN + "Back");
		backInventoryItem.setItemMeta(backInventoryItemMeta);
		SmellyInventory.backInventoryItem = backInventoryItem;

		ItemStack closeInventoryItem = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta closeInventoryItemMeta = closeInventoryItem.getItemMeta();
		closeInventoryItemMeta.setDisplayName(ChatColor.GREEN + "Close");
		closeInventoryItem.setItemMeta(closeInventoryItemMeta);
		SmellyInventory.closeInventoryItem = closeInventoryItem;

		// Empty inventory handler
		SmellyInventory.emptySmellyInventoryHandler = new EmptySmellyInventoryHandler();

        SmellyInventory.initialized = true;
	}

	public static boolean allowChestInventoryActions() {
		return allowChestInventoryActions;
	}

	public static SmellyInventory getSmellyInventory(InventoryClickEvent event) {
		return SmellyInventory.getSmellyInventory(event.getView().getTopInventory());
	}

	public static SmellyInventory getSmellyInventory(InventoryCloseEvent event) {
		return SmellyInventory.getSmellyInventory(event.getView().getTopInventory());
	}

	public static SmellyInventory getSmellyInventory(Inventory inventory) {
		if (inventory != null && inventory.getHolder() instanceof FakeHolder) {
			return ((FakeHolder) inventory.getHolder()).getSmellyInventory();
		}
		return null;
	}

	public static boolean isBackInventoryItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(SmellyInventory.backInventoryItem.getItemMeta().getDisplayName());
	}

	public static ItemStack getBackInventoryItem() {
		return SmellyInventory.backInventoryItem;
	}

	public static boolean isCloseInventoryItem(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(SmellyInventory.closeInventoryItem.getItemMeta().getDisplayName());
	}

	public static ItemStack getCloseInventoryItem() {
		return SmellyInventory.closeInventoryItem;
	}

	public static EmptySmellyInventoryHandler getEmptySmellyInventoryHandler() {
		return SmellyInventory.emptySmellyInventoryHandler;
	}

	private static class EmptySmellyInventoryHandler implements SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {

		}

		@Override
		public void handleInventoryCloseEvent(FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	/*
	  SEPARATION
	 */

	private FakeHolder fakeHolder;

	private Inventory mainInventory;

	public SmellyInventory(SmellyInventoryHandler smellyInventoryHandler, int size, String name) {
		this(smellyInventoryHandler, name, size);
	}

	public SmellyInventory(SmellyInventoryHandler smellyInventoryHandler, String name, int size) {
		this.fakeHolder = new FakeHolder(this, smellyInventoryHandler);

		// Inventory title length check
		if (name.length() > 32) name = name.substring(0, 32);

		// Create the main inventory
		Inventory mainInventory = SmellyInventory.plugin.getServer().createInventory(this.fakeHolder, size, name);

		// Set close inventory item
		mainInventory.setItem(size - 1, SmellyInventory.getCloseInventoryItem());

		this.fakeHolder.setInventory(mainInventory);
		this.mainInventory = mainInventory;
	}

	public Inventory createInventory(FakeHolder parentInventoryHolder, SmellyInventoryHandler smellyInventoryHandler,
	                                 int slotPointer, int size, String name) {
		return this.createInventory(parentInventoryHolder, smellyInventoryHandler, slotPointer, name, size);
	}

	public Inventory createInventory(FakeHolder parentInventoryHolder, SmellyInventoryHandler smellyInventoryHandler,
	                                 int slotPointer, String name, int size) {
		// Create new FakeHolder for new inventory
		FakeHolder fakeHolder = new FakeHolder(this, smellyInventoryHandler, parentInventoryHolder.getInventory());

		// Inventory title length check
		if (name.length() > 32) name = name.substring(0, 32);

		Inventory inventory = SmellyInventory.plugin.getServer().createInventory(fakeHolder, size, name);

		// Set the inventory in the fake holder
		fakeHolder.setInventory(inventory);

		// Add new inventory as sub-inventory to parent inventory
		parentInventoryHolder.addSubInventory(slotPointer, inventory);

		// Set back inventory item
		inventory.setItem(size - 1, SmellyInventory.getBackInventoryItem());

		return inventory;
	}

	public FakeHolder getFakeHolder() {
		return fakeHolder;
	}

	public FakeHolder createFakeHolderForKitPreviews() {
		return new FakeHolder(this, SmellyInventory.emptySmellyInventoryHandler);
	}

	public Inventory getMainInventory() {
		return mainInventory;
	}

	public Inventory getParentInventory(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof FakeHolder) {
			return this.getParentInventory((FakeHolder) event.getView().getTopInventory().getHolder());
		}
		return null;
	}

	public Inventory getParentInventory(FakeHolder fakeHolder) {
		return fakeHolder.getParentInventory();
	}

	public void handleInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getView().getTopInventory();
		FakeHolder fakeHolder = ((FakeHolder) inventory.getHolder());

		// Retarded bug-abusing player check
		if (event.getRawSlot() >= inventory.getSize()) return;

		if (SimplePluginManager.extremeTesting) {
			long start = System.currentTimeMillis();
			fakeHolder.getSmellyInventoryHandler().handleInventoryClickEvent(fakeHolder, (Player) event.getWhoClicked(),
																			 event, event.getCurrentItem(), event.getRawSlot());
			long end = System.currentTimeMillis();

			if (end - start >= SimplePluginManager.extremeTestingThreshold) {
				Bukkit.getLogger().info("Found lag in " + fakeHolder.smellyInventory.getMainInventory().getName() + " (" + fakeHolder.getSmellyInventoryHandler() + ") with slot " + event.getRawSlot() + " and item " + event.getCurrentItem());
			}
		} else {
			fakeHolder.getSmellyInventoryHandler().handleInventoryClickEvent(fakeHolder, (Player) event.getWhoClicked(),
																			 event, event.getCurrentItem(), event.getRawSlot());
		}
	}

	public void handleInventoryClose(InventoryCloseEvent event) {
		Inventory inventory = event.getView().getTopInventory();
		FakeHolder fakeHolder = ((FakeHolder) inventory.getHolder());

		fakeHolder.getSmellyInventoryHandler().handleInventoryCloseEvent(fakeHolder, (Player) event.getPlayer(), event);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Inventory) {
			if (((Inventory) obj).getHolder() instanceof FakeHolder) {
				if (((FakeHolder) ((Inventory) obj).getHolder()).getSmellyInventory() == this) {
					return true;
				}
			}
		}
		return false;
	}

	public class FakeHolder implements InventoryHolder {

		private SmellyInventory smellyInventory;
		private SmellyInventoryHandler smellyInventoryHandler;

		private Inventory inventory;
		private Inventory parentInventory;
		private Map<Integer, Inventory> inventories = new HashMap<>();

		public FakeHolder(SmellyInventory smellyInventory, SmellyInventoryHandler smellyInventoryHandler) {
			this.smellyInventory = smellyInventory;
			this.smellyInventoryHandler = smellyInventoryHandler;
		}

		public FakeHolder(SmellyInventory smellyInventory, SmellyInventoryHandler smellyInventoryHandler, Inventory parentInventory) {
			this.smellyInventory = smellyInventory;
			this.smellyInventoryHandler = smellyInventoryHandler;

			this.parentInventory = parentInventory;
		}

		@Override
		public Inventory getInventory() {
			return this.inventory;
		}

		public void setInventory(Inventory inventory) {
			this.inventory = inventory;
		}

		public SmellyInventory getSmellyInventory() {
			return this.smellyInventory;
		}

		public SmellyInventoryHandler getSmellyInventoryHandler() {
			return this.smellyInventoryHandler;
		}

		public void setSmellyInventoryHandler(SmellyInventoryHandler smellyInventoryHandler) {
			this.smellyInventoryHandler = smellyInventoryHandler;
		}

		public void addSubInventory(int slot, Inventory inventory) {
			this.inventories.put(slot, inventory);
		}

		public Inventory getSubInventory(int slot) {
			return this.inventories.get(slot);
		}

		public Inventory getParentInventory() {
			return this.parentInventory;
		}

		public void setParentInventory(Inventory parentInventory) {
			this.parentInventory = parentInventory;
		}

		public boolean hasSubInventories() {
			return !this.inventories.isEmpty();
		}

	}

	public interface SmellyInventoryHandler {

		/**
		 * Handle an inventory click event
		 * @param player - Player who clicked
		 * @param event - InventoryClickEvent instance
		 * @param item - Item clicked, cannot be air/null
		 * @param slot - Slot clicked
		 */
		void handleInventoryClickEvent(FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot);

		/**
		 * Handle an inventory close event
		 * @param player - Player who clicked
		 * @param event - InventoryCloseEvent instance
		 */
		void handleInventoryCloseEvent(FakeHolder fakeHolder, Player player, InventoryCloseEvent event);


	}

}