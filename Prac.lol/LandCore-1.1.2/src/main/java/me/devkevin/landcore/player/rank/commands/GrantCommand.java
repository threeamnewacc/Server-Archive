package me.devkevin.landcore.player.rank.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.menus.GrantSelectMenu;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 19/01/2023 @ 0:27
 * GrantCommand / land.pvp.core.player.rank.commands / LandCore
 */
public class GrantCommand extends PlayerCommand {
    private final LandCore plugin;

    public GrantCommand(LandCore plugin) {
        super("grant", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /grant <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            assert target != null;
            player.sendMessage(CC.RED + "No player matching " + CC.YELLOW + target.getName() + CC.RED + " is connected to this server");
            return;
        }

        CoreProfile coreProfile = plugin.getProfileManager().getProfile(target.getUniqueId());
        new GrantSelectMenu(coreProfile).openMenu(player);
    }
}
