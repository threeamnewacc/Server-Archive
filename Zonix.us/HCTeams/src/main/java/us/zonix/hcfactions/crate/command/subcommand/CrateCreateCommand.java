package us.zonix.hcfactions.crate.command.subcommand;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.command.CommandArgs;

public class CrateCreateCommand extends PluginCommand {
    @Command(name = "ccrate.create", permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /crate crate <name>");
            return;
        }

        String name = StringUtils.join(args);
        Crate crate = Crate.getByName(name);

        if (crate != null) {
            player.sendMessage(ChatColor.RED + "A crate named '" + crate.getName() + "' already exists.");
            return;
        }

        crate = new Crate(name);
        player.sendMessage(ChatColor.RED + "Crate named '" + crate.getName() + "' successfully created.");
    }
}
