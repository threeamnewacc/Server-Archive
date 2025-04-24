// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.core.api.impl.AltsRequest;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.ArrayList;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class AltsCommand extends Command
{
    private final CorePlugin plugin;
    
    public AltsCommand(final CorePlugin plugin) {
        super("alts", "Get a list of a player's alts.", "/alts <player>", (List)new ArrayList());
        this.plugin = plugin;
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.RED + this.getUsage());
            return true;
        }
        final String name = args[0];
        this.plugin.getRequestManager().sendRequest(new AltsRequest(name), new AbstractCallback("Error fetching alts for " + name + ".") {
            @Override
            public void callback(final JSONObject data) {
            }
        });
        return true;
    }
}
