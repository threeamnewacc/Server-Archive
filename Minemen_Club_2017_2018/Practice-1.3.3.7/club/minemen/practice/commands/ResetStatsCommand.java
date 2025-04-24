// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands;

import java.util.Iterator;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.kit.Kit;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class ResetStatsCommand extends Command
{
    private final Practice plugin;
    
    public ResetStatsCommand() {
        super("resetstats");
        this.plugin = Practice.getInstance();
        this.setUsage(CC.RED + "Usage: /resetstats [player]");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (commandSender instanceof Player && !PlayerUtil.testPermission(commandSender, Rank.ADMIN)) {
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMessage(CC.RED + "Usage: /resetstats <player>");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            playerData.setElo(kit.getName(), 1000);
            playerData.setLosses(kit.getName(), 0);
            playerData.setWins(kit.getName(), 0);
        }
        playerData.setPremiumElo(1000);
        playerData.setPremiumLosses(0);
        playerData.setPremiumWins(0);
        commandSender.sendMessage(CC.PRIMARY + "You reset " + CC.SECONDARY + target.getName() + CC.PRIMARY + "'s stats.");
        return true;
    }
}
