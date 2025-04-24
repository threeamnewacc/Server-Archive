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

public class ToggleScoreboardCommand extends Command
{
    private final Practice plugin;
    
    public ToggleScoreboardCommand() {
        super("tsb");
        this.plugin = Practice.getInstance();
        this.setDescription("Toggles a player's ability to see the sidebar.");
        this.setUsage(CC.RED + "Usage: /tsb");
        this.setAliases((List)Arrays.asList("togglescore", "togglescoreboard", "toggleside", "togglesidebar"));
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] strings) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setScoreboardEnabled(!playerData.isScoreboardEnabled());
        player.sendMessage(playerData.isScoreboardEnabled() ? (CC.GREEN + "You can now see the sidebar.") : (CC.RED + "You can no longer see the sidebar."));
        return true;
    }
}
