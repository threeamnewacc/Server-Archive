package us.zonix.hcfactions.crate.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class CrateKeyCommand extends PluginCommand {
    @Command(name = "ccrate.key", aliases = {"key"}, permission = Rank.DEVELOPER, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /crate key <name> <amount> <player>");
            return;
        }

        Crate crate = Crate.getByName(args[0]);
        if (crate == null) {
            sender.sendMessage(ChatColor.RED + "A crate named '" + args[0] + "' does not exist.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "Invalid amount");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Invalid amount");
            return;
        }

        Player toGive;
        if (args.length == 2) {
            if (sender instanceof Player) {
                toGive = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /crate key <name> <amount> <player>");
                return;
            }
        } else {
            toGive = Bukkit.getPlayer(args[2]);
        }

        if (toGive == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player.");
            return;
        }


        sender.sendMessage(ChatColor.GOLD + "You have successfully given " + ChatColor.YELLOW + amount + ChatColor.GOLD + " crate key" + (amount == 1 ? "" : "s") + " to " + ChatColor.YELLOW + toGive.getName() + ChatColor.GOLD + ".");
        toGive.getInventory().addItem(crate.getKey(amount));
    }
}
