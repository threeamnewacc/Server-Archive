package us.zonix.core.misc.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.punishment.helpers.StaffAuditHelper;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class StaffAuditCommand extends BaseCommand {

    @Command(name = "staffaudit", aliases = "audit", rank = Rank.ADMINISTRATOR, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player sender = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /audit [player]");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                new StaffAuditHelper(sender, args[0]);
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }
}
