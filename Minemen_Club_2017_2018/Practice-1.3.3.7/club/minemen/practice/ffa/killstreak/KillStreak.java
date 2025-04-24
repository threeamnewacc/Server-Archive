// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.ffa.killstreak;

import java.util.List;
import org.bukkit.entity.Player;

public interface KillStreak
{
    void giveKillStreak(final Player p0);
    
    List<Integer> getStreaks();
}
