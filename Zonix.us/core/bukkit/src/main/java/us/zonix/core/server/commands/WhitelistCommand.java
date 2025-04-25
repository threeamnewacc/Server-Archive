package us.zonix.core.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class WhitelistCommand extends BaseCommand {

    @Command(name = "globalwhitelist", aliases = "gwl", rank = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /glw <add/remove/off> [player/server]");
            return;
        }

        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
            this.writeWhitelist(sender, args[0], args[1]);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /glw <add/remove/on/off> [player/server]");
        }
    }

    private void writeWhitelist(CommandSender sender, String action, String target) {
        new BukkitRunnable() {
            public void run() {
                main.getRedisManager().writeWhitelist(action, target);
                sender.sendMessage(ChatColor.GREEN + "Successfully modified the whitelist information.");
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
