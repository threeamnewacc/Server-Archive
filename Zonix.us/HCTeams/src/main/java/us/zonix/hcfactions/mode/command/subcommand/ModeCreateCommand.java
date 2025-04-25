package us.zonix.hcfactions.mode.command.subcommand;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.mode.ModeType;
import us.zonix.hcfactions.util.PluginCommand;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.mode.Mode;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class ModeCreateCommand extends PluginCommand {
    @Command(name = "mode.create", permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /mode create <sotw/eotw>");
            return;
        }

        String name = StringUtils.join(args).toLowerCase();

        if(name.equalsIgnoreCase("sotw") || name.equalsIgnoreCase("eotw")) {
            Mode mode = new Mode(name, false, 0L, ModeType.valueOf(name.toUpperCase()));
            player.sendMessage(ChatColor.RED + "Mode named '" + mode.getName() + "' successfully created.");

        } else {
            player.sendMessage(ChatColor.RED + "Usage: /mode start <sotw/eotw>");
        }
    }
}
