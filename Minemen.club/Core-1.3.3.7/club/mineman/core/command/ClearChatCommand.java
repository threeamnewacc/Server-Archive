// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class ClearChatCommand extends Command
{
    private final CorePlugin plugin;
    private final String message;
    
    public ClearChatCommand(final CorePlugin plugin) {
        super("clearchat");
        this.plugin = plugin;
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; ++i) {
            builder.append("§8 §8 §1 §3 §3 §7 §8 §r \n");
        }
        this.message = builder.toString();
        this.setAliases((List)Collections.singletonList("cc"));
        this.setDescription("Clear the chat");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] strings) {
        if (!PlayerUtil.testPermission(sender, Rank.TRAINEE)) {
            return true;
        }
        this.plugin.getServer().broadcastMessage(this.message);
        return true;
    }
}
