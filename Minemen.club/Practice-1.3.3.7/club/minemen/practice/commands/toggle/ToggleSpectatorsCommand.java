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

public class ToggleSpectatorsCommand extends Command
{
    private final Practice plugin;
    
    public ToggleSpectatorsCommand() {
        super("tsp");
        this.plugin = Practice.getInstance();
        this.setDescription("Toggles a player's ability to spectate you on or off.");
        this.setUsage(CC.RED + "Usage: /tsp");
        this.setAliases((List)Arrays.asList("togglesp", "togglespec", "togglespectator", "togglespectators"));
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setAllowingSpectators(!playerData.isAllowingSpectators());
        player.sendMessage(playerData.isAllowingSpectators() ? (CC.GREEN + "You are now allowing spectators.") : (CC.RED + "You are no longer allowing spectators."));
        return true;
    }
}
