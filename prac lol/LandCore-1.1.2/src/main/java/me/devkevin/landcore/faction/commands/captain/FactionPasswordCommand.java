package me.devkevin.landcore.faction.commands.captain;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 14:02
 * FactionPasswordCommand / me.devkevin.landcore.faction.commands.captain / LandCore
 */
public class FactionPasswordCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionPasswordCommand(LandCore plugin) {
        super("faction.password");
        this.plugin = plugin;
        setAliases("f.password");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(CC.RED + "Usage: /faction password <password>");
            return;
        }

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);
        if (factionProfile.getFaction() == null) {
            player.sendMessage(CC.RED + "You are not in a faction!");
            return;
        }

        Faction faction = factionProfile.getFaction();
        if (!faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the faction leader!");
            return;
        }

        String password = args[0];
        faction.setPassword(password);

        player.sendMessage(CC.YELLOW + "Your faction password is now " + CC.GRAY + "'" + CC.DARK_GREEN + password + CC.GRAY + "'" + CC.YELLOW + ".");
    }
}
