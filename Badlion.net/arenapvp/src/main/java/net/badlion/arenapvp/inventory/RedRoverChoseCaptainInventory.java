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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RedRoverChoseCaptainInventory {

	public static void openSelectSecondCaptainInventory(Player player, RedRoverBattle match) {
		SmellyInventory smellyInventory = new SmellyInventory(new SelectCaptainScreenHandler(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Second Captain");
		int i = 0;
		match.getInventoryPlayers().clear();
		for (UUID playerId : match.getAllPlayers()) {
			Player member = Bukkit.getPlayer(playerId);
			if (member != null) {
				if (match.getCaptain1() == member) {
					continue;
				}
				smellyInventory.getMainInventory().setItem(i, RedRoverChoseCaptainInventory.getSkullForPlayer(member));
				match.getInventoryPlayers().put(i, member.getUniqueId());
			}
			i++;
		}

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}


	private static ItemStack getSkullForPlayer(Player player) {
		return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.GREEN + player.getDisguisedName());
	}


	public static class SelectCaptainScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {


			RedRoverBattle match = (RedRoverBattle) MatchState.getPlayerMatch(player);

			if (!match.getInventoryPlayers().containsKey(slot)) {
				return;
			}

			Player nextFighter = ArenaPvP.getInstance().getServer().getPlayer(match.getInventoryPlayers().get(slot));

			// Is the player still online?
			if (!Gberry.isPlayerOnline(nextFighter)) {
				player.sendFormattedMessage("{0}That player has left the game, please pick a new player!", ChatColor.RED);
				//BukkitUtil.closeInventory(player);
				return;
			}


			if (match.getCaptain2() != null) {
				BukkitUtil.closeInventory(player);
				return;
			}
			player.sendFormattedMessage("{0}Picked {1} as the second captain!", ChatColor.GOLD, ChatColor.GREEN + nextFighter.getDisguisedName());

			match.setCaptain2(nextFighter);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			RedRoverBattle match = (RedRoverBattle) MatchState.getPlayerMatch(player);
			if (match.getCaptain2() != null) {
				return;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!Gberry.isPlayerOnline(player)) {
						return;
					}
					if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof RedRoverChoseCaptainInventory)) {
						RedRoverBattle match = (RedRoverBattle) MatchState.getPlayerMatch(player);
						if (match.isOver()) {
							return;
						}

						openSelectSecondCaptainInventory(player, match);
						player.sendFormattedMessage("{0}Pick the other captain before you close the inventory.", ChatColor.RED);
					}
				}
			}.runTask(ArenaPvP.getInstance());
		}

	}
}
