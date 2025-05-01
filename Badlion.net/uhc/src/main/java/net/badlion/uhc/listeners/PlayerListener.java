package net.badlion.uhc.listeners;

import net.badlion.colors.ColorChangeEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.FoodCommand;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.commands.handlers.BanCommandHandler;
import net.badlion.uhc.commands.handlers.GenerateSpawnsCommandHandler;
import net.badlion.uhc.commands.handlers.StartCommandHandler;
import net.badlion.uhc.commands.handlers.teams.DenyCommandHandler;
import net.badlion.uhc.commands.handlers.teams.LeaveCommandHandler;
import net.badlion.uhc.inventories.SpectatorInventory;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.practice.PracticeManager;
import net.badlion.uhc.tasks.DeathBanTask;
import net.badlion.uhc.tasks.GameTimeTask;
import net.badlion.uhc.tasks.TenMinutesOfflineTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void spectateItemInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& event.getItem() != null && event.getItem().getType() == Material.WATCH) {
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
			if (uhcPlayer.getState() == UHCPlayer.State.HOST || uhcPlayer.getState() == UHCPlayer.State.MOD
					|| uhcPlayer.getState() == UHCPlayer.State.SPEC || uhcPlayer.getState() == UHCPlayer.State.SPEC_IN_GAME) {
				SpectatorInventory.openSpectateInventory(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void playerLoginFirst(PlayerLoginEvent event) {
		// Op bypass
		if (event.getPlayer().isOp()) {
			event.setResult(PlayerLoginEvent.Result.ALLOWED);
			return;
		}

		// Host permission bypass
		if (event.getPlayer().hasPermission("badlion.uhctrial")) {
			event.setResult(PlayerLoginEvent.Result.ALLOWED);
			return;
		}

		// Initialize with KICK_OTHER so we can analyze what is going on
		event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
		event.setKickMessage("A UHC is not currently running. Check http://www.badlion.net/uhc/upcoming-matches for match times.");
	}

	@EventHandler(priority = EventPriority.LAST)
	public void playerLogin(PlayerLoginEvent event) {
		if (BadlionUHC.lockdown && !event.getPlayer().hasPermission("badlion.uhctrial")) {
			event.setKickMessage("UHC is currently being tested/worked on. Check website for future games.");
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			return;
		}

		// We got this far, if it's still KICK_OTHER it means nothing has tampered with this player
		if (event.getResult() == PlayerLoginEvent.Result.KICK_OTHER && BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());

			if (uhcPlayer != null) {
				if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
					event.setResult(PlayerLoginEvent.Result.ALLOWED);
					return;
				}

				if (uhcPlayer.getState() == UHCPlayer.State.DEAD) {
					event.setKickMessage("You have died! Better luck next time :)");
					return;
				}
			}

			if (BadlionUHC.getInstance().isMiniUHC()) {
				event.setKickMessage("The UHC has already started! Become a Donator+/Lion to spectate at http://store.badlion.net/.");
			} else {
				event.setKickMessage("The UHC has already started! Check http://www.badlion.net/uhc/upcoming-matches for match times.");
			}
		}
	}

	@EventHandler
	public void playerHasNotBeenTpd(final PlayerJoinEvent event) {
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());

		// Checking death location
		if (BanCommandHandler.deathLocations.containsKey(event.getPlayer().getUniqueId()) && uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
			Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
				@Override
				public void run() {
					event.getPlayer().teleport(BanCommandHandler.deathLocations.remove(event.getPlayer().getUniqueId()));
				}
			}, 1);
			return;
		}

		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED && uhcPlayer.getState() == UHCPlayer.State.PLAYER
				&& event.getPlayer().getWorld().getName().equals("uhclobby")) { // It's regular!
			BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
				@Override
				public void run() {
					BadlionUHC.getInstance().handlePlayerTeleportAndStart(event.getPlayer());
				}
			}, 1);
		}

		// Nether TP, i love cancer :D
		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED && uhcPlayer.getState() == UHCPlayer.State.PLAYER
				&& !BadlionUHC.getInstance().getConfigurator().getBooleanOption("nether").getValue()
				&& event.getPlayer().getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
			// They are in the nether, and they shouldn't be... Tp dem
			Location location = GenerateSpawnsCommandHandler.getNewLocation(BadlionUHC.getInstance().getBorderShrinkTask().currentRadius);

			if (location != null) {
				Gberry.safeTeleport(event.getPlayer(), location);
			} else {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mc " + uhcPlayer.getUsername() + " needs to be teleported to the main world.");
			}
		}
	}

	@EventHandler(priority = EventPriority.FIRST) // Needs to be first to stop worldedit from trying anything
	public void compassUseEvent(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		// Don't let nerds use the compass - failsafe
		if (event.getItem() != null && event.getItem().getType().equals(Material.COMPASS) && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerJumpOutOfWorld(EntityDamageEvent event) {
		// Hackjob to stop ppl from jumping out of the world
		if (event.getEntity() instanceof Player) {
			if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID) && event.getEntity().getWorld().getName().equals("uhclobby")) {
				event.getEntity().teleport(BadlionUHC.getInstance().getSpawnLocation());
			}

			// Damage teammates w/ health sharing
			if (BadlionUHC.getInstance().getGameType() != UHCTeam.GameType.SOLO) {
				// It's a teams game
				if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMHEALTHSHARE.name()) != null
						&& (boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMHEALTHSHARE.name()).getValue()) {
					// Team health sharing is on
					UHCTeam uhcTeam = UHCPlayerManager.getUHCPlayer(event.getEntity().getUniqueId()).getTeam();
					for (UUID uuid : uhcTeam.getUuids()) {
						Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
						if (player != null) {
							// They are online
							player.damage(event.getDamage());
							//player.setLastDamageCause(event);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void playerSendCommand(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();

		if (message != null) {
			Player player = event.getPlayer();
			if (message.toLowerCase().startsWith("/help") && !message.toLowerCase().startsWith("/helpop")) {
				event.setCancelled(true);

				player.sendMessage(ChatColor.AQUA + "=====Badlion UHC Commands=====");
				player.sendMessage(ChatColor.GOLD + "/h <player> - Look up a player's health");
				player.sendMessage(ChatColor.GOLD + "/g <message> - Sends a message to everyone on the server");
				player.sendMessage(ChatColor.GOLD + "/teams - Main command for custom teams");
			} else if (message.startsWith("/enchant")) {
				event.setCancelled(true);
			} else if (message.equalsIgnoreCase("/cosmetic") || message.equalsIgnoreCase("/cosmetics")
					|| message.equalsIgnoreCase("/cosmeticinventory") || message.equalsIgnoreCase("/cosmeticsinventory")) {
				UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
				if (PracticeManager.isInPractice(uhcPlayer)) {
					event.setCancelled(true);

					player.sendMessage(ChatColor.RED + "You can't access your cosmetics settings in the practice arena!");
				}
			}
		}
	}

	@EventHandler
	public void onColorChange(ColorChangeEvent event) {
		Player p = BadlionUHC.getInstance().getServer().getPlayer(event.getUuid());
		if (p != null) {
			BadlionUHC.getInstance().putDisplayName(event.getUuid(), p.getDisplayName());
		}
	}

	@EventHandler(priority = EventPriority.FIRST)
	public void playerJoin(final PlayerJoinEvent event){
		final Player player = event.getPlayer();

		BadlionUHC.getInstance().putUsername(player.getUniqueId(), player.getName());
		BadlionUHC.getInstance().putDisplayName(player.getUniqueId(), player.getDisplayName());

		// If their name is too long then shorten it
		String name = player.getName();

		try {
			if (name.length() > 12) {
				player.setPlayerListName(name.substring(0, 11));
			}
		} catch (IllegalArgumentException e) {
			// Put unrenderable character before name
			try {
				player.setPlayerListName((char) 0x26f8 + name.substring(0, 11));
			} catch (IllegalArgumentException e2) {
				// 3 players with same truncated name? Add 2 unrenderable characters
				try {
					player.setPlayerListName((char) 0x26f8 + (char) 0x26f8 + name.substring(0, 11));
				} catch (IllegalArgumentException e3) {
					player.kickPlayer("Your name conflicts with someone else, sorry this a bug on our side. This is a temp fix to stop corruption.");
					return;
				}
			}
		}

		// Send Welcome Msgs
		BadlionUHC.getInstance().sendWelcomeMessages(player);

		// Vanish stuff
		BadlionUHC.getInstance().hideAllVanishedPlayers(player);

		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
			UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
			if (uhcp != null) {
				// Check spectate permissions?
				uhcp.checkIfCanSpectate(player);

				// Data tracking (in case they logged out during countdown and joined back)
				if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
					if (MiniStats.getInstance().getPlayerDataListener().getPlayerData(uhcp.getUUID()) == null) {
						PlayerData playerData = new PlayerData(uhcp.getUUID(), player.getName(), BadlionUHC.UHCWORLD_NAME);
						MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(uhcp.getUUID(), playerData);
					}
				}

				// Give them food if they dc'd
				if (FoodCommand.lastFoodAmountGiven != 0 && !uhcp.isWasFed()) {
					uhcp.setWasFed(true);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, FoodCommand.lastFoodAmountGiven));
				}

				// Nether TP mateeeeee
				if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).getValue() != null &&
						!((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).getValue())) {
					if (player.getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)) {
						player.sendMessage(ChatColor.RED + "You logged out during nether TP! You have been teleported to the overworld.");
						player.teleport(netherToNormalLocation(player.getWorld(), player.getLocation()));
					}
				}

				if (uhcp.getState().ordinal() >= UHCPlayer.State.SPEC.ordinal()) {
					player.getInventory().clear();
					player.getInventory().setArmorContents(null);

					BadlionUHC.getInstance().teleportToMainWorldAndVanish(player);
				} else {
					player.setGameMode(GameMode.SURVIVAL);
					uhcp.setAFKTimeLeft(null);

					// Not in any of our UHC Worlds (tp somewhere random within -1000 and 1000 each way
					if (!player.getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME) && !player.getWorld().getName().equals(BadlionUHC.UHCWORLD_NETHER_NAME)
							&& !player.getWorld().getName().equals(BadlionUHC.UHCWORLD_END_NAME)) {
						// TODO: CHECK IF THIS EDGE CASE WORKS (RESISTANCE + DMG ETC)
						BadlionUHC.getInstance().handlePlayerTeleportAndStart(player);
					} else {
						// Remove bad potion effects
						BadlionUHC.getInstance().removeBeginningPotionEffects(player);
					}

					// Give inventory if they got undeathbanned
					if (uhcp.isGiveInventory()) {
						player.getInventory().setContents(uhcp.getInventory());
						player.getInventory().setArmorContents(uhcp.getArmor());
						player.teleport(uhcp.getDeathLocation());
						uhcp.setGiveInventory(false);
						uhcp.setArmor(null);
						uhcp.setInventory(null);
						uhcp.setDeathLocation(null);
					}
				}
			} else {
				// If it hasn't been 20 game minutes yet
				if (!BadlionUHC.getInstance().isMiniUHC() && GameTimeTask.getNumOfSeconds() < 1200) {
					uhcp = UHCPlayerManager.addNewUHCPlayer(player.getUniqueId(), player.getName(), UHCPlayer.State.PLAYER);
					StartCommandHandler.prepPlayerForStart(player);

					// Check spectate permissions?
					uhcp.checkIfCanSpectate(player);

					// Data tracking
					if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
						PlayerData playerData = new PlayerData(uhcp.getUUID(), player.getName(), BadlionUHC.UHCWORLD_NAME);
						MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(uhcp.getUUID(), playerData);
					}

					// If we have custom teams we might be teleporting somewhere
					if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
						StartCommandHandler.handleMergingTeams(uhcp, uhcp.getTeam());

						// We got added to a team just now
						if (StartCommandHandler.teamNeedingPlayers == null) {
							Player p = BadlionUHC.getInstance().getServer().getPlayer(uhcp.getTeam().getLeader());

							// Try to teleport to the leader if we can
							if (p != null) {
								player.teleport(p.getLocation());
							} else {
								player.teleport(GenerateSpawnsCommandHandler.getNewLocation());
							}
						} else {
							// We are making a new team
							if (StartCommandHandler.teamNeedingPlayers == uhcp.getTeam()) {
								player.teleport(GenerateSpawnsCommandHandler.getNewLocation());
							}
						}
					} else {
						player.teleport(GenerateSpawnsCommandHandler.getNewLocation());
					}

					if (FoodCommand.lastFoodAmountGiven != 0) {
						uhcp.setWasFed(true);
						player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, FoodCommand.lastFoodAmountGiven));
					}

					// Give them 5 sec cuz they are about to fall
					BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
						@Override
						public void run() {
							BadlionUHC.getInstance().removeBeginningPotionEffects(player);
						}
					}, 5 * 20);
				} else {
					// Donator+ or others can come and spectate
					UHCPlayerManager.addNewUHCPlayer(player.getUniqueId(), player.getName(), UHCPlayer.State.SPEC);
					BadlionUHC.getInstance().teleportToMainWorldAndVanish(player);
				}
			}
		} else {
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
			boolean justCreated = false;
			if (uhcPlayer == null) {
				justCreated = true;
				uhcPlayer = UHCPlayerManager.addNewUHCPlayer(player.getUniqueId(), player.getName(), UHCPlayer.State.PLAYER);
			}

			// Make intellij happy
			if (uhcPlayer == null) {
				return;
			}

			// Fix any boo boos from typos and crap
			uhcPlayer.setUsername(player.getName());

			final UHCPlayer finalUHCPlayer = uhcPlayer;

			// Check spectate permissions?
			finalUHCPlayer.checkIfCanSpectate(player);

			// Still in lobby, tp them back
			if (BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal() || player.getLocation().getWorld().getName().equals("uhclobby")) {
				Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
					public void run() {
						BadlionUHC.getInstance().handlePlayerJoinLobby(finalUHCPlayer, player);
					}
				}, 1L);
			} else if (BadlionUHC.getInstance().getState().ordinal() == BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal() && player.hasPermission("badlion.donatorplus")) {
				if (justCreated) {
					Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
						public void run() {
							BadlionUHC.getInstance().handlePlayerJoinLobby(finalUHCPlayer, player);
						}
					}, 1L);
				}
			}
		}

		if (!VanishedPlayerListener.isVanishedPlayer(player)) {
			player.setGameMode(GameMode.SURVIVAL);
		}

		ScoreboardUtil.getNewScoreboard(player);

		// Set health scoreboard
		if (BadlionUHC.getInstance().getConfigurator() != null) {
			Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
				public void run() {
					UHCPlayerManager.updateHealthScores(player);
				}
			}, 20L);
		}
	}

	// TODO: Do we need this?
	/*@EventHandler(priority = EventPriority.LAST)
	public void onJoin(PlayerJoinEvent event) {
		Bukkit.broadcastMessage("Player has joined");
		if (!GameModeHandler.GAME_MODES.contains("QUADRANTS")) {
			Bukkit.broadcastMessage("Quadrants is not active");
			return;
		}
		Bukkit.broadcastMessage("Quadrants is active");
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
			Bukkit.broadcastMessage("Game is not pre-game");
			return;
		}
		Bukkit.broadcastMessage("Game is pre-game");
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.getInventory().addItem(ItemStackUtil.createItem(Material.WOOL, ChatColor.RED + "Choose Team"));
		player.updateInventory();
		Bukkit.broadcastMessage("Player has wool - " + player.getInventory().contains(Material.WOOL));
	}*/

	private Location netherToNormalLocation(final World world, final Location originalLocation) {
		if (world == null || originalLocation == null) {
			return null;
		}

		// Get original coordinates
		double x = originalLocation.getX();
		double y = originalLocation.getY();
		double z = originalLocation.getZ();

		// Transform them
		x *= 8;
		y *= 2;
		z *= 8;

		// Try to be on the ground !
		y = Math.min(y, originalLocation.getWorld().getHighestBlockYAt((int) x, (int) z));

		// Create the Location and return it
		return new Location(world, x, y, z);
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
			BadlionUHC.getInstance().updateDeathState(event.getEntity());
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getEntity().getUniqueId());
			uhcPlayer.setInventory(event.getEntity().getInventory().getContents());
			uhcPlayer.setArmor(event.getEntity().getInventory().getArmorContents());
			uhcPlayer.setDeathLocation(event.getEntity().getLocation());

			// Give killer a kill
			Player killer = event.getEntity().getKiller();
			if (killer != null) {
				UHCPlayer killerUHCPlayer = UHCPlayerManager.getUHCPlayer(killer.getUniqueId());
				killerUHCPlayer.addKill();
			}

			MessageUtil.handleDeathMessage(event, event.getEntity(), killer);

			BadlionUHC.getInstance().checkForWinners();
			BanCommandHandler.deathLocations.put(event.getEntity().getUniqueId(), event.getEntity().getLocation());
		}
	}

	@EventHandler
	public void playerRespawn(final PlayerRespawnEvent event) {
		final UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
			Bukkit.getLogger().info(uhcPlayer.getState().name());
			if (uhcPlayer.getState().ordinal() >= UHCPlayer.State.SPEC.ordinal()) {
				BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
					@Override
					public void run() {
						World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
						event.getPlayer().teleport(new Location(w, 0, w.getHighestBlockYAt(0, 0) + 10, 0));

						event.getPlayer().setGameMode(GameMode.CREATIVE);
						event.getPlayer().setFlying(true);
						event.getPlayer().spigot().setCollidesWithEntities(false);
						event.getPlayer().sendMessage(ChatColor.AQUA + "Thank you for playing a UHC provided by the Badlion Network!");
						event.getPlayer().sendMessage(ChatColor.AQUA + "You can now spectate other players but will be unable to interact with the game.");
						event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS), BadlionUHC.getInstance().getSpectatorItem());
						uhcPlayer.setDeathTime(System.currentTimeMillis());

						VanishCommand.vanishPlayer(event.getPlayer());
					}
				}, 5L);
			} else { // Regular scrub
				// Kick em after they respawn
				BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
					@Override
					public void run() {
						event.getPlayer().teleport(BadlionUHC.getInstance().getSpawnLocation());
						event.getPlayer().sendMessage(ChatColor.AQUA + "Thank you for playing a UHC provided by the Badlion Network!");
						event.getPlayer().sendMessage(ChatColor.AQUA + "You will have 30 seconds to say gg or gf before you get kicked off the server.");
						uhcPlayer.setDeathTime(System.currentTimeMillis());
					}
				}, 5L);
				BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new DeathBanTask(event.getPlayer()), 600L);
			}
		} else {
			// Respawn them in the UHC lobby
			event.setRespawnLocation(BadlionUHC.getInstance().getSpawnLocation());
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void playerDamageByEntity(EntityDamageByEntityEvent e) {
		if (!BadlionUHC.getInstance().isPVP()) {
			Entity entity = e.getEntity();
			if (entity instanceof Player) {
				Entity damager = e.getDamager();
				if (damager instanceof Projectile) {
					if (((Projectile) damager).getShooter() instanceof Player) {
						UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(((Player) ((Projectile) damager).getShooter()).getUniqueId());
						if (uhcPlayer.getState() == UHCPlayer.State.MOD) {
							e.setCancelled(true);

							// Send a warning
							if (!((Player) ((Projectile) damager).getShooter()).getUniqueId().equals(BadlionUHC.getInstance().getHost().getUUID())) {
								((Player) e.getDamager()).sendMessage(ChatColor.RED + "You cannot shoot players! A message has been sent to the host.");

								UHCPlayer host = BadlionUHC.getInstance().getHost();
								Player p = BadlionUHC.getInstance().getServer().getPlayer(host.getUUID());
								if (p != null) {
									p.sendMessage(ChatColor.RED + "[WARN] Moderator " + ((Player) e.getDamager()).getName() + " tried to shoot a player!");
								}
							} else {
								if (!PracticeManager.isInPractice(uhcPlayer)) {
									((Player) e.getDamager()).sendMessage(ChatColor.RED + "You cannot shoot players!");
								}
							}
						} else {
							if (!PracticeManager.isInPractice(uhcPlayer)) {
								((Player) ((Projectile) damager).getShooter()).sendMessage(ChatColor.RED + "PVP is currently disabled.");
								e.setCancelled(true);
							}
						}
					}
				} else if (damager instanceof Player) {
					UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(damager.getUniqueId());
					if (uhcPlayer.getState() == UHCPlayer.State.MOD) {
						e.setCancelled(true);
					} else {
						if (!PracticeManager.isInPractice(uhcPlayer)) {
							((Player) damager).sendMessage(ChatColor.RED + "PVP is currently disabled.");
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
		if (uhcPlayer != null) {
			if (BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
				// Hack some fast cleanup
				if (uhcPlayer.getTeamRequest() != null) {
					DenyCommandHandler.handleDenyCommand(event.getPlayer(), new String[1]);
				} else if (BadlionUHC.getInstance().getGameType().equals(UHCTeam.GameType.TEAM)) {
					LeaveCommandHandler.handleLeaveCommand(event.getPlayer(), new String[1]);
				}

				UHCPlayerManager.removeUHCPlayer(event.getPlayer().getUniqueId());
			} else if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.COUNTDOWN  // Check countdown too (this is probably the bug w/ ppl still being in the game even though they arent [scoreboard bug])
					|| BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED) {
				if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
					if (BadlionUHC.getInstance().getConfig().getBoolean("anti-afk", true)) {
						uhcPlayer.setOfflineTask(new TenMinutesOfflineTask(uhcPlayer).runTaskLater(BadlionUHC.getInstance(), 60 * 20 * 10));
					}
				}
			}
		}
	}

}
