package us.zonix.core.server.commands;

import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class SurvivalGamesMenuCommand extends BaseCommand {

    @Command(name = "sgmenu", requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.openInventory(CorePlugin.getInstance().getServerManager().getSgSelector().getCurrentPage());
    }

}
