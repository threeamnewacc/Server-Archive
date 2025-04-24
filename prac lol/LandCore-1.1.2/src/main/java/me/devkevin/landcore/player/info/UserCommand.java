package me.devkevin.landcore.player.info;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.info.menu.UserViewMenu;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 19:51
 * UserCommand / land.pvp.core.player.info / LandCore
 */
public class UserCommand extends PlayerCommand {
    private final LandCore plugin;

    public UserCommand(LandCore plugin) {
        super("user", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /user <player>");
    }


    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        CoreProfile coreProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        new UserViewMenu(coreProfile).openMenu(player);
    }
}
