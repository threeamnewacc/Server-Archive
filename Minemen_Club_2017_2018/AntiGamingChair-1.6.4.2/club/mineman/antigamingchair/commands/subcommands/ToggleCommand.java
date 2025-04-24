// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands.subcommands;

import java.util.HashSet;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.command.CommandSender;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.entity.Player;
import java.util.Set;

public class ToggleCommand implements SubCommand
{
    public static final Set<String> DISABLED_CHECKS;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        if (!PlayerUtil.testPermission((CommandSender)player, Rank.DEVELOPER)) {
            return;
        }
        final String check = args[1].toUpperCase();
        if (!ToggleCommand.DISABLED_CHECKS.remove(check)) {
            ToggleCommand.DISABLED_CHECKS.add(check);
            player.sendMessage(CC.L_PURPLE + "Enabled check " + CC.D_PURPLE + check);
        }
        else {
            player.sendMessage(CC.L_PURPLE + "Disabled check " + CC.D_PURPLE + check);
        }
    }
    
    static {
        DISABLED_CHECKS = new HashSet<String>();
    }
}
