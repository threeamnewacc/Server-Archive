// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.mineman.Mineman;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class ReplyCommand extends Command
{
    private final CorePlugin plugin;
    
    public ReplyCommand(final CorePlugin plugin) {
        super("reply");
        this.plugin = plugin;
        this.setAliases((List)Collections.singletonList("r"));
        this.setDescription("Reply to a player's message.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        final String target = mineman.getLastConversation();
        if (target != null) {
            if (args.length == 0) {
                player.sendMessage(CC.GRAY + "You are currently messaging " + target + ".");
                return true;
            }
            final String message = StringUtil.buildMessage(args, 0);
            this.plugin.getServer().dispatchCommand((CommandSender)player, "msg " + target + " " + message);
        }
        else {
            player.sendMessage(CC.RED + "You are not messaging anyone currently.");
        }
        return true;
    }
}
