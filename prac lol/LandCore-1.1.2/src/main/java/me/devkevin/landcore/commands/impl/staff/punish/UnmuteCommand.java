package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand extends BaseCommand {
    private final LandCore plugin;

    public UnmuteCommand(LandCore plugin) {
        super("unmute", Rank.MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        if (args.length < 2) {
            sender.sendMessage(CC.RED +  "Usage: /unmute <player> <reason> " + CC.GRAY + "[-s]" + CC.RED + ".");
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (profile == null) {
            sender.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        if ((profile != null ? profile.getActiveMute() : null) == null) {
            sender.sendMessage(CC.RED + "That player is not muted.");
            return;
        }

        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        String staffName = sender instanceof Player ? plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getGrant().getRank().getColor() + sender.getName() : CC.D_RED + "Console";

        Punishment punishment = profile.getActiveMute();
        punishment.setPardonedAt(System.currentTimeMillis());
        punishment.setPardonedReason(args[1]);
        punishment.setPardoned(true);

        if (sender instanceof Player) {
            punishment.setPardonedBy(((Player) sender).getUniqueId());
        }

        profile.save(true);

        punishment.broadcast(staffName, target, silent);
    }
}
