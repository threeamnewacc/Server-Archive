package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.entity.Player;

public class TeleportCommand extends PlayerCommand {
    private final LandCore plugin;

    public TeleportCommand(LandCore plugin) {
        super("tp", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setAliases("teleport");
        setUsage(CC.RED + "Usage: /teleport <player> [player]");
    }

    private static boolean isOffline(Player checker, Player target) {
        if (target == null) {
            checker.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        return false;
    }

    private void teleport(Player to, Player from) {
        to.teleport(from);
        to.sendMessage(CC.GREEN + "You have been teleported to " + from.getName() + ".");

        CoreProfile fromProfile = plugin.getProfileManager().getProfile(from.getUniqueId());

        if (fromProfile.hasStaff()) {
            from.sendMessage(CC.GREEN + to.getName() + " has been teleported to you.");
        }
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (isOffline(player, target)) {
            return;
        }

        if (args.length < 2) {
            teleport(player, target);
        } else {
            Player target2 = plugin.getServer().getPlayer(args[1]);

            if (isOffline(player, target2)) {
                return;
            }

            teleport(target, target2);

            player.sendMessage(CC.GREEN + "Teleported " + target.getName() + " to " + target2.getName() + ".");
        }
    }
}
