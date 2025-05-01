package net.badlion.potpvp.inventories.lobby;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.tdm.TDMGame;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TDMInventory {

	private static SmellyInventory smellyInventory;
	private static SmellyInventory.SmellyInventoryHandler screenHandler;

	private static ItemStack autoAssignItem;

	private static Map<UUID, TDMGame> playerToTDMGames = new HashMap<>();

	public static void initialize() {
		TDMInventory.screenHandler = new TDMInventoryScreenHandler();

		TDMInventory.smellyInventory = new SmellyInventory(TDMInventory.screenHandler, 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "TDM");

		TDMInventory.autoAssignItem = ItemStackUtil.createItem(Material.LEATHER_HELMET, ChatColor.GREEN + "Auto Assign",
				ChatColor.YELLOW + "Puts you on the team", ChatColor.YELLOW + "with the fewest players");
	}

	public static void openTDMInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open tdm inventory");

		BukkitUtil.openInventory(player, TDMInventory.smellyInventory.getMainInventory());
	}

	public static void updateTDMInventory(boolean initialUpdate) {
		Inventory inventory = TDMInventory.smellyInventory.getMainInventory();
		inventory.clear();

		// Fill with the tdm items
		for (TDMGame tdmGame : TDMGame.getTDMGames()) {
			inventory.addItem(tdmGame.getTDMItem());
		}

		inventory.setItem(17, SmellyInventory.getCloseInventoryItem());

		if (initialUpdate) {
			// Initialize sub-inventories
			int i = 0;
			for (TDMGame tdmGame : TDMGame.getTDMGames()) {
				Inventory subInventory = TDMInventory.smellyInventory.createInventory(TDMInventory.smellyInventory.getFakeHolder(),
						TDMInventory.screenHandler, i, 9, ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Your Team");

				subInventory.addItem(TDMInventory.autoAssignItem);

				List<ItemStack> items = TDMInventory.getTeamSelectionItems(tdmGame);
				for (ItemStack item : items) {
					subInventory.addItem(item);
				}

				i++;
			}

		}
	}

	public static void updateTeamPlayerCountItems(TDMGame tdmGame) {
		int i = 0;
		for (ItemStack itemStack : TDMInventory.smellyInventory.getMainInventory().getContents()) {
			if (itemStack == null || itemStack.getType() == Material.AIR) {
				i++;
				continue;
			}
				if (tdmGame == TDMGame.getTDMGame(itemStack)) {
					Inventory inventory = TDMInventory.smellyInventory.getFakeHolder().getSubInventory(i);

					inventory.clear();

					inventory.addItem(TDMInventory.autoAssignItem);
					inventory.setItem(8, SmellyInventory.getBackInventoryItem());

					List<ItemStack> items = TDMInventory.getTeamSelectionItems(tdmGame);
					for (ItemStack item : items) {
						inventory.addItem(item);
					}
				}
		}
	}

	public static List<ItemStack> getTeamSelectionItems(TDMGame tdmGame) {
		List<ItemStack> items = new ArrayList<>();

		for (TDMGame.TDMTeam tdmTeam : tdmGame.getTeams()) {
			ItemStack item = ItemStackUtil.createItem(Material.LEATHER_HELMET, tdmTeam.getColor() + tdmTeam.getName(),
					tdmTeam.getColor().toString() + tdmTeam.getSize() + "/" + tdmGame.getTeamSize(), "",
					ChatColor.YELLOW + "Donators can choose their teams");

			// Color leather
			LeatherArmorMeta itemMeta = ((LeatherArmorMeta) item.getItemMeta());
			itemMeta.setColor(Gberry.getColorFromChatColor(tdmTeam.getColor()));
			item.setItemMeta(itemMeta);

			items.add(item);
		}

		return items;
	}

	private static class TDMInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			// Did they click in the team select inventory?
			if (fakeHolder.getInventory().getSize() == 9) {
				TDMGame tdmGame = TDMInventory.playerToTDMGames.remove(player.getUniqueId());

				if (tdmGame != null) {
					// Get team with fewest players
					TDMGame.TDMTeam lowestTeam = null;
					for (TDMGame.TDMTeam tdmTeam2 : tdmGame.getTeams()) {
						if (lowestTeam == null) {
							lowestTeam = tdmTeam2;
							continue;
						}

						if (tdmTeam2.getSize() < lowestTeam.getSize()) {
							lowestTeam = tdmTeam2;
						}
					}

					TDMGame.TDMTeam tdmTeam = null;

					// Auto assign item
					if (slot == 0) {
						// Add player to team with least players
						tdmTeam = lowestTeam;
					} else { // Team item
						// Check donator permission
						if (!player.hasPermission("badlion.donatorplus")) {
							player.sendMessage(ChatColor.RED + "Only donator plus can choose teams! Choose auto assign.");

							BukkitUtil.closeInventory(player);
							return;
						}

						String displayName = item.getItemMeta().getDisplayName().substring(2);
						for (TDMGame.TDMTeam tdmTeam2 : tdmGame.getTeams()) {
						  	if (displayName.equals(tdmTeam2.getName())) {
							    tdmTeam = tdmTeam2;
							    break;
						    }
						}

						// Check to make sure team they want to join doesn't have
						// 3 or more players than the lowest team
						if (tdmTeam.getSize() >= lowestTeam.getSize() + 2) {
							player.sendMessage(ChatColor.RED + "This team has too many players, please choose another.");

							BukkitUtil.closeInventory(player);
							return;
						}
					}

					Group group = PotPvP.getInstance().getPlayerGroup(player);

					try {
						GroupStateMachine.tdmState.addAssignedTeam(player, tdmTeam);

						GroupStateMachine.lobbyState.transition(GroupStateMachine.matchMakingState, group);
						GroupStateMachine.matchMakingState.push(GroupStateMachine.tdmState, group, tdmGame);
					} catch (IllegalStateTransitionException e) {
						// Clear cache
						GroupStateMachine.tdmState.getAssignedTeam(player);

						PotPvP.getInstance().somethingBroke(player, group);
					}
				}
			} else {
				// Store which tdm game this is
				TDMInventory.playerToTDMGames.put(player.getUniqueId(), TDMGame.getTDMGame(item));

				BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			// Remove metadata we store
			if (fakeHolder.getInventory().getSize() == 9) {
				TDMInventory.playerToTDMGames.remove(player.getUniqueId());
			}

		}

	}

}