// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util;

import java.util.UUID;
import club.mineman.core.mineman.Mineman;
import java.beans.ConstructorProperties;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import club.mineman.core.util.finalutil.CC;
import java.util.function.Function;
import org.bukkit.OfflinePlayer;
import java.util.Comparator;
import club.mineman.core.util.finalutil.PlayerUtil;
import java.util.Collection;
import java.util.ArrayList;
import club.mineman.core.CorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class PlayerList
{
    private final List<Player> players;
    
    public static PlayerList getVisiblyOnline(final CommandSender sender) {
        return getOnline().visibleTo(sender);
    }
    
    public static PlayerList getOnline() {
        return new PlayerList(new ArrayList<Player>(CorePlugin.getInstance().getServer().getOnlinePlayers()));
    }
    
    public PlayerList visibleTo(final CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            this.players.removeIf(other -> other != player && !player.canSee(other));
        }
        return this;
    }
    
    public PlayerList canSee(final CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            this.players.removeIf(other -> other == player || !other.canSee(player));
        }
        return this;
    }
    
    public PlayerList visibleRankSorted() {
        this.players.sort(PlayerUtil.VISIBLE_RANK_ORDER);
        return this;
    }
    
    public List<String> asColoredNames() {
        return this.players.stream().map((Function<? super Object, ?>)OfflinePlayer::getUniqueId).map(uuid -> CorePlugin.getInstance().getPlayerManager().getPlayer(uuid)).map(mineman -> mineman.getRank().getColor() + mineman.getName() + CC.R).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
    }
    
    public List<Player> getPlayers() {
        return this.players;
    }
    
    @ConstructorProperties({ "players" })
    public PlayerList(final List<Player> players) {
        this.players = players;
    }
}
