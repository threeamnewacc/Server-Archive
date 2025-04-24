// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;

public class EloCommand extends Command
{
    public EloCommand() {
        super("elo");
        this.setDescription("View a player's Elo.");
        this.setUsage(CC.RED + "Usage: /elo [player]");
        this.setAliases((List)Arrays.asList("stats", "lb", "leaderboard", "leaderboards"));
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        sender.sendMessage(CC.PRIMARY + "Visit " + CC.SECONDARY + "http://minemen.club " + CC.PRIMARY + "to view the leaderboards.");
        if (args.length == 0) {
            sender.sendMessage(CC.PRIMARY + "Here are your stats: " + CC.PRIMARY + "http://minemen.club/user/" + sender.getName());
        }
        else {
            sender.sendMessage(CC.SECONDARY + args[0] + CC.PRIMARY + "'s stats: " + CC.SECONDARY + "http://minemen.club/user/" + args[0]);
        }
        return true;
    }
}
