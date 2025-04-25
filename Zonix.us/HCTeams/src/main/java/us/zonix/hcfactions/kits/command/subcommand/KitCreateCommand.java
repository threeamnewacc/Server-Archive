package us.zonix.hcfactions.kits.command.subcommand;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.kits.Kit;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.command.CommandArgs;

public class KitCreateCommand extends PluginCommand {
    @Command(name = "kit.create", permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /kit create <name>");
            return;
        }

        String name = StringUtils.join(args);
        Kit kit = Kit.getByName(name);

        if (kit != null) {
            player.sendMessage(ChatColor.RED + "A kit named '" + kit.getName() + "' already exists.");
            return;
        }

        kit = new Kit(name);
        kit.save();

        player.sendMessage(ChatColor.RED + "Kit named '" + kit.getName() + "' successfully created.");
    }
}
