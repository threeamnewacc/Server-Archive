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
import java.util.Arrays;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class AdminChatCommand extends Command
{
    private final CorePlugin plugin;
    
    public AdminChatCommand(final CorePlugin plugin) {
        super("adminchat");
        this.plugin = plugin;
        this.setAliases((List)Arrays.asList("ac", "ad"));
        this.setDescription("Enter admin chat.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (args.length == 0) {
            mineman.setInAdminChat(!mineman.isInAdminChat());
            player.sendMessage(CC.GREEN + "You have " + (mineman.isInAdminChat() ? "entered" : "left") + " admin chat.");
        }
        else {
            final String message = String.join(" ", (CharSequence[])args);
            PlayerUtil.messageStaff(CC.DARK_RED + "[Admin] " + mineman.getRank().getColor() + player.getName() + CC.R + ": " + message, Rank.ADMIN);
        }
        return true;
    }
}
