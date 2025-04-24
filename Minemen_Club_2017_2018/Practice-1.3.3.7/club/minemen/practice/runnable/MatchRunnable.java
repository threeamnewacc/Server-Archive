// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import club.minemen.practice.match.MatchTeam;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import java.beans.ConstructorProperties;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import club.minemen.practice.match.MatchState;
import org.bukkit.Sound;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.match.Match;
import club.minemen.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private final Match match;
    
    public void run() {
        switch (this.match.getMatchState()) {
            case STARTING: {
                if (this.match.decrementCountdown() != 0) {
                    this.match.broadcastWithSound(CC.PRIMARY + "The match starts in " + CC.SECONDARY + this.match.getCountdown() + CC.PRIMARY + " second(s)...", Sound.CLICK);
                    break;
                }
                this.match.setMatchState(MatchState.FIGHTING);
                this.match.broadcastWithSound(CC.PRIMARY + "The match has started!", Sound.FIREWORK_BLAST);
                if (this.match.isRedrover()) {
                    this.plugin.getMatchManager().pickPlayer(this.match);
                    break;
                }
                break;
            }
            case SWITCHING: {
                if (this.match.decrementCountdown() == 0) {
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.clearEntitiesToRemove();
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.plugin.getMatchManager().pickPlayer(this.match);
                    break;
                }
                break;
            }
            case ENDING: {
                if (this.match.decrementCountdown() == 0) {
                    this.plugin.getTournamentManager().removeTournamentMatch(this.match);
                    this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask((int)id));
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.getTeams().forEach(team -> team.alivePlayers().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset));
                    this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);
                    this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
                    this.match.getOriginalBlockChanges().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
                    if (this.match.getKit().isBuild() || this.match.getKit().isSpleef()) {
                        this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
                        this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
                    }
                    this.plugin.getMatchManager().removeMatch(this.match);
                    this.cancel();
                    break;
                }
                break;
            }
        }
    }
    
    @ConstructorProperties({ "match" })
    public MatchRunnable(final Match match) {
        this.plugin = Practice.getInstance();
        this.match = match;
    }
}
