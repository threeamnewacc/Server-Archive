package net.badlion.arenapvp.helper;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.listener.SpectatorListener;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectatorHelper {

	private static ItemStack compassItem;
	private static ItemStack spectateFFAItem;
	private static ItemStack spectateEventsItem;
	private static ItemStack spectateTDMItem;
	private static ItemStack spectatorToggleOffItem;
	private static ItemStack spectatorTeleportItem;
	private static ItemStack spectatorLeatherHelmetColorsOn;
	private static ItemStack spectatorLeatherHelmetColorsOff;

	public static void initialize() {
		SpectatorHelper.compassItem = ItemStackUtil.createItem(Material.COMPASS, ChatColor.GREEN + "Teleportation Tool");
		SpectatorHelper.spectatorTeleportItem = ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Player Selector");

		SpectatorHelper.spectatorLeatherHelmetColorsOn = ItemStackUtil.createItem(Material.INK_SACK, (short) 10, ChatColor.GRAY + "Colored Helmets: " + ChatColor.GREEN + ChatColor.BOLD.toString() + "ON");
		SpectatorHelper.spectatorLeatherHelmetColorsOff = ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.GRAY + "Colored Helmets: " + ChatColor.RED + ChatColor.BOLD.toString() + "OFF");

		SpectatorHelper.spectatorToggleOffItem = ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Spectator Toggle Off");
	}

	public static void setGameModeCreative(Player player) {
		player.setGameMode(GameMode.CREATIVE);
		player.spigot().setCollidesWithEntities(false); // hax
		player.getInventory().clear();
		player.setFlying(true);
	}

	public static void setGameModeSurvival(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.spigot().setCollidesWithEntities(true); // hax
		player.getInventory().clear();
		player.setFlying(false);
	}


	// Once you go into spectator on the server there should not be any way out. The only way out is back to lobby.
	public static void activateSpectateGameMode(Player player) {
		State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(player);
		try {
			currentState.transition(TeamStateMachine.spectatorState, player);
		} catch (IllegalStateTransitionException e) {
			player.sendFormattedMessage("{0}Cannot use spectator at the moment.", ChatColor.RED);
			return;
		}

		SpectatorHelper.setGameModeCreative(player);

		SpectatorListener.spectatorWarmup.put(player.getUniqueId(), System.currentTimeMillis());

		player.getInventory().setItem(0, SpectatorHelper.compassItem);
		player.getInventory().setItem(8, SpectatorHelper.spectatorToggleOffItem);

		player.updateInventory();

		for (Player p2 : ArenaPvP.getInstance().getServer().getOnlinePlayers()) {
			if (p2 == player) {
				continue;
			}
			// Show player if they aren't a spectator
			if (TeamStateMachine.spectatorState.contains(p2)) {
				// Hide players from each other if they are a spectator
				player.hidePlayer(p2);
				Gberry.log("VISIBILITY", "EnableSpec: " + player.getName() + " hides " + p2.getName());
			} else {
				player.showPlayer(p2);
				Gberry.log("VISIBILITY", "EnableSpec: " + player.getName() + " shows " + p2.getName());
			}
			p2.hidePlayer(player);
			Gberry.log("VISIBILITY", "EnableSpec: " + p2.getName() + " hides " + player.getName());
		}
	}

	public static void disableSpectateGameMode(Player player) {
		SpectatorHelper.setGameModeSurvival(player);

		for (Player p2 : ArenaPvP.getInstance().getServer().getOnlinePlayers()) {
			if (p2 == player) {
				continue;
			}
			if (TeamStateMachine.spectatorState.contains(p2)) {
				// Hide all the spectators
				player.hidePlayer(p2);
				Gberry.log("VISIBILITY", "DisableSpec: " + player.getName() + " hides " + p2.getName());
			}
			// Show this player to everyone
			p2.showPlayer(player);
			Gberry.log("VISIBILITY", "DisableSpec: " + p2.getName() + " shows " + player.getName());
		}

	}

	public static ItemStack getCompassItem() {
		return compassItem;
	}

	public static ItemStack getSpectatorTeleportItem() {
		return spectatorTeleportItem;
	}

	public static ItemStack getSpectatorLeatherHelmetColorsOn() {
		return spectatorLeatherHelmetColorsOn;
	}

	public static ItemStack getSpectatorLeatherHelmetColorsOff() {
		return spectatorLeatherHelmetColorsOff;
	}
}
