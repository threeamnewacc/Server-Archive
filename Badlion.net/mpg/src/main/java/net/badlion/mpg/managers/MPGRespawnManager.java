package net.badlion.mpg.managers;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MPGRespawnManager extends BukkitUtil.Listener {

	private static Set<UUID> respawningPlayers = new HashSet<>();

	public static void addPlayerRespawning(MPGPlayer mpgPlayer, Location location) {
		new RespawnTask(mpgPlayer, location).runTaskTimer(MPG.getInstance(), 20L, 20L);
	}

	public static void removePlayerRespawning(MPGPlayer mpgPlayer) {
		MPGRespawnManager.removePlayerRespawning(mpgPlayer.getUniqueId());
	}

	public static void removePlayerRespawning(Player player) {
		MPGRespawnManager.removePlayerRespawning(player.getUniqueId());
	}

	public static void removePlayerRespawning(UUID uuid) {
		MPGRespawnManager.respawningPlayers.remove(uuid);
	}

	public static void handlePlayerRespawningVisibility(MPGPlayer mpgPlayer, boolean join) {
		Player player = mpgPlayer.getPlayer();

		// No respawning players?
		if (MPGRespawnManager.respawningPlayers == null) return;

		// Is player joining the game?
		if (join) {
			// Hide respawning players when they join
			for (UUID uuid : MPGRespawnManager.respawningPlayers) {
				if (mpgPlayer.getUniqueId() == uuid) continue;

				player.hidePlayer(MPG.getInstance().getServer().getPlayer(uuid));
			}
		} else {
			// Show respawning players after they leave
			for (UUID uuid : MPGRespawnManager.respawningPlayers) {
				if (mpgPlayer.getUniqueId() == uuid) continue;

				player.showPlayer(MPG.getInstance().getServer().getPlayer(uuid));
			}
		}

		// Is player actually respawning too?
		if (MPGRespawnManager.respawningPlayers.contains(mpgPlayer.getUniqueId())) {
			// Show this player to players in the game
			for (MPGPlayer mpgPlayer2 : MPGPlayerManager.getAllMPGPlayers()) {
				Player pl = MPG.getInstance().getServer().getPlayer(mpgPlayer2.getUniqueId());

				pl.showPlayer(player);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		// Cancel damage if they try hitting a player while respawning
		if (MPGRespawnManager.respawningPlayers.contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Player damagePlayer = null;
		if (damager instanceof Projectile) {
			damagePlayer = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
		} else if (damager instanceof Player) {
			damagePlayer = (Player) damager;
		}

		if (damagePlayer != null) {
			// Cancel damage if they try hitting a player while respawning
			if (MPGRespawnManager.respawningPlayers.contains(damagePlayer.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	private static class RespawnTask extends BukkitRunnable {

		private int seconds = 0;

		private Player player;
		private MPGPlayer mpgPlayer;

		private Location location;

		public RespawnTask(MPGPlayer mpgPlayer, Location location) {
			this.player = mpgPlayer.getPlayer();
			this.mpgPlayer = mpgPlayer;
			this.location = location;

			this.player.setGameMode(GameMode.CREATIVE);
			this.player.spigot().setCollidesWithEntities(false);

			// Hide from other players
			for (MPGPlayer mpgPlayer2 : MPGPlayerManager.getAllMPGPlayers()) {
				if (this.player.getUniqueId() == mpgPlayer2.getUniqueId()) {
					continue;
				}

				Player pl = MPG.getInstance().getServer().getPlayer(mpgPlayer2.getUniqueId());

				pl.hidePlayer(this.player);
			}

			this.player.sendMessage(ChatColor.YELLOW + "You will respawn in "
					+ MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.RESPAWN_TIME) + " seconds!");

			MPGRespawnManager.respawningPlayers.add(this.player.getUniqueId());
		}

		@Override
		public void run() {
			this.seconds++;

			// Did they log off?
			if (!Gberry.isPlayerOnline(this.player)) {
				MPGRespawnManager.removePlayerRespawning(this.player);

				this.cancel();
				return;
			}

			// Did the game end?
			if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.POST_GAME) {
				MPGRespawnManager.removePlayerRespawning(this.player);

				this.cancel();
				return;
			}

			if (this.seconds == MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.RESPAWN_TIME)) {
				// Give them resistance
				this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
						20 * MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.RESPAWN_RESISTANCE_TIME), 128));

				this.player.teleport(this.location);

				// Show to other players
				for (MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
					if (this.player.getUniqueId() == mpgPlayer.getUniqueId()) {
						continue;
					}

					Player pl = mpgPlayer.getPlayer();

					pl.showPlayer(this.player);
				}

				MPGRespawnManager.removePlayerRespawning(this.player);

				// Set back to survival mode
				this.player.setGameMode(GameMode.SURVIVAL);
				this.player.spigot().setCollidesWithEntities(true);

				// Call handle player respawn
				this.mpgPlayer.handlePlayerRespawn(player);

				this.player.sendMessage(ChatColor.YELLOW + "You have respawned!");
				this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

				this.cancel();
			} else if (MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.RESPAWN_TIME) - this.seconds <= 3) {
				this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 0.5f);
			}
		}

	}

}
