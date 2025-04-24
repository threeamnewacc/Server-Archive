// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.listeners;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.entity.Entity;
import org.bukkit.Material;
import club.minemen.practice.match.Match;
import org.bukkit.plugin.Plugin;
import club.minemen.practice.player.PlayerData;
import club.minemen.core.event.PreShutdownEvent;
import club.minemen.practice.Practice;
import org.bukkit.event.Listener;

public class ShutdownListener implements Listener
{
    private final Practice plugin;
    
    public ShutdownListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler
    public void onPreShutdown(final PreShutdownEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            this.plugin.getPlayerManager().getAllData().iterator();
            final Iterator iterator2;
            while (iterator2.hasNext()) {
                final PlayerData playerData = iterator2.next();
                this.plugin.getPlayerManager().saveData(playerData);
            }
            return;
        });
        for (final Match match : this.plugin.getMatchManager().getMatches().values()) {
            match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
            match.getOriginalBlockChanges().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
            match.getEntitiesToRemove().forEach(Entity::remove);
        }
    }
}
