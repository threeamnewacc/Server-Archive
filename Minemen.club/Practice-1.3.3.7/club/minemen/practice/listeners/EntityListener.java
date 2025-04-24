// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.listeners;

import java.util.Iterator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.entity.PotionSplashEvent;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.entity.Arrow;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import club.minemen.practice.match.Match;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.match.MatchState;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import club.minemen.practice.Practice;
import org.bukkit.event.Listener;

public class EntityListener implements Listener
{
    private final Practice plugin;
    
    public EntityListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            switch (playerData.getPlayerState()) {
                case FIGHTING: {
                    final Match match = this.plugin.getMatchManager().getMatch(playerData);
                    if (match.getMatchState() != MatchState.FIGHTING) {
                        e.setCancelled(true);
                    }
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                        break;
                    }
                    break;
                }
                default: {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
                    }
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        final Player entity = (Player)e.getEntity();
        Player damager;
        if (e.getDamager() instanceof Player) {
            damager = (Player)e.getDamager();
        }
        else {
            if (!(e.getDamager() instanceof Projectile)) {
                return;
            }
            damager = (Player)((Projectile)e.getDamager()).getShooter();
        }
        final PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
        final PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());
        if (damagerData.getPlayerState() != PlayerState.FIGHTING || entityData.getPlayerState() != PlayerState.FIGHTING) {
            e.setCancelled(true);
            return;
        }
        final Match match = this.plugin.getMatchManager().getMatch(entityData);
        if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
            e.setCancelled(true);
            return;
        }
        if (match.getKit().isSpleef() || match.getKit().isSumo()) {
            e.setDamage(0.0);
        }
        if (e.getDamager() instanceof Player) {
            damagerData.setCombo(damagerData.getCombo() + 1);
            damagerData.setHits(damagerData.getHits() + 1);
            if (damagerData.getCombo() > damagerData.getLongestCombo()) {
                damagerData.setLongestCombo(damagerData.getCombo());
            }
            entityData.setCombo(0);
            if (match.getKit().isSpleef()) {
                e.setCancelled(true);
            }
        }
        else if (e.getDamager() instanceof Arrow) {
            final Arrow arrow = (Arrow)e.getDamager();
            if (arrow.getShooter() instanceof Player) {
                final Player shooter = (Player)arrow.getShooter();
                if (!entity.getName().equals(shooter.getName())) {
                    final double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / 2.0;
                    if (health > 0.0) {
                        shooter.sendMessage(CC.SECONDARY + entity.getName() + CC.PRIMARY + " is now at " + CC.SECONDARY + health + "\u2764" + CC.PRIMARY + ".");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPotionSplash(final PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        for (final PotionEffect effect : e.getEntity().getEffects()) {
            if (effect.getType().equals((Object)PotionEffectType.HEAL)) {
                final Player shooter = (Player)e.getEntity().getShooter();
                if (e.getIntensity((LivingEntity)shooter) <= 0.5) {
                    final PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
                    if (shooterData != null) {
                        shooterData.setMissedPots(shooterData.getMissedPots() + 1);
                    }
                    break;
                }
                break;
            }
        }
    }
}
