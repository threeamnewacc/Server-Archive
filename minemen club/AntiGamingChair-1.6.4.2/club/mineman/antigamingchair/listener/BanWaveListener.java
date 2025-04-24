// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.listener;

import java.beans.ConstructorProperties;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import club.mineman.antigamingchair.runnable.BanWaveRunnable;
import club.mineman.core.util.finalutil.CC;
import club.mineman.antigamingchair.event.BanWaveEvent;
import club.mineman.antigamingchair.AntiGamingChair;
import org.bukkit.event.Listener;

public class BanWaveListener implements Listener
{
    private final AntiGamingChair plugin;
    
    @EventHandler
    public void onBanWave(final BanWaveEvent e) {
        this.plugin.getServer().broadcastMessage(CC.S + "--------------------------------------------------\n" + CC.R + "\u2718 " + CC.PINK + "AntiGamingChair has been ordered to commence a ban wave by " + CC.D_PURPLE + ((e.getInstigator() == null) ? "CONSOLE" : e.getInstigator()) + CC.PINK + ".\n" + CC.R + CC.S + "--------------------------------------------------\n");
        this.plugin.getBanWaveManager().setBanWaveStarted(true);
        this.plugin.getBanWaveManager().loadCheaters();
        new BanWaveRunnable(this.plugin).runTaskTimerAsynchronously((Plugin)this.plugin, 20L, 20L);
    }
    
    @ConstructorProperties({ "plugin" })
    public BanWaveListener(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
