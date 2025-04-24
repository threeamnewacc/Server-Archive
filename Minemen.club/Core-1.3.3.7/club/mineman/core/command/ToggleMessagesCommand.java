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

public class ToggleMessagesCommand extends Command
{
    private final CorePlugin plugin;
    
    public ToggleMessagesCommand(final CorePlugin plugin) {
        super("togglemessages");
        this.plugin = plugin;
        this.setDescription("Toggle private messages.");
        this.setAliases((List)Collections.singletonList("tpm"));
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        mineman.setCanSeeMessages(!mineman.isCanSeeMessages());
        player.sendMessage(CC.GREEN + "You can " + (mineman.isCanSeeMessages() ? "now" : "no longer") + " see private messages.");
        return true;
    }
}
