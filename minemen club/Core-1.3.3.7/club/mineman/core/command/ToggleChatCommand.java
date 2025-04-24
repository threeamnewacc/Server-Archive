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

public class ToggleChatCommand extends Command
{
    private final CorePlugin plugin;
    
    public ToggleChatCommand(final CorePlugin plugin) {
        super("togglechat");
        this.plugin = plugin;
        this.setDescription("Toggle chat to all messages except staff messages.");
        this.setAliases((List)Collections.singletonList("tgc"));
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        mineman.setChatEnabled(!mineman.isChatEnabled());
        player.sendMessage(CC.GREEN + "You can " + (mineman.isChatEnabled() ? "now" : "no longer") + " see public chat.");
        return true;
    }
}
