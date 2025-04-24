package me.devkevin.practice.match.listener.entity;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.profile.Profile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * Copyright 03/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class EntityListener implements Listener {

    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity().hasMetadata("NPC")) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (player == null) {
                return;
            }

            Profile practicePlayerData = plugin.getProfileManager().getProfileData(player.getUniqueId());
            switch (practicePlayerData.getState()) {
                case FIGHTING:
                    Match match = plugin.getMatchManager().getMatch(practicePlayerData);
                    if (match.getMatchState() != MatchState.FIGHTING) {
                        e.setCancelled(true);
                    }
                    if ((match.getKit().isSumo() && e.getCause() == EntityDamageEvent.DamageCause.FALL) || (player.getFallDistance() >= 50 && e.getCause() == EntityDamageEvent.DamageCause.FALL) || match.getKit().isBoxing() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        e.setCancelled(true);
                    }
                    break;
                case SPECTATING:
                    if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                        player.setFireTicks(0);
                        e.setCancelled(true);
                        return;
                    }

                    Match spectatedMatch = plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
                    Location locationA = spectatedMatch.getStandaloneArena() != null ? spectatedMatch.getStandaloneArena().getA().toBukkitLocation() : spectatedMatch.getArena().getA().toBukkitLocation();

                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        player.setHealth(player.getMaxHealth());
                        if (locationA != null) {
                            player.teleport(locationA);
                        } else {
                            player.teleport(plugin.getCustomLocationManager().getSpawn().toBukkitLocation());
                        }
                    }
                    e.setCancelled(true);
                    break;
                default:
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        e.getEntity().teleport(plugin.getCustomLocationManager().getSpawn().toBukkitLocation());
                    }
                    e.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player entity = (Player) event.getEntity();
        Player damager;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow && ((Projectile) event.getDamager()).getShooter() != ((Player) event.getEntity()).getPlayer() && event.getDamager() != null) {
            damager = (Player) ((Projectile) event.getDamager()).getShooter();
        } else {
            return;
        }

        Profile entityData = plugin.getProfileManager().getProfileData(entity.getUniqueId());
        Profile damagerData = plugin.getProfileManager().getProfileData(damager.getUniqueId());

        if (!entity.canSee(damager) && damager.canSee(entity)) {
            event.setCancelled(true);
            return;
        }

        if (entityData == null || damagerData == null) {
            event.setCancelled(true);
            return;
        }

        Match match = plugin.getMatchManager().getMatch(entityData);
        if (match == null) {
            event.setDamage(0.0D);
            return;
        }
        if (match.getMatchState() != MatchState.FIGHTING) {
            return;
        }
        if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
            event.setCancelled(true);
            return;
        }
        if (match.getKit().isSpleef() || match.getKit().isSumo() || match.getKit().isBoxing()) {
            event.setDamage(0.0D);
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            damagerData.setCombo(damagerData.getCombo() + 1);
            damagerData.setHits(damagerData.getHits() + 1);
            if (damagerData.getCombo() > damagerData.getLongestCombo()) {
                damagerData.setLongestCombo(damagerData.getCombo());
            }
            entityData.setCombo(0);

            if (isCritical(damager)) {
                damagerData.incrementCriticalHits();
            }

            if (entity.isBlocking()) {
                entityData.incrementBlockedHits();
            }

            if (match.getKit().isSpleef()) {
                event.setCancelled(true);
            }
            if (match.getKit().isBoxing()) {
                if (damagerData.getHits() >= 100) {
                    plugin.getMatchManager().removeFighter(entity, entityData, true);
                }
            }
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(shooter.getUniqueId());

                if (!entity.getName().equals(shooter.getName())) {
                    double health = Math.ceil(entity.getHealth() - event.getFinalDamage()) / 2.0D;

                    if (health > 0.0D) {
                        shooter.sendMessage(coreProfile.getGrant().getRank().getColor() + entity.getDisplayName() + CC.GOLD + " has been shot " + CC.GRAY + " (" + CC.RED + health + "\u2764" + CC.GRAY + ")");
                    }
                }
            }
        }
    }

    private boolean isCritical(Player attacker) {
        return attacker.getFallDistance() > 0.0F &&
                !attacker.isOnGround() &&
                !attacker.isInsideVehicle() &&
                !attacker.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                attacker.getLocation().getBlock().getType() != Material.LADDER &&
                attacker.getLocation().getBlock().getType() != Material.VINE;
    }
}
