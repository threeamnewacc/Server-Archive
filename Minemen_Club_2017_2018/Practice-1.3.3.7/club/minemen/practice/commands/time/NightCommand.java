// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.time;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;

public class NightCommand extends Command
{
    public NightCommand() {
        super("night");
        this.setDescription("Set player time to night.");
        this.setUsage(CC.RED + "Usage: /night");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ((Player)sender).setPlayerTime(18000L, true);
        sender.sendMessage(CC.GREEN + "Time set to night.");
        return true;
    }
}
