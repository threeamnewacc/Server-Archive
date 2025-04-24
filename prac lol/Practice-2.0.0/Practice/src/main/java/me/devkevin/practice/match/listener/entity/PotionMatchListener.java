package me.devkevin.practice.match.listener.entity;

import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Copyright 15/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PotionMatchListener implements Listener {

    private final Practice plugin = Practice.getInstance();


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        if (profile == null) return;

        Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

        if (match == null) return;

        if (match.isFighting()) {
            for (PotionEffect effect : event.getEntity().getEffects()) {
                if (!effect.getType().equals(PotionEffectType.INVISIBILITY)) {
                    Player shooter = (Player) event.getEntity().getShooter();
                    if (shooter == null) {
                        return;
                    }

                    Profile shooterData = this.plugin.getProfileManager().getProfileData(shooter.getUniqueId());
                    shooterData.setPotionsThrown(shooterData.getPotionsThrown() + 1);

                    /*if (event.getIntensity(shooter) <= 0.5D) {
                        shooterData.setPotionsMissed(shooterData.getPotionsMissed() + 1);
                    }*/

                    break;
                }
            }
            for (PotionEffect effect : event.getEntity().getEffects()) {
                if (effect.getType().equals(PotionEffectType.HEAL)) {
                    profile.getPotions().add(event.getPotion().getLocation());
                    if (event.getIntensity(player) <= 0.5D) {
                        profile.incrementPotionsMissed();
                    }
                }
            }
        }
    }
}