package us.zonix.hcfactions.crate.command.subcommand;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.util.command.CommandArgs;

public class CrateListCommand extends PluginCommand {
    @Command(name = "ccrate.list", permission = Rank.DEVELOPER, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        sender.sendMessage(ChatColor.GREEN + "Listing all registered crates:");
        for (Crate crate : Crate.getCrates()) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + crate.getName());
        }

    }
}
