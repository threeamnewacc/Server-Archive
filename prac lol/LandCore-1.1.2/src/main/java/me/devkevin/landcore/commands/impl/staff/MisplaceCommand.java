package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 17:57
 * MisplaceCommand / me.devkevin.landcore.commands.impl.staff / LandCore
 */
public class MisplaceCommand extends BaseCommand {
    private final LandCore plugin;

    public MisplaceCommand(LandCore plugin) {
        super("xp", Rank.MANAGER);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target = this.plugin.getServer().getPlayer(args[0]);

        CoreProfile coreProfile = this.plugin.getProfileManager().getProfile(target.getUniqueId());
        try {
            double d = Double.parseDouble(args[1]);
            if (d > 0.25) {
                sender.sendMessage(CC.RED + "Warning! " + d + " is over the recommended amount. The recommended amount is 0.25.");
            }
            coreProfile.setMisplace(d);
            sender.sendMessage(CC.GREEN + "Misplace set to " + d + ".");
        } catch (Exception ex) {
            sender.sendMessage(CC.RED + "Invalid amount.");
        }
    }
}
