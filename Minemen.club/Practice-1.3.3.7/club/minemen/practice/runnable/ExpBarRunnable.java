// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import org.bukkit.entity.Player;
import java.util.Iterator;
import java.util.UUID;
import club.minemen.core.CorePlugin;
import club.minemen.core.timer.impl.EnderpearlTimer;
import club.minemen.practice.Practice;

public class ExpBarRunnable implements Runnable
{
    private final Practice plugin;
    
    @Override
    public void run() {
        final EnderpearlTimer timer = (EnderpearlTimer)CorePlugin.getInstance().getTimerManager().getTimer((Class)EnderpearlTimer.class);
        for (final UUID uuid : timer.getCooldowns().keySet()) {
            final Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                final long time = timer.getRemaining(player);
                final int seconds = (int)Math.round(time / 1000.0);
                player.setLevel(seconds);
                player.setExp(time / 15000.0f);
            }
        }
    }
    
    public ExpBarRunnable() {
        this.plugin = Practice.getInstance();
    }
}
