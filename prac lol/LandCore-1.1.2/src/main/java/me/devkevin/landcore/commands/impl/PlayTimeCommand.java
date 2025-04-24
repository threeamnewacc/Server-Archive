package me.devkevin.landcore.commands.impl;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.utils.message.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/01/2023 @ 1:13
 * PlayTimeCommand / land.pvp.core.commands.impl / LandCore
 */
public class PlayTimeCommand extends PlayerCommand {
    private final LandCore plugin;

    public PlayTimeCommand(LandCore plugin) {
        super("playtime");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            long time = player.getStatistic(Statistic.PLAY_ONE_TICK);

            player.sendMessage(CC.translate("&eYour playtime is &6" + DurationFormatUtils.formatDurationWords(time * 50L, true, true) + " &eon this server."));
        } else {
            Player target = this.plugin.getServer().getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                assert target != null;
                player.sendMessage(CC.RED + "No player matching " + CC.YELLOW + target.getName() + CC.RED + " is connected to this server");
                return;
            }

            long time = target.getStatistic(Statistic.PLAY_ONE_TICK);

            player.sendMessage(CC.translate(plugin.getProfileManager().getProfile(target.getUniqueId()).getGrant().getRank().getColor() + target.getName() + "'s &eplaytime is &6" + DurationFormatUtils.formatDurationWords(time * 50L, true, true) + " &eon this server."));
        }
    }
}
