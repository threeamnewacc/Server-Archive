package net.badlion.capturetheflag.manager;

import net.badlion.capturetheflag.CTF;
import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.capturetheflag.CTFTeam;
import net.badlion.capturetheflag.tasks.FlagBaseRespawnTask;
import net.badlion.capturetheflag.tasks.FlagRespawnTask;
import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.managers.MPGKitManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlagManager {

	public static void pickUpFlag(CTFPlayer ctfPlayer, CTFTeam ctfTeam) {
		ItemStack flagHat = new ItemStack(Material.WOOL, 1, ctfTeam.getWoolColorFromChatColor(ctfTeam.getColor()));
		ctfPlayer.getPlayer().getInventory().setHelmet(flagHat);
		ctfPlayer.getPlayer().updateInventory();
		Gberry.broadcastMessage(MPG.MPG_PREFIX + ctfPlayer.getTeam().getColor() + ctfPlayer.getUsername()
				+ ChatColor.GOLD + " has taken " + ctfTeam.getColor() + ctfTeam.getTeamName() + " Team's"
				+ ChatColor.GOLD + " flag!");
		ctfTeam.setFlagState(CTFTeam.FlagState.TAKEN);
		ctfTeam.removeFlag();
		ctfTeam.setFlagLocation(null);
		ctfPlayer.incrementFlagsPickedUp();
		addFlagItem(ctfPlayer, ctfTeam);
	}

	public static void deliverFlag(CTFPlayer ctfPlayer, CTFTeam ctfTeam) {
		// Remove flag from player
		MPGKitManager.loadKit(ctfPlayer.getPlayer(), CTF.getInstance().getCTFGame().getGamemode().getDefaultKit(), false);
		ctfPlayer.getTeam().addScore(1);
		Gberry.broadcastMessage(MPG.MPG_PREFIX + ctfPlayer.getTeam().getColor() + ctfPlayer.getUsername()
				+ ChatColor.GOLD + " has captured " + ctfTeam.getColor() + ctfTeam.getTeamName() + " Team's"
				+ ChatColor.GOLD + " flag!");

		ctfTeam.setFlagState(CTFTeam.FlagState.RESPAWN);
		FlagBaseRespawnTask flagBaseRespawnTask = new FlagBaseRespawnTask(ctfTeam);
		flagBaseRespawnTask.runTaskTimer(CTF.getInstance(), 0L, 1L);
		ctfPlayer.incrementFlagsDelivered();
	}

	public static void dropFlag(CTFPlayer ctfPlayer, CTFTeam ctfTeam, Location dropLocation) {
		ctfTeam.setFlagState(CTFTeam.FlagState.DROPPED);
		ctfTeam.setFlagLocation(dropLocation);
		ctfTeam.placeFlag();
		FlagRespawnTask flagRespawnTask = new FlagRespawnTask(ctfTeam);
		flagRespawnTask.runTaskTimer(CTF.getInstance(), 0L, 1L);
		Gberry.broadcastMessage(MPG.MPG_PREFIX + ctfPlayer.getTeam().getColor() + ctfPlayer.getUsername()
				+ ChatColor.GOLD + " has dropped " + ctfTeam.getColor() + ctfTeam.getTeamName() + " Team's"
				+ ChatColor.GOLD + " flag!");
	}

	private static void addFlagItem(CTFPlayer ctfPlayer, CTFTeam flagTeam) {
		Player player = ctfPlayer.getPlayer();
		if (player.getInventory().firstEmpty() == -1) {
			for (int i = 9; i <= 17; i++) {
				ItemStack item = player.getInventory().getItem(i);
				if (!isFlag(item)) {
					player.getInventory().setItem(i, null);
					player.getInventory().addItem(flagTeam.getFlagItem());
					player.getWorld().dropItem(player.getLocation(), item);
				}
			}
		} else {
			player.getInventory().addItem(flagTeam.getFlagItem());
		}
		player.updateInventory();
	}

	public static boolean isFlag(ItemStack item) {
		return item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
				item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Team Flag");
	}


}
