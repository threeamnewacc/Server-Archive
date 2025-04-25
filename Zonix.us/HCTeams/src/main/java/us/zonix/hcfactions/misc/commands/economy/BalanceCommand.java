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
import us.zonix.hcfactions.util.command.CommandArgs;

public class BalanceCommand extends PluginCommand {

    @Command(name = "balance", aliases = {"bal", "eco", "money", "economy", "$"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Player toCheck;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /balance <player>");
                return;
            }
            toCheck = (Player) sender;
        } else {
            toCheck = Bukkit.getPlayer(StringUtils.join(args));
        }

        if (toCheck == null) {
            sender.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join(args) + "' found online.");
            return;
        }

        Profile profile = Profile.getByPlayer(toCheck);

        if(profile == null) {
            sender.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join(args) + "' found online.");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + toCheck.getName() + "'s Balance: " + ChatColor.WHITE + "$" + profile.getBalance());
    }

}
