package us.zonix.hcfactions.misc.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;

public class ReclaimRemoveCommand extends PluginCommand {

    @Command(name = "rcremove", inGameOnly = false, permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {

        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /rcremove <player>");
        } else if(args.length == 1) {

            Player toCheck = Bukkit.getPlayer(args[0]);

            if(!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(ChatColor.RED + "Usage: /rcremove <player>");
                return;
            }


            if (toCheck == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }

            Profile toProfile = Profile.getByPlayer(toCheck);

            if(toProfile == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }


            toProfile.setReclaim(false);
            sender.sendMessage(ChatColor.YELLOW + "You have set the reclaim value to false for " + toCheck.getName());

            new BukkitRunnable() {

                @Override
                public void run() {
                    toProfile.save();
                }
            }.runTaskAsynchronously(this.main);
        }

    }

}
