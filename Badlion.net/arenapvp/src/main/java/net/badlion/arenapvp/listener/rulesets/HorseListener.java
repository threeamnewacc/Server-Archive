package net.badlion.arenapvp.listener.rulesets;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.arenas.Arena;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HorseListener implements Listener {

	private Set<UUID> quittingPlayers = new HashSet<>();

	@EventHandler
	public void onHealthRegenEvent(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.horseRuleSet)) {
				if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.horseRuleSet)) {
			this.quittingPlayers.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onVehicleExitEvent(VehicleExitEvent event) {
		if (event.getExited() instanceof Player) {
			Player player = (Player) event.getExited();

			// Are they online?
			if (!this.quittingPlayers.remove(player.getUniqueId())) {

				if (MatchState.playerIsInMatchAndUsingRuleSet(player, KitRuleSet.horseRuleSet)) {
					if (!MatchState.getPlayerMatch(player).isOver()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public void teleport(Player player, Location location, Arena arena) {
		HorseListener.createHorseAndAttach(player, location, arena);
	}

	public static Horse createHorse(Player player, Location location, Arena arena) {
		Horse horse = (Horse) player.getWorld().spawnEntity(location, EntityType.HORSE);
		horse.setAdult();
		horse.setTamed(true);
		horse.setAgeLock(true);
		horse.setVariant(Horse.Variant.HORSE);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
		horse.setStyle(Horse.Style.values()[Gberry.generateRandomInt(0, Horse.Style.values().length - 1)]);
		horse.setColor(Horse.Color.values()[Gberry.generateRandomInt(0, Horse.Color.values().length - 1)]);
		horse.setJumpStrength(0.8D);
		horse.setMaxHealth(40D);
		horse.setHealth(horse.getMaxHealth());
		horse.setSpeed(0.2125D);
		horse.setOwner(player);

		arena.getLivingEntities().add(horse);

		return horse;
	}

	public static void createHorseAndAttach(final Player player, Location location, Arena arena) {
		final Horse horse = HorseListener.createHorse(player, location, arena);

		player.setFallDistance(0);

		new BukkitRunnable() {
			public void run() {
				if (Gberry.isPlayerOnline(player)) {
					horse.setPassenger(player);
				}
			}
		}.runTaskLater(ArenaPvP.getInstance(), 1L);
	}

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();

			if (MatchState.playerIsInMatchAndUsingRuleSet(damaged, KitRuleSet.horseRuleSet)) {
				if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

					if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
						damager.sendMessage(ChatColor.GOLD + damaged.getDisguisedName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
								Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + " " + MessageUtil.HEART_WITH_COLOR);
					}
				}
			}
		}
	}


}
