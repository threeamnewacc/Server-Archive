package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 17:25
 * AltsCommand / me.devkevin.landcore.commands.impl.staff / LandCore
 */
public class AltsCommand extends PlayerCommand {
    private final LandCore plugin;

    public AltsCommand(LandCore plugin) {
        super("alts", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setAliases("dupeip");
        setUsage(CC.RED + "Usage: /alts <name:ip>");
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = plugin.getServer().getPlayer(args[0]);
        CoreProfile coreProfile = this.plugin.getProfileManager().getProfile(player.getUniqueId());

        if (target == null || coreProfile == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        List<CoreProfile> alts = new ArrayList<>();

        for (UUID altUUID : coreProfile.getKnownAlts()) {
            CoreProfile altProfile = this.plugin.getProfileManager().getProfile(altUUID);

            if (altProfile != null) {
                alts.add(altProfile);
            }
        }

        if (alts.isEmpty()) {
            player.sendMessage(CC.RED + "This player has no known alt accounts.");
        } else {
            StringBuilder builder = new StringBuilder();

            for (CoreProfile altProfile : alts) {
                builder.append(altProfile.getName());
                builder.append(", ");
            }

            player.sendMessage(CC.GOLD + "Alts: " + CC.R + builder);
        }
    }
}
