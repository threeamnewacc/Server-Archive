package us.zonix.core.misc.command.game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class FeedCommand extends BaseCommand {

    @Command(name = "feed", rank = Rank.ADMINISTRATOR, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.sendMessage(ChatColor.GOLD + "You now have full hunger.");
        player.setFoodLevel(20);
    }

}
