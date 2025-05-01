package net.badlion.arenalobby.helpers;

import net.badlion.arenalobby.Group;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class PlayerHelper {

	public static String getHeartsLeftString(ChatColor color, double healthLeft) {
		return " (" + Math.ceil(healthLeft) / 2D + " " + MessageUtil.HEART_WITH_COLOR + color + ")";
	}

	public static void healAndPrepPlayerForBattle(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.spigot().setCollidesWithEntities(true);

		PlayerHelper.healPlayer(player);

		// Clear all buffs on them too...not sure when we wouldn't want to do this
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		player.setFireTicks(0);
		PlayerHelper.removeArrows(player);

		// No item exploits
		player.closeInventory();
		player.setItemOnCursor(null);
		//player.getInventory().setHeldItemSlot(0);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
	}

	public static void healAndPrepGroupForBattle(Group group) {
		PlayerHelper.healAndPrepPlayersForBattle(group.players());
	}

	public static void healAndPrepPlayersForBattle(List<Player> players) {
		for (Player player : players) {
			PlayerHelper.healAndPrepPlayerForBattle(player);
		}
	}

	public static void healPlayer(Player player) {
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setExhaustion(0);
	}

	public static void removeArrows(Player player) {
		player.setArrowsStuck(0);
	}

}
