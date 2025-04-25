package us.zonix.hcfactions.kits.command.subcommand;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.kits.Kit;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class KitListCommand extends PluginCommand {
    @Command(name = "kit.list", permission = Rank.DEVELOPER, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        sender.sendMessage(ChatColor.GREEN + "Listing all registered kits:");
        for (Kit kit : Kit.getKits()) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + kit.getName());
        }

    }
}
