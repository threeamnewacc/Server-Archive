package net.badlion.gfactions.listeners;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.badlion.gfactions.GFactions;

public class PVPProtectionListener implements Listener {
	
	private GFactions plugin;
	
	public PVPProtectionListener(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

			@Override
			public void run() {
				plugin.checkIfPlayerHasProtection(player);
			}
		});
	}
	
	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if (player != null) {

            if (this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString()) &&
                    this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString())) {
                final int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                final long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                final long currentTime = System.currentTimeMillis();

                // Ok they are still protected...update their time in the DB
                if ((timeJoined + timeRemaining) > currentTime) {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Update DB
                            plugin.updateProtection(player, (timeRemaining - (currentTime - timeJoined)));
                        }
                    });
                } else {
                    // Their PVP protection is over, time to remove from the system
                    this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(player);
                        }
                    });
                }
            }
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
		DamageCause type = event.getCause();
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			final Player player = (Player) entity;
            if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString()) &&
                    this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
                int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                long currentTime = System.currentTimeMillis();

                // Ok they are still protected
                if ((timeJoined + timeRemaining) > currentTime) {
                    // Fire protection
                    if ((type == DamageCause.FIRE || type == DamageCause.FIRE_TICK) && player.getLocation().getBlockY() >= 60 && player.getLocation().getWorld().getName().equals("world")) {
                        event.setCancelled(true);
                        player.setFireTicks(0);
                        return;
                    }
                } else {
                    // Their PVP protection is over, time to remove from the system
                    this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(player);
                        }
                    });
                }
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		DamageCause type = event.getCause();
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			final Player player = (Player) entity;
            if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString()) &&
                    this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
                int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                long currentTime = System.currentTimeMillis();

                // Ok they are still protected
                if ((timeJoined + timeRemaining) > currentTime) {
                    // Lava protection
                    if (type == DamageCause.LAVA && player.getLocation().getBlockY() >= 60 && player.getLocation().getWorld().getName().equals("world")) {
                        event.setCancelled(true);
                        player.setFireTicks(0);
                        return;
                    }
                } else {
                    // Their PVP protection is over, time to remove from the system
                    this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(player);
                        }
                    });
                }
            }
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPotionSplash(PotionSplashEvent event) {
		// Stop the pvp protected person from harming others
		if (event.getEntity().getShooter() instanceof Player) {
			final Player player = (Player) event.getEntity().getShooter();
            if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString()) &&
                    this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
                int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                long currentTime = System.currentTimeMillis();

                // Ok they are still protected
                if ((timeJoined + timeRemaining) > currentTime) {
                    // Cancel pot effects
                    Collection<PotionEffect> effects = event.getEntity().getEffects();
                    for (Entity entity : event.getAffectedEntities()) {
                        for (PotionEffect effect : effects) {
                            if (effect.getType().equals(PotionEffectType.HARM) || effect.getType().equals(PotionEffectType.POISON)
                                    || effect.getType().equals(PotionEffectType.SLOW) || effect.getType().equals(PotionEffectType.WEAKNESS)) {
                                event.setIntensity((LivingEntity) entity, 0.0);
                                player.sendMessage(ChatColor.RED + "You have PVP Protection.  You cannot hurt others.");
                            }
                        }
                    }
                } else {
                    // Their PVP protection is over, time to remove from the system
                    this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(player);
                        }
                    });
                }
                return;
            }
		}
		
		// Protect the people who have protection on
		Collection<LivingEntity> entities = event.getAffectedEntities();
		for (Entity entity : entities) {
			if (entity instanceof Player) {
				final Player player = (Player) entity;
                if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString()) &&
                        this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
                    int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                    long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                    long currentTime = System.currentTimeMillis();

                    // Ok they are still protected
                    if ((timeJoined + timeRemaining) > currentTime) {
                        Collection<PotionEffect> effects = event.getEntity().getEffects();
                        for (PotionEffect effect : effects) {
                            if (effect.getType().equals(PotionEffectType.HARM) || effect.getType().equals(PotionEffectType.POISON)
                                    || effect.getType().equals(PotionEffectType.SLOW) || effect.getType().equals(PotionEffectType.WEAKNESS)) {
                                event.setIntensity((LivingEntity) entity, 0.0);
                                if (event.getEntity().getShooter() instanceof Player) {
                                    ((Player) event.getEntity().getShooter()).sendMessage(ChatColor.RED + "One of the players you hit with your splash potion has PVP Protection.");
                                }
                            }
                        }
                    } else {
                        // Their PVP protection is over, time to remove from the system
                        this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                        this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                            @Override
                            public void run() {
                                // Purge from DB
                                plugin.removeProtection(player);
                            }
                        });
                    }
                }
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			final Player player = (Player) entity;
			
			// Only check for when player attacking
			if (!canDamage(event.getDamager())) {
				// Ok they still have PVP Prot on...should they?
                if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(player.getUniqueId().toString()) &&
                        this.plugin.getMapNameToJoinTime().containsKey(player.getUniqueId().toString())) {
                    int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(player.getUniqueId().toString());
                    long timeJoined = this.plugin.getMapNameToJoinTime().get(player.getUniqueId().toString());
                    long currentTime = System.currentTimeMillis();

                    // Ok they are still protected
                    if ((timeJoined + timeRemaining) > currentTime) {
                        event.setCancelled(true);
                        entity = event.getDamager();
                        Entity shooter = entity;

                        // Get the shooter if it was a projectile
                        if (entity instanceof Projectile) {
                            shooter = (Player) ((Projectile) entity).getShooter();
                            entity.remove();
                        }
                        if (shooter instanceof Player) {
                            ((Player) shooter).sendMessage(ChatColor.RED + "This player has PVP protection.");
                        }
                        return;
                    } else {
                        // Their PVP protection is over, time to remove from the system
                        this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                        this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                            @Override
                            public void run() {
                                // Purge from DB
                                plugin.removeProtection(player);
                            }
                        });
                    }
                    return;
                }
			}

			// Stop them from attacking other people
			Player attacker = null;
			if (event.getDamager() instanceof Player) {
				attacker = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
				attacker = (Player) ((Projectile) event.getDamager()).getShooter();
			}
			
			if (attacker != null) {
				// Make sure the pvp protected person can't attack
                if (this.plugin.getMapNameToPvPTimeRemaining().containsKey(attacker.getUniqueId().toString()) &&
                        this.plugin.getMapNameToJoinTime().containsKey(attacker.getUniqueId().toString())) {
                    int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(attacker.getUniqueId().toString());
                    long timeJoined = this.plugin.getMapNameToJoinTime().get(attacker.getUniqueId().toString());
                    long currentTime = System.currentTimeMillis();

                    // Ok they are still protected...don't allow them to attack others
                    if ((timeJoined + timeRemaining) > currentTime) {
                        event.setCancelled(true);
                        if (event.getDamager() instanceof Projectile) {
                            event.getDamager().remove();
                        }

                        attacker.sendMessage(ChatColor.RED + "Cannot attack others with pvp protection on.");
                    } else {
                        // Their PVP protection is over, time to remove from the system
                        this.plugin.getMapNameToPvPTimeRemaining().remove(attacker.getUniqueId().toString());
                        this.plugin.getMapNameToJoinTime().remove(attacker.getUniqueId().toString());

                        final Player tmp = attacker;
                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                            @Override
                            public void run() {
                                // Purge from DB
                                plugin.removeProtection(tmp);
                            }
                        });
                    }
				}
			}
		}
	}
	
	private boolean canDamage(Entity entity) {
		if (entity instanceof Player) {
			return false;
		} else if (entity instanceof Projectile && ((Projectile) entity).getShooter() instanceof Player) {
			return false;
		}
		return true;
	}

    @EventHandler
    public void onOpenChest(PlayerInteractEvent event) {
        // Ripped from WG Line https://github.com/sk89q/worldguard/blob/master/src/main/java/com/sk89q/worldguard/bukkit/WorldGuardPlayerListener.java#L896
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.CHEST
                    || type == Material.JUKEBOX //stores the (arguably) most valuable item
                    || type == Material.DISPENSER
                    || type == Material.BREWING_STAND
                    || type == Material.TRAPPED_CHEST
                    || type == Material.HOPPER
                    || type == Material.DROPPER) {
                if (this.plugin.getMapNameToJoinTime().containsKey(event.getPlayer().getUniqueId().toString()) &&
                        this.plugin.getMapNameToPvPTimeRemaining().containsKey(event.getPlayer().getUniqueId().toString())) {
                    int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(event.getPlayer().getUniqueId().toString());
                    long timeJoined = this.plugin.getMapNameToJoinTime().get(event.getPlayer().getUniqueId().toString());
                    long currentTime = System.currentTimeMillis();

                    // Ok they are still protected...don't allow them to attack others
                    if ((timeJoined + timeRemaining) > currentTime) {
                        event.setCancelled(true);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.getPlayer().sendMessage(ChatColor.RED + "Cannot use this when you have pvp protection on.");
                    } else {
                        // Their PVP protection is over, time to remove from the system
                        this.plugin.getMapNameToPvPTimeRemaining().remove(event.getPlayer().getUniqueId().toString());
                        this.plugin.getMapNameToJoinTime().remove(event.getPlayer().getUniqueId().toString());

                        final Player tmp = event.getPlayer();
                        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                            @Override
                            public void run() {
                                // Purge from DB
                                plugin.removeProtection(tmp);
                            }
                        });
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreakChest(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.CHEST
                || type == Material.JUKEBOX //stores the (arguably) most valuable item
                || type == Material.DISPENSER
                || type == Material.FURNACE
                || type == Material.BURNING_FURNACE
                || type == Material.BREWING_STAND
                || type == Material.TRAPPED_CHEST
                || type == Material.HOPPER
                || type == Material.DROPPER) {
            if (this.plugin.getMapNameToJoinTime().containsKey(event.getPlayer().getUniqueId().toString()) &&
                    this.plugin.getMapNameToPvPTimeRemaining().containsKey(event.getPlayer().getUniqueId().toString())) {
                int timeRemaining = this.plugin.getMapNameToPvPTimeRemaining().get(event.getPlayer().getUniqueId().toString());
                long timeJoined = this.plugin.getMapNameToJoinTime().get(event.getPlayer().getUniqueId().toString());
                long currentTime = System.currentTimeMillis();

                // Ok they are still protected...don't allow them to attack others
                if ((timeJoined + timeRemaining) > currentTime) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "Cannot break this when you have pvp protection on.");
                } else {
                    // Their PVP protection is over, time to remove from the system
                    this.plugin.getMapNameToPvPTimeRemaining().remove(event.getPlayer().getUniqueId().toString());
                    this.plugin.getMapNameToJoinTime().remove(event.getPlayer().getUniqueId().toString());

                    final Player tmp = event.getPlayer();
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                        @Override
                        public void run() {
                            // Purge from DB
                            plugin.removeProtection(tmp);
                        }
                    });
                }
            }
        }
    }

}
