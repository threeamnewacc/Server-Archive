package me.devkevin.practice.match.listener.game;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.event.impl.MatchStartEvent;
import me.devkevin.practice.match.task.MatchLogicTask;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 02/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchStartListener implements Listener {

    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Kit kit = match.getKit();

        if (!kit.isEnabled()) {
            match.broadcastMessage(CC.RED + "This kit is currently disabled.");
            this.plugin.getMatchManager().removeMatch(match);
            return;
        }

        if (kit.isBuild() || kit.isSpleef()) {
            if (match.getArena().getAvailableArenas().size() > 0) {
                match.setStandaloneArena(match.getArena().getAvailableArena());
                plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
            } else {
                match.broadcastMessage(CC.RED + "There are no arenas available at this moment.");
                plugin.getMatchManager().removeMatch(match);
                return;
            }
        }

        Set<Player> matchPlayers = new HashSet<>();
        match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
            matchPlayers.add(player);

            this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());

            Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
            /*profile.getCachedPlayer().clear();
            profile.setFollowingId(null);
            profile.setFollowing(false);*/

            player.setAllowFlight(false);
            player.setFlying(false);

            profile.setCurrentMatchID(match.getMatchId());
            profile.setTeamID(team.getTeamID());

            profile.setPotionsMissed(0);
            profile.setPotionsThrown(0);
            profile.setLongestCombo(0);
            profile.setCombo(0);
            profile.setHits(0);

            PlayerUtil.reset(player);

            CustomLocation locationA = match.getStandaloneArena() != null ? match.getStandaloneArena().getA() : match.getArena().getA();
            CustomLocation locationB = match.getStandaloneArena() != null ? match.getStandaloneArena().getB() : match.getArena().getB();

            if (player.getWorld().isChunkLoaded(player.getLocation().getChunk())) {
                player.getWorld().refreshChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
            }

            /*// As we've added a new entire bundle pack of practice arenas and im so fucking lazy that i won't separate it in different chunks and alone positions
            // so what I do here is to set the center of the visible area at the midpoint between the minimum and maximum limits in X and Z, and the size of the
            // visible area as the distance between the maximum limit and the minimum limit in the longest direction.
            //
            // Any chunks outside the set limits will not be loaded for the player, which means that the player will only see the blocks and entities within the limited area.
            int minX = match.getStandaloneArena() != null ? match.getStandaloneArena().getMin().toBukkitLocation().getBlockX() : match.getArena().getMin().toBukkitLocation().getBlockX();
            int minZ = match.getStandaloneArena() != null ? match.getStandaloneArena().getMin().toBukkitLocation().getBlockZ() : match.getArena().getMin().toBukkitLocation().getBlockZ();

            int maxX = match.getStandaloneArena() != null ? match.getStandaloneArena().getMax().toBukkitLocation().getBlockX() : match.getArena().getMax().toBukkitLocation().getBlockX();
            int maxZ = match.getStandaloneArena() != null ? match.getStandaloneArena().getMax().toBukkitLocation().getBlockZ() : match.getArena().getMax().toBukkitLocation().getBlockZ();

            WorldBorder worldBorder = player.getWorld().getWorldBorder();

            worldBorder.setCenter((minX + maxX) / 2.0, (minZ + maxZ) / 2.0);
            worldBorder.setSize(Math.max(maxX - minX, maxZ - minZ));*/

            player.teleport(team.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation());

            if (kit.isBoxing()) {

                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

                if (coreProfile.hasDonor()) {
                    player.getInventory().setContents(this.plugin.getHotbarItem().getBoxingSwordDonor());
                } else {
                    player.getInventory().setContents(this.plugin.getHotbarItem().getBoxingSword());
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

                player.sendMessage(CC.YELLOW + "First to 100 hits wins the match!");
            }

            if (kit.isCombo()) {
                player.setMaximumNoDamageTicks(3);
            }

            if (kit.getName().equalsIgnoreCase("Axe")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            }

            if (kit.getName().equalsIgnoreCase("Strategy")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
            }

            if (kit.getName().equalsIgnoreCase("Bard") && kit.getName().equalsIgnoreCase("ArcherHCF")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
            }

            plugin.getMatchManager().giveKits(player, kit);

            profile.setState(ProfileState.FIGHTING);
        }));

        for (Player player : matchPlayers) {
            for (Player online : this.plugin.getServer().getOnlinePlayers()) {
                online.hidePlayer(player);
                player.hidePlayer(online);
            }
        }

        for (Player player : matchPlayers) {
            for (Player other : matchPlayers) {
                player.showPlayer(other);
            }
        }

        new MatchLogicTask(match).runTaskTimer(this.plugin, 20L, 20L);
    }
}
