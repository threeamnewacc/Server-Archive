package us.zonix.core.punishment.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class KickCommand extends BaseCommand {

    @Command(name = "kick", rank = Rank.TRIAL_MOD)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Please specify a player to kick.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player could not be found.");
            return;
        }

        String reason = null;
        boolean silent = false;

        if (args.length > 1) {
            silent = args[args.length - 1].equalsIgnoreCase("-s");

            if (!silent || (args.length > 2)) {
                StringBuilder sb = new StringBuilder();

                for (int i = 1; i < (silent ? args.length - 1 : args.length); i++) {
                    sb.append(args[i]).append(" ");
                }

                reason = sb.toString().trim();
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (silent && profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                player.sendMessage(ChatColor.GREEN + target.getName() + " has been kicked by " + sender.getName() + " for " + (reason == null ? "Kicked" : reason) + ".");
            }
            else {
                player.sendMessage(ChatColor.GREEN + target.getName() + " has been kicked by " + sender.getName() + ".");
            }
        }

        target.kickPlayer(ChatColor.RED + "You have been kicked by " + sender.getName() + ".\n\n" + ChatColor.RED + "Reason: " + (reason == null ? "Kicked" : reason));
    }

}
