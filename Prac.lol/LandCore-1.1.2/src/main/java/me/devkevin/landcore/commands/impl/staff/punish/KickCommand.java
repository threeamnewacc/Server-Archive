package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.utils.Duration;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class KickCommand extends BaseCommand {
    private final LandCore plugin;

    public KickCommand(LandCore plugin) {
        super("kick", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (args.length < 2) {
            sender.sendMessage(CC.RED +  "Usage: /kick <player> <reason> " + CC.GRAY + "[-s]" + CC.RED + ".");
        }

        if (profile == null) {
            sender.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        String staffName = sender instanceof Player ? plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getGrant().getRank().getColor() + sender.getName() : CC.D_RED + "Console";


        Punishment punishment = new Punishment(target.getUniqueId(), PunishmentType.KICK, System.currentTimeMillis(),
                args[1], -1);

        if (sender instanceof Player) {
            punishment.setAddedBy(((Player) sender).getUniqueId());
        }

        Objects.requireNonNull(profile).getPunishments().add(punishment);
        profile.save(true);

        punishment.broadcast(staffName, target, true);

        Player player = profile.getPlayer();

        if (player != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.kickPlayer(punishment.getKickMessage());
                }
            }.runTask(LandCore.getInstance());
        }
    }
}
