package us.zonix.core.misc.command.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class TeleportCommand extends BaseCommand {

    @Command(name = "teleport", aliases = {"tp"}, rank = Rank.MODERATOR, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /teleport <player>");
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Teleporting...");
        player.teleport(target);
    }

}
