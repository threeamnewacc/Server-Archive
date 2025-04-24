// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.manager.MinemanManager;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class SilenceChatCommand extends Command
{
    private final CorePlugin plugin;
    
    public SilenceChatCommand(final CorePlugin plugin) {
        super("silencechat");
        this.plugin = plugin;
        this.setAliases((List)Collections.singletonList("mutechat"));
        this.setDescription("Silence the chat.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] strings) {
        if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        final MinemanManager minemanManager = this.plugin.getPlayerManager();
        minemanManager.setChatSilenced(!minemanManager.isChatSilenced());
        this.plugin.getServer().broadcastMessage(minemanManager.isChatSilenced() ? (CC.B_RED + "The chat has been silenced by " + sender.getName() + ".") : (CC.B_GREEN + "The chat is no longer silenced."));
        return true;
    }
}
