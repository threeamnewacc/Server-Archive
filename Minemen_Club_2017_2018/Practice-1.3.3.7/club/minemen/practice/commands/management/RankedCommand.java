// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.management;

import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class RankedCommand extends Command
{
    private final Practice plugin;
    
    public RankedCommand() {
        super("ranked");
        this.plugin = Practice.getInstance();
        this.setDescription("Manage server ranked mode.");
        this.setUsage(CC.RED + "Usage: /ranked");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        final boolean enabled = this.plugin.getQueueManager().isRankedEnabled();
        this.plugin.getQueueManager().setRankedEnabled(!enabled);
        sender.sendMessage(CC.GREEN + "Ranked matches are now " + (enabled ? (CC.RED + "disabled") : (CC.GREEN + "enabled")) + CC.GREEN + ".");
        return true;
    }
}
