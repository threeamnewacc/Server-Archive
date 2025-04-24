// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

import club.mineman.core.mineman.Mineman;
import org.bukkit.command.CommandSender;
import club.mineman.core.CorePlugin;
import org.bukkit.Bukkit;
import club.mineman.core.rank.Rank;
import org.bukkit.entity.Player;
import java.util.Comparator;

public final class PlayerUtil
{
    public static final Comparator<Player> VISIBLE_RANK_ORDER;
    
    public PlayerUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }
    
    public static void messageStaff(final String message) {
        messageStaff(message, Rank.TRAINEE);
    }
    
    public static void messageStaff(final String message, final Rank rank) {
        Bukkit.getOnlinePlayers().stream().filter(player -> CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).hasRank(rank)).forEach(player -> player.sendMessage(message));
    }
    
    public static boolean testPermission(final CommandSender sender, final Rank requiredRank) {
        if (sender instanceof Player) {
            final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player)sender).getUniqueId());
            if (!mineman.hasRank(requiredRank)) {
                sender.sendMessage(StringUtil.NO_PERMISSION);
                return false;
            }
        }
        return true;
    }
    
    static {
        VISIBLE_RANK_ORDER = ((a, b) -> {
            final Mineman minemanA = CorePlugin.getInstance().getPlayerManager().getPlayer(a.getUniqueId());
            final Mineman minemanB = CorePlugin.getInstance().getPlayerManager().getPlayer(b.getUniqueId());
            return -minemanA.getRank().compareTo(minemanB.getRank());
        });
    }
}
