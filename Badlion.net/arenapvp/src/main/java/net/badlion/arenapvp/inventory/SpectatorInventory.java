package net.badlion.arenapvp.inventory;

import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.matchmaking.Match;
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

public class SpectatorInventory {


	public static void openSelectPlayersInventory(Player player, Match match) {
		SmellyInventory smellyInventory = new SmellyInventory(new SelectPlayerScreenHandler(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Teleport to a Player");

		fillInventory(smellyInventory.getMainInventory(), match);

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());

	}

	public static void fillInventory(Inventory inventory, Match match) {
		for(Team team : match.getTeams()){
			for(Player member : team.members()){
				if(team.isActive(member)){
					inventory.addItem(getSkullForPlayer(member, team.toString()));
				}
			}
		}
	}


	private static ItemStack getSkullForPlayer(Player player, String lore) {
		return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.GREEN + player.getDisguisedName(), lore);
	}


	public static class SelectPlayerScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (!item.getType().equals(Material.SKULL_ITEM)) {
				return;
			}

			if(TeamStateMachine.spectatorState.contains(player)){
				Player target = Bukkit.getPlayerExact(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
				if(target != null){
					player.teleport(target);
					player.sendFormattedMessage("{0}Teleported to {1}", ChatColor.GREEN, target.getDisguisedName());
				}else{
					player.sendFormattedMessage("{0}Player not found.", ChatColor.RED);
				}
			}
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
		}

	}
}
