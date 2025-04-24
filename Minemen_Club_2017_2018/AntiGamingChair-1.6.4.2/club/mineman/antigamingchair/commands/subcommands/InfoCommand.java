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

public class InfoCommand implements SubCommand
{
    private final AntiGamingChair plugin;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
        }
        else {
            final PlayerData targetData = this.plugin.getPlayerDataManager().getPlayerData(target);
            final StringBuilder builder = new StringBuilder("§8 §8 §1 §3 §3 §7 §8 §r\n");
            builder.append(CC.L_PURPLE).append(target.getName()).append("'s AGC info:\n");
            builder.append(CC.L_PURPLE).append("Client: ").append(CC.D_PURPLE).append(targetData.getClient().getName()).append("\n");
            builder.append(CC.L_PURPLE).append("Last CPS: ").append(CC.D_PURPLE).append(targetData.getCps()).append("\n");
            builder.append(CC.L_PURPLE).append("Ping: ").append(CC.D_PURPLE).append(targetData.getPing()).append("\n");
            builder.append("§8 §8 §1 §3 §3 §7 §8 §r\n");
            player.sendMessage(builder.toString());
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public InfoCommand(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
