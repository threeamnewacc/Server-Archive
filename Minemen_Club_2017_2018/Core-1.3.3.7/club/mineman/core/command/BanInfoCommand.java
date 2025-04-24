// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.core.api.impl.BanInfoRequest;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Arrays;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class BanInfoCommand extends Command
{
    private final CorePlugin plugin;
    
    public BanInfoCommand(final CorePlugin plugin) {
        super("baninfo", "Get info on a player's punishments.", "/bminfo <player>", (List)Arrays.asList("bminfo", "binfo"));
        this.plugin = plugin;
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.RED + this.getUsage());
            return true;
        }
        final String name = args[0];
        this.plugin.getRequestManager().sendRequest(new BanInfoRequest(name), new AbstractCallback("Error fetching ban info for " + name) {
            @Override
            public void callback(final JSONObject data) {
                final String response = (String)data.get((Object)"response");
                if (response.equalsIgnoreCase("success")) {
                    final String name = (String)data.get((Object)"name");
                    final boolean muted = data.get((Object)"muted") == Boolean.TRUE;
                    final boolean banned = data.get((Object)"banned") == Boolean.TRUE;
                    final boolean blacklisted = data.get((Object)"blacklisted") == Boolean.TRUE;
                    final String muteTime = (String)data.get((Object)"mute-time");
                    final String banTime = (String)data.get((Object)"ban-time");
                    final StringBuilder sb = new StringBuilder(CC.PINK + "Punishment Info for " + name + ":\n");
                    sb.append(CC.PINK).append("Muted: ");
                    BanInfoCommand.this.appendBuilder(sb, muted, muteTime);
                    sb.append("\n");
                    sb.append(CC.PINK).append("Banned: ");
                    BanInfoCommand.this.appendBuilder(sb, banned, banTime);
                    sb.append("\n");
                    sb.append(CC.PINK).append("Blacklisted: ").append(CC.D_PURPLE).append(blacklisted ? "Yes" : "No");
                    sender.sendMessage(sb.toString());
                }
                else if (response.equalsIgnoreCase("player-not-found")) {
                    sender.sendMessage(CC.RED + "Player not found.");
                }
            }
        });
        return true;
    }
    
    private void appendBuilder(final StringBuilder sb, final boolean state, final String time) {
        sb.append(CC.D_PURPLE);
        if (time != null && state) {
            sb.append("Yes").append(CC.PINK).append(" until ").append(CC.D_PURPLE).append(time);
        }
        else if (state) {
            sb.append("Forever");
        }
        else {
            sb.append("No");
        }
    }
}
