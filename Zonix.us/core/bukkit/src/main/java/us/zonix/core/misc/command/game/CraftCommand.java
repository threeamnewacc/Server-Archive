package us.zonix.core.misc.command.game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class CraftCommand extends BaseCommand {

    @Command(name = "craft", aliases = {"workbench", "crafting"}, rank = Rank.ADMINISTRATOR, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.openWorkbench(null, true);
        player.sendMessage(ChatColor.GOLD + "Opening crafting table...");
    }

}
