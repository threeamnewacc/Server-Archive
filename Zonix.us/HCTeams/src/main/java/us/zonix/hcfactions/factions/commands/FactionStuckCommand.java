package us.zonix.hcfactions.factions.commands;

import us.zonix.hcfactions.profile.teleport.ProfileTeleportType;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.events.player.PlayerInitiateFactionTeleportEvent;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.teleport.ProfileTeleportTask;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.teleport.ProfileTeleportType;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionStuckCommand extends FactionCommand {
    @Command(name = "f.stuck", aliases = {"faction.stuck", "factions.stuck"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile.getTeleportWarmup() != null) {
            return;
        }

        int time = 0;
        String worldName = player.getLocation().getWorld().getName();
        String root = "TELEPORT_COUNTDOWN.STUCK";


        for (String world : new String[]{"OVERWORLD", "NETHER", "END"}) {
            if (worldName.equalsIgnoreCase(mainConfig.getString(world))) {
                if (!mainConfig.getBoolean(root + "." + world + ".ENABLED")) {
                    player.sendMessage(langConfig.getString("ERROR.NO_STUCK_TELEPORT_IN_WORLD"));
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


        Location location = null;
        int current = 5;
        while (location == null) {
            for (int x = player.getLocation().getBlockX() - current; x < player.getLocation().getBlockX() + current; x++) {
                for (int z = player.getLocation().getBlockZ() - current; z < player.getLocation().getBlockZ() + current; z++) {
                    Location newLocation = new Location(player.getLocation().getWorld(), x, 0, z);
                    List<Claim> claims = Claim.getClaimsAt(newLocation);
                    if (claims == null) {
                        location = newLocation;
                    } else {
                        for (Claim claim : claims) {
                            if (claim.getFaction() instanceof PlayerFaction) {
                                break;
                            }
                        }
                        location = newLocation;
                        break;
                    }
                }
            }
            current +=5;
        }

        location.setDirection(player.getLocation().getDirection());

        player.sendMessage(langConfig.getString("FACTION_OTHER.TELEPORTING_TO_STUCK").replace("%TIME%", formatted));
        profile.setTeleportWarmup(new ProfileTeleportTask(new PlayerInitiateFactionTeleportEvent(player, null, ProfileTeleportType.STUCK_TELEPORT, time, location.getWorld().getHighestBlockAt(location).getLocation(), player.getLocation())));
        profile.getTeleportWarmup().runTaskLaterAsynchronously(main, (long) (time * 20));
    }
}
