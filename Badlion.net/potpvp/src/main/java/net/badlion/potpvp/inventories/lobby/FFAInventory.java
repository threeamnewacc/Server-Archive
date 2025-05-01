package net.badlion.potpvp.inventories.lobby;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FFAInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new FFAInventoryScreenHandler(), 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "FFA");

		// Fill with the ffa items
		for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
			smellyInventory.getMainInventory().addItem(ffaWorld.getFFAItem());
		}

		FFAInventory.smellyInventory = smellyInventory;
	}

	public static void openFFAInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open ffa inventory");

		BukkitUtil.openInventory(player, FFAInventory.smellyInventory.getMainInventory());
	}

	public static void updateFFAInventory() {
		Inventory inventory = FFAInventory.smellyInventory.getMainInventory();
		inventory.clear();

		for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
			inventory.addItem(ffaWorld.getFFAItem());
		}

		inventory.setItem(17, SmellyInventory.getCloseInventoryItem());
	}

	private static class FFAInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			FFAWorld ffaWorld = FFAWorld.getFFAWorld(item);
			if (ffaWorld != null) {
				try {
					GroupStateMachine.lobbyState.transition(GroupStateMachine.matchMakingState, group);
					GroupStateMachine.matchMakingState.push(GroupStateMachine.ffaState, group, ffaWorld);
				} catch (IllegalStateTransitionException e) {
					PotPvP.getInstance().somethingBroke(player, group);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}