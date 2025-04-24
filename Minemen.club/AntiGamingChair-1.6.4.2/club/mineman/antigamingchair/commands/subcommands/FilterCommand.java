// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands.subcommands;

import java.beans.ConstructorProperties;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.core.util.finalutil.StringUtil;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;

public class FilterCommand implements SubCommand
{
    private final AntiGamingChair plugin;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (args.length < 2) {
            final StringBuilder builder = new StringBuilder("§8 §8 §1 §3 §3 §7 §8 §r\n");
            builder.append(CC.L_PURPLE).append("Filtered phrases:\n");
            builder.append(playerData.getFilteredPhrases().toString().replace("[", CC.D_PURPLE).replace("]", "").replaceAll(", ", "\n"));
            builder.append("\n§8 §8 §1 §3 §3 §7 §8 §r\n");
            player.sendMessage(builder.toString());
            return;
        }
        final String phrase = StringUtil.buildMessage(args, 1);
        playerData.toggleKeywordFilter(phrase.toLowerCase());
        player.sendMessage(CC.L_PURPLE + "The phrase " + CC.D_PURPLE + phrase + CC.L_PURPLE + " is " + (playerData.isKeywordFiltered(phrase.toLowerCase()) ? "now filtered" : "no longer filtered") + " in your alerts!");
    }
    
    @ConstructorProperties({ "plugin" })
    public FilterCommand(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
