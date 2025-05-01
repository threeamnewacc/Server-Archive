package net.badlion.potpvp.managers;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.events.RefreshKitEvent;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.tdm.TDMGame;
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

public class RespawnManager extends BukkitUtil.Listener {

    private static Set<Player> respawningPlayers = new HashSet<>();
	private static Map<Game, List<Player>> gameRespawningPlayers = new HashMap<>();

    public static void addPlayerRespawning(Game game, Player player, Location location, int respawnTime, int resistanceTime) {
	    if (!RespawnManager.gameRespawningPlayers.containsKey(game)) {
		    RespawnManager.gameRespawningPlayers.put(game, new ArrayList<Player>());
	    }

	    new RespawnTask(game, player, location, respawnTime, resistanceTime).runTaskTimer(PotPvP.getInstance(), 20L, 20L);
    }

    public static void removePlayerRespawning(Game game, Player player) {
        RespawnManager.respawningPlayers.remove(player);

        List<Player> respawningPlayers = RespawnManager.gameRespawningPlayers.get(game);
	    respawningPlayers.remove(player);

	    // No more respawning players for our Game object?
	    if (respawningPlayers.isEmpty()) {
		    // Messy way of doing it but this is a pretty efficient way
		    // of referencing the respawning players with the game object

		    // Avoid memory leaks
		    RespawnManager.gameRespawningPlayers.remove(game);
	    }
    }

	public static void handlePlayerRespawningVisibility(Game game, Group group, boolean join) {
		Player player = group.getLeader();
		List<Player> respawningPlayers = RespawnManager.gameRespawningPlayers.get(game);

		// No respawning players?
		if (respawningPlayers == null) return;

		// Is player joining the game?
		if (join) {
			// Hide respawning players when they join
			for (Player respawningPlayer : respawningPlayers) {
				if (player == respawningPlayer) continue;

				player.hidePlayer(respawningPlayer);
			}
		} else {
			// Show respawning players after they leave
			for (Player respawningPlayer : respawningPlayers) {
				if (player == respawningPlayer) continue;

				player.showPlayer(respawningPlayer);
			}
		}

		// Is player actually respawning too?
		if (respawningPlayers.contains(player)) {
			// Show this player to players in the game
			for (Player pl : game.getPlayers()) {
				pl.showPlayer(player);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		// Cancel damage if they try hitting a player while respawning
		if (RespawnManager.respawningPlayers.contains(event.getPlayer())) {
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
            if (RespawnManager.respawningPlayers.contains(damagePlayer)) {
                event.setCancelled(true);
            }
        }
    }

    private static class RespawnTask extends BukkitRunnable {

        private Game game;
        private Player player;
	    private Location location;

        private int seconds = 0;
	    private int respawnTime;
	    private int resistanceTime;

        public RespawnTask(Game game, Player player, Location location, int respawnTime, int resistanceTime) {
            this.game = game;
            this.player = player;
	        this.location = location;

            this.respawnTime = respawnTime;
            this.resistanceTime = resistanceTime;

            player.setGameMode(GameMode.CREATIVE);
            player.spigot().setCollidesWithEntities(false);

            // Hide from other players
            for (Player pl : this.game.getPlayers()) {
                if (pl == player) {
                    continue;
                }

                pl.hidePlayer(player);
            }

	        this.player.sendMessage(ChatColor.YELLOW + "You will respawn in " + respawnTime + " seconds!");

	        RespawnManager.respawningPlayers.add(player);
	        RespawnManager.gameRespawningPlayers.get(game).add(player);
        }

        @Override
        public void run() {
	        this.seconds++;

            // Did they log off or leave?
            if (!Gberry.isPlayerOnline(this.player)
		            || (GroupStateMachine.getInstance().getCurrentState(PotPvP.getInstance().getPlayerGroup(this.player)) != GroupStateMachine.slaughterState
                    && GroupStateMachine.getInstance().getCurrentState(PotPvP.getInstance().getPlayerGroup(this.player)) != GroupStateMachine.kothState
		            && GroupStateMachine.getInstance().getCurrentState(PotPvP.getInstance().getPlayerGroup(this.player)) != GroupStateMachine.tdmState)) {
                RespawnManager.removePlayerRespawning(this.game, this.player);

	            this.cancel();
                return;
            }

            // Did game end?
            if (this.game.isOver()) {
                PotPvP.getInstance().healAndTeleportToSpawn(this.player);

                RespawnManager.removePlayerRespawning(this.game, this.player);

	            this.cancel();
                return;
            }

	        // TDM check
	        if (this.game instanceof TDMGame) {
		        TDMGame tdmGame = (TDMGame) this.game;
		        if (tdmGame.isVoting()) {
			        // Show to other players
			        for (Player pl : this.game.getPlayers()) {
				        pl.showPlayer(this.player);
			        }

			        this.cancel();
			        return;
		        }
	        }

            if (this.seconds == this.respawnTime) {
	            PlayerHelper.healAndPrepPlayerForBattle(this.player);

	            // Messy but MEH
	            if (this.game instanceof RefreshKitEvent) {
		            ((RefreshKitEvent) this.game).refreshKit(this.player, false);
	            } else if (this.game instanceof TDMGame) {
		            ((TDMGame) this.game).refreshKit(this.player);
	            }

	            // Give them 6 seconds of resistance
	            this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * this.resistanceTime, 128));

	            this.player.teleport(this.location);

	            // Call follow event
	            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(this.player));

	            // Show to other players
	            for (Player pl : this.game.getPlayers()) {
		            if (pl == this.player) continue;

		            pl.showPlayer(this.player);
	            }

	            RespawnManager.removePlayerRespawning(this.game, this.player);

	            this.player.sendMessage(ChatColor.YELLOW + "You have respawned!");
	            this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);

	            this.cancel();
            } else if (this.respawnTime - this.seconds <= 3) {
	            this.player.playSound(this.player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 0.5f);
            }
        }

    }

}
