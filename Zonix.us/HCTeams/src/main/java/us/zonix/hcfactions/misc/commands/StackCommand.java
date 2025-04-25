package us.zonix.hcfactions.misc.commands;

import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class StackCommand extends PluginCommand {

    @Command(name = "stack", aliases = {"more"}, inGameOnly = true, permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.getInventory().getItemInHand().setAmount(64);
    }


}
