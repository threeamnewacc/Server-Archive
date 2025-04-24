package me.devkevin.landcore.faction.commands.leader;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 13:38
 * FactionDescriptionCommand / me.devkevin.landcore.faction.commands.leader / LandCore
 */
public class FactionDescriptionCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionDescriptionCommand(LandCore plugin) {
        super("faction.description");
        this.plugin = plugin;
        setAliases("f.description");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(CC.RED + "Usage: /faction description <text>");
            return;
        }

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile.getFaction() == null) {
            player.sendMessage(CC.RED + "You are not in a faction!");
            return;
        }

        Faction faction = factionProfile.getFaction();

        if (faction.getLeader() != player.getUniqueId()) {
            player.sendMessage(CC.RED + "You are not the faction leader!");
            return;
        }

        String description = StringUtil.buildString(args, 0);
        faction.setDescription(description);
        faction.broadcast(player.getDisplayName() + CC.YELLOW + " has set the faction's description to " + CC.GRAY + "'" + CC.DARK_GREEN + description + CC.GRAY + "'" + CC.YELLOW + ".");
    }
}
