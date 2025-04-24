package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {
    private final LandCore plugin;

    public VanishCommand(LandCore plugin) {
        super("vanish", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        boolean vanished = !profile.isVanished();

        profile.setVanished(vanished);

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            plugin.getStaffManager().hideVanishedStaffFromPlayer(online);
        }

        player.sendMessage(vanished ? CC.GREEN + "Poof, you vanished." : CC.RED + "You're visible again.");
    }
}
