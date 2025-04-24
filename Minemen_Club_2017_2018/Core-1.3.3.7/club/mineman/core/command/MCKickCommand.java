// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class MCKickCommand extends Command
{
    public MCKickCommand() {
        super("mckick");
        this.setDescription("Kick a player via Minecraft's kick system");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if (!PlayerUtil.testPermission(commandSender, Rank.ADMIN)) {
            return true;
        }
        ((Player)commandSender).performCommand("minecraft:kick" + String.join(" ", (CharSequence[])strings));
        return true;
    }
}
