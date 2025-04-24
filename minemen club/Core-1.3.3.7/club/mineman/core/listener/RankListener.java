// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.listener;

import org.bukkit.event.EventHandler;
import club.mineman.core.rank.Rank;
import org.bukkit.entity.Player;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.event.player.RankChangeEvent;
import org.bukkit.event.Listener;

public class RankListener implements Listener
{
    @EventHandler
    void onRankChange(final RankChangeEvent e) {
        final Player player = e.getPlayer();
        final Rank rank = e.getTo();
        player.setPlayerListName(rank.getColor() + player.getName() + CC.R);
    }
}
