package net.badlion.gfactions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionRelationEvent;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Faction faction = FPlayers.i.get(player).getFaction();

        // Setup scoreboard for relationship tags if player has faction
        if (!faction.getId().equals("0")) {
            Scoreboard board = player.getScoreboard();

	        if (board == GFactions.plugin.getServer().getScoreboardManager().getMainScoreboard()) {
		        board = GFactions.plugin.getServer().getScoreboardManager().getNewScoreboard();
	        }

            Team mates = board.registerNewTeam("mates");
            mates.setPrefix(ChatColor.GREEN.toString());

            //Team allies = board.registerNewTeam("allies");
            //allies.setPrefix(ChatColor.LIGHT_PURPLE.toString());

            Team enemies = board.registerNewTeam("enemies");
            enemies.setPrefix(ChatColor.RED.toString());

            for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
                if (player != pl) {
                    FPlayer fPlayer = FPlayers.i.get(pl);
                    if (fPlayer.getFaction().getId().equals("0")) {
                        continue;
                    }

                    switch (faction.getRelationTo(FPlayers.i.get(pl))) {
                        case MEMBER:
                        case OFFICER:
                        case LEADER:
                        case RECRUIT:
                            mates.addPlayer(pl);
                            pl.getScoreboard().getTeam("mates").addPlayer(player);
                            break;
                        /*case ALLY:
                            allies.addPlayer(p2);
                            p2.getScoreboard().getTeam("allies").addPlayer(player);
                            break;*/
                        case ENEMY:
                            enemies.addPlayer(pl);
                            pl.getScoreboard().getTeam("enemies").addPlayer(player);
                            break;
                    }
                }
            }

            player.setScoreboard(board);
        } else {
			player.setScoreboard(GFactions.plugin.getServer().getScoreboardManager().getNewScoreboard());
		}
    }

    @EventHandler
    public void factionRelationChangeEvent(FactionRelationEvent event) {
        for (Player player : event.getFaction().getOnlinePlayers()) {
            Scoreboard board = player.getScoreboard();

            Team mates = board.getTeam("mates");
            mates.setPrefix(ChatColor.GREEN.toString());

            //Team allies = board.getTeam("allies");
            //allies.setPrefix(ChatColor.LIGHT_PURPLE.toString());

            Team enemies = board.getTeam("enemies");
            enemies.setPrefix(ChatColor.RED.toString());

            for (Player pl : event.getTargetFaction().getOnlinePlayers()) {
                if (player != pl) {
                    FPlayer fPlayer = FPlayers.i.get(pl);
                    if (fPlayer.getFaction().getId().equals("0")) {
                        continue;
                    }

                    switch (event.getRelation()) {
                        case MEMBER:
                        case OFFICER:
                        case LEADER:
                        case RECRUIT:
                            mates.addPlayer(pl);
                            pl.getScoreboard().getTeam("mates").addPlayer(player);
                            break;
                        /*case ALLY:
                            allies.addPlayer(pl);
                            pl.getScoreboard().getTeam("allies").addPlayer(player);
                            break;*/
                        case ENEMY:
                            enemies.addPlayer(pl);
                            pl.getScoreboard().getTeam("enemies").addPlayer(player);
                            break;
                        case NEUTRAL:
                            if (!pl.getScoreboard().getTeam("allies").removePlayer(player)) {
                                pl.getScoreboard().getTeam("enemies").removePlayer(player);
                            }
                    }
                }
            }
        }
    }

    @EventHandler
    public void factionJoinEvent(FPlayerJoinEvent event) {
        Player player = event.getFPlayer().getPlayer();
        Scoreboard board = player.getScoreboard();

	    if (board == GFactions.plugin.getServer().getScoreboardManager().getMainScoreboard()) {
		    board = GFactions.plugin.getServer().getScoreboardManager().getNewScoreboard();
		    player.setScoreboard(board);
	    }

        Team mates = board.getTeam("mates");
        if (mates == null) {
            mates = board.registerNewTeam("mates");
            mates.setPrefix(ChatColor.GREEN.toString());
        }

        //Team allies = board.registerNewTeam("allies");
        //allies.setPrefix(ChatColor.LIGHT_PURPLE.toString());

        Team enemies = board.getTeam("enemies");
        if (enemies == null) {
            enemies = board.registerNewTeam("enemies");
            enemies.setPrefix(ChatColor.RED.toString());
        }

        for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
            if (player != pl) {
                FPlayer fPlayer = FPlayers.i.get(pl);
                if (fPlayer.getFaction().getId().equals("0")) {
                    continue;
                }

                switch (event.getFaction().getRelationTo(FPlayers.i.get(pl))) {
                    case MEMBER:
                    case OFFICER:
                    case LEADER:
                    case RECRUIT:
                        mates.addPlayer(pl);
                        pl.getScoreboard().getTeam("mates").addPlayer(player);
                        break;
                    /*case ALLY:
                        allies.addPlayer(p2);
                        p2.getScoreboard().getTeam("allies").addPlayer(p);
                        break;*/
                    case ENEMY:
                        enemies.addPlayer(pl);
                        pl.getScoreboard().getTeam("enemies").addPlayer(player);
                        break;
                }
            }
        }

        player.setScoreboard(board);
    }

    @EventHandler
    public void factionLeaveEvent(FPlayerLeaveEvent e) {
        Player player = e.getFPlayer().getPlayer();

        if (player != null) {
			// Don't hard reset, just remove the teams
			Team team = player.getScoreboard().getTeam("mates");
			team.unregister();
			//team = p.getScoreboard().getTeam("allies");
			//team.unregister();
			team = player.getScoreboard().getTeam("enemies");
			team.unregister();

			for (Player pl : GFactions.plugin.getServer().getOnlinePlayers()) {
                if (player != pl) {
                    FPlayer fPlayer = FPlayers.i.get(pl);
                    if (fPlayer.getFaction().getId().equals("0")) {
                        continue;
                    }

                    // Chance that scoreboard or team can be null
                    if (pl.getScoreboard() == null) {
                        System.out.println("TAG LISTENER F PLAYER LEAVE EVENT SCOREBOARD NULL");
                        continue;
                    } else if (pl.getScoreboard().getTeam("enemies") == null) {
                        System.out.println("TAG LISTENER F PLAYER LEAVE EVENT TEAM \"ENEMIES\" NULL");
                        continue;
                    }

	                if (!pl.getScoreboard().getTeam("enemies").removePlayer(player)) {
		                pl.getScoreboard().getTeam("mates").removePlayer(player);
	                }
                    /*Team allies = p2.getScoreboard().getTeam("allies");
                    if (allies != null && !allies.removePlayer(p)) {
                        if (!p2.getScoreboard().getTeam("enemies").removePlayer(p)) {
                            p2.getScoreboard().getTeam("mates").removePlayer(p);
                        }
                    }*/
                }
            }
        }
    }

}
