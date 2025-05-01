package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.SpawnPointManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnSelectorInventory {

	private static SmellyInventory smellyInventory;

	private static Map<Integer, String> rulesetSpawnItems = new HashMap<>();

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new LobbySelectorClickHandler(), 36,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Spawn Selector");

		smellyInventory.getFakeHolder().setParentInventory(SettingsInventory.smellyInventory.getMainInventory());
		// Fill with the kit rule set items
		SpawnSelectorInventory.smellyInventory = smellyInventory;
		SpawnSelectorInventory.updateLobbyInventory();
	}

	public static void openSpawnSelectorMenu(Player player) {
		BukkitUtil.openInventory(player, SpawnSelectorInventory.smellyInventory.getMainInventory());
	}

	public static void updateLobbyInventory() {
		SpawnSelectorInventory.smellyInventory.getMainInventory().clear();
		int i = 0;

		ItemStack globalItem = ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GOLD + "Global Leaderboards");
		ItemMeta globalItemMeta = globalItem.getItemMeta();
		List<String> globalItemLore = new ArrayList<>();
		globalItemLore.add(ChatColor.BLUE + "Click to teleport to the global leaderboards.");
		globalItemMeta.setLore(globalItemLore);
		globalItem.setItemMeta(globalItemMeta);
		SpawnSelectorInventory.smellyInventory.getMainInventory().setItem(i, globalItem);
		SpawnSelectorInventory.rulesetSpawnItems.put(i, "Global");
		i++;
		for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_1V1).values()) {
			ItemStack item = ItemStackUtil.createItem(ladder.getKitRuleSet().getKitItem().getType(), 1, ladder.getKitRuleSet().getKitItem().getDurability(), ChatColor.GREEN + ladder.getKitRuleSet().getName() + " Leaderboards");
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "Click to teleport to the " + ladder.getKitRuleSet().getName() + " leaderboards.");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);
			SpawnSelectorInventory.smellyInventory.getMainInventory().setItem(i, item);
			SpawnSelectorInventory.rulesetSpawnItems.put(i, ladder.getKitRuleSet().getName());
			i++;
		}

		SpawnSelectorInventory.smellyInventory.getMainInventory().setItem(35, SmellyInventory.getBackInventoryItem());
	}

	private static class LobbySelectorClickHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			if (item != null) {
				if (SpawnSelectorInventory.rulesetSpawnItems.containsKey(slot)) {
					String kitName = SpawnSelectorInventory.rulesetSpawnItems.get(slot);
					if (kitName != null) {
						if(SpawnPointManager.ladderLocationMap.containsKey(kitName)) {
							player.sendFormattedMessage("{0}Teleporting...", ChatColor.GOLD + ChatColor.BOLD.toString());
							Gberry.safeTeleport(player, SpawnPointManager.ladderLocationMap.get(kitName));
						}else{
							player.sendFormattedMessage("{0}There is no spawn point for this ladder.", ChatColor.RED);
						}
					}
				}
				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
