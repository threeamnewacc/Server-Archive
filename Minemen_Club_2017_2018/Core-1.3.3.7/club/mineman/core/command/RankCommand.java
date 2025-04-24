// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.api.APIMessage;
import club.mineman.core.mineman.Mineman;
import org.bukkit.event.Event;
import club.mineman.core.event.player.RankChangeEvent;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.impl.RankUpdateRequest;
import org.bukkit.entity.Player;
import club.mineman.core.util.finalutil.TimeUtil;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class RankCommand extends Command
{
    private final CorePlugin plugin;
    
    public RankCommand(final CorePlugin plugin) {
        super("rank");
        this.plugin = plugin;
        this.setDescription("Set the rank of a player");
    }
    
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(CC.RED + "Please use /rank <player> <rank> <duration>");
            return true;
        }
        final Rank rank = Rank.getByName(args[1]);
        if (rank == null) {
            sender.sendMessage(CC.RED + "This rank does not exist.");
            return true;
        }
        final long duration = TimeUtil.toMillis(args[2]);
        final int giverId = (sender instanceof Player) ? this.plugin.getPlayerManager().getPlayer(((Player)sender).getUniqueId()).getId() : -1;
        this.plugin.getRequestManager().sendRequest(new RankUpdateRequest(rank, args[0], duration, giverId), new RequestCallback() {
            @Override
            public void callback(final JSONObject data) {
                final String s;
                final String response = s = (String)data.get((Object)"response");
                switch (s) {
                    case "success": {
                        CorePlugin.getInstance().getServer().getLogger().info(sender.getName() + " updated " + args[0] + " permissive rank to " + rank.getName());
                        sender.sendMessage(CC.GREEN + "You have given " + args[0] + " the " + rank.getName() + " rank.");
                        final Player player = RankCommand.this.plugin.getServer().getPlayer(args[0]);
                        if (player != null) {
                            final Mineman targetMineman = RankCommand.this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
                            if (targetMineman != null) {
                                RankCommand.this.plugin.getServer().getPluginManager().callEvent((Event)new RankChangeEvent(targetMineman, targetMineman.getRank(), rank));
                                targetMineman.setRank(rank);
                            }
                            break;
                        }
                        break;
                    }
                    case "invalid-player": {
                        sender.sendMessage(CC.RED + "Player not found.");
                        break;
                    }
                    case "player-never-joined": {
                        sender.sendMessage(CC.RED + "Player has never joined.");
                        break;
                    }
                    default: {
                        RankCommand.this.plugin.getServer().getLogger().warning("Unknown error occurred when updating " + args[0] + " permissive rank to " + rank.getName() + ". " + data.toJSONString());
                        break;
                    }
                }
            }
            
            @Override
            public void error(final String message) {
                CorePlugin.getInstance().getServer().getLogger().warning("Error updating Mineman rank for " + args[0] + " to " + rank.getName() + ". " + message);
            }
        });
        return true;
    }
}
