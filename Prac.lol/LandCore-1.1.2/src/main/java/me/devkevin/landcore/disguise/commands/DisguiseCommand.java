package me.devkevin.landcore.disguise.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.disguise.manager.DisguiseManager;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.api.NickAPI;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 18:58
 * DisguiseCommand / me.devkevin.landcore.disguise / LandCore
 */
public class DisguiseCommand extends BaseCommand {
    private final LandCore plugin;

    public DisguiseCommand(LandCore plugin) {
        super("disguise", Rank.TRIAL_MOD); // temp rank for test
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if ("random".equals(args[0])) {
            String disguiseName;
            String disguiseSkin = DisguiseManager.generateSkin().split(":")[0];

            if (DisguiseManager.nickData.get(player) != null) {
                disguiseName = DisguiseManager.nickData.get(player);
            } else {
                disguiseName = DisguiseManager.generate();
            }

            plugin.getDisguiseManager().setPlayerDisguise(player, disguiseName, disguiseSkin);

            // first disguise a player and then after 2 sec open the rank menu
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // check is player in nicked yet so we won't get errors
                if (NickAPI.isNicked(player)) {
                    player.openInventory(this.plugin.getDisguiseMenu().getRankMenu().getCurrentPage());
                }
            }, 20L);


        } else {
            if (NickAPI.nickExists(args[0])) {
                player.sendMessage(CC.RED + "That name is already used.");
                return;
            }

            plugin.getDisguiseManager().setPlayerDisguise(player, args[0], args[1]);

            // first disguise a player and then after 2 sec open the rank menu
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // check is player in nicked yet so we won't get errors
                if (NickAPI.isNicked(player)) {
                    player.openInventory(this.plugin.getDisguiseMenu().getRankMenu().getCurrentPage());
                }
            }, 20L);
        }
    }
}
