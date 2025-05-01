package net.badlion.mpg;

import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.NameTagUtil;
import net.badlion.ministats.Game;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.bukkitevents.MPGGameStateChangeEvent;
import net.badlion.mpg.exceptions.IllegalGameStateTransition;
import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.inventories.SkullPlayerInventory;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.listeners.GameListener;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.CheckForEndGame;
import net.badlion.mpg.tasks.DisconnectTimerTask;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import net.badlion.mpg.tasks.VoidCheckerTask;
import net.badlion.mpg.tasks.VotingTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class MPGGame extends Game {

	public static final String DM_PREFIX = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "DeathMatch" + ChatColor.DARK_RED + "] " + ChatColor.GOLD;

    // Game State
    // LOBBY - Initial State when players are waiting in the lobby
    // VOTING - When players are voting (if this applies)
    // PREGAME - When players are waiting at spawns ready to go
    // GAME - When Game starts
	// PRE_DEATH_MATCH - DM mode starting soon, countdown to teleport to start etc
	// DEATH_MATCH_COUNTDOWN - DM mode, countdown to actual DM start
	// DEATH_MATCH - DM mode
    // ENDGAME - End game
    // POSTGAME - When a match ends
    public enum GameState {
        LOBBY, VOTING, PRE_GAME, GAME_COUNTDOWN, GAME, PRE_DEATH_MATCH, DEATH_MATCH_COUNTDOWN, DEATH_MATCH, POST_GAME
    }

    private GameState gameState = GameState.LOBBY;

	protected MPGKit kit;
    protected MPGWorld world;

    protected Gamemode gamemode;

	private GameListener gameListener = new GameListener();

	private boolean isClanGame = false;

	private int senderClanId;
	private String senderClanName;
	private int targetClanId;
	private String targetClanName;

	// Voting
	private Object lastVote;
	private boolean votingEnabled = false;
	private Map<UUID, Integer> playerVotes = new HashMap<>();
	private List<Object> voteObjects = new ArrayList<>();
	private Map<Object, Integer> objectVotes = new HashMap<>();

	public MPGGame() {
		super();

		MPG.getInstance().setMPGGame(this);

		MPG.getInstance().getServer().getPluginManager().registerEvents(this.gameListener, MPG.getInstance());
	}

	public MPGGame(Gamemode gamemode) {
		this();

		this.gamemode = gamemode;
	}

	public MPGGame(MPGWorld world) {
		this(null, world);
	}

	public MPGGame(Gamemode gamemode, MPGWorld world) {
		super(world.getGWorld());

		this.world = world;
		this.gamemode = gamemode;

		MPG.getInstance().setMPGGame(this);

		MPG.getInstance().getServer().getPluginManager().registerEvents(this.gameListener, MPG.getInstance());

		// Load MPGWorld
		world.load();
	}

    // Plugins should override this if they want to do custom stuff
    public void createGameTasks() {
        new GameTimeTask().runTaskTimer(MPG.getInstance(), 20L, 20L);

	    new VoidCheckerTask().runTaskTimer(MPG.getInstance(), 1L, 1L);

	    new CheckForEndGame().runTaskTimer(MPG.getInstance(), 1L, 1L);
    }

    public void setGameState(GameState state) {
        if (this.gameState.ordinal() >= state.ordinal() && this.gameState.ordinal() >= GameState.GAME.ordinal()) {
            throw new IllegalGameStateTransition("Transition from " + this.gameState + " to " + state);
        }

	    System.out.println("!!!!!!!!!!!!!!!!!!!!!! CHANGING GAME STATE FROM " + this.gameState + " TO " + state);

        switch (state) {
            case LOBBY:
                throw new IllegalGameStateTransition();
	        case VOTING:
		        break;
            case PRE_GAME:
                if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
                    // Get all players
                    List<Player> players = new ArrayList<>();
                    for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
	                    players.add(mpgPlayer.getPlayer());
                    }

	                // Players aren't on at this point if server is using matchmaking
	                if (!MPG.USES_MATCHMAKING) {
		                SkullPlayerInventory.addSkullForPlayers(players);
	                }
                }

                this.preGame();
                break;
            case GAME:
	            // Start listening for stats
                MiniStats.getInstance().startListening();

	            // Store start time for stat tracking here of everyone
	            long time = new DateTime(DateTimeZone.UTC).getMillis();
	            this.setStartTime(time);
	            for (MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
		            mpgPlayer.setStartTime(time);
	            }

                // Set map for everyone (just to be safe)
                for (final MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
	                if (mpgPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
		                final Player player = mpgPlayer.getPlayer();

		                // NOTE: Don't neccessarily need to send this, method has bugs though
		                // Are we using custom name tags for this game?
		                /*if (MPG.GAME_TYPE == MPG.GameType.PARTY && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NAME_TAGS)) {
			                // Send the destroy packets for the disguised name tags
			                NameTagUtil.removePlayerNameTag(player);
		                }*/

		                // Did we manually disguise this player?
		                if (mpgPlayer.isManuallyDisguised()) {
			                try {
				                // Was this player originally disguised?
				                if (mpgPlayer.getOldDisguisedName() != null) {
					                Bukkit.getLogger().info("Undisguising already disguised player " + mpgPlayer.getUniqueId() + " for game start");
					                DisguiseManager.undisguisePlayer(player, false);
					                DisguiseManager.disguisePlayerWithSavedDisguise(player);
				                } else {
					                Bukkit.getLogger().info("Undisguising player " + mpgPlayer.getUniqueId() + " for game start");
					                DisguiseManager.undisguisePlayer(player, false);
				                }
			                } catch (IllegalStateException e) {
				                e.printStackTrace();
			                }
		                }

		                MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(mpgPlayer.getUniqueId(), mpgPlayer);

		                // Are we using custom name tags for this game?
		                if (MPG.GAME_TYPE == MPG.GameType.PARTY && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NAME_TAGS)) {
			                if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NUMBERS)) {
				                // Show "&4[Team #]" for games that use team numbers
				                BukkitUtil.runTaskLater(new Runnable() {
					                @Override
					                public void run() {
						                NameTagUtil.createPlayerNameTag(player, mpgPlayer.getTeam().getPrefix(), "");
					                }
				                }, 2L);
			                } else {
				                // Show "&4" for games that don't use team numbers
				                BukkitUtil.runTaskLater(new Runnable() {
					                @Override
					                public void run() {
						                NameTagUtil.createPlayerNameTag(player, mpgPlayer.getTeam().getColor().toString(), "");
					                }
				                }, 2L);
			                }
		                }
	                } else if (mpgPlayer.getState() == MPGPlayer.PlayerState.DC) {
		                // Did we manually disguise this player?
		                if (mpgPlayer.isManuallyDisguised()) {
			                try {
				                // Was this player originally disguised?
				                if (mpgPlayer.getOldDisguisedName() != null) {
					                Bukkit.getLogger().info("Undisguising already disguised player " + mpgPlayer.getUniqueId() + " for game start");
					                DisguiseManager.undisguisePlayer(mpgPlayer.getUniqueId());
					                DisguiseManager.disguisePlayerWithSavedDisguise(mpgPlayer.getUniqueId());
				                } else {
					                Bukkit.getLogger().info("Undisguising player " + mpgPlayer.getUniqueId() + " for game start");
					                DisguiseManager.undisguisePlayer(mpgPlayer.getUniqueId());
				                }
			                } catch (IllegalStateException e) {
				                e.printStackTrace();
			                }
		                }

		                // Start off disconnect timer task once the game has started
		                mpgPlayer.setDisconnectTimerTask(BukkitUtil.runTaskLater(new DisconnectTimerTask(mpgPlayer),
				                MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.MAX_DISCONNECT_LENGTH) * 20L));

		                MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(mpgPlayer.getUniqueId(), mpgPlayer);
	                } else {
		                mpgPlayer.setTrackData(false);
                    }
                }

	            this.createGameTasks();

	            this.startGame();
                break;
	        case PRE_DEATH_MATCH:
		        this.preDeathMatch();
		        break;
	        case DEATH_MATCH_COUNTDOWN:
		        this.deathMatchCountdown();
		        break;
            case DEATH_MATCH:
                this.deathMatch();
                break;
	        case POST_GAME:
		        this.endGame(new HashMap<String, Object>());
		        break;
        }

        this.gameState = state;

	    // Fire off event so plugins can hook in
	    MPG.getInstance().getServer().getPluginManager().callEvent(new MPGGameStateChangeEvent(this));
    }

    public void endGame(final Map<String, Object> extraPayload) {
	    // Run game time task one more time when state is actually set to post game
	    BukkitUtil.runTaskNextTick(new Runnable() {
		    @Override
		    public void run() {
			    // Run one last time
			    GameTimeTask.getInstance().run();
		    }
	    });

	    // Unregister listeners
	    MiniStats.getInstance().stopListening();

	    // Only unregister game listener if we're not using MM (not rebooting server for sure)
	    // because we still require some of its functionality
	    if (!MPG.USES_MATCHMAKING) {
		    HandlerList.unregisterAll(this.gameListener);
	    }

	    // Save clan stats if this was a clan game?
	    if (this.isClanGame()) {
		    BukkitUtil.runTaskAsync(new Runnable() {
			    @Override
			    public void run() {
				    int winningClanId = MPGPlayerManager.getMPGPlayer(MPGGame.this.getWinners().iterator().next()).getTeam().getClanId();
				    int losingClanId = winningClanId == MPGGame.this.senderClanId ? MPGGame.this.targetClanId : MPGGame.this.senderClanId;

				    String winQuery = "UPDATE clan_duel_stats SET wins = wins + 1 WHERE clan_id = ? AND game_type = ?;\n"
						    + "INSERT INTO clan_duel_stats (clan_id, game_type, wins, losses) SELECT ?, ?, ?, ? WHERE NOT EXISTS "
						    + "(SELECT 1 FROM clan_duel_stats WHERE clan_id = ? AND game_type = ?);";

				    String loseQuery = "UPDATE clan_duel_stats SET losses = losses + 1 WHERE clan_id = ? AND game_type = ?;\n"
						    + "INSERT INTO clan_duel_stats (clan_id, game_type, wins, losses) SELECT ?, ?, ?, ? WHERE NOT EXISTS "
						    + "(SELECT 1 FROM clan_duel_stats WHERE clan_id = ? AND game_type = ?);";

				    String recordQuery = "INSERT INTO clan_duel_records (time, clan_id, other_clan_id, game_type, match_id, win) SELECT ?, ?, ?, ?, ?, ?;";

				    Connection connection = null;
				    PreparedStatement ps = null;

				    try {
					    connection = Gberry.getConnection();

					    ps = connection.prepareStatement(winQuery);

					    ps.setInt(1, winningClanId);
					    ps.setString(2, MiniStats.TAG.toLowerCase());
					    ps.setInt(3, winningClanId);
					    ps.setString(4, MiniStats.TAG.toLowerCase());
					    ps.setInt(5, 1);
					    ps.setInt(6, 0);
					    ps.setInt(7, winningClanId);
					    ps.setString(8, MiniStats.TAG.toLowerCase());

					    Gberry.executeUpdate(connection, ps);

					    // Don't leak!!!
					    Gberry.closeComponents(ps);

					    ps = connection.prepareStatement(loseQuery);

					    ps.setInt(1, losingClanId);
					    ps.setString(2, MiniStats.TAG.toLowerCase());
					    ps.setInt(3, losingClanId);
					    ps.setString(4, MiniStats.TAG.toLowerCase());
					    ps.setInt(5, 0);
					    ps.setInt(6, 1);
					    ps.setInt(7, losingClanId);
					    ps.setString(8, MiniStats.TAG.toLowerCase());

					    Gberry.executeUpdate(connection, ps);

					    // Don't leak!!!
					    Gberry.closeComponents(ps);

					    ps = connection.prepareStatement(recordQuery);

					    ps.setTimestamp(1, new Timestamp(MPGGame.this.getEndTime()));
					    ps.setInt(2, MPGGame.this.senderClanId);
					    ps.setInt(3, MPGGame.this.targetClanId);
					    ps.setString(4, MiniStats.TAG.toLowerCase());
					    ps.setString(5, MiniStats.MATCH_ID);

					    if (winningClanId == MPGGame.this.senderClanId) {
						    ps.setBoolean(6, true);
					    } else {
						    ps.setBoolean(6, false);
					    }

					    Gberry.executeUpdate(connection, ps);

					    // Don't leak!!!
					    Gberry.closeComponents(ps);

					    ps = connection.prepareStatement(recordQuery);

					    ps.setTimestamp(1, new Timestamp(MPGGame.this.getEndTime()));
					    ps.setInt(2, MPGGame.this.targetClanId);
					    ps.setInt(3, MPGGame.this.senderClanId);
					    ps.setString(4, MiniStats.TAG.toLowerCase());
					    ps.setString(5, MiniStats.MATCH_ID);

					    if (winningClanId == MPGGame.this.targetClanId) {
						    ps.setBoolean(6, true);
					    } else {
						    ps.setBoolean(6, false);
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

	    if (MPG.USES_MATCHMAKING) {
		    // Send dead player requests for winners and shutdown server request
		    BukkitUtil.runTaskAsync(new Runnable() {
			    @Override
			    public void run() {
				    JSONObject payload;

				    payload = new JSONObject();

				    List<String> uuids = new ArrayList<>();

				    for (UUID uuid : MPGGame.this.getWinners()) {
					    uuids.add(uuid.toString());
				    }

				    payload.put("uuids", uuids);

				    payload.put("match_id", MiniStats.MATCH_ID);

				    payload.put("server_region", Gberry.serverRegion.name().toLowerCase());
				    payload.put("server_type", Gberry.serverType.getInternalName());
				    payload.put("ladder", MPGGame.this.gamemode.getName().toLowerCase());

				    if (MPG.GAME_TYPE == MPG.GameType.FFA) {
					    payload.put("type", MPG.GameType.FFA.name().toLowerCase());
				    } else {
					    payload.put("type", MPG.GameType.PARTY.name().toLowerCase());
				    }

				    // Append extra payload
				    for (Map.Entry<String, Object> entry : extraPayload.entrySet()) {
					    payload.put(entry.getKey(), entry.getValue());
				    }

				    System.out.println(payload);

				    MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_REMOVE_PLAYERS, payload);

				    /*payload = new JSONObject();

				    payload.put("server_name", Gberry.serverName);
				    payload.put("server_region", Gberry.serverRegion.name().toLowerCase());
				    payload.put("server_type", Gberry.serverType.getInternalName());
				    payload.put("ladder", MPGGame.this.gamemode.getName().toLowerCase());

				    if (MPG.GAME_TYPE == MPG.GameType.FFA) {
					    payload.put("type", MPG.GameType.FFA.name().toLowerCase());
				    } else {
					    payload.put("type", MPG.GameType.PARTY.name().toLowerCase());
				    }

				    System.out.println(payload);

				    JSONObject response;
				    do {
					    response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_SHUTDOWN_SERVER, payload);
				    } while (response == null);*/

				    BukkitUtil.runTask(new Runnable() {
					    @Override
					    public void run() {
						    MPGGame.this.handleEndCycle();
					    }
				    });
			    }
		    });
	    } else {
		    MPGGame.this.handleEndCycle();
	    }

	    // Only set MPG game to null if we're not using MM (not rebooting server for sure)
	    // because we still require some of its functionality
	    if (!MPG.USES_MATCHMAKING) {
		    MPG.getInstance().setMPGGame(null);
	    }
    }

	private void handleEndCycle() {
		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.REBOOT_ON_GAME_END)) {
			new BukkitRunnable() {
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
				}
			}.runTaskLater(MPG.getInstance(), 200L);

			// Send all online players back to SERVER_ON_END
			if (MPG.USES_MATCHMAKING) {
				BukkitUtil.runTaskLater(new Runnable() {
					@Override
					public void run() {
						final List<Player> sendPlayersList = new ArrayList<>();

						for (MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
							Player player = mpgPlayer.getPlayer();
							if (player != null && player.isOnline()) {
								sendPlayersList.add(player);
							}
						}

						BukkitUtil.runTaskAsync(new Runnable() {
							@Override
							public void run() {
								MatchmakingMCPListener.mpgLobbyServerSender.sendPlayersToLobby(sendPlayersList);
							}
						});
					}
				}, 140L); // 3 seconds before /stop
			}
		} else {
			// TODO: START INTERMISSION TASK
		}
	}

    public abstract void preGame();

	public abstract void startGame();

	public abstract void preDeathMatch();

	public abstract void deathMatchCountdown();

	public abstract void deathMatch();

    public abstract boolean checkForEndGame();

    public void addToInventory(Set<Inventory> inventorySet, Location location) {
        Chest chest = (Chest) location.getBlock().getState();
        chest.getInventory().clear();

        // Handle double chests
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
            DoubleChestInventory doubleChestInventory = (DoubleChestInventory) doubleChest.getInventory();
            inventorySet.add(doubleChestInventory.getLeftSide());
            inventorySet.add(doubleChestInventory.getRightSide());
        } else {
            inventorySet.add(chest.getInventory());
        }
    }

    public void fillChest(Inventory inventory, int tier) {
        if (this.gamemode == null) {    // TODO: TAKE THIS OUT AND PUT IT INTO SG
            throw new RuntimeException("No game mode specified when trying to fill chests");
        }

	    // Don't fill the chest if it's not empty
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return;
            }
        }

	    int numberSlots = inventory.getSize() - 1;

        // Num of items based on game mode
	    int numOfItems = this.getGamemode().getNumOfTierRandom(tier) + this.getGamemode().getNumOfTierGuaranteed(tier);

	    Set<Integer> slotsUsed = new HashSet<>();
        Set<Material> itemsUsed = new HashSet<>();

	    // Handle random items
        for (int i = 0; i < numOfItems; i++) {
	        // Get a random slot
            int slot;
            do {
                slot = Gberry.generateRandomInt(0, numberSlots);
            } while (slotsUsed.contains(slot));

            slotsUsed.add(slot);

            // Get a unique item
            ItemStack itemStack;
	        do {
		        itemStack = this.gamemode.getTierItem(tier);
	        } while (itemsUsed.contains(itemStack.getType()));

            itemsUsed.add(itemStack.getType());
            inventory.setItem(slot, itemStack);
        }

		// Handle common items
		List<ItemStack> commonItems = this.gamemode.getCommonTierItems(tier);
		if (commonItems != null) {
			for (ItemStack itemStack : commonItems) {
				// Get a random slot
				int slot;
				do {
					slot = Gberry.generateRandomInt(0, numberSlots);
				} while (slotsUsed.contains(slot));

				inventory.setItem(slot, itemStack);
			}
		}
    }

	public void startVotingTask() {
		// We got enough players, let them vote on random maps
		MPG.getInstance().getMPGGame().setVotingEnabled(true);

		// Loop through until we have the total number of map choices available
		for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_VOTE_CHOICES); i++) {
			boolean found = false;

			do {
				Object randomObject = MPG.VOTE_OBJECTS.get(Gberry.generateRandomInt(0, MPG.VOTE_OBJECTS.size() - 1));
				if (!this.voteObjects.contains(randomObject)) {
					if (this.lastVote != null && randomObject == this.lastVote
							&& !MPG.getInstance().getBooleanOption(MPG.ConfigFlag.CAN_VOTE_FOR_LAST_WINNER)) {
						continue;
					}

					this.voteObjects.add(randomObject);
					this.objectVotes.put(randomObject, 0);
					found = true;
				}
			} while (!found);
		}

		VotingTask votingTask = new VotingTask();
		votingTask.runTaskTimer(MPG.getInstance(), 20, 20);
		MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.VOTING); // Go from STANDBY to VOTING
	}

    public boolean hasPermissionToSpectate(MPGPlayer mpgPlayer) {
	    return this.hasPermissionToSpectate(mpgPlayer.getPlayer());
    }

	public boolean hasPermissionToSpectate(Player player) {
		return player.hasPermission("badlion.donatorplus");
	}

    public GameState getGameState() {
        return this.gameState;
    }

	public MPGKit getKit() {
		return this.kit;
	}

	public void setKit(MPGKit kit) {
		this.kit = kit;
	}

	public MPGWorld getWorld() {
		return this.world;
	}

	public void setWorld(MPGWorld world) {
		this.world = world;

		// Set and load GWorld
		this.setGWorld(world.getGWorld());

		// Load MPGWorld
		world.load();
	}

	public boolean isClanGame() {
		return this.isClanGame;
	}

	public void setIsClanGame(boolean clanGame) {
		this.isClanGame = clanGame;
	}

	public int getSenderClanId() {
		return this.senderClanId;
	}

	public void setSenderClanId(int senderClanId) {
		this.senderClanId = senderClanId;
	}

	public String getSenderClanName() {
		return this.senderClanName;
	}

	public void setSenderClanName(String senderClanName) {
		this.senderClanName = senderClanName;
	}

	public int getTargetClanId() {
		return this.targetClanId;
	}

	public void setTargetClanId(int targetClanId) {
		this.targetClanId = targetClanId;
	}

	public String getTargetClanName() {
		return this.targetClanName;
	}

	public void setTargetClanName(String targetClanName) {
		this.targetClanName = targetClanName;
	}

	public Object getLastVote() {
		return this.lastVote;
	}

	public void setLastVote(Object lastVote) {
		this.lastVote = lastVote;
	}

	public boolean isVotingEnabled() {
		return this.votingEnabled;
	}

	public void setVotingEnabled(boolean votingEnabled) {
		// Reset votes if voting has finished
		if (!votingEnabled) {
			this.playerVotes.clear();
			this.voteObjects.clear();
			this.objectVotes.clear();
		}

		this.votingEnabled = votingEnabled;
	}

	public void addPlayerVote(Player player, int number) {
		this.playerVotes.put(player.getUniqueId(), number);

		Object object = this.voteObjects.get(number);
		this.objectVotes.put(object, this.objectVotes.get(object) + this.getPlayerNumberOfVotes(player));
	}

	public Integer getPlayerVote(UUID uuid) {
		return this.playerVotes.get(uuid);
	}

	public void resetPlayerVote(Player player) {
		Integer oldVote = MPG.getInstance().getMPGGame().getPlayerVote(player.getUniqueId());
		if (oldVote != null) {
			Object voteObject = this.getVoteObject(oldVote);
			this.objectVotes.put(voteObject, this.getNumberOfVotes(voteObject) - this.getPlayerNumberOfVotes(player));
		}
	}

	public int getPlayerNumberOfVotes(Player player) {
		int numOfVotes = MPG.NUM_OF_DEFAULT_VOTES;
		if (player.isOp()) {
			numOfVotes = MPG.NUM_OF_OP_VOTES;
		} else if (player.hasPermission("badlion.lionplus")) {
			numOfVotes = MPG.NUM_OF_LION_PLUS_VOTES;
		} else if (player.hasPermission("badlion.lion")) {
			numOfVotes = MPG.NUM_OF_LION_VOTES;
		} else if (player.hasPermission("badlion.donatorplus")) {
			numOfVotes = MPG.NUM_OF_DONATOR_PLUS_VOTES;
		} else if(player.hasPermission("badlion.donator")) {
			numOfVotes = MPG.NUM_OF_DONATOR_VOTES;
		}

		return numOfVotes;
	}

	public Object getVoteObject(int index) {
		return this.voteObjects.get(index);
	}

	public int getNumberOfVotes(Object object) {
		return this.objectVotes.get(object);
	}

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

}
