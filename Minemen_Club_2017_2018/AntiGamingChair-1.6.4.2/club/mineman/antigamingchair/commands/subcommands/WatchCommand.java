// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands.subcommands;

import java.beans.ConstructorProperties;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;

public class WatchCommand implements SubCommand
{
    private final AntiGamingChair plugin;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
        }
        else {
            final PlayerData targetData = this.plugin.getPlayerDataManager().getPlayerData(target);
            targetData.togglePlayerWatching(player);
            player.sendMessage(targetData.isPlayerWatching(player) ? (CC.L_PURPLE + "You are now watching " + CC.D_PURPLE + target.getName()) : (CC.L_PURPLE + "You are no longer watching " + CC.D_PURPLE + target.getName()));
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public WatchCommand(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
