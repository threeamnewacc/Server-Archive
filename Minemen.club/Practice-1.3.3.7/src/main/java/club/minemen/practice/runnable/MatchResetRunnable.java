package club.minemen.practice.runnable;

import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;


@RequiredArgsConstructor
public class MatchResetRunnable extends BukkitRunnable {

	private final Practice plugin = Practice.getInstance();
	private final Match match;

	@Override
	public void run() {
		int count = 0;

		if (this.match.getKit().isBuild()) {
			for (Location location : this.match.getPlacedBlockLocations()) {
				if (++count <= 15) {
					location.getBlock().setType(Material.AIR);
					this.match.removePlacedBlockLocation(location);
				} else {
					break;
				}
			}
		} else {
			for (BlockState blockState : this.match.getOriginalBlockChanges()) {
				if (++count <= 15) {
					blockState.getLocation().getBlock().setType(blockState.getType());
					this.match.removeOriginalBlockChange(blockState);
				} else {
					break;
				}
			}
		}

		if (count < 15) {
			this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
			this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
			this.cancel();
		}
	}
}
