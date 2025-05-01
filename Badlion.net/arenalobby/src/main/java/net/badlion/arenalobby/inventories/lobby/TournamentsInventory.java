package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.arenalobby.managers.TournamentManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentsInventory {

	private static SmellyInventory smellyInventory;
	private static Map<Integer, TournamentManager.Tournament> slotToTournament = new HashMap<>();

	public static void fillTournamentsInventory() {
		SmellyInventory smellyInventory = new SmellyInventory(new TournamentsInventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Tournaments");

		TournamentsInventory.smellyInventory = smellyInventory;
		TournamentsInventory.updateTournamentsInventory();
	}

	public static void openTournamentsInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open Tournaments inventory");

		BukkitUtil.openInventory(player, TournamentsInventory.smellyInventory.getMainInventory());
	}

	public static void updateTournamentsInventory() {
		Inventory tournamentsInventory = TournamentsInventory.smellyInventory.getMainInventory();
		tournamentsInventory.clear();
		slotToTournament.clear();
		int slot = 0;
		for (TournamentManager.Tournament tournament : TournamentManager.getActiveTournaments()) {
			Ladder ladder = tournament.getLadder();
			ItemStack item = ItemStackUtil.createItem(KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet()).getType(), 1);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.BLUE + ladder.getKitRuleSet().getName());
			List<String> itemLore = new ArrayList<>();
			String name = ladder.getLadderType().getNiceName().replace(" Ranked", "");
			itemLore.add(ChatColor.BLUE + "Type: " + name);
			itemLore.add(ChatColor.BLUE + "Size: " + tournament.getSlots());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Click to join tournament.");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			tournamentsInventory.addItem(item);
			slotToTournament.put(slot, tournament);
			slot++;
		}
		tournamentsInventory.setItem(26, SmellyInventory.getCloseInventoryItem());
	}

	private static class TournamentsInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			TournamentManager.Tournament tournament = slotToTournament.get(event.getRawSlot());
			if (tournament != null) {
				TournamentManager.joinQueue(player, tournament);
				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}