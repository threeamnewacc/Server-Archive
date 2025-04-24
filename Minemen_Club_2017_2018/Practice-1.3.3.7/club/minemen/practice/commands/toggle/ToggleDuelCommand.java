// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.toggle;

import club.minemen.practice.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class ToggleDuelCommand extends Command
{
    private final Practice plugin;
    
    public ToggleDuelCommand() {
        super("tdr");
        this.plugin = Practice.getInstance();
        this.setDescription("Toggles a player's duel requests on or off.");
        this.setUsage(CC.RED + "Usage: /tdr");
        this.setAliases((List)Arrays.asList("toggleduel", "toggleduels", "td"));
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setAcceptingDuels(!playerData.isAcceptingDuels());
        player.sendMessage(playerData.isAcceptingDuels() ? (CC.GREEN + "You are now accepting duel requests.") : (CC.RED + "You are no longer accepting duel requests."));
        return true;
    }
}
