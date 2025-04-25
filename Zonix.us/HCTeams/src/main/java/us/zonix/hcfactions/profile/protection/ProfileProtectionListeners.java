package us.zonix.hcfactions.profile.protection;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldown;
import org.bukkit.event.EventPriority;
import us.zonix.hcfactions.mode.Mode;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownType;

public class ProfileProtectionListeners implements Listener {

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isRespawning() && player.isDead()) {
            player.spigot().respawn();
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Profile profile = Profile.getByPlayer(event.getEntity());
        profile.setProtection(null);
        profile.setRespawning(true);
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isRespawning() && profile.getDeathban() == null) {
            if (profile.getProtection() == null) {
                if (!main.isKitmapMode()) {
                    profile.setProtection(new ProfileProtection(ProfileProtection.DEFAULT_DURATION));
                    profile.getProtection().pause();
                }

                profile.setLeftSpawn(false);
            }

            profile.setRespawning(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        for (Mode mode : Mode.getModes()) {
            if (mode.isSOTWActive()) {
                return;
            }
        }

        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager;

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            else if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                }
                else {
                    damager = null;
                }
            }
            else {
                damager = null;
            }

            if (damager != null && !damaged.getName().equals(damager.getName())) {
                Profile profile = Profile.getByPlayer(damager);
                Profile damagedProfile = Profile.getByPlayer(damaged);

                if (profile.getProtection() != null) {
                    damager.sendMessage(main.getLanguageConfig().getString("PVP_PROTECTION.HAVE_SELF").replace("%TIME%", profile.getProtection().getTimeLeft()));
                    event.setCancelled(true);
                    return;
                }

                if (damagedProfile.getProtection() != null) {
                    damager.sendMessage(main.getLanguageConfig().getString("PVP_PROTECTION.HAVE_OTHER").replace("%PLAYER%", damaged.getName()));
                    event.setCancelled(true);
                    return;
                }

                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG);

                if (cooldown != null) {
                    profile.getCooldowns().remove(cooldown);
                }

                profile.getCooldowns().add(new ProfileCooldown(ProfileCooldownType.SPAWN_TAG, ProfileCooldownType.SPAWN_TAG.getDuration()));

                cooldown = damagedProfile.getCooldownByType(ProfileCooldownType.SPAWN_TAG);

                if (cooldown != null) {
                    damagedProfile.getCooldowns().remove(cooldown);
                }

                damagedProfile.getCooldowns().add(new ProfileCooldown(ProfileCooldownType.SPAWN_TAG, ProfileCooldownType.SPAWN_TAG.getDuration()));
            }
        }
    }

    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Profile profile = Profile.getByPlayer(player);

            if (profile.getProtection() != null) {
                for (LivingEntity affected : event.getAffectedEntities()) {
                    if (affected instanceof Player) {
                        event.setIntensity(affected, 0);
                    }
                }
            }

            for (LivingEntity affected : event.getAffectedEntities()) {
                if (affected instanceof Player) {
                    Player affectedPlayer = (Player) affected;

                    if (affectedPlayer.equals(player)) {
                        continue;
                    }

                    Profile affectedProfile = Profile.getByPlayer(affectedPlayer);

                    if (affectedProfile.getProtection() != null) {
                        event.setIntensity(affectedPlayer, 0);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && (event.getCause().name().contains("FIRE") || event.getCause().name().contains("LAVA"))) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByPlayer(player);

            if (profile.getProtection() != null) {
                event.setCancelled(true);
            }
        }
    }

}
