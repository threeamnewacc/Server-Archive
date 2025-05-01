package net.badlion.skywars.listeners;

import net.badlion.gberry.UnregistrableListener;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.skywars.SkyPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class SkyWarsGameListener implements Listener, UnregistrableListener {

    private MPGPlayer currentSpawningPlayer;
    private Map<LivingEntity, MPGPlayer> spawnedMobs = new HashMap<>();
    private Set<UUID> playersWhoHaveClickedAChest = new HashSet<>(); // TODO: Remove when we rework chests

    // TODO: Remove when we rework chests
    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.CHEST) {
                MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
                if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
                    if (!this.playersWhoHaveClickedAChest.contains(event.getPlayer().getUniqueId())) {
                        this.playersWhoHaveClickedAChest.add(event.getPlayer().getUniqueId());
                        Chest chest = (Chest) event.getClickedBlock().getState();
                        boolean found = false;
                        for (ItemStack itemStack :chest.getInventory().getContents()) {
                            if (itemStack == null) {
                                continue;
                            }

                            if (itemStack.getType() == Material.WOOD || itemStack.getType() == Material.COBBLESTONE
                                    || itemStack.getType() == Material.STONE) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            chest.getInventory().addItem(new ItemStack(Material.WOOD, 24, (short) 0));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTakeFallDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                SkyPlayer skyPlayer = (SkyPlayer) MPGPlayerManager.getMPGPlayer(player.getUniqueId());
                if (skyPlayer.isIgnoreFallDamage()) {
                    skyPlayer.setIgnoreFallDamage(false);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.ANVIL || event.getBlock().getType() == Material.ENCHANTMENT_TABLE ) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break this");
        }
    }

    @EventHandler
    public void onPlayerRightClickWithSpawnEgg(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.MONSTER_EGG) {
                this.currentSpawningPlayer = MPGPlayerManager.getMPGPlayer(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            this.spawnedMobs.put(event.getEntity(), this.currentSpawningPlayer);

            // Special AI
            if (event.getEntity() instanceof Wolf) {
                Wolf wolf = (Wolf) event.getEntity();
                wolf.setTamed(true);
                wolf.setOwner(this.currentSpawningPlayer.getPlayer());
                wolf.setSitting(false);
            } else if (event.getEntity() instanceof PigZombie) {
                PigZombie zombiePigman = (PigZombie) event.getEntity();
                zombiePigman.setAnger(Short.MAX_VALUE);
            }

            this.currentSpawningPlayer = null; // Don't leak memory
        } else if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
    public void onMobDoesDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            MPGPlayer target = MPGPlayerManager.getMPGPlayer(((Player) event.getEntity()).getUniqueId());

            // Not a player
            if (!(event.getDamager() instanceof Player)) {
                // Something hit us
                if (event.getDamager() instanceof Projectile) {
                    ProjectileSource source = ((Projectile) event.getDamager()).getShooter();
                    if (source instanceof Player) {
                        return;
                    }

                    if (source instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) source;
                        MPGPlayer damager = this.spawnedMobs.get(livingEntity);
                        if (damager != null) {
                            target.setLastDamageCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                            target.setLastDamager(damager);
                        }      // TODO: TALK TO SMELLY FOR THIS, THERE'S A WAY USING MESSAGEUTIL SHIT
                    }
                } else if (event.getDamager() instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) event.getDamager();
                    MPGPlayer damager = this.spawnedMobs.get(livingEntity);
                    if (damager != null) {
                        target.setLastDamageCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                        target.setLastDamager(damager);
                    }
                }
            }
        }
    }

    // TODO: What happens if you die to a mob someone spawned, u need to get kill credit
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            MPGPlayer spawnedPlayer = this.spawnedMobs.get(entity);
            if (spawnedPlayer != null) {
                if (event.getTarget() instanceof Player) {
                    Player targetPlayer = (Player) event.getTarget();
                    if (targetPlayer.getUniqueId().equals(spawnedPlayer.getUniqueId())) {
                        // Find a new target
                        List<Player> players = new ArrayList<>();
                        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
                            Player pl = MPG.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId());
                            if (pl != null) {
                                if (pl.getUniqueId().equals(spawnedPlayer.getUniqueId())) {
                                    continue;
                                }

                                players.add(pl);
                            }
                        }

                        // Error...
                        if (players.size() == 0) {
                            event.setCancelled(true);
                            return;
                        }

                        // Find closest player
                        Player closestPlayer = players.get(0);
                        double closestDistance = entity.getLocation().distance(closestPlayer.getLocation());
                        for (Player pl : players) {
                            double distance = entity.getLocation().distance(pl.getLocation());
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                closestPlayer = pl;
                            }
                        }

                        // Set new target
                        event.setTarget(closestPlayer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTargetLiving(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            MPGPlayer spawnedPlayer = this.spawnedMobs.get(entity);
            if (spawnedPlayer != null) {
                if (event.getTarget() instanceof Player) {
                    Player targetPlayer = (Player) event.getTarget();
                    if (targetPlayer.getUniqueId().equals(spawnedPlayer.getUniqueId())) {
                        // Find a new target
                        List<Player> players = new ArrayList<>();
                        for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
                            Player pl = MPG.getInstance().getServer().getPlayer(mpgPlayer.getUniqueId());
                            if (pl != null) {
                                if (pl.getUniqueId().equals(spawnedPlayer.getUniqueId())) {
                                    continue;
                                }

                                players.add(pl);
                            }
                        }

                        // Error...
                        if (players.size() == 0) {
                            event.setCancelled(true);
                            return;
                        }

                        // Find closest player
                        Player closestPlayer = players.get(0);
                        double closestDistance = entity.getLocation().distance(closestPlayer.getLocation());
                        for (Player pl : players) {
                            double distance = entity.getLocation().distance(pl.getLocation());
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                closestPlayer = pl;
                            }
                        }

                        // Set new target
                        event.setTarget(closestPlayer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityCombustEvent(EntityCombustEvent event) {
        if (event.getEntity() instanceof Monster) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBoom(EntityExplodeEvent event) {
        Iterator<Block> blocks = event.blockList().iterator();
        while (blocks.hasNext()) {
            Block block = blocks.next();
            if (block.getType() == Material.CHEST || block.getType() == Material.ANVIL || block.getType() == Material.ENCHANTMENT_TABLE) {
                blocks.remove();
            }
        }
    }

    @EventHandler
    public void onTntPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
        }
    }

    public void unregister() {
        EntityDamageEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
        CreatureSpawnEvent.getHandlerList().unregister(this);
        EntityCombustEvent.getHandlerList().unregister(this);
        EntityTargetLivingEntityEvent.getHandlerList().unregister(this);
        EntityExplodeEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        EntityTargetEvent.getHandlerList().unregister(this);
    }

}
