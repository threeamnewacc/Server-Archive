package net.badlion.arenapvp.helper;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DeathHelper {

	// Once you go into spectator on the server there should not be any way out. The only way out is back to lobby.
	public static void enableDeathMode(Player player) {
		State<Player> currentState = TeamStateMachine.getInstance().getCurrentState(player);
		try {
			currentState.transition(TeamStateMachine.deathState, player);
		} catch (IllegalStateTransitionException e) {
			e.printStackTrace();
			return;
		}

		SpectatorHelper.setGameModeCreative(player);

		player.updateInventory();

		for (Player p2 : ArenaPvP.getInstance().getServer().getOnlinePlayers()) {
			if (p2 == player) {
				continue;
			}
			// Show player if they aren't a spectator
			if (TeamStateMachine.spectatorState.contains(p2)) {
				// Hide players from each other if they are a spectator
				player.hidePlayer(p2);
				Gberry.log("VISIBILITY", "EnableDeath: " + player.getName() + " hides " + p2.getName());
			} else {
				player.showPlayer(p2);
				Gberry.log("VISIBILITY", "EnableDeath: " + player.getName() + " shows " + p2.getName());
			}
			p2.hidePlayer(player);
			Gberry.log("VISIBILITY", "EnableDeath: " + p2.getName() + " hides " + player.getName());
		}
	}

	public static void disableDeathMode(Player player) {
		SpectatorHelper.setGameModeSurvival(player);

		for (Player p2 : ArenaPvP.getInstance().getServer().getOnlinePlayers()) {
			if (p2 == player) {
				continue;
			}
			if (TeamStateMachine.spectatorState.contains(p2)) {
				// Hide all the spectators
				player.hidePlayer(p2);
				Gberry.log("VISIBILITY", "DisableDeath: " + player.getName() + " hides " + p2.getName());
			}
			// Show this player to everyone
			p2.showPlayer(player);
			Gberry.log("VISIBILITY", "DisableDeath: " + p2.getName() + " shows " + player.getName());
		}

	}


}
