package net.badlion.arenatablist.pvp.listeners;

import net.badlion.arenapvp.event.MatchEndEvent;
import net.badlion.arenapvp.event.MatchStartEvent;
import net.badlion.arenatablist.ArenaTabList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class ArenaPvPListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Give them a basic tablist on join update it with match details once the match starts.
		Player player = event.getPlayer();
		if (player != null) {
			if (ArenaTabList.getInstance().getArenaPvPTabListManager().getTabList(player) == null) {
				ArenaTabList.getInstance().getArenaPvPTabListManager().createTabList(player);
			}
		}
	}


	@EventHandler
	public void onMatchStart(MatchStartEvent event){
		for(Player member : event.getTeam1().keySet()){
			if(member != null) {
				ArenaTabList.getInstance().getArenaPvPTabListManager().updateMatchInfo(member, event.getTeam1(), event.getTeam2(), event.getTeam1Rank(), event.getTeam2Rank());
			}
		}
		for(Player member : event.getTeam2().keySet()){
			if(member != null) {
				ArenaTabList.getInstance().getArenaPvPTabListManager().updateMatchInfo(member, event.getTeam2(), event.getTeam1(), event.getTeam2Rank(), event.getTeam1Rank());
			}
		}
	}


	@EventHandler
	public void onMatchEnd(MatchEndEvent event){
		for(Player member : event.getPlayers()){
			// Clear out all the match data when it ends
			ArenaTabList.getInstance().getArenaPvPTabListManager().updateMatchInfo(member, new HashMap<Player, Boolean>(), new HashMap<Player, Boolean>(), null, null);
		}
	}

}
