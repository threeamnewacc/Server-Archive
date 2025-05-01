package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PartyRequestInventory {

	private static ItemStack acceptRequestItem;
	private static ItemStack denyRequestItem;
	private static ItemStack denyAllRequestItem;

	private static ItemStack placeHolderItem;

	private static PartyRequestScreenHandler partyRequestScreenHandler;

	public static void initialize() {
		// Items
		PartyRequestInventory.acceptRequestItem = ItemStackUtil.createItem(Material.WOOL, (short) 13, ChatColor.GREEN + "Accept",
				ChatColor.YELLOW + "Click item to accept", ChatColor.YELLOW + "the party invite");

		PartyRequestInventory.denyRequestItem = ItemStackUtil.createItem(Material.WOOL, (short) 14, ChatColor.GREEN + "Deny",
				ChatColor.YELLOW + "Click item to deny", ChatColor.YELLOW + "the party invite");

		PartyRequestInventory.denyAllRequestItem = ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Turn Off Party Invites",
				ChatColor.YELLOW + "Click item to deny", ChatColor.YELLOW + "the party invite",
				ChatColor.YELLOW + "and not accept any", ChatColor.YELLOW + "other party invites");

		PartyRequestInventory.placeHolderItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.GREEN + "Party Invite");

		// Screen handlers
		PartyRequestInventory.partyRequestScreenHandler = new PartyRequestScreenHandler();
	}

	private static ItemStack getSkullForPlayer(Player player, boolean leader) {
		/*
		 * If we decide to do .setOwner()
		  ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		  SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		  skullMeta.setDisplayName(ChatColor.GREEN + (leader ? "(Leader) " : "") + player.getName());
		  //skullMeta.setOwner(player.getName());
		  skull.setItemMeta(skullMeta);
		*/
		return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.GREEN + (leader ? "(Leader) " : "") + player.getName());
	}

	public static void openPartyRequestInventory(Player player, Group requester) {
		// Find out # of rows needed for inventory
		int size = requester.players().size();
		int rowsNeeded = (int) Math.floor(size / 9D);
		if (size % 9 != 0) rowsNeeded++;
		if (rowsNeeded > 5) rowsNeeded = 5;
		int inventorySize = 9 * (rowsNeeded + 1);

		SmellyInventory smellyInventory = new SmellyInventory(PartyRequestInventory.partyRequestScreenHandler, inventorySize,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Party Invite - " + requester.getLeader().getName());

		// Add players
		smellyInventory.getMainInventory().addItem(PartyRequestInventory.getSkullForPlayer(requester.getLeader(), true));
		for (Player pl : requester.players()) {
			if (pl != requester.getLeader()) {
				smellyInventory.getMainInventory().addItem(PartyRequestInventory.getSkullForPlayer(pl, false));
			}
		}

		// Add items
		smellyInventory.getMainInventory().setItem(inventorySize - 9, PartyRequestInventory.placeHolderItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 8, PartyRequestInventory.placeHolderItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 7, PartyRequestInventory.acceptRequestItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 6, PartyRequestInventory.placeHolderItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 5, PartyRequestInventory.denyRequestItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 4, PartyRequestInventory.placeHolderItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 3, PartyRequestInventory.denyAllRequestItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 2, PartyRequestInventory.placeHolderItem);
		smellyInventory.getMainInventory().setItem(inventorySize - 1, PartyRequestInventory.placeHolderItem);

		PotPvPPlayerManager.addDebug(player, "Open party request inventory - request from " + requester.getLeader().getName());

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	private static class PartyRequestScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			int size = fakeHolder.getInventory().getSize() - 1;

			if (slot == size - 6) {
				player.performCommand("party accept");
			} else if (slot == size - 4) {
				player.performCommand("party deny");
			} else if (slot == size - 2) {
				player.performCommand("party deny");

				// Disallow party invites if they're allowed
                MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(player);
				if (messageOptions.getMessageTagBoolean(MessageManager.MessageType.PARTY)) {
					messageOptions.toggleMessageOption(MessageManager.MessageType.PARTY);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (GroupStateMachine.partyRequestState.contains(group)) {
				player.performCommand("party deny");
			}
		}

	}

}
