package us.zonix.practice.runnable;

import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.event.match.MatchEndEvent;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchState;

public class MatchRunnable extends BukkitRunnable {
    private final Practice plugin = Practice.getInstance();
    private final Match match;

    public void run() {
        switch (this.match.getMatchState()) {
            case STARTING:
                if (this.match.decrementCountdown() == 0) {
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.match.broadcast(ChatColor.GREEN + "The match has started.");
                    if (this.match.getKit().isBuild()) {
                        this.match
                            .broadcast(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Stacking Blocks will result in a ban.");
                    }

                    if (this.match.isRedrover()) {
                        this.plugin.getMatchManager().pickPlayer(this.match);
                    }

                    this.match.setStartTime(new Date());
                } else {
                    this.match.broadcast(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + this.match.getCountdown() + ChatColor.YELLOW + "...");
                }
                break;
            case SWITCHING:
                if (this.match.decrementCountdown() == 0) {
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.clearEntitiesToRemove();
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.plugin.getMatchManager().pickPlayer(this.match);
                }
                break;
            case RESTARTING:
                if (this.match.decrementCountdown() == 0) {
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.match.broadcast(ChatColor.GREEN + "The match has started.");
                    if (this.match.getKit().isBuild()) {
                        this.match
                            .broadcast(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "WARNING " + ChatColor.RED + "Stacking Blocks will result in a ban.");
                    }

                    if (this.match.isRedrover()) {
                        this.plugin.getMatchManager().pickPlayer(this.match);
                    }
                } else {
                    this.match.broadcast(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + this.match.getCountdown() + ChatColor.YELLOW + "...");
                }
                break;
            case ENDING:
                if (this.match.decrementCountdown() == 0) {
                    this.plugin.getTournamentManager().removeTournamentMatch(this.match);
                    this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask(id));
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.getTeams().forEach(team -> team.alivePlayers().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset));
                    this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);
                    if (this.match.getKit().isBuild() || this.match.getKit().isSpleef()) {
                        new MatchResetRunnable(this.match).runTask(this.plugin);
                    }

                    this.plugin.getMatchManager().removeMatch(this.match);
                    this.cancel();
                }
                break;
            case FIGHTING:
                int remainingOne = this.match.getTeams().get(0) == null ? 0 : (int)this.match.getTeams().get(0).alivePlayers().count();
                int remainingTwo = this.match.getTeams().get(1) == null ? 0 : (int)this.match.getTeams().get(1).alivePlayers().count();
                if (remainingOne == 0) {
                    this.plugin
                        .getServer()
                        .getPluginManager()
                        .callEvent(new MatchEndEvent(this.match, this.match.getTeams().get(1), this.match.getTeams().get(0)));
                } else if (remainingTwo == 0) {
                    this.plugin
                        .getServer()
                        .getPluginManager()
                        .callEvent(new MatchEndEvent(this.match, this.match.getTeams().get(0), this.match.getTeams().get(1)));
                }
        }
    }

    public MatchRunnable(Match match) {
        this.match = match;
    }
}
