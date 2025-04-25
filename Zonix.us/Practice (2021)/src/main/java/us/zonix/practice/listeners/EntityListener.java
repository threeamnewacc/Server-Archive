package us.zonix.practice.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.lights.LightsEvent;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.oitc.OITCPlayer;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.redrover.RedroverEvent;
import us.zonix.practice.events.redrover.RedroverPlayer;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.events.sumo.SumoPlayer;
import us.zonix.practice.events.tnttag.TNTTagEvent;
import us.zonix.practice.events.tnttag.TNTTagPlayer;
import us.zonix.practice.events.waterdrop.WaterDropEvent;
import us.zonix.practice.events.woolmixup.WoolMixUpEvent;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchState;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class EntityListener implements Listener {
    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onCreateSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player)e.getEntity();
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            switch (playerData.getPlayerState()) {
                case FIGHTING:
                    Match match = this.plugin.getMatchManager().getMatch(playerData);
                    if (match.getMatchState() != MatchState.FIGHTING) {
                        e.setCancelled(true);
                    }

                    if (e.getCause() == DamageCause.VOID) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                    }

                    if (match.getKit().isParkour()) {
                        e.setCancelled(true);
                    }
                    break;
                case EVENT:
                    PracticeEvent<?> event = this.plugin.getEventManager().getEventPlaying(player);
                    if (event != null) {
                        if (event.getState() == EventState.WAITING) {
                            e.setCancelled(true);
                            if (e.getCause() == DamageCause.VOID) {
                                CustomLocation location = event.getSpawnLocations().get(0);
                                if (location != null) {
                                    player.teleport(location.toBukkitLocation());
                                }
                            }
                        } else if (event instanceof SumoEvent) {
                            SumoEvent sumoEvent = (SumoEvent)event;
                            SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
                            if (e.getCause() == DamageCause.FALL) {
                                e.setCancelled(true);
                            } else if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayer.SumoState.FIGHTING) {
                                e.setCancelled(false);
                            }
                        } else if (event instanceof OITCEvent) {
                            OITCEvent oitcEvent = (OITCEvent)event;
                            OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
                            if (oitcPlayer != null && oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING && e.getCause() != DamageCause.FALL) {
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                            }
                        } else if (event instanceof ParkourEvent) {
                            e.setCancelled(true);
                        } else if (event instanceof WaterDropEvent) {
                            if (e.getCause() == DamageCause.FALL) {
                                WaterDropEvent waterDropEvent = (WaterDropEvent)event;
                                waterDropEvent.onDeath().accept(player);
                            } else if (e.getCause() == DamageCause.VOID) {
                                WaterDropEvent waterDropEvent = (WaterDropEvent)event;
                                waterDropEvent.teleportToSpawn(player);
                            }

                            e.setCancelled(true);
                        } else if (event instanceof WoolMixUpEvent) {
                            if (e.getCause() == DamageCause.VOID) {
                                WoolMixUpEvent woolMixUpEvent = (WoolMixUpEvent)event;
                                woolMixUpEvent.onDeath().accept(player);
                            }

                            if (e.getCause() == DamageCause.FALL) {
                                WoolMixUpEvent woolMixUpEvent = (WoolMixUpEvent)event;
                                woolMixUpEvent.onDeath().accept(player);
                            }

                            e.setCancelled(true);
                        } else if (event instanceof TNTTagEvent && e.getCause() == DamageCause.FALL) {
                            e.setCancelled(true);
                        }
                    }
                    break;
                case FFA:
                    e.setCancelled(false);
                    break;
                default:
                    if (e.getCause() == DamageCause.VOID
                        && (playerData.getPlayerState() != PlayerState.FFA || playerData.getPlayerState() != PlayerState.EVENT)) {
                        e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
                    }

                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player entity = (Player)e.getEntity();
            Player damager = this.isPlayerDamager(e);
            if (damager != null) {
                PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
                PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());
                if (entityData != null && damagerData != null) {
                    boolean isEventEntity = this.plugin.getEventManager().getEventPlaying(entity) != null;
                    boolean isEventDamager = this.plugin.getEventManager().getEventPlaying(damager) != null;
                    PracticeEvent eventDamager = this.plugin.getEventManager().getEventPlaying(damager);
                    PracticeEvent eventEntity = this.plugin.getEventManager().getEventPlaying(entity);
                    if (damagerData.getPlayerState() == PlayerState.SPECTATING
                        || this.plugin.getEventManager().getSpectators().containsKey(damager.getUniqueId())) {
                        e.setCancelled(true);
                    } else if (damagerData.getPlayerState() == PlayerState.FFA && entityData.getPlayerState() == PlayerState.FFA) {
                        e.setCancelled(false);
                    } else if ((entity.canSee(damager) || !damager.canSee(entity)) && damager.getGameMode() != GameMode.SPECTATOR) {
                        if ((!isEventDamager || !(eventDamager instanceof ParkourEvent)) && (!isEventEntity || !(eventEntity instanceof ParkourEvent))) {
                            if ((!isEventDamager || !(eventDamager instanceof WaterDropEvent)) && (!isEventEntity || !(eventEntity instanceof WaterDropEvent))) {
                                if ((!isEventDamager || !(eventDamager instanceof WoolMixUpEvent))
                                    && (!isEventEntity || !(eventEntity instanceof WoolMixUpEvent))) {
                                    if ((!isEventDamager || !(eventDamager instanceof LightsEvent))
                                        && (!isEventEntity || !(eventEntity instanceof LightsEvent))) {
                                        if ((!isEventDamager || !(eventDamager instanceof TNTTagEvent))
                                            && (!isEventEntity || !(eventEntity instanceof TNTTagEvent))) {
                                            if ((
                                                    !isEventDamager
                                                        || !(eventDamager instanceof RedroverEvent)
                                                        || ((RedroverEvent)eventDamager).getPlayer(damager).getState() == RedroverPlayer.RedroverState.FIGHTING
                                                )
                                                && (
                                                    !isEventEntity
                                                        || !(eventDamager instanceof RedroverEvent)
                                                        || ((RedroverEvent)eventEntity).getPlayer(entity).getState() == RedroverPlayer.RedroverState.FIGHTING
                                                )
                                                && (isEventDamager || damagerData.getPlayerState() == PlayerState.FIGHTING)
                                                && (isEventEntity || entityData.getPlayerState() == PlayerState.FIGHTING)) {
                                                if ((
                                                        !isEventDamager
                                                            || !(eventDamager instanceof SumoEvent)
                                                            || ((SumoEvent)eventDamager).getPlayer(damager).getState() == SumoPlayer.SumoState.FIGHTING
                                                    )
                                                    && (
                                                        !isEventEntity
                                                            || !(eventDamager instanceof SumoEvent)
                                                            || ((SumoEvent)eventEntity).getPlayer(entity).getState() == SumoPlayer.SumoState.FIGHTING
                                                    )
                                                    && (isEventDamager || damagerData.getPlayerState() == PlayerState.FIGHTING)
                                                    && (isEventEntity || entityData.getPlayerState() == PlayerState.FIGHTING)) {
                                                    if ((!isEventDamager || !(eventDamager instanceof OITCEvent))
                                                        && (!isEventEntity || !(eventEntity instanceof OITCEvent))
                                                        && (isEventDamager || damagerData.getPlayerState() == PlayerState.FIGHTING)
                                                        && (isEventEntity || entityData.getPlayerState() == PlayerState.FIGHTING)) {
                                                        if ((entityData.getPlayerState() != PlayerState.EVENT || !(eventEntity instanceof SumoEvent))
                                                            && (damagerData.getPlayerState() != PlayerState.EVENT || !(eventDamager instanceof SumoEvent))) {
                                                            if ((entityData.getPlayerState() != PlayerState.EVENT || !(eventEntity instanceof RedroverEvent))
                                                                && (
                                                                    damagerData.getPlayerState() != PlayerState.EVENT
                                                                        || !(eventDamager instanceof RedroverEvent)
                                                                )) {
                                                                Match match = this.plugin.getMatchManager().getMatch(entityData);
                                                                if (match == null) {
                                                                    e.setDamage(0.0);
                                                                } else if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
                                                                    e.setCancelled(true);
                                                                } else if (match.getKit().isParkour()) {
                                                                    e.setCancelled(true);
                                                                } else {
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
                                                                    } else if (e.getDamager() instanceof Arrow) {
                                                                        Arrow arrow = (Arrow)e.getDamager();
                                                                        if (arrow.getShooter() instanceof Player) {
                                                                            Player shooter = (Player)arrow.getShooter();
                                                                            if (!entity.getName().equals(shooter.getName())) {
                                                                                double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / 2.0;
                                                                                if (health > 0.0) {
                                                                                    shooter.sendMessage(
                                                                                        ChatColor.YELLOW
                                                                                            + "[*] "
                                                                                            + ChatColor.GREEN
                                                                                            + entity.getName()
                                                                                            + " has been shot."
                                                                                            + ChatColor.DARK_GRAY
                                                                                            + " ("
                                                                                            + ChatColor.RED
                                                                                            + health
                                                                                            + "❤"
                                                                                            + ChatColor.DARK_GRAY
                                                                                            + ")"
                                                                                    );
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            e.setDamage(0.0);
                                                        }
                                                    } else {
                                                        if (isEventEntity
                                                            && isEventDamager
                                                            && eventEntity instanceof OITCEvent
                                                            && eventDamager instanceof OITCEvent) {
                                                            OITCEvent oitcEvent = (OITCEvent)eventDamager;
                                                            OITCPlayer oitcKiller = oitcEvent.getPlayer(damager);
                                                            OITCPlayer oitcPlayer = oitcEvent.getPlayer(entity);
                                                            if (oitcKiller.getState() != OITCPlayer.OITCState.FIGHTING
                                                                || oitcPlayer.getState() != OITCPlayer.OITCState.FIGHTING) {
                                                                e.setCancelled(true);
                                                                return;
                                                            }

                                                            if (e.getDamager() instanceof Arrow) {
                                                                Arrow arrow = (Arrow)e.getDamager();
                                                                if (arrow.getShooter() instanceof Player && damager != entity) {
                                                                    oitcPlayer.setLastKiller(oitcKiller);
                                                                    e.setDamage(0.0);
                                                                    eventEntity.onDeath().accept(entity);
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    e.setCancelled(true);
                                                }
                                            } else {
                                                e.setCancelled(true);
                                            }
                                        } else {
                                            if (isEventEntity && isEventDamager && eventEntity instanceof TNTTagEvent && eventDamager instanceof TNTTagEvent) {
                                                TNTTagEvent tntTagEvent = (TNTTagEvent)eventDamager;
                                                TNTTagPlayer tnt = tntTagEvent.getPlayer(damager);
                                                TNTTagPlayer other = tntTagEvent.getPlayer(entity);
                                                if (tntTagEvent.getTntTagState() != TNTTagEvent.TNTTagState.RUNNING) {
                                                    e.setCancelled(true);
                                                }

                                                if (tntTagEvent.getTntTagState() == TNTTagEvent.TNTTagState.RUNNING && tnt.isTagged() && !other.isTagged()) {
                                                    tnt.setTagged(false);
                                                    tnt.update();
                                                    other.setTagged(true);
                                                    other.update();
                                                }

                                                e.setDamage(0.0);
                                            }
                                        }
                                    } else {
                                        e.setCancelled(true);
                                    }
                                } else {
                                    e.setCancelled(true);
                                }
                            } else {
                                e.setCancelled(true);
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (player instanceof Player) {
            PracticeEvent tnt = Practice.getInstance().getEventManager().getEventPlaying((Player)player);
            if (tnt instanceof TNTTagEvent) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            for (PotionEffect effect : e.getEntity().getEffects()) {
                if (effect.getType().equals(PotionEffectType.HEAL)) {
                    Player shooter = (Player)e.getEntity().getShooter();
                    if (e.getIntensity(shooter) <= 0.5) {
                        PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
                        if (shooterData != null) {
                            shooterData.setMissedPots(shooterData.getMissedPots() + 1);
                        }
                    }
                    break;
                }
            }
        }
    }

    private Player isPlayerDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof EnderPearl) {
            return null;
        } else if (event.getDamager() instanceof Player) {
            return (Player)event.getDamager();
        } else {
            return event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player
                ? (Player)((Projectile)event.getDamager()).getShooter()
                : null;
        }
    }
}
