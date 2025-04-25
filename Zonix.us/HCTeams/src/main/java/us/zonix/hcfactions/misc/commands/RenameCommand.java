package us.zonix.hcfactions.misc.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class RenameCommand extends PluginCommand {

    @Command(name = "rename", permission = Rank.DEVELOPER)
    public void onCommand(final CommandArgs command) {
        final Player player = (Player)command.getSender();
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You have nothing in your hand...");
            return;
        }
        if (command.getArgs().length < 1) {
            player.sendMessage(ChatColor.RED + "You did not supply a name.");
            return;
        }

        String name = ChatColor.translateAlternateColorCodes('&', StringUtils.join(command.getArgs(), ' ', 0, command.getArgs().length));

        final ItemStack itemStack = player.getItemInHand().clone();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);

        player.getInventory().setItemInHand(itemStack);
        player.updateInventory();
        player.sendMessage(ChatColor.GOLD + "Renamed item to " + name);
    }
}
