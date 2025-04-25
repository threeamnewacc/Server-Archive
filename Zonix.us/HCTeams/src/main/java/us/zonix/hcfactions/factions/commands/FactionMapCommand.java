package us.zonix.hcfactions.factions.commands;

import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.claims.ClaimPillar;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.claims.ClaimPillar;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

import java.util.*;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionMapCommand extends FactionCommand {
    @Command(name = "f.map", aliases = {"faction.map", "factions.map"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isViewingMap()) {
            for (ClaimPillar claimPillar : profile.getMapPillars()) {
                claimPillar.remove();
            }
            profile.getMapPillars().clear();
            player.sendMessage(langConfig.getString("FACTION_MAP.MAP_REMOVED"));
            profile.setViewingMap(false);
            return;
        }

        Set<Claim> toDisplay = new HashSet<>();
        int[] pos = new int[]{player.getLocation().getBlockX(), player.getLocation().getBlockZ()};

        for (int x = pos[0] - 64; x < pos[0] + 64; x++) {
            for (int z = pos[1] - 64; z < pos[1] + 64; z++) {
                Location location = new Location(player.getWorld(), x, 0, z);
                ArrayList<Claim> claims = Claim.getClaimsAt(location);
                if (claims != null) {
                    for (Claim claim : claims) {
                        if (claim.getWorldName().equalsIgnoreCase(location.getWorld().getName())) {
                            toDisplay.add(claim);
                        }
                    }
                }
            }
        }

        if (toDisplay.isEmpty()) {
            player.sendMessage(langConfig.getString("ERROR.NO_CLAIMS_NEARBY"));
            return;
        }

        Map<Faction, Material> shown = new HashMap<>();
        for (Claim claim : toDisplay) {
            Faction faction = claim.getFaction();
            Material material;
            if (faction == profile.getFaction()) {
                if (mainConfig.getString("FACTION_MAP.PILLAR.FRIENDLY").equalsIgnoreCase("RANDOM")) {
                   material = Claim.getMapBlocks().get(new Random().nextInt(Claim.getMapBlocks().size()));
                } else {
                    material = Material.valueOf(mainConfig.getString("FACTION_MAP.PILLAR.FRIENDLY"));
                }
            } else if (profile.getFaction() != null && faction instanceof PlayerFaction && profile.getFaction().getAllies().contains(faction)) {
                if (mainConfig.getString("FACTION_MAP.PILLAR.ALLY").equalsIgnoreCase("RANDOM")) {
                    material = Claim.getMapBlocks().get(new Random().nextInt(Claim.getMapBlocks().size()));
                } else {
                    material = Material.valueOf(mainConfig.getString("FACTION_MAP.PILLAR.ALLY"));
                }
            } else if (!(faction instanceof PlayerFaction)) {
                SystemFaction systemFaction = (SystemFaction) faction;
                if (systemFaction.isDeathban()) {
                    if (mainConfig.getString("FACTION_MAP.PILLAR.SYSTEM_FACTION.DEATHBAN").equalsIgnoreCase("RANDOM")) {
                        material = Claim.getMapBlocks().get(new Random().nextInt(Claim.getMapBlocks().size()));
                    } else {
                        material = Material.valueOf(mainConfig.getString("FACTION_MAP.PILLAR.SYSTEM_FACTION.DEATHBAN"));
                    }
                } else {
                    if (mainConfig.getString("FACTION_MAP.PILLAR.SYSTEM_FACTION.NON-DEATHBAN").equalsIgnoreCase("RANDOM")) {
                        material = Claim.getMapBlocks().get(new Random().nextInt(Claim.getMapBlocks().size()));
                    } else {
                        material = Material.valueOf(mainConfig.getString("FACTION_MAP.PILLAR.SYSTEM_FACTION.NON-DEATHBAN"));
                    }
                }
            } else {
                if (mainConfig.getString("FACTION_MAP.PILLAR.ENEMY").equalsIgnoreCase("RANDOM")) {
                    material = Claim.getMapBlocks().get(new Random().nextInt(Claim.getMapBlocks().size()));
                } else {
                    material = Material.valueOf(mainConfig.getString("FACTION_MAP.PILLAR.ENEMY"));
                }
            }
            if (!(shown.containsKey(faction))) {
                shown.put(claim.getFaction(), material);

                for (Location corner : claim.getCorners()) {
                    profile.getMapPillars().add(new ClaimPillar(player, corner).show(material, 0));
                }

                String name = material.name().toLowerCase();
                name = name.replace("_", " ");
                String[] segments = name.split(" ");
                name = "";
                for (String segment : segments) {
                    segment = segment.substring(0, 1).toUpperCase() + segment.substring(1, segment.length());
                    if (name.equals("")) {
                        name = segment;
                    } else {
                        name = name + " " + segment;
                    }
                }

                if (faction instanceof PlayerFaction) {
                    if (profile.getFaction() == faction) {
                        player.sendMessage(langConfig.getString("FACTION_MAP.DISPLAY.FRIENDLY").replace("%BLOCK%", name).replace("%FACTION%", faction.getName()));
                    } else if (profile.getFaction() != null && profile.getFaction().getAllies().contains(faction)) {
                        player.sendMessage(langConfig.getString("FACTION_MAP.DISPLAY.ALLY").replace("%BLOCK%", name).replace("%FACTION%", faction.getName()));
                    } else {
                        player.sendMessage(langConfig.getString("FACTION_MAP.DISPLAY.ENEMY").replace("%BLOCK%", name).replace("%FACTION%", faction.getName()));
                    }
                } else {
                    player.sendMessage(langConfig.getString("FACTION_MAP.DISPLAY.SYSTEM_FACTION").replace("%BLOCK%", name).replace("%FACTION%", faction.getName()).replace("%COLOR%", ((SystemFaction)faction).getColor() + ""));
                }
            } else {
                for (Location corner : claim.getCorners()) {
                    profile.getMapPillars().add(new ClaimPillar(player, corner).show(shown.get(claim.getFaction()), 0));
                }
            }
        }

        profile.setViewingMap(true);
    }
}
