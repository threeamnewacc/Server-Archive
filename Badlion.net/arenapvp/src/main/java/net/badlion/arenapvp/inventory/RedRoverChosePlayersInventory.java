package net.badlion.arenapvp.inventory;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.matchmaking.RedRoverBattle;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.Gberry;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedRoverChosePlayersInventory {


	public static void openSelectPlayersInventory(Player player, RedRoverBattle match) {
		SmellyInventory smellyInventory = new SmellyInventory(new SelectPlayerScreenHandler(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Player Selector");

		fillInventory(smellyInventory.getMainInventory(), match);

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}


	public static void fillInventory(Inventory inventory, RedRoverBattle redRoverBattle) {
		inventory.clear();
		List<UUID> playerIds = redRoverBattle.getAllPlayers();
		List<Player> players = new ArrayList<>();
		List<Player> team1Members = redRoverBattle.getTeam1().members();
		List<Player> team2Members = redRoverBattle.getTeam2().members();

		for (UUID playerId : playerIds) {
			Player member = Bukkit.getPlayer(playerId);
			if (member != null) {
				if (!team1Members.contains(member) && !team2Members.contains(member)) {
					players.add(member);
				}
			}
		}

		// Inventory players map is so we can get the slot each player is in for the click events
		redRoverBattle.getInventoryPlayers().clear();

		int i = 0;
		for (Player member : players) {
			redRoverBattle.getInventoryPlayers().put(i, member.getUniqueId());
			inventory.setItem(i, getSkullForPlayer(member));
			i++;
		}
	}

	private static ItemStack getSkullForPlayer(Player player) {
		return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.GREEN + player.getDisguisedName());
	}


	public static class SelectPlayerScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (!item.getType().equals(Material.SKULL_ITEM)) {
				return;
			}

			RedRoverBattle match = (RedRoverBattle) MatchState.getPlayerMatch(player);

			if (!match.getInventoryPlayers().containsKey(slot)) {
				Gberry.log("REDROVER", "[ChosePlayerInv] Clicked slot did not contain a player");
				return;
			}

			Player nextFighter = ArenaPvP.getInstance().getServer().getPlayer(match.getInventoryPlayers().get(slot));
			Gberry.log("REDROVER", "[ChosePlayerInv] Picked fighter = " + nextFighter.getName());

			// Is the player still online?
			if (!Gberry.isPlayerOnline(nextFighter)) {
				player.sendFormattedMessage("{0}That player has left the game, please pick a new player!", ChatColor.RED);
				//BukkitUtil.closeInventory(player);
				return;
			}

			if (match.getTeam1().contains(nextFighter) || match.getTeam2().contains(nextFighter)) {
				player.sendFormattedMessage("{0}That player is already in a team.", ChatColor.RED);
				return;
			}

			if (match.getCaptain1().equals(player)) {
				Gberry.log("REDROVER", "[ChosePlayerInv] Player is captain1");

				if (match.isCaptain1Picking() == true) {
					Gberry.log("REDROVER", "[ChosePlayerInv] It is captain1 turn to pick");

					match.setCaptain1Picking(false);

					match.setFighter1(nextFighter);

					// Cancel auto pick task
					if (match.getAutoPickPlayerCaptain1Task() != null) {
						match.getAutoPickPlayerCaptain1Task().cancel();
					}
				} else {
					player.sendFormattedMessage("{0}It is not your turn to pick a player!", ChatColor.RED);
				}
			} else if (match.getCaptain2().equals(player)) {
				Gberry.log("REDROVER", "[ChosePlayerInv] Player is captain2");

				if (match.isCaptain1Picking() == false) {
					Gberry.log("REDROVER", "[ChosePlayerInv] It is captain2 turn to pick");
					
					match.setCaptain1Picking(true);

					match.setFighter2(nextFighter);

					if (match.getAutoPickPlayerCaptain2Task() != null) {
						match.getAutoPickPlayerCaptain2Task().cancel();
					}
				} else {
					player.sendFormattedMessage("{0}It is not your turn to pick a player!", ChatColor.RED);
				}
			} else {
				player.sendFormattedMessage("{0}You are not a captain!", ChatColor.RED);
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
