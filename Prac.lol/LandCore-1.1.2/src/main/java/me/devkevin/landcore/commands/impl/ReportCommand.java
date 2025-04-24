package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.inventory.menu.impl.ReportMenu;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.entity.Player;

public class ReportCommand extends PlayerCommand {
    private final LandCore plugin;

    public ReportCommand(LandCore plugin) {
        super("report");
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /report <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        if (player == target) {
            player.sendMessage(CC.RED + "You can't report yourself!");
            return;
        }

        CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (targetProfile.hasStaff()) {
            player.sendMessage(CC.RED + "You can't report a staff member. If this staff member is harassing you or" +
                    " engaging in other abusive manners, please report this or contact a higher staff member.");
            return;
        }

        if (args.length == 1 || args.length == 2) {
            if (target.getName() != null || targetProfile.getName() != null) {
                CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

                profile.setReportingPlayerName(target.getName());
                plugin.getMenuManager().getMenu(ReportMenu.class).open(player);
            } else {
                player.sendMessage(CC.RED + "Error: That player does not exist.");
            }
        }
    }
}
