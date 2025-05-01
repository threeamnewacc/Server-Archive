package net.badlion.arenapvp.state;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.GState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MatchState extends GState<Player> implements Listener {

	private static Map<Player, Match> playerMatchMap = new HashMap<>();


	public MatchState() {
		super("match", "they are in a match.", TeamStateMachine.getInstance());
	}

	@Override
	public void before(Player player) {
		super.before(player);
		Gberry.log("STATE", "MATCH before: " + player.getName());
	}

	@Override
	public void after(Player player) {
		super.after(player);
		Gberry.log("STATE", "MATCH after: " + player.getName());
		PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
		if(potPvPPlayer != null){
			potPvPPlayer.setSelectingKit(false);
		}

		this.removePlayerMatch(player);
	}

	public static Match getPlayerMatch(Player player) {
		return MatchState.playerMatchMap.get(player);
	}


	public void setPlayerMatch(Player player, Match match) {
		// Debug code
		if (MatchState.playerMatchMap.containsKey(player)) {
			try {
				throw new Exception("Unexpected player already located in map " + player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		MatchState.playerMatchMap.put(player, match);
	}

	public Match removePlayerMatch(Player player) {
		return MatchState.playerMatchMap.remove(player);
	}

	public static boolean playerIsInMatchAndUsingRuleSet(Player player, KitRuleSet ruleSet) {
		return TeamStateMachine.matchState.contains(player)
				&& MatchState.getPlayerMatch(player) != null
				&& MatchState.getPlayerMatch(player).getKitRuleSet().getClass().isAssignableFrom(ruleSet.getClass());
	}
}
