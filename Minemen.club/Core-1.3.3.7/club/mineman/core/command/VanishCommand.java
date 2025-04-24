// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import java.util.Iterator;
import club.mineman.core.mineman.Mineman;
import org.bukkit.Bukkit;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class VanishCommand extends Command
{
    private final CorePlugin plugin;
    
    public VanishCommand(final CorePlugin plugin) {
        super("vanish");
        this.plugin = plugin;
        this.setDescription("Vanish instantly.");
        this.setAliases((List)Collections.singletonList("v"));
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        if (!PlayerUtil.testPermission(sender, Rank.MOD)) {
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        Rank hidingFrom = Rank.TRAINEE;
        if (args.length == 1) {
            hidingFrom = Rank.getByName(args[0]);
        }
        else if (hidingFrom.getPriority() >= mineman.getRank().getPriority()) {
            player.sendMessage(CC.RED + "You cannot hide yourself from players with the " + hidingFrom.getColor() + hidingFrom.getName() + CC.RED + " rank!");
            return true;
        }
        if (mineman.isVanishMode()) {
            mineman.setVanishMode(false);
            final Player other;
            this.plugin.getServer().getOnlinePlayers().stream().filter(other -> !other.canSee(player)).forEach(other -> other.showPlayer(player));
            player.sendMessage(CC.GREEN + "You are now visible to all players.");
        }
        else {
            mineman.setVanishMode(true);
            for (final Player other : Bukkit.getOnlinePlayers()) {
                if (player == other) {
                    continue;
                }
                final Mineman otherMineman = this.plugin.getPlayerManager().getPlayer(other.getUniqueId());
                if (otherMineman.getRank().getPriority() > hidingFrom.getPriority()) {
                    continue;
                }
                other.hidePlayer(player);
            }
            player.sendMessage(CC.GOLD + "You are now invisible to players ranked " + hidingFrom.getColor() + hidingFrom.getName() + CC.GOLD + " and below.");
        }
        return true;
    }
}
