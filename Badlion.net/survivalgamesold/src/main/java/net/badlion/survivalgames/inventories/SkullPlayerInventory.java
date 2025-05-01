package net.badlion.survivalgames.inventories;

import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class SkullPlayerInventory {

	private static SmellyInventory smellyInventory;

	private static Map<String, ItemStack> playerSkulls = new LinkedHashMap<>();

	public static void initialize() {
		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new SpectatePlayerInventoryScreenHandler(), 27,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Alive Players");

		SkullPlayerInventory.smellyInventory = smellyInventory;
	}

	public static void openSpectatePlayerInventory(Player player) {
		BukkitUtil.openInventory(player, SkullPlayerInventory.smellyInventory.getMainInventory());
	}

	public static void addSkullForPlayer(List<String> players) {
		for (String player : players) {
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
			skullMeta.setDisplayName(ChatColor.GREEN + player);
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "Click to teleport to");
			lore.add(ChatColor.YELLOW + player);
			skullMeta.setLore(lore);
			//skullMeta.setOwner(group.getLeader().getName());
			skull.setItemMeta(skullMeta);

			// Add to our list
			SkullPlayerInventory.playerSkulls.put(player, skull);

			SkullPlayerInventory.smellyInventory.getMainInventory().addItem(skull);
		}
	}

	public static void removeSkullForPlayer(SGPlayer sgPlayer) {
		String name = sgPlayer.getListName();

		// Remove from our list
		SkullPlayerInventory.playerSkulls.remove(name);

		// Clear inventory
		SkullPlayerInventory.smellyInventory.getMainInventory().clear();

		// Add skulls
		for (ItemStack skull : SkullPlayerInventory.playerSkulls.values()) {
			SkullPlayerInventory.smellyInventory.getMainInventory().addItem(skull);
		}

		// Add close inventory item
		SkullPlayerInventory.smellyInventory.getMainInventory().setItem(26, SmellyInventory.getCloseInventoryItem());

	}

	private static class SpectatePlayerInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			String name = item.getItemMeta().getDisplayName().substring(2);
            Bukkit.getLogger().info("name " + name);
            Player p2 = SmellyInventory.plugin.getServer().getPlayerExact(name);
			if (p2 != null && p2.isOnline()) {
				player.teleport(p2); // This automatically closes the opened inventory
			} /*else if (DisguiseCommand.DISGUISED_PLAYERS_NAMES.containsValue(name)) {
                for (Map.Entry<UUID, String> entry : DisguiseCommand.DISGUISED_PLAYERS_NAMES.entrySet()) {
                    // We found the famous player
                    Bukkit.getLogger().info("name1 " + entry.getValue());
                    if (entry.getValue().equals(name)) {
                        p2 = SmellyInventory.plugin.getServer().getPlayer(entry.getKey());
                        if (p2 != null && p2.isOnline()) {
                            player.teleport(p2); // This automatically closes the opened inventory
                        }

                        return;
                    }
                }
            }*/
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
