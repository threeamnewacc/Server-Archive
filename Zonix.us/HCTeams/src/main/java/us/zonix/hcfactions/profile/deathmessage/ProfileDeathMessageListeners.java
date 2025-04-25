package us.zonix.hcfactions.profile.deathmessage;

import us.zonix.hcfactions.util.ItemNames;
import us.zonix.hcfactions.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.ItemNames;

import java.util.AbstractMap;

public class ProfileDeathMessageListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByPlayer(player);
            LivingEntity damager;

            if (event.getDamager() instanceof LivingEntity) {
                damager = (LivingEntity) event.getDamager();
            }
            else if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() != null) {
                    damager = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                }
                else {
                    damager = null;
                }
            }
            else {
                damager = null;
            }

            if (damager == null) {
                return;
            }

            if (profile.isCombatLogged()) {
                return;
            }

            if (!(damager instanceof Player) && player.getHealth() - event.getFinalDamage() <= 0) {
                new ProfileDeathMessage(ProfileDeathMessageTemplate.MOB, profile, damager.getType().getName());
                return;
            }

            if (damager instanceof Player) {
                profile.setLastDamager(new AbstractMap.SimpleEntry<>(damager.getUniqueId(), ((Player) damager).getItemInHand()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByPlayer(player);
        Player killer = player.getKiller();
        EntityDamageEvent.DamageCause cause;

        event.setDeathMessage(null);

        if (profile.isCombatLogged()) {
            profile.setCombatLogged(false);
            return;
        }

        if (player.getLastDamageCause() == null) {
            cause = EntityDamageEvent.DamageCause.CUSTOM;
        }
        else {
            cause = player.getLastDamageCause().getCause();
        }

        if (killer == null) {
            if (cause == EntityDamageEvent.DamageCause.PROJECTILE || cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                return;
            }

            ProfileDeathMessageTemplate template;

            try {
               template = ProfileDeathMessageTemplate.valueOf(cause.name());
            }
            catch (Exception exception) {
                return;
            }

            if(!profile.isRespawning()) {
                new ProfileDeathMessage(template, profile);
            }

        } else {
            Profile killerProfile = Profile.getByPlayer(killer);
            String weapon = "their fists";

            if (cause != null) {
                if (profile.getLastDamager().getKey().equals(killer.getUniqueId())) {
                    ItemStack item = profile.getLastDamager().getValue();

                    if (item != null) {
                        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                            weapon = item.getItemMeta().getDisplayName();
                        }
                        else {
                            weapon = ItemNames.lookup(item);
                        }
                    }
                }
                else {
                    if (killer.getItemInHand() != null && killer.getItemInHand().getType() != Material.AIR) {
                        if (killer.getItemInHand().hasItemMeta() && killer.getItemInHand().getItemMeta().hasDisplayName()) {
                            weapon = killer.getItemInHand().getItemMeta().getDisplayName();
                        }
                        else {
                            weapon = ItemNames.lookup(killer.getItemInHand());
                        }
                    }
                }
            }

            player.setLastDamageCause(null);

            new ProfileDeathMessage(ProfileDeathMessageTemplate.PLAYER, profile, killerProfile, weapon);
        }

    }

}
