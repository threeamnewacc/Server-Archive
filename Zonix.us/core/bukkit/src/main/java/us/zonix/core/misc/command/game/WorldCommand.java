package us.zonix.core.misc.command.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class WorldCommand extends BaseCommand {

    @Command(name = "world", aliases = {"changeworld"}, rank = Rank.MANAGER)
    public void onCommand(CommandArgs command) {

        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if(!(sender instanceof Player)) {
            return;
        }

        final Player player = (Player)sender;

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /world <world>");
            return;
        }

        final World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World '" + args[0] + "' not found.");
            return;
        }

        if (player.getWorld().equals(world)) {
            sender.sendMessage(ChatColor.RED + "You are already in that world.");
            return;
        }

        final Location origin = player.getLocation();
        final Location location = new Location(world, origin.getX(), origin.getY(), origin.getZ(), origin.getYaw(), origin.getPitch());

        player.sendMessage(ChatColor.GRAY + "Teleporting...");
        player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);

    }
}
