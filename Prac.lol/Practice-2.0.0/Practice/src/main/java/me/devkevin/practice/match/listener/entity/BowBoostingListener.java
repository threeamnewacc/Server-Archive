package me.devkevin.practice.match.listener.entity;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/02/2023 @ 2:04
 * BowBoostingListener / me.devkevin.practice.match.listener.entity / Practice
 */
public class BowBoostingListener implements Listener {

    @EventHandler
    public void onEntityShoot(EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        Vector direction = entity.getLocation().getDirection();
        Arrow arrow = (Arrow) event.getProjectile();

        double speed = arrow.getVelocity().length();
        Vector velocity = direction.multiply(speed);
        arrow.setVelocity(velocity);
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Arrow arrow;
        Entity damager;
        Player player = event.getPlayer();
        Vector velocity = event.getVelocity();

        EntityDamageEvent eventDamage = player.getLastDamageCause();

        if (eventDamage != null && !eventDamage.isCancelled() && eventDamage instanceof EntityDamageByEntityEvent &&
                (damager = ((EntityDamageByEntityEvent) eventDamage).getDamager()) instanceof Arrow &&
                (arrow = (Arrow) damager).getShooter().equals(player)) {

            double speed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
            Vector dir = arrow.getLocation().getDirection().normalize();
            double xVelocity = 1.2;
            double zVelocity = 1.2;
            Vector newVelocity = new Vector((dir.getX() * speed * -1.0) * xVelocity, velocity.getY(), dir.getZ() * speed * zVelocity);

            event.setVelocity(newVelocity);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player entity = (Player) event.getEntity();
            Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

            if (entity == damager) {
                event.setDamage(0);
            }
        }
    }
}
