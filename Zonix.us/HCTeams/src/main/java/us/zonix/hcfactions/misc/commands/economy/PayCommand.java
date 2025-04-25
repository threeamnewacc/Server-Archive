package us.zonix.hcfactions.misc.commands.economy;

import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class PayCommand extends PluginCommand {

    @Command(name = "pay", aliases = {"paymoney", "sendmoney"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if(!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
        } else if(args.length == 2) {

            Player toCheck = Bukkit.getPlayer(args[0]);

            if(!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
                return;
            }

            int amount = Integer.parseInt(args[1]);

            if(amount < 5) {
                sender.sendMessage(ChatColor.RED + "You must send at least $5.");
                return;
            }

            if (toCheck == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }

            Profile fromProfile = Profile.getByPlayer(player);
            Profile toProfile = Profile.getByPlayer(toCheck);

            if(toProfile == null || fromProfile == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }

            if (fromProfile.getBalance() < amount) {
                sender.sendMessage(ChatColor.RED + "You don't have sufficient funds. ($" + amount + ")");
                return;
            }

            fromProfile.setBalance( (fromProfile.getBalance() - amount) );
            toProfile.setBalance( (toProfile.getBalance() + amount) );
            player.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.GOLD + "$" + amount + ChatColor.YELLOW + " to " + toCheck.getName());
            toCheck.sendMessage(ChatColor.YELLOW + "You received " + ChatColor.GOLD + "$" + amount + ChatColor.YELLOW + " from " + player.getName());
        }

    }

}
