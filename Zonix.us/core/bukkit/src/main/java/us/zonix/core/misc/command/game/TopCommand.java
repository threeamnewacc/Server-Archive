package us.zonix.core.misc.command.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.LocationUtil;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class TopCommand extends BaseCommand implements Listener {

    @Command(name = "top", rank = Rank.DEVELOPER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Location location = LocationUtil.getHighestLocation(player.getLocation());

        if (location != null) {
            player.sendMessage(ChatColor.GRAY + "Teleporting...");
            player.teleport(location);
        }
    }

}
