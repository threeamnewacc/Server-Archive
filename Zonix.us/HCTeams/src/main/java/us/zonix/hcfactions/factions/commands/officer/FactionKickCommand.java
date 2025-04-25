package us.zonix.hcfactions.factions.commands.officer;

import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.events.player.PlayerLeaveFactionEvent;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.commands.FactionCommand;
import us.zonix.hcfactions.factions.events.player.PlayerLeaveFactionEvent;
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
public class FactionKickCommand extends FactionCommand {
    @Command(name = "f.kick", aliases = {"faction.kick", "factions.kick"}, inFactionOnly = true, isOfficerOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (command.getArgs().length == 0) {
            player.sendMessage(langConfig.getString("TOO_FEW_ARGS.KICK"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFaction();

        if (command.getArgs(0).equalsIgnoreCase(player.getName())) {
            player.sendMessage(langConfig.getString("ERROR.KICK_YOURSELF"));
            return;
        }

        UUID uuid;
        String name;
        Player toDemote = Bukkit.getPlayer(command.getArgs(0));

        if (toDemote == null) {
            SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(command.getArgs(0));

            if (offlinePlayer != null) {
                uuid = offlinePlayer.getUuid();
                name = offlinePlayer.getName();
            }
            else {
                player.sendMessage(langConfig.getString("ERROR.NOT_ONLINE").replace("%PLAYER%", command.getArgs(0)));
                return;
            }
        }
        else {
            uuid = toDemote.getUniqueId();
            name = toDemote.getName();
        }

        if (!playerFaction.getAllPlayerUuids().contains(uuid)) {
            player.sendMessage(langConfig.getString("ERROR.NOT_IN_YOUR_FACTION").replace("%PLAYER%", name));
            return;
        }

        if (playerFaction.getLeader().equals(uuid)) {
            player.sendMessage(langConfig.getString("ERROR.CANT_KICK_LEADER"));
            return;
        }
        
        if (playerFaction.getOfficers().contains(uuid) && playerFaction.getOfficers().contains(player.getUniqueId())) {
            player.sendMessage(langConfig.getString("ERROR.CANT_KICK_OTHER_OFFICER"));
            return;
        }

        playerFaction.sendMessage(langConfig.getString("ANNOUNCEMENTS.FACTION.PLAYER_KICKED").replace("%KICKED_PLAYER%", name).replace("%PLAYER%", player.getName()));
        playerFaction.getOfficers().remove(uuid);
        playerFaction.getMembers().remove(uuid);

        Profile kickProfile = Profile.getByUuid(uuid);

        if (kickProfile == null) {
            kickProfile = new Profile(uuid);
        }

        kickProfile.setFaction(null);

        Profile finalKickProfile = kickProfile;
        new BukkitRunnable() {

            @Override
            public void run() {
                finalKickProfile.save();
            }
        }.runTaskAsynchronously(this.main);


        Player kickPlayer = Bukkit.getPlayer(uuid);

        if (kickPlayer != null) {
            Bukkit.getPluginManager().callEvent(new PlayerLeaveFactionEvent(kickPlayer, playerFaction));
        }
    }
}
