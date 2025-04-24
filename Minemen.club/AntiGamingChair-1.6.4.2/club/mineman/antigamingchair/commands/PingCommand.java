// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands;

import club.mineman.antigamingchair.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.mineman.core.util.finalutil.CC;
import club.mineman.antigamingchair.AntiGamingChair;
import org.bukkit.command.Command;

public class PingCommand extends Command
{
    private final AntiGamingChair plugin;
    
    public PingCommand(final AntiGamingChair plugin) {
        super("ping");
        this.plugin = plugin;
        this.usageMessage = CC.RED + "Usage: /ping <player>";
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player)((args.length < 1 || this.plugin.getServer().getPlayer(args[0]) == null) ? sender : this.plugin.getServer().getPlayer(args[0]));
            final PlayerData data = this.plugin.getPlayerDataManager().getPlayerData(player);
            sender.sendMessage(CC.PINK + player.getName() + " has a ping of " + CC.D_PURPLE + data.getPing() + CC.PINK + " ms.");
        }
        return true;
    }
}
