// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import java.util.UUID;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import club.mineman.core.util.PlayerList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.bukkit.Bukkit;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class WhoCommand extends Command
{
    private final CorePlugin plugin;
    
    public WhoCommand(final CorePlugin plugin) {
        super("who");
        this.plugin = plugin;
        this.setAliases((List)Collections.singletonList("list"));
        this.setDescription("View all online players.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        final StringBuilder builder = new StringBuilder();
        final Rank[] ranks = Rank.RANKS;
        ArrayUtils.reverse((Object[])ranks);
        Arrays.stream(Rank.RANKS).forEach(rank -> builder.append(rank.getColor()).append(rank.getName()).append(" "));
        builder.append("\n");
        final List<String> players = new PlayerList((List<Player>)this.plugin.getPlayerManager().getPlayers().keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).map((Function<? super Object, ?>)this.plugin.getServer()::getPlayer).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList())).visibleRankSorted().asColoredNames();
        builder.append(CC.R).append("(").append(players.size()).append("/").append(this.plugin.getServer().getMaxPlayers()).append("): ").append(players.toString().replace("[", "").replace("]", ""));
        sender.sendMessage(builder.toString());
        return true;
    }
}
