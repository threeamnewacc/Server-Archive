package net.badlion.arenalobby.inventories.lobby;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
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

public class FFAInventory {

	public enum FFAType {
		NODEBUFF_FFA("NoDebuff FFA", "nodebuffffa", Material.POTION, (short) 8229),
		SG_FFA("SG FFA", "sgffa", Material.FISHING_ROD),
		SOUP_FFA("Soup FFA", "soupffa", Material.MUSHROOM_SOUP),
		UHC_FFA("UHC FFA", "uhcffa", Material.GOLDEN_APPLE);

		private String niceName;

		private String serverName;
		private ItemStack ffaItem;

		private int numberOfPlayers = 0;

		private FFAType(String niceName, String serverName, Material type) {
			this(niceName, serverName, type, (short) 0);
		}

		private FFAType(String niceName, String serverName, Material type, short data) {
			this.niceName = niceName;
			this.serverName = serverName;

			this.ffaItem = ItemStackUtil.createItem(type, data, ChatColor.GREEN + "Join " + niceName, ChatColor.YELLOW + "Players: 0");
		}

		public String getNiceName() {
			return this.niceName;
		}

		public String getServerName() {
			return this.serverName;
		}

		public ItemStack getFFAItem() {
			// Update player count
			ItemStack item = this.ffaItem;
			item.setAmount((this.numberOfPlayers <= 0) ? 1 : (this.numberOfPlayers > 64) ? 64 : this.numberOfPlayers);
			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "Players: " + this.numberOfPlayers);

			// Hardcode for the LULZ
			if (this == SOUP_FFA) {
				lore.add("");
				lore.add(ChatColor.RED + "Coming Soon!");
			}

			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);

			return this.ffaItem;
		}

		public void setNumberOfPlayers(int numberOfPlayers) {
			this.numberOfPlayers = numberOfPlayers;
		}

	}

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		FFAInventory.smellyInventory = new SmellyInventory(new FFAInventoryScreenHandler(), 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "FFA");

		for (FFAType ffaType : FFAType.values()) {
			FFAInventory.smellyInventory.getMainInventory().addItem(ffaType.getFFAItem());
		}
	}

	public static void openFFAInventory(Player player) {
		BukkitUtil.openInventory(player, FFAInventory.smellyInventory.getMainInventory());
	}

	public static void updateFFAInventory(Map<String, String> ffaPlayerCounts) {
		int slot = 0;
		for (FFAType ffaType : FFAType.values()) {
			ffaType.setNumberOfPlayers(Integer.valueOf(ffaPlayerCounts.get(ffaType.getServerName())));

			FFAInventory.smellyInventory.getMainInventory().setItem(slot++, ffaType.getFFAItem());
		}
	}

	private static class FFAInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			// Get FFA type
			FFAType ffaType = FFAType.values()[slot];

			final String server = Gberry.serverRegion.name().toLowerCase() + ffaType.getServerName();

			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					// Send message to player so we don't spook them
					player.sendFormattedMessage("{0}Connecting...", ChatColor.YELLOW + ChatColor.BOLD.toString());

					// Tell bungee to send them to the server
					Gberry.sendToServer(player, server);
				}
			});
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}