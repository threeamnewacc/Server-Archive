package net.badlion.combattag.listeners;

import net.badlion.banmanager.BanManager;
import net.badlion.banmanager.events.PunishedPlayerEvent;
import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.combattag.events.CombatTagCreateEvent;
import net.badlion.combattag.events.CombatTagDamageEvent;
import net.badlion.combattag.events.CombatTagJoinEvent;
import net.badlion.combattag.events.CombatTagKilledEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTagListener implements Listener {

    public static Map<Chunk, Integer> chunksWithTagsInThem = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByPlayerEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamage() != 0D) {
            if (event.getDamager() instanceof Player) {
                CombatTagPlugin.getInstance().addCombatTagged(((Player) event.getEntity()));

                if (CombatTagPlugin.getInstance().isTagDamager()) {
                    CombatTagPlugin.getInstance().addCombatTagged(((Player) event.getDamager()));
                }
            } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
                CombatTagPlugin.getInstance().addCombatTagged(((Player) event.getEntity()));

                if (CombatTagPlugin.getInstance().isTagDamager()) {
                    CombatTagPlugin.getInstance().addCombatTagged(((Player) ((Arrow) event.getDamager()).getShooter()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        CombatTagPlugin.getInstance().removeCombatTagged(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerBannedEvent(PunishedPlayerEvent event) {
        if (event.getPunishmentType() == BanManager.PUNISHMENT_TYPE.BAN) {
            LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(event.getUuid());
            if (loggerNPC != null) {
                loggerNPC.remove(LoggerNPC.REMOVE_REASON.DEATH);
            }
        }
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        if (CombatTagListener.chunksWithTagsInThem.containsKey(event.getChunk())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

	    LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(uuid);

	    if (loggerNPC != null) {
		    CombatTagJoinEvent combatTagJoinEvent = new CombatTagJoinEvent(loggerNPC);
		    CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(combatTagJoinEvent);

		    // Always update their health
		    event.getPlayer().setHealth(loggerNPC.getEntity().getHealth());

		    // Copy over potion effects
		    for (PotionEffect potionEffect : loggerNPC.getEntity().getActivePotionEffects()) {
			    if (!potionEffect.getType().equals(PotionEffectType.SLOW) && !potionEffect.getType().equals(PotionEffectType.JUMP)) {
				    event.getPlayer().addPotionEffect(potionEffect, true);
			    }
		    }

		    // Teleport player to LoggerNPC location
		    event.getPlayer().teleport(loggerNPC.getEntity().getLocation());

		    loggerNPC.remove(LoggerNPC.REMOVE_REASON.REJOIN);
	    }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // Are they combat tagged?
        if (CombatTagPlugin.getInstance().isInCombat(uuid) && !event.getPlayer().isDead() && event.getPlayer().getHealth() > 0.0) {
            CombatTagCreateEvent combatTagCreateEvent = new CombatTagCreateEvent(event.getPlayer());
            CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(combatTagCreateEvent);

	        if (!combatTagCreateEvent.isCancelled()) {
		        // Make a default logger
		        if (combatTagCreateEvent.getLoggerNPC() == null) {
			        new LoggerNPC(event.getPlayer());
		        }
	        }
        }
    }

    @EventHandler
    public void onPlayerHurtCombatLoggerEvent(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entityDamageEvent;

            // Is it a combat log npc?
            if (event.getEntity() instanceof Zombie) {
                Player damager = null;
                if (event.getDamager() instanceof Player) {
                    damager = ((Player) event.getDamager());
                } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
                    damager = ((Player) ((Arrow) event.getDamager()).getShooter());
                } else if (event.getDamager() instanceof ThrownPotion && ((ThrownPotion) event.getDamager()).getShooter() instanceof Player) {
                    // allow health potions to heal loggers - usually health potions DAMAGE zombies
                    event.setCancelled(true);
                    LivingEntity livingEntity = (LivingEntity) event.getEntity();
                    livingEntity.setHealth(Math.min(livingEntity.getHealth() + event.getDamage(), livingEntity.getMaxHealth()));
                }

                if (damager == null) return;

                if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
                    LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getCombatLoggerFromEntity(event.getEntity());
                    CombatTagDamageEvent combatTagDamageEvent = new CombatTagDamageEvent(loggerNPC, damager, event.getDamage(), event.getFinalDamage());
                    CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(combatTagDamageEvent);
                    if (combatTagDamageEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (entityDamageEvent.getEntity() instanceof Zombie && entityDamageEvent.getEntity().hasMetadata("CombatLoggerNPC")) {
                LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger((UUID) entityDamageEvent.getEntity().getMetadata("CombatLoggerNPC").get(0).value());
                CombatTagDamageEvent combatTagDamageEvent = new CombatTagDamageEvent(loggerNPC, null, entityDamageEvent.getDamage(), entityDamageEvent.getFinalDamage());
                CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(combatTagDamageEvent);
                if (combatTagDamageEvent.isCancelled()) {
                    entityDamageEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent event) {
        // Is it a combat log npc?
        if (event.getEntity() instanceof Zombie) {
            if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
                LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger((UUID) event.getEntity().getMetadata("CombatLoggerNPC").get(0).value());

	            CombatTagKilledEvent combatTagKilledEvent = new CombatTagKilledEvent(loggerNPC);
                CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(combatTagKilledEvent);

                // Get rid of regular drops
                // TODO: Store a player's EXP levels and calculate what it should drop
                event.setDroppedExp(0);
                event.getDrops().clear();

                loggerNPC.remove(LoggerNPC.REMOVE_REASON.DEATH);
            }
        }
    }

    @EventHandler
    public void entityCombustEvent(EntityCombustEvent event) {
        // Is it a combat log npc?
        if (event.getEntity() instanceof Zombie) {
            if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        // Is it a combat log npc?
        if (event.getEntity() instanceof Zombie) {
            if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
                event.setCancelled(true);
            }
        }
    }

}
