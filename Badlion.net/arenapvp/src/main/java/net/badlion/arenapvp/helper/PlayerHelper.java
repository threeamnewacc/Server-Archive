package net.badlion.arenapvp.helper;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.manager.EnderPearlManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.EntityUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
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

		// No pearl glitches
		EnderPearlManager.remove(player);

		// No item exploits
		PlayerHelper.clearInventory(player);
	}

	public static void healAndPrepGroupForBattle(Team team) {
		PlayerHelper.healAndPrepPlayersForBattle(team.members());
	}

	public static void healAndPrepPlayersForBattle(List<Player> players) {
		for (Player player : players) {
			PlayerHelper.healAndPrepPlayerForBattle(player);
		}
	}

	public static void healPlayer(Player player) {
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setExhaustion(0);
	}

	public static void removeArrows(Player player) {
		player.setArrowsStuck(0);
	}

	public static void clearInventory(Player player) {
		player.closeInventory();
		player.setItemOnCursor(null);
		player.getInventory().setHeldItemSlot(0);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
	}

	public static void showDyingNPC(Player player) {
		Location loc = player.getLocation();
		final List<Player> players = new ArrayList<>();
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other != player && other.getWorld() == player.getWorld() && other.getLocation().distanceSquared(loc) < 32 * 32) {
				players.add(other);
			}
		}
		Object entityPlayer = TinyProtocolReferences.getPlayerHandle.invoke(player);
		Object spawnPacket = TinyProtocolReferences.invokeSpawnPacketConstructor(entityPlayer, 0); // version is now unused
		int entityID = EntityUtil.newEntityID();
		TinyProtocolReferences.spawnPacketEntityID.set(spawnPacket, entityID);
		Object statusPacket = TinyProtocolReferences.packetEntityStatusConstructor.invoke();
		TinyProtocolReferences.packetEntityStatusEntityID.set(statusPacket, entityID);
		TinyProtocolReferences.packetEntityStatusStatusID.set(statusPacket, (byte) 3);
		Object destroyPacket = TinyProtocolReferences.destroyPacketConstructor.invoke(new int[]{entityID});
		for (Player other : players) {
			Gberry.protocol.sendPacket(other, spawnPacket);
			Gberry.protocol.sendPacket(other, statusPacket);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player other : players) {
					Gberry.protocol.sendPacket(other, destroyPacket);
				}
			}
		}.runTaskLater(ArenaPvP.getInstance(), 7);
	}

}
