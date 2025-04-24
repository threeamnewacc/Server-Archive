package me.devkevin.landcore.faction.commands.leader;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 13:46
 * FactionDisbandCommand / me.devkevin.landcore.faction.commands.leader / LandCore
 */
public class FactionDisbandCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionDisbandCommand(LandCore plugin) {
        super("faction.disband");
        this.plugin = plugin;
        setAliases("f.disband");
    }

    @Override
    public void execute(Player player, String[] args) {
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

        player.sendMessage(CC.RED + CC.B + "You have successfully disbanded your faction!");

        faction.getMembers().remove(player.getUniqueId());
        factionProfile.setFaction(null);
        faction.broadcast(CC.RED + CC.B + "Your faction has been disbanded.");

        faction.getMembers().forEach(u -> {
            Player member = this.plugin.getServer().getPlayer(u);

            if (member != null) {
                FactionProfile memberProfile = this.plugin.getFactionManager().getProfile(member);
                memberProfile.setFaction(null);
            }
        });

        this.plugin.getFactionManager().getFactions().remove(faction);
    }
}
