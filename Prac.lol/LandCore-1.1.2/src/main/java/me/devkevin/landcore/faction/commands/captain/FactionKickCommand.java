package me.devkevin.landcore.faction.commands.captain;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 13:59
 * FactionKickCommand / me.devkevin.landcore.faction.commands.captain / LandCore
 */
public class FactionKickCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionKickCommand(LandCore plugin) {
        super("faction.kick");
        this.plugin = plugin;
        setAliases("f.kick");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(CC.RED + "Usage: /faction kick <player>");
            return;
        }

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);
        if (factionProfile.getFaction() == null) {
            player.sendMessage(CC.RED + "You are not in a faction!");
            return;
        }

        Faction faction = factionProfile.getFaction();
        if (!faction.getCaptains().contains(player.getUniqueId()) && !faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not a faction captain!");
            return;
        }

        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(args[0]);
        if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer() == player) {
            player.sendMessage(CC.RED + "You may not kick yourself from the faction!");
            return;
        }

        if (!faction.getMembers().contains(offlinePlayer.getUniqueId())) {
            player.sendMessage(player.getName() + CC.RED + " is not in your faction!");
            return;
        }

        if ((faction.getCaptains().contains(offlinePlayer.getUniqueId()) && !faction.getLeader().equals(player.getUniqueId())) || faction.getLeader() == offlinePlayer.getUniqueId()) {
            player.sendMessage(CC.RED + "You may only kick faction members.");
            return;
        }

        faction.getMembers().remove(offlinePlayer.getUniqueId());
        faction.getCaptains().remove(offlinePlayer.getUniqueId());

        if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer().isOnline()) {
            Player target = offlinePlayer.getPlayer();

            FactionProfile targetProfile = this.plugin.getFactionManager().getProfile(target);
            targetProfile.setFaction(null);

            target.sendMessage(CC.RED + CC.B + "You have been kicked from the faction!");
        }

        faction.broadcast(player.getName() + CC.YELLOW + " has been kicked from the faction!");
    }
}
