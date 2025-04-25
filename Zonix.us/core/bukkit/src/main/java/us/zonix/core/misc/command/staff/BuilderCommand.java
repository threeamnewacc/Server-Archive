package us.zonix.core.misc.command.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.BungeeUtil;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class BuilderCommand extends BaseCommand {

    @Command(name = "buildserver", rank = Rank.BUILDER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        BungeeUtil.sendToServer(player, "build");
    }
}
