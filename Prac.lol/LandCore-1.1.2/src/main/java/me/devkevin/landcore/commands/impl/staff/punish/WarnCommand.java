package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 15:52
 * WarnCommand / me.devkevin.landcore.commands.impl.staff.punish / LandCore
 */
public class WarnCommand extends BaseCommand {
    private final LandCore plugin;

    public WarnCommand(LandCore plugin) {
        super("warn", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        if (args.length < 2) {
            sender.sendMessage(CC.RED +  "Usage: /warn <player> <reason> " + CC.GRAY + "[-s]" + CC.RED + ".");
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (profile == null) {
            sender.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        String staffName = sender instanceof Player ? plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getGrant().getRank().getColor() + sender.getName() : CC.D_RED + "Console";

        Punishment punishment = new Punishment(target.getUniqueId(), PunishmentType.WARN, System.currentTimeMillis(),
                args[1], -1);

        if (sender instanceof Player) {
            punishment.setAddedBy(((Player) sender).getUniqueId());
        }

        Objects.requireNonNull(profile).getPunishments().add(punishment);
        profile.save(true);

        Player player = profile.getPlayer();

        if (player != null) {
            String senderName = sender instanceof Player ? plugin.getProfileManager().getProfiles().get(((Player) sender).getUniqueId()).getGrant().getRank().getColor() : CC.D_RED + "Console";
            player.sendMessage(CC.RED + "You have been warned by " + senderName + CC.RED + " for: " + CC.YELLOW + args[1]);
        }

        punishment.broadcast(staffName, target, silent);
    }
}
