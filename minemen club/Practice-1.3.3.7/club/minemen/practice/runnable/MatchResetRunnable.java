// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import org.bukkit.block.BlockState;
import org.bukkit.Material;
import org.bukkit.Location;
import club.minemen.practice.match.Match;
import club.minemen.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchResetRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private final Match match;
    
    public void run() {
        int count = 0;
        if (this.match.getKit().isBuild()) {
            for (final Location location : this.match.getPlacedBlockLocations()) {
                if (++count > 15) {
                    break;
                }
                location.getBlock().setType(Material.AIR);
                this.match.removePlacedBlockLocation(location);
            }
        }
        else {
            for (final BlockState blockState : this.match.getOriginalBlockChanges()) {
                if (++count > 15) {
                    break;
                }
                blockState.getLocation().getBlock().setType(blockState.getType());
                this.match.removeOriginalBlockChange(blockState);
            }
        }
        if (count < 15) {
            this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
            this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
            this.cancel();
        }
    }
    
    @ConstructorProperties({ "match" })
    public MatchResetRunnable(final Match match) {
        this.plugin = Practice.getInstance();
        this.match = match;
    }
}
