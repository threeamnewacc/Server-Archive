// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.task;

import java.beans.ConstructorProperties;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.CorePlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastTask extends BukkitRunnable
{
    private static final String[] BROADCAST_MESSAGES;
    private final CorePlugin plugin;
    private int index;
    
    public void run() {
        if (++this.index >= BroadcastTask.BROADCAST_MESSAGES.length) {
            this.index = 0;
        }
        final String broadcastMessage = BroadcastTask.BROADCAST_MESSAGES[this.index];
        this.plugin.getServer().broadcastMessage(CC.GOLD + "[MMC] " + broadcastMessage);
    }
    
    @ConstructorProperties({ "plugin" })
    public BroadcastTask(final CorePlugin plugin) {
        this.index = -1;
        this.plugin = plugin;
    }
    
    static {
        BROADCAST_MESSAGES = new String[] { CC.PINK + "Butterfly clicking" + CC.D_PURPLE + " is strongly discouraged and may result in a ban.", CC.D_PURPLE + "Enjoy the server? Help keep it up AND get some awesome perks by donation here: " + CC.PINK + "http://store.mineman.club", CC.D_PURPLE + "Applications are now open! You can apply for staff here: " + CC.PINK + "http://bit.ly/2uVkSg3", CC.D_PURPLE + "Think you're #1? Check out our leaderboards here: " + CC.PINK + "http://mineman.club/leaderboards" };
    }
}
