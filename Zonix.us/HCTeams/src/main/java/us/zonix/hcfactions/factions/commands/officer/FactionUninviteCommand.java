package us.zonix.hcfactions.factions.commands.officer;

import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;

import java.util.UUID;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionUninviteCommand extends FactionCommand {
    @Command(name = "f.uninvite", aliases = {"faction.uninvite", "factions.uninvite", "f.uninv", "factions.uninv", "faction.uninv"}, inFactionOnly = true, isOfficerOnly = true) //any other aliases..?
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (command.getArgs().length == 0) {
            player.sendMessage(langConfig.getString("TOO_FEW_ARGS.UNINVITE"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFaction();

        UUID uuid;
        String name;
        Player toInvite = Bukkit.getPlayer(command.getArgs(0));

        if (toInvite == null) {
            SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(command.getArgs(0));
            if (offlinePlayer != null) {
                uuid = offlinePlayer.getUuid();
                name = offlinePlayer.getName();
            } else {
                player.sendMessage(langConfig.getString("ERROR.NOT_ONLINE").replace("%PLAYER%", command.getArgs(0)));
                return;
            }
        } else {
            uuid = toInvite.getUniqueId();
            name = toInvite.getName();
        }

        if (!playerFaction.getInvitedPlayers().containsKey(uuid)) {
            player.sendMessage(langConfig.getString("ERROR.NEVER_INVITED").replace("%PLAYER%", name));
            return;
        }

        playerFaction.getInvitedPlayers().remove(uuid);
        playerFaction.sendMessage(langConfig.getString("ANNOUNCEMENTS.FACTION.PLAYER_UNINVITED").replace("%PLAYER%", player.getName()).replace("%UNINVITED_PLAYER%", name));
    }
}
