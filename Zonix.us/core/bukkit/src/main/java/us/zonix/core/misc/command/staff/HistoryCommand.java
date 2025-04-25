package us.zonix.core.misc.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.punishment.helpers.HistoryHelper;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class HistoryCommand extends BaseCommand {

    @Command(name = "history", rank = Rank.MODERATOR, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player sender = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /history [player]");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                new HistoryHelper(sender, args[0]);
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }
}
