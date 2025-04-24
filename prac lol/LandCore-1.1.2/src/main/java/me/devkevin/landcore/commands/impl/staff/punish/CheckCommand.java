package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.menu.PunishmentsMenu;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 15:26
 * CheckCommand / me.devkevin.landcore.commands.impl.staff.punish / LandCore
 */
public class CheckCommand extends PlayerCommand {
    private final LandCore plugin;

    public CheckCommand(LandCore plugin) {
        super("check", Rank.MOD);
        this.plugin = plugin;
        setAliases("c");
    }


    @Override
    public void execute(Player player, String[] args) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());

        if (profile == null) {
            player.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        new PunishmentsMenu(profile).openMenu(player);
    }
}
