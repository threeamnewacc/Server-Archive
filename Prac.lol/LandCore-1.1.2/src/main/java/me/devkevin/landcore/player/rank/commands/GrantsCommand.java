package me.devkevin.landcore.player.rank.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.menus.GrantViewMenu;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.TaskUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 13:19
 * GrantsCommand / land.pvp.core.player.rank.commands / LandCore
 */
public class GrantsCommand extends PlayerCommand {
    private final LandCore plugin;

    public GrantsCommand(LandCore plugin) {
        super("grants", Rank.MANAGER);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        TaskUtil.run(() -> {
            Player target = plugin.getServer().getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                assert target != null;
                player.sendMessage(CC.RED + "No player matching " + CC.YELLOW + target.getName() + CC.RED + " is connected to this server");
                return;
            }

            CoreProfile coreProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

            if (coreProfile.getGrants().isEmpty()) {
                player.sendMessage(CC.translate("&cThat player has &l0 &cgrants."));
                return;
            }

            new GrantViewMenu(coreProfile).openMenu(player);
        });
    }
}
