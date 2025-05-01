package net.badlion.potpvp.inventories.duel;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.DuelHelper;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DuelRequestChooseCustomKitInventory {

	private static DuelRequestChooseCustomKitScreenHandler duelRequestChooseCustomKitScreenHandler;

	public static void initialize() {
		// Screen handlers
		DuelRequestChooseCustomKitInventory.duelRequestChooseCustomKitScreenHandler = new DuelRequestChooseCustomKitScreenHandler();
	}

	public static void openDuelRequestChooseCustomKitInventory(final Player player) {
		// Create smelly inventory
		final SmellyInventory smellyInventory = new SmellyInventory(DuelRequestChooseCustomKitInventory.duelRequestChooseCustomKitScreenHandler,
				27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Custom Kit");

		// Fill custom kit items
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				final List<Integer> customKitNumbers = KitHelper.getSavedCustomKitNumbers(player, "Custom");
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						for (Integer customKitNumber : customKitNumbers) {
							smellyInventory.getMainInventory().addItem(KitHelper.createCustomKitInventoryItem(customKitNumber));
						}

						PotPvPPlayerManager.addDebug(player, "Open duel choose custom kit inventory");

						BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
					}
				});
			}
		});
	}

	private static class DuelRequestChooseCustomKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (event.getClick().equals(ClickType.MIDDLE)) { // Preview kit
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				BukkitUtil.closeInventory(player);

				// Can only be null if 15s pass for custom kit selection and player is still in selection inventory
				DuelHelper.DuelCreator duelCreator = GroupStateMachine.duelRequestState.getDuelCreator(group);

				// Fail safe - this should never get hit
				if (duelCreator == null) {
					PotPvPPlayerManager.addDebug(player, "Not all players have selected their custom kits, duel cancelled.");

					player.sendMessage(ChatColor.RED + "Not all players have selected their custom kits, duel cancelled.");
					player.closeInventory();
					return;
				}

				duelCreator.setCustomKit(player, KitHelper.getCustomKitNumberFromItem(item));

				// Start duel if all players have chosen a custom kit
				if (duelCreator.allCustomKitsSelected()) {
					PotPvPPlayerManager.addDebug(player, "All custom kits selected, start duel");
					DuelHelper.createDuel(player, duelCreator);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
