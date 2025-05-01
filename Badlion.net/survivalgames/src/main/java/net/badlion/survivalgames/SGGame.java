package net.badlion.survivalgames;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.FireWorkUtil;
import net.badlion.gberry.utils.Pair;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.MPGWorld;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import net.badlion.survivalgames.inventories.SelectionChestInventory;
import net.badlion.survivalgames.listeners.PreGameListener;
import net.badlion.survivalgames.managers.SGSidebarManager;
import net.badlion.survivalgames.tasks.ChestRefillTask;
import net.badlion.survivalgames.tasks.SupplyDropTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchBoundaryTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchCheckerTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchDamageTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchStartCountdownTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchTeleportCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SGGame extends MPGGame {

	private static final int PRE_DEATHMATCH_DAMAGE_THRESHOLD = 20;

	private int position = 0;

	private Listener preGameListener;

    private boolean chestsRefilled = false;

	private Map<UUID, Integer> ratings = new HashMap<>();

	private int deathmatchStartTime;

	private Map<Integer, Map<Location, Inventory>> tierChests = new HashMap<>();
	private Map<Location, SupplyDropTask.SupplyDrop> supplyDropChests = new HashMap<>();

    public SGGame(Gamemode gamemode) {
	    super(gamemode);

	    // Initialize selection chest inventory
	    SelectionChestInventory.initialize();
    }

	@Override
	public void preGame() {
		this.preGameListener = new PreGameListener();
		SurvivalGames.getInstance().getServer().getPluginManager().registerEvents(this.preGameListener, SurvivalGames.getInstance());

		final Set<Integer> usedSpawnLocations = new HashSet<>();

		final Map<UUID, Location> playerLocations = new HashMap<>();

		final List<MPGTeam> teams = MPGTeamManager.getAllMPGTeams();

		final Iterator<MPGTeam> it = teams.iterator();

		// Teleport one player per tick
		new BukkitRunnable() {
			private final int numberOfSpawnLocations = SGGame.this.getWorld().getSpawnLocations().size();

			@Override
			public void run() {
				if (!it.hasNext()) {
					// Start pregame countdown task
					new PreGameCountdownTask(playerLocations).runTaskTimer(MPG.getInstance(), 0L, 1L);

					// Set to countdown state
					SGGame.this.setGameState(GameState.GAME_COUNTDOWN);

					this.cancel();
					return;
				}

				MPGTeam team = it.next();

				// FFA uses random spawn locations and same with PARTY games,
				// but teammates need to be spawned next to each other in PARTY games.

				// Clans use the RBXXRBXXRXXRBXXRBXXBXX spawn pattern
				if (SGGame.this.isClanGame()) {
					int n = 0;
					final int[] team1Spawns = new int[]{0, 4, 8, 11, 14};

					for (UUID uuid : team.getUUIDs()) {
						playerLocations.put(uuid, SGGame.this.getWorld().getSpawnLocation(team1Spawns[n++]));
					}

					n = 0;
					final int[] team2Spawns = new int[]{1, 5, 12, 16, 19};

					for (UUID uuid : it.next().getUUIDs()) {
						playerLocations.put(uuid, SGGame.this.getWorld().getSpawnLocation(team2Spawns[n++]));
					}
				} else if (MPG.GAME_TYPE == MPG.GameType.FFA) {
					// Get a random unused spawn location number
					int n;
					do {
						n = Gberry.generateRandomInt(0, this.numberOfSpawnLocations - 1);
					} while (!usedSpawnLocations.add(n));

					playerLocations.put(team.getLeader(), SGGame.this.getWorld().getSpawnLocation(n));
				} else {
					// Get a random unused spawn location number
					int n;
					do {
						n = Gberry.generateRandomInt(0, (this.numberOfSpawnLocations - 1) / 2);
					} while (!usedSpawnLocations.add(n));

					n *= 2;

					for (UUID uuid : team.getUUIDs()) {
						playerLocations.put(uuid, SGGame.this.getWorld().getSpawnLocation(n++));
					}
				}
			}
		}.runTaskTimer(SurvivalGames.getInstance(), 0L, 1L);
	}
	@Override
	public void startGame() {
		// Disable old listener
		HandlerList.unregisterAll(this.preGameListener);

		Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + "GO!");

		Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

		// Clear random entities
		this.world.clearNonPlayerEntities();

		// Start off chest refill task
		new ChestRefillTask().runTaskTimer(SurvivalGames.getInstance(), 0L, 4L);

		// Start off supply drop task
		new SupplyDropTask().runTaskTimer(SurvivalGames.getInstance(), 0L, 4L);

		// Start off deathmatch checker task
		new DeathMatchCheckerTask().runTaskTimer(SurvivalGames.getInstance(), 0L, 1L);

		// Register sidebar manager
		SurvivalGames.getInstance().getServer().getPluginManager().registerEvents(new SGSidebarManager(), SurvivalGames.getInstance());
	}

	@Override
	public void preDeathMatch() {
		// Set start time incase deathmatch started early
		this.deathmatchStartTime = GameTimeTask.getInstance().getTotalSeconds()
				+ MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME);

		// Start teleport countdown task
		new DeathMatchTeleportCountdownTask().runTaskTimer(MPG.getInstance(), 0L, 20L);
	}

	@Override
	public void deathMatchCountdown() {
		// Broadcast at old locations
		Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

		final Set<Integer> usedSpawnLocations = new HashSet<>();

		Map<UUID, Location> playerLocations = new HashMap<>();

		final List<MPGTeam> teams = MPGTeamManager.getAllMPGTeams();

		final Iterator<MPGTeam> it = teams.iterator();

		final int numberOfSpawnLocations = SGGame.this.getWorld().getDeathMatchLocations().size();

		// Go through, teleport them to the death match arena
		while (it.hasNext()) {
			MPGTeam team = it.next();

			// Does this team have no alive players?
			boolean hasAlivePlayers = false;
			for (UUID uuid : team.getUUIDs()) {
				MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

				if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER || mpgPlayer.getState() == MPGPlayer.PlayerState.DC) {
					hasAlivePlayers = true;
				}
			}

			if (!hasAlivePlayers) continue;

			// Is this a clan game?
			// Clans use the RRRRRXBBBBBX spawn pattern
			if (SGGame.this.isClanGame()) {
				int n = 0;
				final int[] team1Spawns = new int[]{0, 1, 2, 3, 4};

				for (UUID uuid : team.getUUIDs()) {
					SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(uuid);

					// Skip dead people
					if (sgPlayer.getState().ordinal() > MPGPlayer.PlayerState.DC.ordinal()) continue;

					Location location = SGGame.this.getWorld().getDeathMatchLocation(team1Spawns[n++]);
				 	playerLocations.put(uuid, location);

					// Teleport the player/logger npc
					if (sgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
						sgPlayer.getPlayer().teleport(location);
					} else {
						CombatTagPlugin.getInstance().getLogger(sgPlayer.getUniqueId()).getEntity().teleport(location);
					}

					// Penalize alive players who haven't dealt enough damage before deathmatch
					PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().get(sgPlayer.getUniqueId());
					if (playerData.getDamageDealt() >= SGGame.PRE_DEATHMATCH_DAMAGE_THRESHOLD) {
						sgPlayer.setReachedPreDMDamageThreshold(true);
					}
				}

				n = 0;
				final int[] team2Spawns = new int[]{6, 7, 8, 9, 10};

				for (UUID uuid : it.next().getUUIDs()) {
					SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(uuid);

					// Skip dead people
					if (sgPlayer.getState().ordinal() > MPGPlayer.PlayerState.DC.ordinal()) continue;

					Location location = SGGame.this.getWorld().getDeathMatchLocation(team2Spawns[n++]);
					playerLocations.put(uuid, location);

					// Teleport the player/logger npc
					if (sgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
						sgPlayer.getPlayer().teleport(location);
					} else {
						CombatTagPlugin.getInstance().getLogger(sgPlayer.getUniqueId()).getEntity().teleport(location);
					}

					// Penalize alive players who haven't dealt enough damage before deathmatch
					PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().get(sgPlayer.getUniqueId());
					if (playerData.getDamageDealt() >= SGGame.PRE_DEATHMATCH_DAMAGE_THRESHOLD) {
						sgPlayer.setReachedPreDMDamageThreshold(true);
					}
				}

				break;
			}

			// FFA uses random spawn locations and same with PARTY games,
			// but teammates need to be spawned next to each other in PARTY games

			int n;
			if (MPG.GAME_TYPE == MPG.GameType.FFA) {
				// Get a random unused spawn location number
				do {
					n = Gberry.generateRandomInt(0, numberOfSpawnLocations - 1);
				} while (!usedSpawnLocations.add(n));

				playerLocations.put(team.getLeader(), SGGame.this.getWorld().getDeathMatchLocation(n));
			} else {
				// Get a random unused spawn location number
				do {
					n = Gberry.generateRandomInt(0, (numberOfSpawnLocations - 1) / 2);
				} while (!usedSpawnLocations.add(n));

				n *= 2;
			}

			for (UUID uuid : team.getUUIDs()) {
				SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(uuid);

				// Skip dead people
				if (sgPlayer.getState().ordinal() > MPGPlayer.PlayerState.DC.ordinal()) continue;

				Location location = SGGame.this.getWorld().getDeathMatchLocation(n++);

				playerLocations.put(uuid, location);

				// Teleport the player/logger npc
				if (sgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
					System.out.println(sgPlayer.getUsername() + " | " + location);
					System.out.println(sgPlayer.getUsername() + " BEFORE: " + sgPlayer.getPlayer().getLocation());
					sgPlayer.getPlayer().teleport(location);
					System.out.println(sgPlayer.getUsername() + " AFTER: " + sgPlayer.getPlayer().getLocation());
				} else {
					CombatTagPlugin.getInstance().getLogger(sgPlayer.getUniqueId()).getEntity().teleport(location);
				}

				// Penalize alive players who haven't dealt enough damage before deathmatch
				PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().get(sgPlayer.getUniqueId());
				if (playerData.getDamageDealt() >= SGGame.PRE_DEATHMATCH_DAMAGE_THRESHOLD) {
					sgPlayer.setReachedPreDMDamageThreshold(true);
				}
			}
		}

		// Teleport spectators to the deathmatch spectator location a tick later
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.SPECTATOR)) {
					Player player = mpgPlayer.getPlayer();
					if (player != null) {
						player.teleport(SGGame.this.getWorld().getSpectatorLocation());
					}
				}
			}
		});

		// Broadcast at new locations
		Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

		// Start countdown task
		new DeathMatchStartCountdownTask(playerLocations).runTaskTimer(MPG.getInstance(), 0L, 1L);
	}

	@Override
	public void deathMatch() {
		Gberry.broadcastMessageNoBalance(MPGGame.DM_PREFIX + "FIGHT!");

		Gberry.broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

		// Start damage task if deathmatch goes on for too long
		// (Has to be 1 tick after so state is changed by then)
		new DeathMatchDamageTask().runTaskTimer(MPG.getInstance(), 1L, 1L);

		// Start task to force players within deathmatch bounds if there is no special deathmatch arena
		if (!SurvivalGames.getInstance().getSGGame().getWorld().hasDeathmatchArena()) {
			// (Has to be 1 tick after so state is changed by then)
			new DeathMatchBoundaryTask().runTaskTimer(MPG.getInstance(), 1L, 5L);
		}
	}

	@Override
	public boolean checkForEndGame() {
		ConcurrentLinkedQueue<MPGPlayer> onlineSGPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER);
		ConcurrentLinkedQueue<MPGPlayer> offlineSGPlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DC);

		int maxNumberOfPlayersToEndGame = 1;

		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			maxNumberOfPlayersToEndGame = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.PLAYERS_PER_TEAM);
		}

		// Do we have few enough people to end the game?
		if (onlineSGPlayers.size() + offlineSGPlayers.size() <= maxNumberOfPlayersToEndGame) {
			SGPlayer sgPlayer;

			// Get the first player
			if (!onlineSGPlayers.isEmpty()) {
				sgPlayer = (SGPlayer) onlineSGPlayers.iterator().next();
			} else {
				sgPlayer = (SGPlayer) offlineSGPlayers.iterator().next();
			}

			MPGTeam team = sgPlayer.getTeam();

			// Do additional checks for the team if this is a party game
			if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
				// Make sure that all alive players are part of this team
				for (MPGPlayer onlineSGPlayer : onlineSGPlayers) {
					if (sgPlayer == onlineSGPlayer) continue;

					if (team != onlineSGPlayer.getTeam()) {
						return false;
					}
				}

				for (MPGPlayer offlineSGPlayer : offlineSGPlayers) {
					if (sgPlayer == offlineSGPlayer) continue;

					if (team != offlineSGPlayer.getTeam()) {
						return false;
					}
				}
			}

			final List<UUID> teamUUIDs = team.getUUIDs();
			for (UUID uuid : teamUUIDs) {
				SGPlayer teamSGPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(uuid);

				// Increment internal cache of games won
				teamSGPlayer.incrementGamesWon();

				// Add this player to the winners list
				this.addWinner(uuid);
			}

			// Is this not a clan game?
			if (!this.isClanGame()) {
				// Add wins in the ratings table
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						String query = "UPDATE sg_ladder_ratings_s2 SET wins = wins + 1 WHERE uuid = ?";

						for (int i = 1; i < teamUUIDs.size(); i++) {
							query += " or uuid = ?";
						}

						query += ";";

						Connection connection = null;
						PreparedStatement ps = null;

						try {
							connection = Gberry.getConnection();

							ps = connection.prepareStatement(query);

							for (int i = 0; i < teamUUIDs.size(); i++) {
								ps.setString(i + 1, teamUUIDs.get(i).toString());
							}

							Gberry.executeUpdate(connection, ps);

						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							Gberry.closeComponents(ps, connection);
						}
					}
				});
			}

			// Launch fireworks
			for (Location location : this.getWorld().getSpawnLocations()) {
				FireWorkUtil.shootFirework(location);
			}

			for (Location location : this.getWorld().getDeathMatchLocations()) {
				FireWorkUtil.shootFirework(location);
			}

			return true;
		}

		return false;
	}

	@Override
	public void endGame(Map<String, Object> extraPayload) {
		Map<String, Object> ratingData = new HashMap<>();

		Iterator<UUID> it = this.getWinners().iterator();

		// Is this a clan game?
		if (this.isClanGame()) {
			while (it.hasNext()) {
				UUID uuid = it.next();
				SGPlayer sgPlayer = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid));

				this.fillRatingData(ratingData, uuid, sgPlayer);
			}
		} else if (MPG.GAME_TYPE == MPG.GameType.PARTY) { // Is this a party game?
			UUID uuid1 = it.next();
			UUID uuid2 = it.next();

			SGPlayer sgPlayer1 = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid1));
			SGPlayer sgPlayer2 = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid2));

			// Are both players alive at the end?
			if (sgPlayer1.getState() == MPGPlayer.PlayerState.PLAYER && sgPlayer2.getState() == MPGPlayer.PlayerState.PLAYER) {
				// Player 1 and player 2 are alive
				int rating1 = this.getPlayerRating(uuid1);
				int rating2 = this.getPlayerRating(uuid2);

				// Does player 1 have the higher rating?
				if (rating1 > rating2) {
					// Fill player 2's data
					this.fillRatingData(ratingData, uuid2, sgPlayer2);

					// Fill player 1's data
					this.fillRatingData(ratingData, uuid1, sgPlayer1);
				} else {
					// Fill player 1's data
					this.fillRatingData(ratingData, uuid1, sgPlayer1);

					// Fill player 2's data
					this.fillRatingData(ratingData, uuid2, sgPlayer2);
				}
			} else if (sgPlayer1.getState() == MPGPlayer.PlayerState.PLAYER) {
				// Player 1 is alive
				this.fillRatingData(ratingData, uuid1, sgPlayer1);
			} else {
				// Player 2 is alive
				this.fillRatingData(ratingData, uuid2, sgPlayer2);
			}
		} else {
			UUID uuid = it.next();
			SGPlayer sgPlayer = ((SGPlayer) MPGPlayerManager.getMPGPlayer(uuid));

			this.fillRatingData(ratingData, uuid, sgPlayer);
		}

		extraPayload.put("rating_data", ratingData);

		super.endGame(extraPayload);
	}

	public void openChest(final Player player, SGPlayer sgPlayer, Location location) {
		Pair<Integer, Inventory> pair = this.getTierChestInventory(location);
		final Inventory inventory = pair.getB();

		boolean isEmpty = true;
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				isEmpty = false;
				break;
			}
		}

		// Has anybody else opened the chest?
		if (inventory.getViewers().isEmpty()) {
			// Broadcast chest open sound
			location.getWorld().playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_OPEN", "BLOCK_CHEST_OPEN"), 0.5F, (float) Math.random() * 0.1F + 0.9F);

			// Create chest open packet
			Object blockActionPacket = TinyProtocolReferences.invokeBlockActionPacketConstructor(location.getBlockX(),
					location.getBlockY(), location.getBlockZ(), TinyProtocolReferences.getNMSBlock(location.getBlock()), 1, 1);

			// Broadcast to everyone
			for (Player pl : Bukkit.getOnlinePlayers()) {
				Gberry.protocol.sendPacket(pl, blockActionPacket);
			}
		} else {
			// Play chest open sound only to the player opening it
			player.playSound(location, EnumCommon.getEnumValueOf(Sound.class, "CHEST_OPEN", "BLOCK_CHEST_OPEN"), 0.5F, (float) Math.random() * 0.1F + 0.9F);
		}

		// Open the chest
		BukkitUtil.openInventory(player, inventory);

		// Don't count stats if chests have refilled, if player already opened this chest, or if chest is empty
		if (!this.chestsRefilled && !isEmpty && !sgPlayer.getChestLocationsOpened().contains(location)) {
			sgPlayer.getChestLocationsOpened().add(location);

			sgPlayer.incrementNumberTierChestsOpened(pair.getA());
		}
	}

	private void fillRatingData(Map<String, Object> ratingData, UUID uuid, SGPlayer sgPlayer) {
		Map<String, Object> map = new HashMap<>();

		// Calculate this player's score
		int score = sgPlayer.calculateScore();

		// Insert extra data into payload
		map.put("score", score);
		map.put("position", sgPlayer.getPosition());

		ratingData.put(uuid.toString(), map);
	}

	public void openSupplyDrop(final Player player, SGPlayer sgPlayer, SupplyDropTask.SupplyDrop supplyDrop) {
		Inventory inventory = supplyDrop.getInventory();

		boolean isEmpty = true;
		for (ItemStack item : inventory.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				isEmpty = false;
				break;
			}
		}

		// Has anybody else opened the chest?
		if (inventory.getViewers().isEmpty()) {
			// Broadcast chest open sound
			supplyDrop.getLocation().getWorld().playSound(supplyDrop.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CHEST_OPEN", "BLOCK_CHEST_OPEN"), 0.5F, (float) Math.random() * 0.1F + 0.9F);

			// Create chest open packet
			Object blockActionPacket = TinyProtocolReferences.invokeBlockActionPacketConstructor(
					supplyDrop.getLocation().getBlockX(), supplyDrop.getLocation().getBlockY(),
					supplyDrop.getLocation().getBlockZ(), TinyProtocolReferences.getNMSBlock(supplyDrop.getLocation().getBlock()), 1, 1);

			// Broadcast to everyone
			for (Player pl : Bukkit.getOnlinePlayers()) {
				Gberry.protocol.sendPacket(pl, blockActionPacket);
			}
		} else {
			// Play chest open sound only to the player opening it
			supplyDrop.getLocation().getWorld().playSound(supplyDrop.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CHEST_OPEN", "BLOCK_CHEST_OPEN"), 0.5F, (float) Math.random() * 0.1F + 0.9F);
		}

		// Open the chest
		BukkitUtil.openInventory(player, inventory);

		// Don't count stats if player already opened this supply drop or if supply drop is empty
		if (!isEmpty && !sgPlayer.getSupplyDropLocationsOpened().contains(supplyDrop.getLocation())) {
			sgPlayer.getSupplyDropLocationsOpened().add(supplyDrop.getLocation());
		}
	}

	public void fillChests() {
		for (Map.Entry<Integer, Map<Location, Inventory>> entry : this.tierChests.entrySet()) {
			for (Map.Entry<Location, Inventory> entry2 : entry.getValue().entrySet()) {
				this.fillChest(entry2.getValue(), entry.getKey());
			}
		}
	}

	public void refillChests() {
		this.fillChests();

		this.chestsRefilled = true;
	}

	public void getDBUserRatings(final UUID uuid) {
		if (this.ratings.containsKey(uuid)) return;

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM sg_ladder_ratings_s2 WHERE uuid = ? AND gamemode = ?;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, uuid.toString());
					ps.setString(2, SGGame.this.gamemode.getName().toLowerCase());

					rs = Gberry.executeQuery(connection, ps);

					if (rs.next()) {
						SGGame.this.ratings.put(uuid, rs.getInt("rating"));
					} else {
						SGGame.this.ratings.put(uuid, RatingUtil.DEFAULT_RATING);

						// Don't leak connections!!!
						Gberry.closeComponents(ps);

						query = "INSERT INTO sg_ladder_ratings_s2 (gamemode, uuid, rating, wins, losses) VALUES (?, ?, ?, ?, ?);";

						ps = connection.prepareStatement(query);

						ps.setString(1, "classic");
						ps.setString(2, uuid.toString());
						ps.setInt(3, RatingUtil.DEFAULT_RATING);
						ps.setInt(4, 0);
						ps.setInt(5, 0);

						Gberry.executeUpdate(connection, ps);
					}

					// Increment the max position for this game
					SGGame.this.incrementPosition();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		});
	}

	public Pair<Integer, Inventory> getTierChestInventory(Location location) {
		for (Map.Entry<Integer, Map<Location, Inventory>> entry : this.tierChests.entrySet()) {
			Inventory inventory = entry.getValue().get(location);

			if (inventory != null) return Pair.of(entry.getKey(), inventory);
		}

		return null;
	}

    @Override
    public SGWorld getWorld() {
        return (SGWorld) this.world;
    }

	@Override
	public void setWorld(MPGWorld world) {
		super.setWorld(world);

		// Set region flags
		this.region.setAllowPistonUsage(true);
	}

	/**
	 * Returns the position after the increment
	 */
	public int incrementPosition() {
		return ++this.position;
	}

	/**
	 * Returns the position before the decrement
	 */
	public int decrementPosition() {
		return this.position--;
	}

	public int getPlayerRating(UUID uuid) {
		return this.ratings.get(uuid);
	}

	public void addSupplyDropChest(Location location, SupplyDropTask.SupplyDrop supplyDrop) {
		this.supplyDropChests.put(location, supplyDrop);
	}

	public SupplyDropTask.SupplyDrop getSupplyDropChest(Location location) {
		return this.supplyDropChests.get(location);
	}

	public Map<Integer, Map<Location, Inventory>> getTierChests() {
		return tierChests;
	}

	public boolean areChestsRefilled() {
        return this.chestsRefilled;
    }

	public int getDeathmatchStartTime() {
		return this.deathmatchStartTime;
	}

	public void setDeathmatchStartTime() {
		this.deathmatchStartTime = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_START_TIME)
				+ MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME);
	}

}
