package net.badlion.mpg.listeners;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.events.CombatTagCreateEvent;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.combattag.events.CombatTagKilledEvent;
import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.gberry.utils.NameTagUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.events.MiniPlayerStatsSaveEvent;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGCreatePlayerEvent;
import net.badlion.mpg.inventories.SkullPlayerInventory;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.CheckForEndGame;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.EventPriority.LASTER;

public class GameListener implements Listener {

	@EventHandler
	public void onCombatTagCreateEvent(CombatTagCreateEvent event) {
		// Cancel this because we create it our own way that is incompatible with this event
		event.setCancelled(true);
	}

    // We want this as FIRST so it can run before the one in MPGPlayerManager
    @EventHandler(priority = EventPriority.FIRST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
	    final Player player = event.getPlayer();

	    // Always give mute/ban permissions to staff members
	    if (player.hasPermission("badlion.staff")) {
		    MPG.getInstance().addMuteBanPerms(player);
	    }

        final MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());

	    // Are they reconnecting?
        if (MPG.USES_MATCHMAKING) {
	        // Does this MPGPlayer not exist? (Player is a spectator)
	        if (mpgPlayer == null) {
		        // Is there a game running?
		        if (MiniStats.MATCH_ID != null) {
			        // This is a player trying to spectate, do they have permission?
			        if (MPG.getInstance().getMPGGame().hasPermissionToSpectate(player)) {
				        // Create an MPGPlayer for them
				        MPGCreatePlayerEvent mpgCreatePlayerEvent = new MPGCreatePlayerEvent(player);
				        MPG.getInstance().getServer().getPluginManager().callEvent(mpgCreatePlayerEvent);

				        if (mpgCreatePlayerEvent.getMPGPlayer() == null) {
					        throw new RuntimeException("MPGPlayer object not properly extended and created");
				        }

				        // Set MPGPlayer state to spectator if it's not already
				        mpgCreatePlayerEvent.getMPGPlayer().setState(MPGPlayer.PlayerState.SPECTATOR);
			        } else {
				        // Kick player because they don't have permission to spectate
				        player.kickPlayer(ChatColor.YELLOW + "Become a Donator+/Lion to spectate at http://store.badlion.net/.");
			        }
		        } else {
			        // There is no game running, kick all peasants, except for admins
			        if (!player.hasPermission("badlion.admin") && !player.hasPermission("badlion.developer")
					        && !player.hasPermission("CaptainKickass63") && !player.hasPermission("Gorille") && !player.hasPermission("LaFerrari")) {
				        player.kickPlayer(ChatColor.RED + "There is no game currently running! Join a game through the game lobby!");
			        }
		        }
	        } else {
		        // So this is either a player logging in, a spectator rejoining, a dc'ed person rejoining,
		        // or a person who is in the dead state is rejoining (their combat logger died)
		        if (mpgPlayer.getState() == MPGPlayer.PlayerState.DC) {
			        // Is the player connecting while the game is counting down?
			        if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN) {
				        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DISGUISE_PLAYERS_DURING_COUNTDOWN)) {
					        mpgPlayer.setManuallyDisguised(true);

					        // Disguise this player for the game countdown
					        if (player.isDisguised()) {
						        Bukkit.getLogger().info("Disguising already disguised player " + player.getUniqueId() + " for game start");
						        mpgPlayer.setOldDisguisedName(player.getDisguisedName());

						        // Save this player's disguised name so when we "undisguise" later we can give this name to the player
						        DisguiseManager.savePlayerDisguise(player);

						        DisguiseManager.undisguisePlayer(player, false);
						        DisguiseManager.disguisePlayer(player, false, true);
					        } else {
						        Bukkit.getLogger().info("Disguising player " + player.getUniqueId() + " for game start");
						        DisguiseManager.disguisePlayer(player, false, false);
					        }
				        }
			        }

			        // Are we using custom name tags for this game?
			        if (MPG.GAME_TYPE == MPG.GameType.PARTY && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NAME_TAGS)) {
				        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NUMBERS)) {
					        // Show "&4[Team #]" for games that use team numbers
					        BukkitUtil.runTaskLater(new Runnable() {
						        @Override
						        public void run() {
							        NameTagUtil.createPlayerNameTag(player, mpgPlayer.getTeam().getPrefix(), "");
						        }
					        }, 4L);
				        } else {
					        // Show "&4" for games that don't use team numbers
					        BukkitUtil.runTaskLater(new Runnable() {
						        @Override
						        public void run() {
							        NameTagUtil.createPlayerNameTag(player, mpgPlayer.getTeam().getColor().toString(), "");
						        }
					        }, 4L);
				        }
			        }

			        // Transfer back to player state
			        mpgPlayer.setState(MPGPlayer.PlayerState.PLAYER);
		        } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.DEAD) {
			        // Is spectating enabled?
			        if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH)) {
				        // Do they have permission to spectate?
				        if (MPG.getInstance().getMPGGame().hasPermissionToSpectate(player)) {
					        // Change state to spectator
					        mpgPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
				        } else {
					        // Kick player because they don't have permission to spectate
					        player.kickPlayer(ChatColor.YELLOW + "Become a Donator+/Lion to spectate at http://store.badlion.net/.");
				        }
			        }
		        } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR) {
			        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DISABLE_SPECTATOR_ON_JOIN)) {
				        mpgPlayer.setState(MPGPlayer.PlayerState.PLAYER);
			        } else {
				        mpgPlayer.handleSpectator(player);
			        }
		        } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			        // Hide spectators
			        mpgPlayer.hideSpectators();

			        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
				        // Add their skull to the spectator inventory
				        SkullPlayerInventory.addSkullForPlayer(player);
			        }
		        }
	        }
        } else {
	        if (mpgPlayer != null) {
		        // So this is either a player logging in, a spectator rejoining, a dc'ed person rejoining,
		        // or a person who is i
		        // n the dead state is rejoining (their combat logger died)
		        if (mpgPlayer.getState() == MPGPlayer.PlayerState.DC) {
			        // Transfer back to player state
			        mpgPlayer.setState(MPGPlayer.PlayerState.PLAYER);
		        } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.DEAD) {
			        // Is spectating enabled?
			        if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH)) {
				        // Do they have permission to spectate?
				        if (MPG.getInstance().getMPGGame().hasPermissionToSpectate(player)) {
					        // Change state to spectator
					        mpgPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
				        } else {
					        // Kick player because they don't have permission to spectate
					        player.kickPlayer(ChatColor.YELLOW + "Become a Donator+/Lion to spectate at http://store.badlion.net/.");
				        }
			        }
		        } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR) {
			        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DISABLE_SPECTATOR_ON_JOIN)) {
				        mpgPlayer.setState(MPGPlayer.PlayerState.PLAYER);
			        } else {
				        mpgPlayer.handleSpectator(player);
			        }
		        } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			        // Hide spectators
			        mpgPlayer.hideSpectators();

			        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
				        // Delay a lil bit because of disguise race conditions :(
				        BukkitUtil.runTaskLater(new Runnable() {
					        @Override
					        public void run() {
						        // Add their skull to the spectator inventory
						        SkullPlayerInventory.addSkullForPlayer(player);
					        }
				        }, 10L);
			        }
		        }
	        }
        }

	    if (mpgPlayer != null) {
		    // Add health under name
		    if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.HEALTH_UNDER_NAME)) {
			    mpgPlayer.addHealthObjective(player);
		    }
	    }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getEntity().getUniqueId());

	    // Safety check
	    if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN) {
		    Bukkit.getLogger().info("PlayerDeathEvent DURING GAME COUNTDOWN with player " + event.getEntity().getName() + " in state " + mpgPlayer.getState());
	    }

	    // Safety check
	    if (mpgPlayer.getState() != MPGPlayer.PlayerState.PLAYER) {
		    Bukkit.getLogger().info("PlayerDeathEvent with player " + event.getEntity().getName() + " in state " + mpgPlayer.getState());
		    return;
	    }

	    // Change to death state
	    mpgPlayer.setState(MPGPlayer.PlayerState.DEAD);

	    MessageUtil.handleDeathMessage(event, event.getEntity(), event.getEntity().getKiller());
    }

	@EventHandler
	public void onCombatTagKilledEvent(CombatTagKilledEvent event) {
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getLoggerNPC().getUUID());

		// Safety check
		if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME_COUNTDOWN) {
			Bukkit.getLogger().info("PlayerDeathEvent DURING GAME COUNTDOWN with player " + mpgPlayer.getUsername() + " in state " + mpgPlayer.getState());
		}

		// Safety check
		if (mpgPlayer.getState() != MPGPlayer.PlayerState.DC) {
			Bukkit.getLogger().info("PlayerDeathEvent with player " + mpgPlayer.getUsername() + " in state " + mpgPlayer.getState());
			return;
		}

		MessageUtil.handleDeathMessage(event, event.getLoggerNPC().getEntity(), event.getLoggerNPC().getEntity().getKiller());

		// MPGPlayer object is set to death state in MPGLoggerNPC
	}

	@EventHandler
	public void onPlayerItemsDroppedFromDeathEvent(PlayerItemsDroppedFromDeathEvent event) {
		if (!MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DROP_ITEMS_ON_DEATH)) {
			for (Item item : event.getItemsDroppedOnDeath()) {
				item.remove();
			}
		}
	}

	@EventHandler
	public void onCombatTagDropInventoryEvent(CombatTagDropInventoryEvent event) {
		if (!MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DROP_ITEMS_ON_DEATH)) {
			for (int i = 0; i < event.getArmor().length; i++) {
				event.getArmor()[i] = null;
			}

			for (int i = 0; i < event.getInventory().length; i++) {
				event.getInventory()[i] = null;
			}
		}
	}

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

	    // Did the game end?
	    if (MPG.getInstance().getMPGGame() == null) {
		    // Game ended, just respawn them at their own location
		    event.setRespawnLocation(player.getLocation());
	    }

	    // Handle the player respawn and get the correct respawn location
	    Location respawnLocation = mpgPlayer.handlePlayerRespawnScreen(event.getPlayer());
	    event.setRespawnLocation(respawnLocation);

	    if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH)) {
		    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%SPEC ON RESPAWN%%%%%%%%%%%%%%");
		    // Does this player have permission to spectate?
		    if (MPG.getInstance().getMPGGame().hasPermissionToSpectate(player)) {
			    System.out.println("SETTING TO SPECTATOR ON RESPAWN");
			    mpgPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
		    } else if (MPG.USES_MATCHMAKING) {
			    System.out.println("ABOUT TO SEND BACK TO LOBBY ON RESPAWN");

			    // Send player back to the lobby
			    BukkitUtil.runTaskAsync(new Runnable() {
				    @Override
				    public void run() {
					    player.sendMessage(ChatColor.AQUA + "Thank you for playing!");
					    player.sendMessage(ChatColor.YELLOW + "Become a Donator+/Lion to spectate at http://store.badlion.net/.");

					    JSONObject payload = new JSONObject();

					    payload.put("uuid", player.getUniqueId().toString());

					    JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_CHECK_GAME, payload);

					    System.out.println(response);

					    List<Player> list = new ArrayList<>();
					    list.add(player);

					    MatchmakingMCPListener.mpgLobbyServerSender.sendPlayersToLobby(list);
				    }
			    });
		    } else {
			    // Does this player have permission to spectate?
			    if (MPG.getInstance().getMPGGame().hasPermissionToSpectate(player)) {
				    mpgPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
			    } else {
				    player.kickPlayer(ChatColor.YELLOW + "Become a Donator+/Lion to spectate at http://store.badlion.net/.");
			    }
		    }
	    }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
	    Player player = event.getPlayer();

        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
	    if (mpgPlayer != null && mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
		    // Increment the number of times this player has disconnected
		    mpgPlayer.incrementNumOfTimesDisconnected();

		    if (MPG.ALLOW_DISCONNECTS
				    && mpgPlayer.getNumOfTimesDisconnected() <= MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.MAX_NUM_OF_DISCONNECTS)) {
			    // Set to DC state
			    mpgPlayer.setState(MPGPlayer.PlayerState.DC);
		    } else {
			    System.out.println("DC KILLING PLAYER " + mpgPlayer.getUsername());

			    // Drop their items
			    for (ItemStack item : player.getInventory().getContents()) {
				    if (item == null || item.getType() == Material.AIR) {
					    continue;
				    }

				    player.getWorld().dropItemNaturally(player.getLocation(), item);
			    }

			    for (ItemStack item : player.getInventory().getArmorContents()) {
				    if (item == null || item.getType() == Material.AIR) {
					    continue;
				    }

				    player.getWorld().dropItemNaturally(player.getLocation(), item);
			    }

			    // Send message saying that they died
			    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getDisguisedName() + ChatColor.RED + " died (quit the server)");

			    // Set to DEAD state
			    mpgPlayer.setState(MPGPlayer.PlayerState.DEAD);
		    }
	    }
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.POST_GAME) {
			event.setCancelled(true);
			return;
		}

		// Friendly fire check for team games
		if (MPG.GAME_TYPE == MPG.GameType.PARTY && (event.getEntity() instanceof Player || event.getEntity() instanceof Zombie)) {
			// Is friendly fire enabled?
			if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_FRIENDLY_FIRE)) {
				return;
			}

			Player damagerPlayer = null;

			if (event.getDamager() instanceof Player) {
				damagerPlayer = ((Player) event.getDamager());
			} else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
				damagerPlayer = (Player) ((Projectile) event.getDamager()).getShooter();
			}

			// Were they not hit by a player?
			if (damagerPlayer == null) {
				return;
			}

			MPGPlayer damagerMPGPlayer = MPGPlayerManager.getMPGPlayer(damagerPlayer);
			MPGPlayer damagedMPGPlayer = null;

			// Figure out if the player hit a player or a combat logger
			if (event.getEntity() instanceof Player) {
				damagedMPGPlayer = MPGPlayerManager.getMPGPlayer((Player) event.getEntity());
			} else if (CombatTagPlugin.getInstance().isCombatLogger(event.getEntity())) {
				damagedMPGPlayer = MPGPlayerManager.getMPGPlayer(CombatTagPlugin.getInstance().getCombatLoggerFromEntity(event.getEntity()).getUUID());
			} else {
				return;
			}

			// Are these players on the same team?
			if (damagerMPGPlayer.getTeam() == damagedMPGPlayer.getTeam()) {
				// Was the projectile a fishing rod?
				if (event.getDamager() instanceof FishHook) {
					// Allow fishing rods
					return;
				} else if (event.getDamager() instanceof Player) {
					ItemStack itemInHand = ((Player) event.getDamager()).getItemInHand();

					// Let teammates punch each other
					if (itemInHand == null || itemInHand.getType() == Material.AIR) {
						event.setDamage(0D);
						return;
					}
				}

				// Disable friendly fire damage
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = LASTER, ignoreCancelled = true)
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			double newHealth = player.getHealth() + event.getAmount();

			// Adding player health and the regain amount can go over the player's max health
			if (newHealth > player.getMaxHealth()) newHealth = player.getMaxHealth();

			mpgPlayer.updateHealthObjective(player, newHealth);
		}
	}

	@EventHandler(priority = EventPriority.LASTER, ignoreCancelled = true)
	public void onEntityDamageLastEvent(EntityDamageEvent event) {
		if (CheckForEndGame.getInstance().isGameEnding()
				|| MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.POST_GAME) {
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
			if (player.getHealth() - event.getFinalDamage() > 0D) {
				double newHealth = player.getHealth() - event.getFinalDamage();
				mpgPlayer.updateHealthObjective(player, newHealth);
			}
		}
	}

	@EventHandler
	public void onMiniPlayerSaveStatsEvent(MiniPlayerStatsSaveEvent event) {
		// Is this a clan game?
		if (MPG.getInstance().getMPGGame().isClanGame()) {
			// Don't save player ministats in clan games
			event.setCancelled(true);
		}
	}

}
