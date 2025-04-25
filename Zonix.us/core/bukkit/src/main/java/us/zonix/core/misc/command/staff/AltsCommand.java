package us.zonix.core.misc.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.punishment.helpers.AltsHelper;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class AltsCommand extends BaseCommand {

    @Command(name = "alts", rank = Rank.MODERATOR)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /alts [player]");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                new AltsHelper(sender, args[0]);
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }
}
