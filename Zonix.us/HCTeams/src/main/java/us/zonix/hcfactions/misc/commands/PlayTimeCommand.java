package us.zonix.hcfactions.misc.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.DateUtil;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.command.CommandArgs;

public class PlayTimeCommand extends PluginCommand {

    @Command(name = "playtime", aliases = {"ptime", "nolife"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Player toCheck;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /ptime <player>");
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

        long playtime = System.currentTimeMillis() - (toCheck.getStatistic(Statistic.PLAY_ONE_TICK) * 50);
        String timePlayed = DateUtil.formatDateDiff(playtime);
        sender.sendMessage(ChatColor.BLUE + toCheck.getName() + (toCheck.getName().endsWith("s") ? "'" : "'s") + ChatColor.GRAY + " total playtime is " + ChatColor.WHITE + timePlayed + ChatColor.GRAY + ".");
    }

}
