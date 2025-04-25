package us.zonix.core.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.redis.queue.Queue;
import us.zonix.core.server.ServerData;
import us.zonix.core.server.ServerState;
import us.zonix.core.server.ServerType;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class JoinQueueCommand extends BaseCommand {

    @Command(name = "joinqueue", requiresPlayer = true, aliases = {"queuejoin"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /joinqueue (server)");
            return;
        }

        if (!CorePlugin.getInstance().getQueueManager().isServerOnline()) {
            player.sendMessage(ChatColor.RED + "Queue Server is currently under maintenance.");
            return;
        }

        final String name = args[0];
        final Queue queue;

        if ((queue = CorePlugin.getInstance().getQueueManager().getQueue(name.toLowerCase())) == null) {
            player.sendMessage(ChatColor.RED + "The specified queue doesn't exist.");
            return;
        }

        ServerData serverData = CorePlugin.getInstance().getRedisManager().getServerDataByName(name.toLowerCase());

        if (serverData != null && serverData.getServerType() == ServerType.SURVIVAL_GAMES && serverData.getServerState() == ServerState.SETUP) {
            player.sendMessage(ChatColor.RED + "That server is currently offline.");
            return;
        }

        if (CorePlugin.getInstance().getQueueManager().getQueue(player) != null) {
            player.sendMessage(ChatColor.RED + "You are already queueing for another server.");
            return;
        }

        if (name.equalsIgnoreCase(CorePlugin.getInstance().getServerId())) {
            player.sendMessage(ChatColor.RED + "You are currently in that server.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "You joined the queue for " + queue.getServerName() + ".");

        this.main.getServer().getScheduler().runTaskAsynchronously(this.main, () -> queue.addToQueue(player, Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.SILVER)));
    }

}
