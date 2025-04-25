package us.zonix.core.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class SetSpawnCommand extends BaseCommand {

    @Command(name = "setspawn", rank = Rank.OWNER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        this.main.setSpawnLocation(player.getLocation());

        player.sendMessage(ChatColor.GREEN + "Spawn location has been set.");
    }

}
