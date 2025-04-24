package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.LandCoreAPI;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/03/2023 @ 1:43
 * P_CoinsCommand / me.devkevin.landcore.commands.impl / LandCore
 */
public class PCoinsCommand extends BaseCommand {
    private final LandCore plugin;

    public PCoinsCommand(LandCore plugin) {
        super("pcoin", Rank.MANAGER);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return;
                }

                CoreProfile targetAccount = plugin.getProfileManager().getProfile(target.getUniqueId());

                if (!LandCoreAPI.isInteger(args[2]) || Integer.parseInt(args[2]) < 0) {
                    sender.sendMessage(CC.RED + "Please use numbers.");
                    return;
                }

                targetAccount.setP_coin(Integer.parseInt(args[2]));

                sender.sendMessage("&eYou have successfully set &6" + targetAccount.getP_coin() + "'s &ePCoins to &6" + target.getName() + "&e.\"");
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return;
                }

                CoreProfile targetAccount = plugin.getProfileManager().getProfile(target.getUniqueId());

                if (!LandCoreAPI.isInteger(args[2]) || Integer.parseInt(args[2]) <= 0) {
                    sender.sendMessage(CC.RED + "Please use numbers.");
                    return;
                }

                targetAccount.setP_coin(targetAccount.getP_coin() + Integer.parseInt(args[2]));

                sender.sendMessage("&eYou have successfully added &6" + targetAccount.getP_coin() + "'s &ePCoins to &6" + target.getName() + "&e.\"");
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return;
                }

                CoreProfile targetAccount = plugin.getProfileManager().getProfile(target.getUniqueId());

                if (!LandCoreAPI.isInteger(args[2]) || Integer.parseInt(args[2]) <= 0) {
                    sender.sendMessage(CC.RED + "Please use numbers.");
                    return;
                }

                if (Integer.parseInt(args[2]) > targetAccount.getP_coin()) {
                    sender.sendMessage(CC.translate("&cInvalid amount."));
                    return;
                }

                targetAccount.setP_coin(targetAccount.getP_coin() - Integer.parseInt(args[2]));

                sender.sendMessage("&eYou have successfully removed &6" + targetAccount.getP_coin() + "'s &ePCoins to &6" + target.getName() + "&e.\"");
                return;
            }
        }

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&e&lCoins HELP"));
        sender.sendMessage(CC.translate("&e/pcoin set <player> <amount>"));
        sender.sendMessage(CC.translate("&e/pcoin add <player> <amount>"));
        sender.sendMessage(CC.translate("&e/pcoin remove <player> <amount>"));
        sender.sendMessage(" ");
    }
}
