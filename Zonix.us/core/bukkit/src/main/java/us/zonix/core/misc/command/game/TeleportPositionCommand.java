package us.zonix.core.misc.command.game;

import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class TeleportPositionCommand extends BaseCommand {

    @Command(name = "teleportposition", aliases = {"tppos"}, rank = Rank.MANAGER)
    public void onCommand(CommandArgs command) {

        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if(!(sender instanceof Player)) {
            return;
        }

        final Player player = (Player)sender;

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /tppos <x> <y> <z>");
            return;
        }

        final Integer x = Ints.tryParse(args[0]);
        Integer y = Ints.tryParse(args[1]);
        final Integer z = Ints.tryParse(args[2]);

        if (x == null || z == null || y == null) {
            sender.sendMessage(ChatColor.RED + "Location not found.");
            return;
        }

        player.sendMessage(ChatColor.GRAY + "Teleporting...");
        player.teleport(new Location(player.getWorld(), (double) x, (double) y, (double) z));


    }
}
