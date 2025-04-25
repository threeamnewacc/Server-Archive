package us.zonix.hcfactions.misc.commands.economy;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class SetBalanceCommand extends PluginCommand {

    @Command(name = "setbalance", aliases = {"setbal"}, permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if(!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /setbalance <player> <amount>");
        } else if(args.length == 2) {

            Player toCheck = Bukkit.getPlayer(args[0]);

            if(!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(ChatColor.RED + "Usage: /setbalance <player> <amount>");
                return;
            }

            int amount = Integer.parseInt(args[1]);

            if (toCheck == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }

            Profile toProfile = Profile.getByPlayer(toCheck);

            if(toProfile == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }


            toProfile.setBalance( (amount) );
            player.sendMessage(ChatColor.YELLOW + "You have set the balance " + ChatColor.GOLD + "$" + amount + ChatColor.YELLOW + " to " + toCheck.getName());
            toCheck.sendMessage(ChatColor.YELLOW + "Your balance was set to " + ChatColor.GOLD + "$" + amount);
        }

    }

}
