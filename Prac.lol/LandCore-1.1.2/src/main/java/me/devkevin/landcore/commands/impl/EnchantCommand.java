package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.LandCoreAPI;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.TaskUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 13/02/2023 @ 19:55
 * EnchantCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class EnchantCommand extends BaseCommand {
    private final LandCore plugin;

    public EnchantCommand(LandCore plugin) {
        super("enchant", Rank.DEVELOPER);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        TaskUtil.runAsync(() -> {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(CC.translate("&cCorrect usage: /enchant <enchantment> <level>"));
                return;
            }

            ItemStack item = player.getItemInHand();

            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(LandCoreAPI.Language.ENCHANT_MUST_HOLD_ITEM.toString());
                return;
            }

            if (!LandCoreAPI.isInteger(args[1])) {
                player.sendMessage(LandCoreAPI.Language.USE_NUMBERS.toString());
                return;
            }

            int level = Integer.parseInt(args[1]);

            if (level < 0) {
                player.sendMessage(LandCoreAPI.Language.ENCHANT_ENCHANT_MUST_BE_POSITIVE.toString());
                return;
            }

            if (level > LandCoreAPI.getMaxEnchantLevel(player)) {
                player.sendMessage(LandCoreAPI.Language.ENCHANT_MAXIMUM_LEVEL_EXCEEDED.toString()
                        .replace("<level>", String.valueOf(LandCoreAPI.getMaxEnchantLevel(player))));
                return;
            }

            Enchantment enchantment = LandCoreAPI.getEnchantmentByName(args[0]);

            if (enchantment == null) {
                player.sendMessage(LandCoreAPI.Language.ENCHANT_WRONG_ENCHANTMENT.toString());
                return;
            }

            if (level == 0) {
                if (!item.containsEnchantment(enchantment)) {
                    player.sendMessage(LandCoreAPI.Language.ENCHANT_ITEM_DOESNT_HAVE_ENCHANT.toString());
                    return;
                }

                item.removeEnchantment(enchantment);
                player.sendMessage(LandCoreAPI.Language.ENCHANT_REMOVED.toString()
                        .replace("<enchant>", enchantment.getName().toUpperCase().replace("_", " ") + " " + level));
                return;
            }

            item.addUnsafeEnchantment(enchantment, level);
            player.sendMessage(LandCoreAPI.Language.ENCHANT_ADDED.toString()
                    .replace("<enchant>", enchantment.getName().toUpperCase().replace("_", " ")));
        });
    }
}
