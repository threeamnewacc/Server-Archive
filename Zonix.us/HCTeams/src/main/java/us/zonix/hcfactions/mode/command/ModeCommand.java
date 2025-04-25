package us.zonix.hcfactions.mode.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.mode.command.subcommand.ModeCreateCommand;
import us.zonix.hcfactions.mode.command.subcommand.ModeDeleteCommand;
import us.zonix.hcfactions.mode.command.subcommand.ModeStartCommand;
import us.zonix.hcfactions.mode.command.subcommand.ModeStopCommand;
import us.zonix.hcfactions.util.PluginCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.mode.command.subcommand.ModeDeleteCommand;
import us.zonix.hcfactions.mode.command.subcommand.ModeStopCommand;
import us.zonix.hcfactions.util.command.CommandArgs;

public class ModeCommand extends PluginCommand {

    public ModeCommand() {
        new ModeCreateCommand();
        new ModeDeleteCommand();
        new ModeStartCommand();
        new ModeStopCommand();
    }

    @Command(name = "mode", permission = Rank.DEVELOPER, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender player = command.getSender();
        player.sendMessage(ChatColor.RED + "/mode create <name>");
        player.sendMessage(ChatColor.RED + "/mode delete <name>");
        player.sendMessage(ChatColor.RED + "/mode start <name>");
        player.sendMessage(ChatColor.RED + "/mode stop <name>");
    }
}
