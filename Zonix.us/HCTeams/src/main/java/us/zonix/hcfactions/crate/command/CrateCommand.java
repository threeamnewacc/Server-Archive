package us.zonix.hcfactions.crate.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.crate.command.subcommand.*;
import us.zonix.hcfactions.crate.command.subcommand.*;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class CrateCommand extends PluginCommand {

    public CrateCommand() {
        new CrateCreateCommand();
        new CrateDeleteCommand();
        new CrateItemsCommand();
        new CrateKeyCommand();
        new CrateListCommand();
    }

    @Command(name = "ccrate", permission = Rank.DEVELOPER, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender player = command.getSender();
        player.sendMessage(ChatColor.RED + "/ccreate list");
        player.sendMessage(ChatColor.RED + "/ccrate create <name>");
        player.sendMessage(ChatColor.RED + "/ccrate delete <name>");
        player.sendMessage(ChatColor.RED + "/ccrate items <name>");
        player.sendMessage(ChatColor.RED + "/ccrate key <name> <amount> <player>");
    }
}
