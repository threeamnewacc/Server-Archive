package me.devkevin.landcore.faction.commands.player;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.invite.FactionInvite;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 2:17
 * FactionAcceptCommand / me.devkevin.landcore.faction.commands.player / LandCore
 */
public class FactionAcceptCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionAcceptCommand(LandCore plugin) {
        super("faction.accept");
        this.plugin = plugin;
        setAliases(Arrays.asList("f.accept", "faction.join", "f.join"));
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(CC.RED + "Usage: /faction <player|faction>");
            return;
        }

        String name = args[0];
        FactionInvite factionInvite = getInvite(player, name);

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile.getFaction() != null) {
            player.sendMessage(CC.RED + "You are already in a faction!");
            return;
        }

        if (factionInvite == null && (getFaction(name) == null || getFaction(name).getPassword() == null)) {
            player.sendMessage(CC.RED + "You weren't invited to a faction with this name or a faction with a member with this name.");
            return;
        }

        if (factionInvite == null && getFaction(name).getPassword() != null && args.length < 2) {
            player.sendMessage(CC.RED + "You need the password or an invitation to join this faction.");
            player.sendMessage(CC.YELLOW + "To join with a password, use " + CC.PINK + "/faction join " + name + " <password>");
            return;
        }

        Faction faction = getFaction(name);

        if (faction.getPassword() != null && args.length >= 2 && factionInvite == null) {
            String password = args[1];

            if (!faction.getPassword().equalsIgnoreCase(password)) {
                player.sendMessage(CC.RED + "The password you have entered is incorrect.");
                return;
            }
        }

        factionProfile.setFaction(faction);
        faction.getMembers().add(player.getUniqueId());

        if (factionInvite != null) factionProfile.getInviteList().remove(factionInvite);

        faction.broadcast(player.getDisplayName() + CC.YELLOW + " has joined the faction!");
    }

    private FactionInvite getInvite(Player player, String name) {
        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        return factionProfile.getInviteList().stream().filter(ci -> ci.getFaction() == getFaction(name) && System.currentTimeMillis() - ci.getTimestamp() <= 60000 && !ci.isCancelled()).findFirst().orElse(null);
    }

    private Faction getFaction(String name) {
        return this.plugin.getFactionManager().getFactions().stream().filter(c -> c.getName().equalsIgnoreCase(name) || c.getMembers().contains(this.plugin.getServer().getOfflinePlayer(name).getUniqueId())).findFirst().orElse(null);
    }
}
