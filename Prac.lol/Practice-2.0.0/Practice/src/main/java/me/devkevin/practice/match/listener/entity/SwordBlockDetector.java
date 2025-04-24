package me.devkevin.practice.match.listener.entity;

import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 03/02/2023 @ 14:42
 * SwordBlockDetector / me.devkevin.practice.match.listener.entity / Practice
 */
public class SwordBlockDetector implements Listener {
    private int counter = 0;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player defender = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            Profile defenderProfile = Practice.getInstance().getProfileManager().getProfileData(defender.getUniqueId());
            Profile attackerProfile = Practice.getInstance().getProfileManager().getProfileData(attacker.getUniqueId());


            if (attackerProfile.isFighting() && defenderProfile.isFighting()) {
                if (defender.isBlocking() && defender.getInventory().getItemInHand().getType().toString().contains("SWORD")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (defender.isBlocking() && counter >= 20) {
                                defender.setHealth(defender.getHealth() - 1.0);
                                cancel();
                            } else if (defender.isBlocking()) {
                                counter++;
                            }
                        }
                    }.runTaskAsynchronously(Practice.getInstance());
                }
            }
        }
    }
}
