package club.minemen.practice.runnable;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchState;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class MatchRunnable extends BukkitRunnable {

	private final Practice plugin = Practice.getInstance();
	private final Match match;

	@Override
	public void run() {
		switch (this.match.getMatchState()) {
			case STARTING:
				if (this.match.decrementCountdown() == 0) {
					this.match.setMatchState(MatchState.FIGHTING);
					this.match.broadcastWithSound(CC.PRIMARY + "The match has started!", Sound.FIREWORK_BLAST);
					if (this.match.isRedrover()) {
						this.plugin.getMatchManager().pickPlayer(this.match);
					}
				} else {
					this.match.broadcastWithSound(CC.PRIMARY + "The match starts in " + CC.SECONDARY
							+ this.match.getCountdown() + CC.PRIMARY + " second(s)...", Sound.CLICK);
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
			case ENDING:
				if (this.match.decrementCountdown() == 0) {
					this.plugin.getTournamentManager().removeTournamentMatch(this.match);
					this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask(id));
					this.match.getEntitiesToRemove().forEach(Entity::remove);
					this.match.getTeams().forEach(team ->
							team.alivePlayers().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset));
					this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);
					this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
					this.match.getOriginalBlockChanges().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
					if (this.match.getKit().isBuild() || this.match.getKit().isSpleef()) {
						this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
						this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
					}
					this.plugin.getMatchManager().removeMatch(this.match);
					//new MatchResetRunnable(this.match).runTaskTimer(this.plugin, 20L, 20L);
					this.cancel();
				}
				break;
		}
	}
}
