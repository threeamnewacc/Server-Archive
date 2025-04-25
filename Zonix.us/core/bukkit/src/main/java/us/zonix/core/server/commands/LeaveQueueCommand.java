package us.zonix.core.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.redis.queue.Queue;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class LeaveQueueCommand extends BaseCommand {

    @Command(name = "leavequeue", requiresPlayer = true, aliases = {"queueleave"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!CorePlugin.getInstance().getQueueManager().isServerOnline()) {
            player.sendMessage(ChatColor.RED + "Queue Server is currently under maintenance.");
            return;
        }

        final Queue queue;

        if ((queue = CorePlugin.getInstance().getQueueManager().getQueue(player)) == null) {
            player.sendMessage(ChatColor.RED + "You are not currently in a queue.");
            return;
        }

        player.sendMessage(ChatColor.RED + "You left the queue for " + queue.getServerName() + ".");

        this.main.getServer().getScheduler().runTaskAsynchronously(this.main, () -> queue.removeFromQueue(player));
    }

}
