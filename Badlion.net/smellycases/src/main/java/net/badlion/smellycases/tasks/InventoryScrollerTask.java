package net.badlion.smellycases.tasks;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellycases.Case;
import net.badlion.smellycases.CaseItem;
import net.badlion.smellycases.CaseTier;
import net.badlion.smellycases.SmellyCases;
import net.badlion.smellycases.events.RequestPlayerOwnedCases;
import net.badlion.smellycases.managers.CaseDataManager;
import net.badlion.smellycases.managers.CaseManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InventoryScrollerTask extends BukkitRunnable {

	private static Set<InventoryScrollerTask> runningTasks = new HashSet<>();

	private Player player;
	private Inventory inventory;

	private CaseTier tier;
	private Gberry.ServerType serverType;

	private int ticks = 0;
	private boolean gray = true;
	private boolean closed = false;
	private boolean finished = false;
	private ItemStack winningItem;
	private Case caseToOpen;

	// TODO: RIGHT AS PLAYER OPENS CASE, CASE IS REMOVED, BUT WE SHOULD ADD REWARDS SAFELY, NO WAY TO OPEN CASE W/O GETTING REWARDS RIGHT?
	public InventoryScrollerTask(Player player, Inventory inventory, CaseTier tier, Gberry.ServerType serverType, Case caseToOpen) {
		this.player = player;
		this.inventory = inventory;
		this.caseToOpen = caseToOpen;

		this.tier = tier;
		this.serverType = serverType;

		InventoryScrollerTask.runningTasks.add(this);
	}

	public static InventoryScrollerTask getRunningTask(Player player) {
		for (InventoryScrollerTask task : InventoryScrollerTask.runningTasks) {
			if (task.player.getUniqueId().equals(player.getUniqueId())) {
				return task;
			}
		}
		return null;
	}

	public Inventory getInventory() {
		return inventory;
	}

	private void openWinningsInventory(ItemStack winningItem) {
		while (winningItem == null) {
			// If they close the inventory, get a random item for winningItem, as we don't want them being able to rig it
			winningItem = CaseManager.getRandomCaseItem(this.player, this.tier, this.serverType, this.caseToOpen);
		}
		// TODO: ASK ARCHY ABOUT THE SIZE OF THIS INVENTORY? PROBABLY JUST 18 IS BIG ENOUGH?
		// Use a smelly inventory for the close inventory item
		SmellyInventory smellyInventory = null;

		// Try to figure out which case item the case landed on
		Collection<CaseItem> caseItems = CaseManager.getCaseItems(this.tier, this.serverType);
		for (CaseItem caseItem : caseItems) {
			//if (ItemStackUtils.equals(this.winningItem, caseItem.getItemStack())) { - Doesn't seem to work...?
			if (winningItem.getItemMeta().getDisplayName().equals(caseItem.getItemStack().getItemMeta().getDisplayName())) {
				// Create the smelly inventory
				smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 18,
						ChatColor.GOLD + ChatColor.BOLD.toString() + ChatColor.stripColor(caseItem.getItemStack().getItemMeta().getDisplayName()));

				for (ItemStack itemStack : caseItem.getItems()) {
					smellyInventory.getMainInventory().addItem(Gberry.getGlowItem(itemStack));
				}

				// Take case
				String prize = caseItem.getPrizeName();
				CaseDataManager.onOpenCase(this.player.getUniqueId(), this.serverType, prize, this.caseToOpen);

				// Give reward
				caseItem.rewardPlayer(this.player);

				// Announce what they won
				final String caseItemName = caseItem.getItemStack().getItemMeta().getDisplayName();
				Gberry.broadcastMessage(ChatColor.GOLD + this.player.getName() + " found " + caseItem.getCaseItemRarity().getName() + ChatColor.GOLD + " " + caseItemName + ChatColor.GOLD + " in a " + serverType.getName() + " case!");

				final CaseItem finalCaseItem = caseItem;
				if (Gberry.serverType == Gberry.ServerType.LOBBY) {
					SmellyCases.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyCases.getInstance(), new Runnable() {
						@Override
						public void run() {
							Gberry.sendGSyncEvent(Arrays.asList("LobbyMessage", ChatColor.GOLD + player.getName() + " found " + finalCaseItem.getCaseItemRarity().getName() + ChatColor.GOLD + " " + caseItemName + ChatColor.GOLD + " in a " + serverType.getName() + " case!"));
						}
					});
				}

				// They are no longer opening a case
				InventoryScrollerTask.runningTasks.remove(this);
				CaseManager.openingCases.remove(this.player.getUniqueId());

				// In case they are opening another case without relogging, add the case item to their owned ones
				RequestPlayerOwnedCases.playerOwnedCases.get(this.player.getUniqueId()).add(caseItem.getName());
				break;
			}
		}

		// Play a sound if the player is online
		if (Gberry.isPlayerOnline(this.player)) {
			this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);
		}

		if (smellyInventory == null) {
			// Means that they have wasted a cosmetic case due to the item not being found,
			// charge backs incoming watch out ladies!
			this.player.closeInventory();
			this.player.sendMessage(ChatColor.RED + "Something has gone terribly wrong. Please contact a developer with this error code immediately - #3E9");
			return;
		}

		// Close the case inventory and open the winnings inventory
		BukkitUtil.openInventory(this.player, smellyInventory.getMainInventory());
	}

	@Override
	public void run() {
		if (this.closed) {
			this.cancel();
			return;
		}

		// They left, give them the items
		if (!Gberry.isPlayerOnline(this.player)) {
			closed = true;
			this.openWinningsInventory(null);

			this.cancel();
			return;
		}

		// Check for stop time
		if (this.ticks >= 240) {
			InventoryScrollerTask.this.openWinningsInventory(this.winningItem);
			this.cancel();
			return;
		}

		// Check for reward claim time
		if (this.ticks >= 200) {
			// Change the inventory
			if (Gberry.isPlayerOnline(this.player) && !this.finished &&
					this.player.getOpenInventory() != null && this.player.getOpenInventory().getTopInventory().getHolder() != this.inventory.getHolder()) {
				this.player.openInventory(this.inventory);
				this.finished = true;
			}
			for (int i = 0; i < this.inventory.getSize(); i++) {
				if (i != 13) {
					this.inventory.setItem(i, ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, (byte) (this.gray ? 8 : 14), ChatColor.WHITE.toString()));
				} else {
					this.inventory.setItem(i, Gberry.getGlowItem(this.inventory.getItem(i)));
				}
			}
			if (this.ticks % 8 == 0) {
				this.gray = !this.gray;
			}
			// Play a sound effect for the player
			if (this.ticks == 200) {
				this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 1F, 1F);
			}
			this.ticks += 2;
			return;
		}

		// Slow down the scrolling gradually
		if (this.ticks >= 140 && this.ticks % 10 != 0 //
				|| this.ticks < 140 && this.ticks >= 110 && this.ticks % 8 != 0
				|| this.ticks < 110 && this.ticks >= 90 && this.ticks % 6 != 0
				|| this.ticks < 90 && this.ticks >= 70 && this.ticks % 4 != 0) {
			this.ticks += 2; // We run this every .1s = 2 ticks
			return;
		}

		// Play a sound effect for the player
		if (this.player.getOpenInventory() != null && this.player.getOpenInventory().getTopInventory().getHolder() == this.inventory.getHolder()) {
			this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_PRESSUREPLATE_CLICK_ON"), 1F, 1F);
		}

		// Change the filler items (2 for loops for increased performance)
		for (int i = 0; i < 9; i++) {
			if (i != 4) {
				this.inventory.setItem(i, CaseManager.getRandomFillerItem());
			}
		}
		for (int i = 18; i < 27; i++) {
			if (i != 22) {
				this.inventory.setItem(i, CaseManager.getRandomFillerItem());
			}
		}

		// Move all the reward items one slot to the right
		for (int i = 16; i > 8; i--) {
			ItemStack item = this.inventory.getItem(i);

			// Reference the item that is now in the middle
			if (i + 1 == 13) {
				this.winningItem = item;
			}

			this.inventory.setItem(i + 1, item);
		}

		// Add a new reward item in the left-most slot
		ItemStack newItem = CaseManager.getRandomCaseItem(this.player, this.tier, this.serverType, this.caseToOpen);
		while (newItem == null) {
			newItem = CaseManager.getRandomCaseItem(this.player, this.tier, this.serverType, this.caseToOpen);
		}
		this.inventory.setItem(9, newItem);

		this.ticks += 2; // We run this every .1s = 2 ticks
	}

}
