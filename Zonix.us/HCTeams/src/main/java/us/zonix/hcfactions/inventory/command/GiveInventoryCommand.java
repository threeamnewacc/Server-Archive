package us.zonix.hcfactions.inventory.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class GiveInventoryCommand extends PluginCommand {

    @Command(name = "giveinv", aliases = {"sendinv", "cpto"}, permission = Rank.ADMINISTRATOR)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Player toSend;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/" + command.getLabel() + " <player>");
            return;
        } else {
            toSend = Bukkit.getPlayer(StringUtils.join(args));
            if (toSend == null) {
                player.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join(args) + "' found.");
                return;
            }
        }

        toSend.getInventory().setContents(player.getInventory().getContents());
        toSend.getInventory().setArmorContents(player.getInventory().getArmorContents());
        player.sendMessage(ChatColor.RED + "Inventory successfully sent.");
    }
}
