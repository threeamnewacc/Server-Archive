package net.badlion.arenalobby.inventories.duel;

import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.settings.ArenaSettings;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.arenalobby.managers.ArenaSettingsManager;
import net.badlion.arenalobby.managers.DuelRequestManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelRequestInventory {

	private static ItemStack acceptDuelItem;
	private static ItemStack denyDuelItem;
	private static ItemStack denyAllDuelItem;

	private static DuelRequestScreenHandler duelRequestScreenHandler;

	public static void initialize() {
		// Items
		ItemStack acceptDuelItem = new ItemStack(Material.WOOL, 1, (short) 13);
		ItemMeta acceptDuelItemMeta = acceptDuelItem.getItemMeta();
		acceptDuelItemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Accept");
		List<String> acceptDuelItemLore = new ArrayList<>();
		acceptDuelItemLore.add(ChatColor.YELLOW + "Click item to accept");
		acceptDuelItemLore.add(ChatColor.YELLOW + "the duel request");
		acceptDuelItemMeta.setLore(acceptDuelItemLore);
		acceptDuelItem.setItemMeta(acceptDuelItemMeta);
		DuelRequestInventory.acceptDuelItem = acceptDuelItem;

		ItemStack denyDuelItem = new ItemStack(Material.WOOL, 1, (short) 14);
		ItemMeta denyDuelItemMeta = denyDuelItem.getItemMeta();
		denyDuelItemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Deny");
		List<String> denyDuelItemLore = new ArrayList<>();
		denyDuelItemLore.add(ChatColor.YELLOW + "Click item to deny");
		denyDuelItemLore.add(ChatColor.YELLOW + "the duel request");
		denyDuelItemMeta.setLore(denyDuelItemLore);
		denyDuelItem.setItemMeta(denyDuelItemMeta);
		DuelRequestInventory.denyDuelItem = denyDuelItem;

		ItemStack denyAllDuelItem = new ItemStack(Material.REDSTONE_TORCH_ON);
		ItemMeta denyAllDuelItemMeta = denyAllDuelItem.getItemMeta();
		denyAllDuelItemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Turn Off Duel Requests");
		List<String> denyAllDuelItemLore = new ArrayList<>();
		denyAllDuelItemLore.add(ChatColor.YELLOW + "Click item to deny");
		denyAllDuelItemLore.add(ChatColor.YELLOW + "the duel request");
		denyAllDuelItemLore.add(ChatColor.YELLOW + "and not accept any");
		denyAllDuelItemLore.add(ChatColor.YELLOW + "other duel requests");
		denyAllDuelItemMeta.setLore(denyAllDuelItemLore);
		denyAllDuelItem.setItemMeta(denyAllDuelItemMeta);
		DuelRequestInventory.denyAllDuelItem = denyAllDuelItem;

		// Screen handlers
		DuelRequestInventory.duelRequestScreenHandler = new DuelRequestScreenHandler();
	}

	private static ItemStack getSkullForPlayer(String name) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setDisplayName(ChatColor.GREEN + name);
		//skullMeta.setOwner(player.getName());
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public static void openDuelRequestInventory(String senderName, UUID senderId, Group receivingGroup, boolean redRover, int bestOf) {
		// Figure out how big our inventory needs to be
		int skullSpace = 16;
		int inventorySize = 36;
		//TODO: Get Sender group size from mcp
		int groupSizes = 5;//senderGroup.players().size();
		if (groupSizes > 16 && groupSizes <= 20) {
			skullSpace = 20;
			inventorySize = 45;
		} else if (groupSizes > 20) {
			skullSpace = 24;
			inventorySize = 54;
		}

		String name;
		if (redRover) {
			name = ChatColor.RED + ChatColor.BOLD.toString() + "RR "
					+ ChatColor.GREEN + ChatColor.BOLD.toString() + "Duel Request - " + senderName;
		} else {
			name = ChatColor.GREEN + ChatColor.BOLD.toString() + "Request - " + senderName + " " + (bestOf == 1 ? "bo1" : bestOf == 3 ? "bo3" : bestOf == 5 ? "bo5" : "");
		}

		if (name.length() > 32) name = name.substring(0, 32);

		// Create smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(DuelRequestInventory.duelRequestScreenHandler, inventorySize, name);

		// Fill items
		DuelRequestInventory.fillDuelRequestInventory(DuelRequestManager.getDuelCreator(senderId).getKitRuleSet(),
				smellyInventory.getMainInventory(), inventorySize);

		// Add skulls for own team on left
		List<Player> players = receivingGroup.players();
		for (int i = 0; i < skullSpace; i++) {
			if (i == players.size()) break;

			Player player = players.get(i);

			ItemStack skull = DuelRequestInventory.getSkullForPlayer(player.getDisguisedName());

			// Figure out which slot to insert the skull into
			int nextSlot = i;
			if (i >= 4 && i <= 7) nextSlot = i + 5;
			else if (i >= 8 && i <= 11) nextSlot = i + 10;
			else if (i >= 12 && i <= 15) nextSlot = i + 15;
			else if (i >= 16 && i <= 19) nextSlot = i + 20;
			else if (i >= 20 && i <= 23) nextSlot = i + 25;

			smellyInventory.getMainInventory().setItem(nextSlot, skull);
		}

		// Add skulls for other team on right
		//players = senderGroup.players();
		for (int i = 0; i < skullSpace; i++) {
			if (i == players.size()) break;


			ItemStack skull = DuelRequestInventory.getSkullForPlayer(senderName);

			// Figure out which slot to insert the skull into
			int nextSlot = 8 - i;
			if (i >= 4 && i <= 7) nextSlot = 21 - i;
			else if (i >= 8 && i <= 11) nextSlot = 34 - i;
			else if (i >= 12 && i <= 15) nextSlot = 47 - i;
			else if (i >= 16 && i <= 19) nextSlot = 60 - i;
			else if (i >= 20 && i <= 23) nextSlot = 73 - i;

			smellyInventory.getMainInventory().setItem(nextSlot, skull);
		}

		PotPvPPlayerManager.addDebug(receivingGroup.getLeader(), "Open duel request inventory, request from " + senderName);

		// Open inventory for receiving group's leader
		BukkitUtil.openInventory(receivingGroup.getLeader(), smellyInventory.getMainInventory());
	}

	private static void fillDuelRequestInventory(KitRuleSet kitRuleSet, Inventory inventory, int inventorySize) {
		// Create kit info item
		ItemStack item = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.GREEN + "Kit: " + kitRuleSet.getName());

		// Fill items
		switch (inventorySize) {
			case 36:
				inventory.setItem(4, DuelRequestInventory.acceptDuelItem);
				inventory.setItem(13, DuelRequestInventory.denyDuelItem);
				inventory.setItem(22, item);
				inventory.setItem(31, DuelRequestInventory.denyAllDuelItem);
				break;
			case 45:
				inventory.setItem(4, item);
				inventory.setItem(13, DuelRequestInventory.acceptDuelItem);
				inventory.setItem(22, DuelRequestInventory.denyDuelItem);
				inventory.setItem(31, item);
				inventory.setItem(40, DuelRequestInventory.denyAllDuelItem);
				break;
			case 54:
				inventory.setItem(4, item);
				inventory.setItem(13, item);
				inventory.setItem(22, DuelRequestInventory.acceptDuelItem);
				inventory.setItem(31, DuelRequestInventory.denyDuelItem);
				inventory.setItem(40, item);
				inventory.setItem(49, DuelRequestInventory.denyAllDuelItem);
				break;
		}
	}

	private static class DuelRequestScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());

			// Can only be null if 15s pass for custom kit selection and player is still in custom kit selection inventory
			// Shouldn't even reach here though
			if (duelCreator == null) {
				// Don't think they ever get a chance to reach this, but we'll find out
				Bukkit.getLogger().info("SMELLYISTHEBEST DUEL REQUEST DEBUG");
				player.sendFormattedMessage("{0}Duel request has expired.", ChatColor.RED);
				player.closeInventory();
				return;
			}

			// Verify this again, once at beginning when they /duel and now once at the end
			// Do they have too many players in their party?
		    /*
            if (duelCreator.getSender().players().size() > DuelCommand.MAX_PARTY_DUEL_PLAYERS) {
				// Is the party leader a pleb? (not famous)
				if (!duelCreator.getSender().getLeader().hasPermission("badlion.famous") && !duelCreator.getSender().getLeader().hasPermission("badlion.twitch")
						&& !duelCreator.getSender().getLeader().hasPermission("badlion.youtube") && !duelCreator.getSender().getLeader().hasPermission("badlion.staff")) {
					duelCreator.getSender().sendMessage(ChatColor.RED + "You can only have a maximum of " + DuelCommand.MAX_PARTY_DUEL_PLAYERS + " players in your party.");
					duelCreator.getReceiverIfOnline().sendMessage(ChatColor.RED + "Other party has too many players in there party (max of " + DuelCommand.MAX_PARTY_DUEL_PLAYERS + ").");

					// Deny the duel
					DuelHelper.handleDuelDeny(false, player, group);
					return;
				}
			}
			*/
			int inventorySize = event.getClickedInventory().getSize();

			switch (inventorySize) {
				case 36:
					switch (slot) {
						case 4:
							DuelHelper.handleDuelAccept(player, duelCreator);
							break;
						case 13:
							DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());
							break;
						case 31:
							DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());

							// Toggle duels
							ArenaSettings settings = ArenaSettingsManager.getSettings(player);
							settings.setAllowDuelRequests(!settings.isAllowDuelRequests());
							boolean bool = settings.isAllowDuelRequests();
							if (bool) {
								player.sendFormattedMessage("{0}Now accepting duel requests", ChatColor.YELLOW);
							} else {
								player.sendFormattedMessage("{0}No longer accepting duel requests", ChatColor.YELLOW);
							}
							break;
						default:
							return;
					}
					break;
				case 45:
					switch (slot) {
						case 13:
							DuelHelper.handleDuelAccept(player, duelCreator);
							break;
						case 22:
							DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());
							break;
						case 40:
							DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());

							// Toggle duels
							ArenaSettings settings = ArenaSettingsManager.getSettings(player);
							settings.setAllowDuelRequests(!settings.isAllowDuelRequests());
							boolean bool = settings.isAllowDuelRequests();
							if (bool) {
								player.sendFormattedMessage("{0}Now accepting duel requests", ChatColor.YELLOW);
							} else {
								player.sendFormattedMessage("{0}No longer accepting duel requests", ChatColor.YELLOW);
							}
							break;
						default:
							return;
					}
					break;
				case 54:
					switch (slot) {
						case 22:
							DuelHelper.handleDuelAccept(player, duelCreator);
							break;
						case 31:
							DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());
							break;
						case 49:
							DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());

							// Toggle duels
							ArenaSettings settings = ArenaSettingsManager.getSettings(player);
							settings.setAllowDuelRequests(!settings.isAllowDuelRequests());
							boolean bool = settings.isAllowDuelRequests();
							if (bool) {
								player.sendFormattedMessage("{0}Now accepting duel requests", ChatColor.YELLOW);
							} else {
								player.sendFormattedMessage("{0}No longer accepting duel requests", ChatColor.YELLOW);
							}
							break;
						default:
							return;
					}
					break;
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());
			if (duelCreator != null && !(duelCreator.getKitRuleSet() instanceof CustomRuleSet)) {
				if (!duelCreator.isAccepted()) {
					// Don't leak memory
					DuelHelper.handleDuelDeny(true, player.getUniqueId(), duelCreator.getSenderId());
				}
			}
		}

	}

}
