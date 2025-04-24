package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.utils.Duration;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Objects;
import java.util.UUID;

public class BanCommand extends BaseCommand {
    private final LandCore plugin;

    public BanCommand(LandCore plugin) {
        super("ban", Rank.MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (args.length < 3) {
            sender.sendMessage(CC.RED +  "Usage: /ban <player> <duration> <reason> " + CC.GRAY + "[-s]" + CC.RED + ".");
        }

        if (profile == null) {
            sender.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        if (Objects.requireNonNull(profile).getActiveBan() != null) {
            sender.sendMessage(CC.RED + "That player is already banned.");
            return;
        }

        long duration = Duration.fromString(args[1]).getValue();
        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        if (duration == -1) {
            sender.sendMessage(CC.RED + "That duration is not valid.");
            sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
            return;
        }

        String staffName = sender instanceof Player ? plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getGrant().getRank().getColor() + sender.getName() : CC.D_RED + "Console";

        Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.BAN, System.currentTimeMillis(),
                args[1], duration);

        if (sender instanceof Player) {
            punishment.setAddedBy(((Player) sender).getUniqueId());
        }

        profile.getPunishments().add(punishment);
        profile.save(true);

        punishment.broadcast(staffName, target, silent);

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
