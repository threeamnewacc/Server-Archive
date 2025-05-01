package net.badlion.potpvp.matchmaking;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TournamentMatch extends Match {

    public TournamentMatch( Arena arena, KitRuleSet kitRuleSet) {
        super(arena, false, kitRuleSet);

	    this.ladderType = Ladder.LadderType.Duel;
    }

	@Override
	public void startGame() {
		// Handle custom armor for spectators for team matches
		// Do this before we start the game, because if spectators are following
		// the players then it's in the wrong order
		if (this.group1.isParty()) {
			for (Player player : this.group1.players()) {
				PotPvP.getInstance().getCustomArmorPlayers().put(player.getEntityId(), ChatColor.BLUE);
			}

			for (Player player : this.group2.players()) {
				PotPvP.getInstance().getCustomArmorPlayers().put(player.getEntityId(), ChatColor.RED);
			}
		}

		super.startGame();
	}

	@Override
	public void handleCommonEnd(String reason) {
		super.handleCommonEnd(reason);

		// Handle custom armor for spectators for team matches
		if (this.group1.isParty()) {
			for (Player player : this.group1.players()) {
				PotPvP.getInstance().getCustomArmorPlayers().remove(player.getEntityId());
			}

			for (Player player : this.group2.players()) {
				PotPvP.getInstance().getCustomArmorPlayers().remove(player.getEntityId());
			}
		}

		// Broadcast message saying who won
		if (this.getWinner() != null) {
			if (this.group1.isParty()) {
				Gberry.broadcastMessage(ChatColor.LIGHT_PURPLE + this.getWinner().toString() + " have beaten "
						+ this.getLoser().toString() + " in a Tournament match!");
			} else {
				Gberry.broadcastMessage(ChatColor.LIGHT_PURPLE + this.getWinner().toString() + " has beaten "
						+ this.getLoser().toString() + " in a Tournament match!");
			}
		} else {
			Gberry.broadcastMessage(ChatColor.LIGHT_PURPLE + "The Tournament match has ended in a tie!");
		}
	}

}
