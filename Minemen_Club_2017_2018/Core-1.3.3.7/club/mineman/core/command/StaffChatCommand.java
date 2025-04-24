// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.mineman.Mineman;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class StaffChatCommand extends Command
{
    private final CorePlugin plugin;
    
    public StaffChatCommand(final CorePlugin plugin) {
        super("staffchat");
        this.plugin = plugin;
        this.setAliases((List)Collections.singletonList("sc"));
        this.setDescription("Enter staff chat.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        if (!PlayerUtil.testPermission(sender, Rank.TRAINEE)) {
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (args.length == 0) {
            mineman.setInStaffChat(!mineman.isInStaffChat());
            player.sendMessage(CC.GREEN + "You have " + (mineman.isInStaffChat() ? "entered" : "left") + " staff chat.");
        }
        else {
            final String message = String.join(" ", (CharSequence[])args);
            PlayerUtil.messageStaff(CC.AQUA + "[Staff] " + mineman.getRank().getColor() + player.getName() + CC.R + ": " + message);
        }
        return true;
    }
}
