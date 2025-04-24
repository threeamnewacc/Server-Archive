package me.devkevin.landcore.faction.commands.player;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.TaskUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 2:36
 * FactionLeaveCommand / me.devkevin.landcore.faction.commands.player / LandCore
 */
public class FactionLeaveCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionLeaveCommand(LandCore plugin) {
        super("faction.leave");
        this.plugin = plugin;
        setAliases("f.leave");
    }

    @Override
    public void execute(Player player, String[] args) {
        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile.getFaction() != null) {
            player.sendMessage(CC.RED + "You are already in a faction.");
            return;
        }

        Faction faction = factionProfile.getFaction();

        if (faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are the faction leader. You must disband the faction or promote someone else.");
            return;
        }

        faction.getMembers().remove(player.getUniqueId());
        faction.getCaptains().remove(player.getUniqueId());
        factionProfile.setFaction(null);
        faction.broadcast(player.getDisplayName() + CC.YELLOW + " has left the faction!");

        player.sendMessage(CC.RED + CC.B + "You have left the faction!");

        TaskUtil.runAsync(plugin.getFactionManager()::save);
        this.plugin.getFactionManager().savePlayerFaction(player);
    }
}
