package me.devkevin.practice.match.task;

import lombok.RequiredArgsConstructor;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/01/2023 @ 15:48
 * MatchResetRunnable / me.devkevin.practice.match.task / Practice
 */
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
                    blockState.setType(blockState.getType());
                    blockState.update();
                    blockState.update(true);
                    blockState.update(true, false);
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
