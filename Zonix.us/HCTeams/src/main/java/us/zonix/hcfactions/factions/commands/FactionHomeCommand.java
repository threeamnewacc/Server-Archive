package us.zonix.hcfactions.factions.commands;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.factions.events.player.PlayerInitiateFactionTeleportEvent;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import us.zonix.hcfactions.profile.teleport.ProfileTeleportTask;
import us.zonix.hcfactions.profile.teleport.ProfileTeleportType;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.LocationSerialization;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.teleport.ProfileTeleportType;
import us.zonix.hcfactions.util.LocationSerialization;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.util.concurrent.TimeUnit;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionHomeCommand extends FactionCommand {
    @Command(name = "f.home", aliases = {"faction.home", "factions.home", "f.hq", "faction.hq", "factions.hq"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile.getTeleportWarmup() != null) {
            return;
        }

        Faction faction;
        if (command.getArgs().length >= 1 && us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.DEVELOPER)) {
            String name = command.getArgs(0);
            Faction faction1 = PlayerFaction.getAnyByString(name);
            if (faction1 != null) {
                faction = faction1;
            } else {
                player.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_FOUND").replace("%NAME%", name));
                return;
            }
        } else {
            faction = profile.getFaction();

            if (faction == null) {
                player.sendMessage(langConfig.getString("ERROR.NOT_IN_FACTION"));
                return;
            }

        }

        if (faction.getHome() == null) {
            player.sendMessage(langConfig.getString("ERROR.HOME_NOT_SET"));
            return;
        }

        Location location = LocationSerialization.deserializeLocation(faction.getHome());

        Claim claim = Claim.getProminentClaimAt(player.getLocation());

        if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
            player.teleport(LocationSerialization.deserializeLocation(faction.getHome()));
            return;
        }

        if (us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.DEVELOPER) && command.getArgs().length >= 1) {
            player.teleport(LocationSerialization.deserializeLocation(faction.getHome()));
        } else {

            if(claim != null && (claim.getFaction() instanceof PlayerFaction && (claim.getFaction() != faction) && mainConfig.getBoolean("FACTION_GENERAL.FACTION_HOME_IN_ENEMY_TERRITORY"))) {
                player.sendMessage(langConfig.getString("ERROR.NO_HOME_ENEMY"));
                return;
            }

            if (profile.getProtection() != null) {
                player.sendMessage(langConfig.getString("ERROR.NO_HOME_PVP_PROTECTION"));
                return;
            }

            int time = 0;
            String worldName = player.getLocation().getWorld().getName();
            String root = "TELEPORT_COUNTDOWN.HOME";

            for (String world : new String[]{"OVERWORLD", "NETHER", "END"}) {
                if (worldName.equalsIgnoreCase(mainConfig.getString(world))) {
                    if (!mainConfig.getBoolean(root + "." + world + ".ENABLED")) {
                        player.sendMessage(langConfig.getString("ERROR.NO_HOME_TELEPORT_IN_WORLD"));
                        return;
                    } else {
                        time = mainConfig.getInt(root + "." + world + ".TIME");
                    }
                }
            }

            long hours = TimeUnit.SECONDS.toHours(time);
            long minutes = TimeUnit.SECONDS.toMinutes(time) - (hours * 60);
            long seconds = TimeUnit.SECONDS.toSeconds(time) - ((hours * 60 * 60) + (minutes * 60));

            String formatted;

            if (hours == 0 && minutes > 0 && seconds > 0) {
                formatted = minutes + " minutes and " + seconds + " seconds";
            } else if (hours == 0 && minutes > 0 && seconds == 0) {
                formatted = minutes + " minutes";
            } else if (hours == 0 && minutes == 0 && seconds > 0) {
                formatted = seconds + " seconds";
            } else if (hours > 0 && minutes > 0 && seconds == 0) {
                formatted = hours + " hours and " + minutes + " minutes";
            } else if (hours > 0 && minutes == 0 && seconds > 0) {
                formatted = hours + " hours and " + seconds + " seconds";
            } else {
                formatted = hours + "hours, " + minutes + " minutes and " + seconds + " seconds";
            }

            if (hours == 1) {
                formatted = formatted.replace("hours", "hour");
            }

            if (minutes == 1) {
                formatted = formatted.replace("minutes", "minute");
            }

            if (seconds == 1) {
                formatted = formatted.replace("seconds", "second");
            }


            player.sendMessage(langConfig.getString("FACTION_OTHER.TELEPORTING_TO_HOME").replace("%TIME%", formatted + ""));
            profile.setTeleportWarmup(new ProfileTeleportTask(new PlayerInitiateFactionTeleportEvent(player, profile.getFaction(), ProfileTeleportType.HOME_TELEPORT, time, location, player.getLocation())));
            profile.getTeleportWarmup().runTaskLaterAsynchronously(main, (long) (time * 20));
        }
    }
}
