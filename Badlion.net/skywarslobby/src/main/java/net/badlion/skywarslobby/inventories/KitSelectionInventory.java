package net.badlion.skywarslobby.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.skywarslobby.kits.KitType;
import net.badlion.skywarslobby.kits.PlayerKits;
import net.badlion.skywarslobby.kits.SkyWarsKit;
import net.badlion.skywarslobby.managers.KitCreationManager;
import net.badlion.skywarslobby.managers.SkyWarsKitManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitSelectionInventory {

    public final static int TOTAL_KITS = 8;
    public final static int LION_KITS = 8;
    public final static int DONATOR_PLUS_KITS = 6;
    public final static int DONATOR_KITS = 4;
    public final static int FREE_KITS = 2;

	private static GamemodeSelectorInventoryScreenHandler gamemodeSelectorInventoryScreenHandler;
	private static KitSelectionInventoryScreenHandler kitSelectionInventoryScreenHandler;

	public static void initialize() {
		KitSelectionInventory.gamemodeSelectorInventoryScreenHandler = new GamemodeSelectorInventoryScreenHandler();
		KitSelectionInventory.kitSelectionInventoryScreenHandler = new KitSelectionInventoryScreenHandler();
	}

    public static void openKitInventory(Player player) {
        if (!player.isOp() && !player.getName().equals("Gorille")) {
			player.sendMessage(ChatColor.RED + "Feature not yet available.");
	        return;
		}

	    PlayerKits playerKits = SkyWarsKitManager.getPlayerKits(player);
	    if (!playerKits.isLoaded()) {
		    player.sendMessage(ChatColor.RED + "SkyWars kits not yet loaded. Try again.");
		    return;
	    }

	    SmellyInventory smellyInventory = new SmellyInventory(KitSelectionInventory.gamemodeSelectorInventoryScreenHandler, 9,
	    	    ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Gamemode");

	    // Add and create inventories for each kit type
	    for (KitType kitType : KitType.values()) {
		    smellyInventory.getMainInventory().addItem(kitType.getItem());

		    smellyInventory.createInventory(smellyInventory.getFakeHolder(), KitSelectionInventory.kitSelectionInventoryScreenHandler,
				    kitType.ordinal(), 18, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select " + kitType + " Kit to Edit");

		    KitSelectionInventory.fillKits(smellyInventory.getFakeHolder().getSubInventory(kitType.ordinal()), player, kitType);
	    }

	    BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
    }

	private static void fillKits(Inventory inventory, Player player, KitType kitType) {
		Map<Integer, SkyWarsKit> kits = SkyWarsKitManager.getAllKits(player, kitType);
		int i = 1;
		for (Map.Entry<Integer, SkyWarsKit> kitEntry : kits.entrySet()) {
			SkyWarsKit kit = kitEntry.getValue();
			ItemStack item = kit.getPreviewItem().clone();
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName(ChatColor.GREEN + "Kit #" + kit.getKitNumber());

			List<String> lore = new ArrayList<>();

			if (i > KitSelectionInventory.getNumOfAvailableKits(player)) {
				// Overwrite item with red glass pane
				item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);

				lore.add("");
				lore.add(ChatColor.RED + "This kit is not available to you");
				lore.add("");
				lore.add(ChatColor.GOLD + "To gain access to this rank buy");
				lore.add(ChatColor.GOLD + "a donator rank at http://store.badlion.net");
			}

			meta.setLore(lore);
			item.setItemMeta(meta);

			inventory.addItem(item);
			i += 1;
		}
	}

	private static int getNumOfAvailableKits(Player player) {
		if (player.hasPermission("badlion.lion")) {
			return KitSelectionInventory.LION_KITS;
		} else if (player.hasPermission("badlion.donatorplus")) {
			return KitSelectionInventory.DONATOR_PLUS_KITS;
		} else if (player.hasPermission("badlion.donator")) {
			return KitSelectionInventory.DONATOR_KITS;
		}

		return KitSelectionInventory.FREE_KITS;
	}

	private static class GamemodeSelectorInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

    private static class KitSelectionInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
	        KitType kitType = KitType.valueOf(fakeHolder.getInventory().getName().split(" ")[1].toUpperCase());

	        String[] parts = item.getItemMeta().getDisplayName().split(" "); // Get #1
	        int kitNumber = Integer.parseInt(parts[1].substring(1)); // Chop off the #

	        if (kitNumber > KitSelectionInventory.getNumOfAvailableKits(player)) {
		        player.sendMessage(ChatColor.RED + "You do not have access to this kit");
		        player.sendMessage(ChatColor.GOLD + "To gain access to this kit buy a donator rank at http://store.badlion.net/");

		        BukkitUtil.closeInventory(player);
		        return;
	        }

	        SkyWarsKit kit = SkyWarsKitManager.getKit(player, kitType, kitNumber);

	        PlayerKits playerKits = SkyWarsKitManager.getPlayerKits(player);

	        SkyWarsKitManager.loadKit(player, kit);

	        // Teleport them to kit creation
	        KitCreationManager.teleportToKitCreation(player);

	        playerKits.setKitEditing(kit);

	        BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
