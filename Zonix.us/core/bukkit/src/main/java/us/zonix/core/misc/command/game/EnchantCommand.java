package us.zonix.core.misc.command.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.ItemBuilder;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class EnchantCommand extends BaseCommand {

    @Command(name = "enchant", rank = Rank.MANAGER, requiresPlayer = true)
    public void onCommand(final CommandArgs command) {
        Player player = command.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You have nothing in your hand...");
            return;
        }

        if (command.getArgs().length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /enchant <enchant> <level>");
            return;
        }

        Enchantment enchantment;

        try {
            enchantment = Enchantment.getByName(command.getArgs()[0].toUpperCase());
        }
        catch (Exception ex) {
            enchantment = null;
        }

        if (enchantment == null) {
            player.sendMessage(ChatColor.RED + "You did not supply a valid enchantment.");
            return;
        }

        ItemBuilder builder = new ItemBuilder(player.getItemInHand().clone()).enchantment(enchantment, Integer.parseInt(command.getArgs()[1]));
        player.getInventory().setItemInHand(builder.build());
        player.updateInventory();
        player.sendMessage(ChatColor.GOLD + "You have applied the enchantment.");
    }

}
