package me.devkevin.landcore.player.rank.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.event.player.PlayerRankChangeEvent;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.Grant;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.TaskUtil;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.time.TimeFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends BaseCommand {
    private final LandCore plugin;

    public RankCommand(LandCore plugin) {
        super("rank", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /rank <player> <rank>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        TaskUtil.runAsync(() -> {
            Grant grant;
            if (args.length < 4) {
                sender.sendMessage(CC.translate("&cInvalid usage: /setrank <player> <rank> <duration> <reason>"));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null || !target.isOnline()) {
                assert target != null;
                sender.sendMessage(CC.RED + "No player matching " + CC.YELLOW + target.getName() + CC.RED + " is connected to this server");
                return;
            }

            CoreProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());
            Rank rank = Rank.getByName(args[1]);

            if (rank == null) {
                sender.sendMessage(CC.translate("&cThat rank doesn't exist."));
                return;
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 3; i < args.length; i++)
                builder.append(args[i]).append(" ");

            String reason = builder.toString().trim();

            long duration = TimeFormatUtils.parseTime(args[2]);

            if (duration == -1L) {
                grant = new Grant(rank, -1L, System.currentTimeMillis(), sender.getName(), reason);
            } else {
                grant = new Grant(rank, System.currentTimeMillis() + duration, System.currentTimeMillis(), sender.getName(), reason);
            }

            targetProfile.setGrant(grant);
            targetProfile.getGrants().add(grant);

            plugin.getServer().getPluginManager().callEvent(new PlayerRankChangeEvent(target, targetProfile, grant, grant.getDuration()));

            sender.sendMessage(CC.translate("&aYou have set &e" + target.getName() + " &arank to " + rank.getColor() + rank.getName()));
            target.sendMessage(CC.translate("&aYour rank has been set to " + rank.getColor() + rank.getName()));

            targetProfile.save(true);
        });
    }
}
