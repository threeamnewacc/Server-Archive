package us.zonix.core.server.commands;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class SlowChatCommand extends BaseCommand {

    @Command(name = "slowchat", aliases = {"slow"}, requiresPlayer = true, rank = Rank.TRIAL_MOD)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (Profile.getByUuid(player.getUniqueId()).getRank() == Rank.MEDIA_OWNER) {
            player.sendMessage(ChatColor.RED + "You don't have enough permissions.");
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /slowchat [seconds]");
            return;
        }

        if (!NumberUtils.isNumber(args[0])) {
            player.sendMessage(ChatColor.RED + "Invalid time.");
            return;
        }

        int time = Integer.parseInt(args[0]);

        this.main.getRedisManager().setChatSlowDownTime((long) time * 1000L);

        Bukkit.broadcastMessage(ChatColor.RED + (this.main.getRedisManager().getChatSlowDownTime() > 0 ? "Chat has been slowed by " + player.getName() + " for " + time + "s." : "Slow Chat has been removed by " + player.getName() + "."));
    }

}
