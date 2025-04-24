package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbanCommand extends BaseCommand {
    private final LandCore plugin;

    public UnbanCommand(LandCore plugin) {
        super("unban", Rank.MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (args.length < 2) {
            sender.sendMessage(CC.RED +  "Usage: /unban <player> <reason> " + CC.GRAY + "[-s]" + CC.RED + ".");
        }

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (profile == null) {
            sender.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        if ((profile != null ? profile.getActiveBan() : null) == null) {
            sender.sendMessage(CC.RED + "That player is not banned.");
            return;
        }

        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        String staffName = sender instanceof Player ? plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getGrant().getRank().getColor() + sender.getName() : CC.D_RED + "Console";

        Punishment punishment = profile.getActiveBan();
        punishment.setPardonedAt(System.currentTimeMillis());
        punishment.setPardonedReason(args[1]);
        punishment.setPardoned(true);

        if (sender instanceof Player) {
            punishment.setPardonedBy(((Player) sender).getUniqueId());
        }

        profile.save(true);

        punishment.broadcast(staffName, target.getPlayer(), silent);
    }
}
