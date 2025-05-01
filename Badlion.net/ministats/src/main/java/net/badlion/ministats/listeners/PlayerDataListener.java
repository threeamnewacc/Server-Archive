package net.badlion.ministats.listeners;

import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataListener implements Listener {

	private boolean trackStats = false;

	private ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerData playerData = this.playerDataMap.get(player.getUniqueId());
		if (this.trackStats && playerData != null && playerData.isTrackData()) {
			playerData.setPotionEffects(player.getActivePotionEffects());
			playerData.setItems(player.getInventory().getContents());
			playerData.setArmor(player.getInventory().getArmorContents());
			playerData.setFood(player.getFoodLevel());
			playerData.setHealth(player.getHealth());
			playerData.setLastTimeOnline(new DateTime(DateTimeZone.UTC).getMillis());
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void onPlayerTakeDamage(EntityDamageEvent event) {
		if (this.trackStats && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
            PlayerData playerData = this.playerDataMap.get(player.getUniqueId());
            if (playerData != null && playerData.isTrackData()) {
                double finalDamageDone = event.getFinalDamage();
                if (player.getHealth() - finalDamageDone < 0) {
                    finalDamageDone = player.getHealth();
                }

                playerData.setDamageTaken(playerData.getDamageTaken() + finalDamageDone);

                if (event instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                    Player attacker = null;
                    boolean isSwordSwing = false;
                    if (damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof Player) {
                        attacker = (Player) ((Arrow) damager).getShooter();
                    } else if (damager instanceof Player) {
                        attacker = (Player) damager;

                        // Track if they have a sword
                        if (attacker.getItemInHand() != null && this.isSwordOrAxe(attacker.getItemInHand())) {
                            isSwordSwing = true;
                        }
                    }

                    // Track damage dealt too
                    if (attacker != null && attacker.spigot().getCollidesWithEntities()) {
                        PlayerData playerData2 = this.playerDataMap.get(attacker.getUniqueId());
                        if (playerData2 != null && playerData2.isTrackData()) {
                            // Update their sword hits if applicable
                            if (isSwordSwing) {
                                playerData2.setSwordHits(playerData2.getSwordHits() + 1);
                            }

                            playerData2.setDamageDealt(playerData2.getDamageDealt() + finalDamageDone);
                        }
                    }
                }
            }
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void entityDeathEvent(EntityDeathEvent event) {
		// Some plugins like MPG we want to control this
		if (MiniStats.DISABLE_PLAYER_LISTENER_DEATHS) {
			return;
		}

		// Is it a combat log npc?
		if (event.getEntity() instanceof Zombie) {
			if (event.getEntity().hasMetadata("CombatLoggerNPC")) {
				UUID uuid = (UUID) event.getEntity().getMetadata("CombatLoggerNPC").get(0).value();

				// Add a death to that class for player
				PlayerData playerData = this.getPlayerData(uuid);
				if (this.trackStats && playerData != null && playerData.isTrackData()) {
					playerData.setDeaths(playerData.getDeaths() + 1);

					// Reset their kill streak
					playerData.setCurrentKillStreak(0);

					if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
						// Add a kill to player's killstreak
						PlayerData killerPlayerData = this.getPlayerData(event.getEntity().getKiller());
						if (killerPlayerData.isTrackData()) {
							killerPlayerData.setCurrentKillStreak(killerPlayerData.getCurrentKillStreak() + 1);

							if (killerPlayerData.getCurrentKillStreak() > killerPlayerData.getHighestKillStreak()) {
								killerPlayerData.setHighestKillStreak(killerPlayerData.getCurrentKillStreak());
							}

							// Add a player kill
							killerPlayerData.addKill();
							killerPlayerData.addPlayerKill(playerData, event.getEntity());
						}
					} else {
						// Store a suicide for the combat logger
						playerData.addSuicide(event.getEntity());
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.FIRST)
	public void playerDeathEvent(PlayerDeathEvent event) {
		// Some plugins like MPG we want to control this
		if (MiniStats.DISABLE_PLAYER_LISTENER_DEATHS) {
			return;
		}

		Player player = event.getEntity();

		// Add a death to that class for player
		PlayerData playerData = this.getPlayerData(player);
		if (this.trackStats && playerData != null && playerData.isTrackData()) {
			playerData.setDeaths(playerData.getDeaths() + 1);

			// Reset their kill streak
			playerData.setCurrentKillStreak(0);

			if (player.getKiller() != null && player.getKiller() instanceof Player) {
				// Add a kill to player's killstreak
				PlayerData killerPlayerData = this.getPlayerData(player.getKiller());
				if (killerPlayerData.isTrackData()) {
					killerPlayerData.setCurrentKillStreak(killerPlayerData.getCurrentKillStreak() + 1);

					if (killerPlayerData.getCurrentKillStreak() > killerPlayerData.getHighestKillStreak()) {
						killerPlayerData.setHighestKillStreak(killerPlayerData.getCurrentKillStreak());
					}

					// Add a player kill
					killerPlayerData.addPlayerKill(player);
				}
			} else {
                // Store a suicide for the player
                playerData.addSuicide(event.getEntity());
            }
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void arrowHitPlayerEvent(EntityDamageByEntityEvent event) {
		if (this.trackStats && event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
			Player player = (Player) ((Arrow) event.getDamager()).getShooter();

			// Add a successful arrow hit to player data
			PlayerData playerData = this.getPlayerData(player);

			if (playerData != null && playerData.isTrackData()) {
				playerData.setArrowsHitTarget(playerData.getArrowsHitTarget() + 1);
			}
		}
	}

	@EventHandler(priority=EventPriority.LAST, ignoreCancelled=true)
	public void bowFireEvent(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		PlayerData playerData = this.getPlayerData(player);
		if (this.trackStats && playerData != null && playerData.isTrackData()) {
			playerData.setArrowsFired(playerData.getArrowsFired() + 1);
		}
	}

	// Don't ignore cancelled because Bukkit is retarded and it cancels the event by default or something
	@EventHandler(priority=EventPriority.LAST)
	public void swordSwingEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (item != null) {
			PlayerData playerData = this.getPlayerData(player);
			if (this.trackStats && playerData != null && playerData.isTrackData()) {
				if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
					if (this.isSwordOrAxe(item)) {
						// Add a sword swing
						playerData.setSwordSwings(playerData.getSwordSwings() + 1);
					} else if (item.getType().equals(Material.BOW)) {
						// Add a bow punch
						playerData.setBowPunches(playerData.getBowPunches() + 1);
					}
				} else if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (this.isSwordOrAxe(item)) {
						// Add a sword block
						playerData.setSwordBlocks(playerData.getSwordBlocks() + 1);
					}
				}
			}
		}
	}

	public boolean isSwordOrAxe(ItemStack item) {
		return item.getType().equals(Material.WOOD_SWORD) || item.getType().equals(Material.STONE_SWORD)
				|| item.getType().equals(Material.GOLD_SWORD) || item.getType().equals(Material.IRON_SWORD)
				|| item.getType().equals(Material.DIAMOND_SWORD) || item.getType().equals(Material.WOOD_AXE)
				|| item.getType().equals(Material.STONE_AXE) || item.getType().equals(Material.GOLD_AXE)
				|| item.getType().equals(Material.IRON_AXE) || item.getType().equals(Material.DIAMOND_AXE);
	}

	public PlayerData getPlayerData(Player player) {
		return this.playerDataMap.get(player.getUniqueId());
	}

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerDataMap.get(uuid);
    }

	public ConcurrentHashMap<UUID, PlayerData> getPlayerDataMap() {
		return playerDataMap;
	}

	public boolean isTrackStats() {
		return trackStats;
	}

	public void setTrackStats(boolean trackStats) {
		this.trackStats = trackStats;
	}

}
