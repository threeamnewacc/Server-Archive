package net.badlion.skywars.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.skywars.SkyWars;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitSelectorInventory {

    public final static int TOTAL_KITS = 8;
    public final static int LION_KITS = 8;
    public final static int DONATOR_PLUS_KITS = 6;
    public final static int DONATOR_KITS = 4;
    public final static int FREE_KITS = 2;

    public static void openKitInventory(Player player) {
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
        if (!mpgPlayer.isKitsLoaded()) {
            player.sendMessage(ChatColor.RED + "SkyWars kits not yet loaded. Try again.");
            return;
        }

        SmellyInventory smellyInventory = new SmellyInventory(new KitSelectorInventoryScreenHandler(), 18,
                                                ChatColor.AQUA + ChatColor.BOLD.toString() + "Select SkyWars Kit");

        // Add Default kit
        smellyInventory.getMainInventory().addItem(SkyWars.getInstance().getCurrentGame().getGamemode().getDefaultKit().getPreviewItem());

        Map<String, MPGKit> kits = MPGKitManager.getAllKits(player);
        int i = 1;
        for (Map.Entry<String, MPGKit> kitEntry : kits.entrySet()) {
            MPGKit kit = kitEntry.getValue();
            ItemStack item = kit.getPreviewItem().clone();
            ItemMeta meta = item.getItemMeta();

            if (!kit.getName().equals("")) {
                meta.setDisplayName(ChatColor.GREEN + kit.getName());
            } else {
                meta.setDisplayName(ChatColor.GREEN + "Kit #" + kit.getKitNumber());
            }

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "Kit #" + kit.getKitNumber());

            if (i > KitSelectorInventory.getNumOfAvailableKits(player)) {
                lore.add("");
                lore.add(ChatColor.RED + "This kit is not available to you");
                lore.add("");
                lore.add(ChatColor.GOLD + "To gain access to this rank buy");
                lore.add(ChatColor.GOLD + "a donator rank at http://store.badlion.net");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            smellyInventory.getMainInventory().addItem(item);
            i += 1;
        }

        while (i <= KitSelectorInventory.TOTAL_KITS) {
            ItemStack item;
            if (i > KitSelectorInventory.getNumOfAvailableKits(player)) {
                item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            } else {
                item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Kit #" + i);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "Kit #" + i);

            if (i > KitSelectorInventory.getNumOfAvailableKits(player)) {
                lore.add("");
                lore.add(ChatColor.RED + "This kit is not available to you");
                lore.add("");
                lore.add(ChatColor.GOLD + "To gain access to this kit buy a " + (player.hasPermission("badlion.donator") ? "higher" : ""));
                lore.add(ChatColor.GOLD + "donator rank at http://store.badlion.net");
            } else {
                lore.add("");
                lore.add(ChatColor.RED + "You have not yet made this kit");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            smellyInventory.getMainInventory().addItem(item);
            i += 1;
        }

        BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
    }

	private static int getNumOfAvailableKits(Player player) {
		if (player.hasPermission("badlion.lion")) {
			return KitSelectorInventory.LION_KITS;
		} else if (player.hasPermission("badlion.donatorplus")) {
			return KitSelectorInventory.DONATOR_PLUS_KITS;
		} else if (player.hasPermission("badlion.donator")) {
			return KitSelectorInventory.DONATOR_KITS;
		}

		return KitSelectorInventory.FREE_KITS;
	}

    private static class KitSelectorInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            // Always close kit
            BukkitUtil.closeInventory(player);

            // Default kit
            if (item.getItemMeta().getDisplayName().startsWith(ChatColor.RESET + "")) {
                MPGPlayerManager.getMPGPlayer(player.getUniqueId()).setKit(SkyWars.getInstance().getCurrentGame().getGamemode().getDefaultKit());
                player.sendMessage(ChatColor.GREEN + "Default kit selected");
                return;
            }

            String[] parts = item.getItemMeta().getLore().get(0).split(" "); // Get #1
            int kitNumber = Integer.parseInt(parts[1].substring(1)); // Chop off the #

            if (kitNumber > KitSelectorInventory.getNumOfAvailableKits(player)) {
                player.sendMessage(ChatColor.RED + "You do not have access to this kit");
                player.sendMessage(ChatColor.GOLD + "To gain access to this kit buy a donator rank at http://store.badlion.net/");
                return;
            }

            if (item.getType() == Material.STAINED_GLASS_PANE) {
                player.sendMessage(ChatColor.RED + "You have not made this kit yet");
                return;
            }

            MPGKit kit = MPGKitManager.getKit(player, kitNumber);
            MPGPlayerManager.getMPGPlayer(player.getUniqueId()).setKit(kit);
            player.sendMessage(ChatColor.GREEN + "Kit #" + kitNumber + " has been selected.");
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
