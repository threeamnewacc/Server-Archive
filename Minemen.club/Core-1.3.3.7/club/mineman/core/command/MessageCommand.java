// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.mineman.Mineman;
import club.mineman.core.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedHashMap;
import club.mineman.core.CorePlugin;
import java.util.UUID;
import java.util.Map;
import org.bukkit.command.Command;

public class MessageCommand extends Command
{
    private final Map<UUID, Map<UUID, Long>> timestamps;
    private final CorePlugin plugin;
    
    public MessageCommand(final CorePlugin plugin) {
        super("msg");
        this.timestamps = new LinkedHashMap<UUID, Map<UUID, Long>>();
        this.plugin = plugin;
        this.setAliases((List)Arrays.asList("tell", "w", "m", "message"));
        this.setDescription("Message a player");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (args.length < 2) {
            player.sendMessage(CC.RED + "Please use /msg <player> <message>");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player is not online.");
            return true;
        }
        final Mineman targetMineman = this.plugin.getPlayerManager().getPlayer(target.getUniqueId());
        if (targetMineman == null) {
            player.sendMessage(ChatColor.RED + "Player is not online.");
            return true;
        }
        final Map<UUID, Long> timestamps = this.timestamps.computeIfAbsent(target.getUniqueId(), k -> new LinkedHashMap());
        if (((targetMineman.hasRank(Rank.YOUTUBER) && !targetMineman.hasRank(Rank.TRAINEE)) || targetMineman.hasRank(Rank.ADMIN)) && !mineman.hasRank(Rank.YOUTUBER)) {
            final Long lastMessageTime = timestamps.get(player.getUniqueId());
            if (lastMessageTime == null || lastMessageTime + 60000L > System.currentTimeMillis()) {
                player.sendMessage(CC.RED + "You can't message this person.");
                return true;
            }
        }
        timestamps.put(player.getUniqueId(), System.currentTimeMillis());
        this.timestamps.put(target.getUniqueId(), timestamps);
        final String message = StringUtil.buildMessage(args, 1);
        final String toMessage = CC.PINK + "(To " + targetMineman.getRank().getColor() + target.getName() + CC.PINK + ") " + message;
        final String fromMessage = CC.PINK + "(From " + mineman.getRank().getColor() + player.getName() + CC.PINK + ") " + message;
        player.sendMessage(toMessage);
        mineman.setLastConversation(args[0]);
        if (targetMineman.isCanSeeMessages() && !targetMineman.isIgnoring(mineman.getId())) {
            target.sendMessage(fromMessage);
            targetMineman.setLastConversation(player.getName());
        }
        return true;
    }
}
