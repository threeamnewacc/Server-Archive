// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.time;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.command.Command;

public class DayCommand extends Command
{
    public DayCommand() {
        super("day");
        this.setDescription("Set player time to day.");
        this.setUsage(CC.RED + "Usage: /day");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ((Player)sender).setPlayerTime(6000L, true);
        sender.sendMessage(CC.GREEN + "Time set to day.");
        return true;
    }
}
