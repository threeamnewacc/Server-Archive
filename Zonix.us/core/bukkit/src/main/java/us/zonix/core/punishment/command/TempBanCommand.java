package us.zonix.core.punishment.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.punishment.helpers.PunishmentHelper;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.Clickable;
import us.zonix.core.util.DateUtil;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class TempBanCommand extends BaseCommand {

    @Command(name = "tempban", rank = Rank.TRIAL_MOD)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 3) {

            Clickable clickable = new Clickable(ChatColor.RED + "Usage: /tempban <target> <time> [reason] [-s]", ChatColor.YELLOW + "Temporarily ban a player from the network.\nAdd a \"-s\" at the end to silently ban a player.", "");

            if(sender instanceof Player) {
                clickable.sendToPlayer((Player) sender);
            }

            return;
        }

        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        Long duration;

        try {
            duration = DateUtil.parseTime(args[1]);
        }
        catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to parse that duration.");
            return;
        }

        if (duration == null) {
            sender.sendMessage(ChatColor.RED + "Failed to parse that duration.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 2; i < (silent ? args.length - 1 : args.length); i++) {
            sb.append(args[i]).append(" ");
        }

        String reason = sb.toString().trim();

        if (reason.equalsIgnoreCase("-s")) {
            sender.sendMessage(ChatColor.RED + "Please provide a valid reason.");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                new PunishmentHelper(sender, args[0], PunishmentType.TEMPBAN, reason, duration, silent, false);
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
