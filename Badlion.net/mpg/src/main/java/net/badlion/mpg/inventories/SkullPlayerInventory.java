package net.badlion.mpg.inventories;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkullPlayerInventory {

	private static SmellyInventory smellyInventory;

	private static Map<UUID, ItemStack> playerSkulls = new LinkedHashMap<>();

	public static void initialize() {
		SkullPlayerInventory.smellyInventory = new SmellyInventory(new SpectatePlayerInventoryScreenHandler(), 54,
															  ChatColor.BOLD + ChatColor.AQUA.toString() + "Alive Players");
	}

	public static void openSpectatePlayerInventory(Player player) {
		BukkitUtil.openInventory(player, SkullPlayerInventory.smellyInventory.getMainInventory());
	}

	public static void addSkullForPlayers(List<Player> players) {
		for (Player player : players) {
			SkullPlayerInventory.addSkullForPlayer(player);
		}
	}

	public static void addSkullForPlayer(Player player) {
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setDisplayName(ChatColor.GREEN + mpgPlayer.getUsername());
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Click to teleport to");
		lore.add(ChatColor.YELLOW + mpgPlayer.getUsername());
		skullMeta.setLore(lore);
		//skullMeta.setOwner(group.getLeader().getName()); // TODO: ???
		skull.setItemMeta(skullMeta);

		// Add to our list
		SkullPlayerInventory.playerSkulls.put(player.getUniqueId(), skull);

		SkullPlayerInventory.smellyInventory.getMainInventory().addItem(skull);
	}

	public static void removeSkullForPlayer(MPGPlayer mpgPlayer) {
		// Remove from our list
		if (SkullPlayerInventory.playerSkulls.remove(mpgPlayer.getUniqueId()) == null) {
			// They had no skull
			return;
		}

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

			// Treat this as the disguised name first and try to get the UUID
			UUID uuid = DisguiseManager.getDisguisedUUID(name);

			// If UUID was null, get the UUID by treating this as the real name
			if (uuid == null) {
				uuid = MPG.getInstance().getUUID(name);
			}

            Player spectatePlayer = SmellyInventory.plugin.getServer().getPlayer(uuid);

			if (spectatePlayer != null && spectatePlayer.isOnline()) {
				player.teleport(spectatePlayer);
			} else {
				LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(uuid);
				if (loggerNPC != null) {
					player.teleport(loggerNPC.getEntity().getLocation());
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
