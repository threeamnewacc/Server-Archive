// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.practice.player.PlayerData;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import club.minemen.practice.Practice;
import club.minemen.core.util.cmd.CommandHandler;

public class FlyCommand implements CommandHandler
{
    private final Practice plugin;
    
    @Command(name = { "fly" }, description = "Toggle flight", rank = Rank.CLUBBER)
    public void onFly(final Player player) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't do that in this state.");
            return;
        }
        player.setAllowFlight(!player.getAllowFlight());
        if (player.getAllowFlight()) {
            player.sendMessage(CC.GREEN + "You are now able to fly.");
        }
        else {
            player.sendMessage(CC.RED + "You are no longer able to fly.");
        }
    }
    
    public FlyCommand() {
        this.plugin = Practice.getInstance();
    }
}
