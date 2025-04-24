// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import org.json.simple.JSONObject;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.core.api.impl.IgnoreUpdateRequest;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class IgnoreCommand extends Command
{
    private final CorePlugin plugin;
    
    public IgnoreCommand(final CorePlugin plugin) {
        super("ignore");
        this.plugin = plugin;
        this.setAliases((List)Collections.singletonList("unignore"));
        this.setDescription("Ignore a player.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (args.length < 1) {
            player.sendMessage(CC.RED + "Please use /ignore <player>");
            return true;
        }
        this.plugin.getRequestManager().sendRequest(new IgnoreUpdateRequest(args[0], mineman.getId()), new AbstractCallback("Error updating ignores for " + player.getName()) {
            @Override
            public void callback(final JSONObject data) {
                final String s;
                final String response = s = (String)data.get((Object)"response");
                switch (s) {
                    case "cant-ignore": {
                        player.sendMessage(CC.RED + "You cannot ignore " + args[0]);
                        break;
                    }
                    case "invalid-player": {
                        player.sendMessage(CC.RED + "Player not found.");
                        break;
                    }
                    case "player-never-joined": {
                        player.sendMessage(CC.RED + "Player has never joined.");
                        break;
                    }
                    case "success": {
                        final Object ignoringObject = data.get((Object)"target-id");
                        int ignoring;
                        if (ignoringObject instanceof Long) {
                            ignoring = ((Long)ignoringObject).intValue();
                        }
                        else {
                            if (!(ignoringObject instanceof Integer)) {
                                IgnoreCommand.this.plugin.getServer().getLogger().warning("Error updating ignore state from " + player.getName() + " for " + args[0]);
                                return;
                            }
                            ignoring = (int)ignoringObject;
                        }
                        if (mineman.toggleIgnore(ignoring)) {
                            player.sendMessage(CC.GREEN + "You are now ignoring " + args[0] + ".");
                            break;
                        }
                        player.sendMessage(CC.GREEN + "You are no longer ignoring " + args[0] + ".");
                        break;
                    }
                }
            }
        });
        return true;
    }
}
