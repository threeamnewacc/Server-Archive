// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.time;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;

public class SunsetCommand extends Command
{
    public SunsetCommand() {
        super("sunset");
        this.setDescription("Set player time to sunset.");
        this.setUsage(CC.RED + "Usage: /sunset");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ((Player)sender).setPlayerTime(12000L, true);
        sender.sendMessage(CC.GREEN + "Time set to sunset.");
        return true;
    }
}
