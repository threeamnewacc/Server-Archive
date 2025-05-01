package net.badlion.mpg;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.mpg.bukkitevents.MPGPlayerStateChangeEvent;
import net.badlion.mpg.exceptions.IllegalPlayerStateTransition;
import net.badlion.mpg.inventories.SkullPlayerInventory;
import net.badlion.mpg.inventories.SpectatorInventory;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGRespawnManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.CheckForEndGame;
import net.badlion.mpg.tasks.DisconnectTimerTask;
import net.badlion.mpg.tasks.PreGameCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class MPGPlayer extends PlayerData {

    public static int NUM_OF_DONATOR_WL_SLOTS = 0;
    public static int NUM_OF_DONATOR_PLUS_WL_SLOTS = 0;
    public static int NUM_OF_LION_WL_SLOTS = 1;
    public static int NUM_OF_LION_PLUS_WL_SLOTS = 2;
    public static int NUM_OF_OP_WL_SLOTS = 1000; // Testing

	private static final int DEAD_LAYING_ENTITY_SKIN_TIME = 60; // In seconds

    protected PlayerState playerState = PlayerState.PLAYER;

	private int position = -1;
	private long startTime = 0;

	// Disconnection tracking
	private int numOfTimesDisconnected = 0;
	private BukkitTask disconnectTimerTask;

    // Donator Whitelist
    protected Set<String> nameWhitelist = new HashSet<>(); // List of the names (not uuids) that this player has whitelisted
    protected Set<String> claimedWhitelist = new HashSet<>(); // Players who have joined and used their whitelist space for this game.

    // Team
    protected MPGTeam team;
    protected MPGTeam invitedTeam;

    // Other
    protected MPGKit kit;
    protected boolean joinedOnce = false;

    // By default we set these flags to true (we set to false in constructor if it's not actually used)
    private boolean kitsLoaded = true;

	private String oldDisguisedName;
	private boolean manuallyDisguised = false;

    public enum PlayerState {
        PLAYER, DC, DEAD, SPECTATOR, MOD, HOST
    }

    public MPGPlayer(UUID uuid, String username) {
        // Player object can be null, we set map name lower down
        super(uuid, username, MPG.getInstance().getMPGGame().getWorld().getGWorld().getNiceWorldName());

        // Set flags
        if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USES_KITS)) {
            this.kitsLoaded = false;
        }

	    // Give them their white list slots
        this.giveWhitelistSlots();

	    // Put them on their own team if there are no teams in this game
	    // Note: If a custom MPGTeam is implemented, MPGTeam will still be used,
	    //       there is no reason to have a custom team for a solo game
	    if (MPG.GAME_TYPE == MPG.GameType.FFA) {
		    this.team = new MPGTeam(username);
		    this.team.add(this);
	    }

	    // Hide spectators since this MPGPlayer is PLAYER state
	    this.hideSpectators();

	    // Add their uuid/username mappings
        MPG.getInstance().putUUID(this.uuid, this.username);
        MPG.getInstance().putUsername(this.uuid, this.username);

        // Is the player online?
        Player player = MPG.getInstance().getServer().getPlayer(this.uuid);
        if (player != null) {
            this.joinedOnce = true;
        }

	    MPGPlayerManager.storeMPGPlayer(this, this.uuid, this.playerState);
    }

	public void hideSpectators() {
		Player player = this.getPlayer();

		if (player == null) return;

		// Hide spectators from this player
		for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(PlayerState.SPECTATOR)) {
			Player pl = mpgPlayer.getPlayer();

			// Are they offline?
			if (pl == null || !Gberry.isPlayerOnline(pl)) continue;

			player.hidePlayer(pl);
			pl.showPlayer(player);
		}

		// Show this player to all other players
		for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(PlayerState.PLAYER)) {
			Player pl = mpgPlayer.getPlayer();

			// Are they offline?
			if (pl == null || !Gberry.isPlayerOnline(pl)) continue;

			player.showPlayer(pl);
			pl.showPlayer(player);
		}
	}

	/**
	 * IfElse for the various donator ranks to allow them to set how many names a donator can whitelist
 	 */
    public void giveWhitelistSlots() {

    }

	/**
	 * WARNING: CANNOT BE CALLED WHEN THE PLAYER IS OFFLINE OR IF THERE IS NO COMBAT TAG
	 */
	public void handlePlayerDeath() {
		LivingEntity livingEntity = this.getPlayer();

		if (livingEntity == null) {
			livingEntity = CombatTagPlugin.getInstance().getLogger(this.uuid).getEntity();
		}

		if (livingEntity == null) {
			throw new RuntimeException("Player " + this.username + " is offline in handlePlayerDeathInternal() with a null logger npc!");
		}

		this.handlePlayerDeathInternal(livingEntity, new HashMap<String, Object>());

		// Check for end game by running the task manually, avoids race conditions
		CheckForEndGame.getInstance().run();
	}

	/**
	 * Internal handle player death method.
	 *  @param livingEntity - Player or MPGLoggerNPC zombie entity
	 * @param extraPayload - Mapping of extra data to be sent to MCP for `matchmaking-default-remove-players`
	 */
    public void handlePlayerDeathInternal(final LivingEntity livingEntity, final Map<String, Object> extraPayload) {
	    if (this.team != null) {
		    this.team.addDeath();
	    }

        this.trackDeathAndGiveKillCreditToKiller(livingEntity);

	    // Track time played, possible they quit before the game starts so check time = 0
	    if (this.startTime != 0) {
		    long totalTimePlayed = (System.currentTimeMillis() - this.startTime) / 1000;
		    this.addTotalTimePlayed(totalTimePlayed);
	    }

	    // Are dead laying entities enabled for this game?
	    if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DEAD_ENTITY_ON_DEATH)) {
		    MPGLoggerNPC mpgLoggerNPC = null;
		    if (!(livingEntity instanceof Player)) {
			    mpgLoggerNPC = (MPGLoggerNPC) CombatTagPlugin.getInstance().getCombatLoggerFromEntity(livingEntity);
		    }

		    final MPGLoggerNPC finalMPGLoggerNPC = mpgLoggerNPC;

		    // Player may be offline at this point, fetch user data manually
		    BukkitUtil.runTaskAsync(new Runnable() {
			    @Override
			    public void run() {
				    final JSONObject disguiseSettings = UserDataManager.getUserDataFromDB(MPGPlayer.this.uuid).getDisguiseSettings();
				    BukkitUtil.runTask(new Runnable() {
					    @Override
					    public void run() {
						    if ((boolean) disguiseSettings.get("is_disguised")) {
							    // Get the player's disguise name
							    String disguiseName = (String) disguiseSettings.get("disguise_name");

							    // Get player's skin information
							    String skinTexture = (String) disguiseSettings.get("skin_texture");
							    String skinSignature = (String) disguiseSettings.get("skin_signature");

							    // Create dead laying entity
							    livingEntity.getWorld().spawnDeadLayingEntity(disguiseName, skinTexture, skinSignature, livingEntity.getLocation(), MPGPlayer.DEAD_LAYING_ENTITY_SKIN_TIME, false);
						    } else {
							    // Create dead laying entity
							    if (livingEntity instanceof Player) {
								    livingEntity.getWorld().spawnDeadLayingEntity(((Player) livingEntity), livingEntity.getLocation(), MPGPlayer.DEAD_LAYING_ENTITY_SKIN_TIME, false);
							    } else {
								    // Did the player login at least once?
								    if (finalMPGLoggerNPC.getSkinTexture() != null) {
									    livingEntity.getWorld().spawnDeadLayingEntity(MPGPlayer.this.username, finalMPGLoggerNPC.getSkinTexture(), finalMPGLoggerNPC.getSkinSignature(), livingEntity.getLocation(), MPGPlayer.DEAD_LAYING_ENTITY_SKIN_TIME, false);
								    } else {
									    // Use steve skin
									    livingEntity.getWorld().spawnDeadLayingEntity(MPGPlayer.this.username, null, null, livingEntity.getLocation(), MPGPlayer.DEAD_LAYING_ENTITY_SKIN_TIME, false);
								    }
							    }
						    }
					    }
				    });
			    }
		    });
	    }

	    // Handle anything extra for the game mode
	    MPG.getInstance().getMPGGame().getGamemode().handleDeath(livingEntity);

	    if (MPG.USES_MATCHMAKING) {
		    // Send dead player request
		    BukkitUtil.runTaskAsync(new Runnable() {
			    @Override
			    public void run() {
				    JSONObject payload = new JSONObject();

				    List<String> uuids = new ArrayList<>();
				    uuids.add(MPGPlayer.this.uuid.toString());

				    payload.put("uuids", uuids);

				    payload.put("match_id", MiniStats.MATCH_ID);

				    payload.put("server_region", Gberry.serverRegion.name().toLowerCase());
				    payload.put("server_type", Gberry.serverType.getInternalName());
				    payload.put("ladder", MPG.getInstance().getMPGGame().getGamemode().getName().toLowerCase());

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
			    }
		    });
	    }
    }

    /**
     * Responsible for giving credit to the killer and tracking stats that we died
     */
    protected void trackDeathAndGiveKillCreditToKiller(LivingEntity livingEntity) {
	    MPGPlayer killerMPGPlayer = null;

	    if (livingEntity.getKiller() != null) {
		    killerMPGPlayer = MPGPlayerManager.getMPGPlayer(livingEntity.getKiller());
	    }

        if (MiniStats.getInstance().getPlayerDataListener().isTrackStats() && this.isTrackData()) {
            this.addDeath();

            // Reset their kill streak
            this.setCurrentKillStreak(0);

            if (killerMPGPlayer != null) {
                // Add a kill to player's killstreak
                if (killerMPGPlayer.isTrackData()) {
                    killerMPGPlayer.setCurrentKillStreak(killerMPGPlayer.getCurrentKillStreak() + 1);
                    if (killerMPGPlayer.getCurrentKillStreak() > killerMPGPlayer.getHighestKillStreak()) {
                        killerMPGPlayer.setHighestKillStreak(killerMPGPlayer.getCurrentKillStreak());
                    }

	                // Store a kill (make sure to call the correct function)
	                PlayerKill playerKill;
	                if (livingEntity instanceof Player) {
		                playerKill = killerMPGPlayer.addPlayerKill(((Player) livingEntity));
	                } else {
		                playerKill = killerMPGPlayer.addPlayerKill(this, livingEntity);
	                }

	                if (livingEntity.getKiller().getItemInHand() != null) {
		                playerKill.setKillerWeaponType(livingEntity.getKiller().getItemInHand().getType());
	                } else {
		                playerKill.setKillerWeaponType(Material.AIR);
	                }

	                if (killerMPGPlayer.getTeam() != null) {
		                killerMPGPlayer.getTeam().addKill();
	                }
                }
            } else {
                // Store a suicide (make sure to call the correct function)
	            if (livingEntity instanceof Player) {
		            this.addSuicide(((Player) livingEntity));
	            } else {
		            this.addSuicide(livingEntity);
	            }
            }
        }

        // Send message to person who died of their opponents health
        if (killerMPGPlayer != null && killerMPGPlayer.getPlayer() != null && livingEntity instanceof Player) {
	        ((Player) livingEntity).sendMessage(ChatColor.GREEN + killerMPGPlayer.getPlayer().getDisguisedName() + " had " + ChatColor.RED
			        + (Math.ceil(killerMPGPlayer.getPlayer().getHealth()) / 2D) + " " + MessageUtil.HEART + " " + ChatColor.GREEN + " when you died.");
        }
    }

    public Location handlePlayerRespawnScreen(Player player) {
	    if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.SPECTATOR_ON_DEATH)) {
		    // Respawn them at the spectator location
		    return MPG.getInstance().getMPGGame().getWorld().getSpectatorLocation();
	    } else if (MPG.ALLOW_RESPAWNING) {
		    // Is there no respawn delay?
		    if (MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.RESPAWN_TIME) == 0) {
			    // Respawn them instantly
			    return this.handlePlayerRespawn(player);
		    } else {
			    // TODO: NPE IF WE ALLOW RESPAWNING IN A NON TEAM GAME
			    MPGRespawnManager.addPlayerRespawning(this, this.team.getRespawnLocation());
			    return player.getLocation(); // Respawn them where they died
		    }
	    }

	    return null;
    }

	/**
	 * Location return type is only if instant respawns
	 * are enabled and if this is returning the player's
	 * actual vanilla respawn location
	 */
	public Location handlePlayerRespawn(Player player) {
		return null;
	}

	/**
	 * Handle when a player transitions to player state
	 */
	public void handlePlayerStateTransition(Player player) {

	}

    /**
     * Handle when a spectator is created and is online by either reconnecting
     * or just have dying, choosing to go into spectator mode etc
     */
    public void handleSpectator(Player player) {
	    System.out.println("handleSpectator() for " + player.getName());

	    // Hide this player from everyone
	    for (Player pl : MPG.getInstance().getServer().getOnlinePlayers()) {
		    pl.hidePlayer(player);

		    // Hide other spectators from this player
		    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(pl.getUniqueId());
		    if (mpgPlayer.getState() == PlayerState.SPECTATOR) {
			    player.hidePlayer(pl);
		    }
	    }

	    player.setGameMode(GameMode.CREATIVE);
	    player.spigot().setCollidesWithEntities(false);

	    for (PotionEffect effect : player.getActivePotionEffects()) {
		    player.removePotionEffect(effect.getType());
	    }

	    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

	    player.getInventory().setArmorContents(new ItemStack[4]);
	    player.getInventory().clear();

	    SpectatorInventory.givePlayerSpectatorItems(player);

	    player.updateInventory();

        // Teleport them to the default spectator location
        if (MPG.getInstance().getMPGGame().getWorld().getSpectatorLocation() != null) {
	        player.teleport(MPG.getInstance().getMPGGame().getWorld().getSpectatorLocation());
        }

	    player.setFlying(true);
    }

    public boolean removeNameFromWhitelist(String name) {
        return this.nameWhitelist.remove(name.toLowerCase());
    }

    public Set<String> getNameWhitelist() {
        return this.nameWhitelist;
    }

    public boolean whitelistPlayer(String name) {
        // Make sure they are not already whitelisted
        name = name.toLowerCase();
        if (MPG.getInstance().isWhitelisted(name)) {
            return false;
        }

        this.nameWhitelist.add(name.toLowerCase());
        MPG.getInstance().addToWhitelist(name);
        return true;
    }

    public boolean canWhiteList() {
        return this.nameWhitelist.size() < this.getAllowedWhitelistSlots();
    }

    public int getAllowedWhitelistSlots() {
        Player player = MPG.getInstance().getServer().getPlayer(this.uuid);
        if (player == null) {
            throw new RuntimeException("Trying to getAllowedWhitelistSlots() of offline player");
        }

        if (player.isOp()) {
            return MPGPlayer.NUM_OF_OP_WL_SLOTS;
        } else if (player.hasPermission("badlion.lionplus")) {
            return MPGPlayer.NUM_OF_LION_PLUS_WL_SLOTS;
        } else if (player.hasPermission("badlion.lion")) {
            return MPGPlayer.NUM_OF_LION_WL_SLOTS;
        } else if (player.hasPermission("badlion.donatorplus")) {
            return MPGPlayer.NUM_OF_DONATOR_PLUS_WL_SLOTS;
        } else if (player.hasPermission("badlion.donator")) {
            return MPGPlayer.NUM_OF_DONATOR_WL_SLOTS;
        }

        return 0;
    }

    public void listWhitelistedNames(Player player) {
        player.sendMessage(ChatColor.GREEN + "This game you have whitelisted:");

        for (String current: this.nameWhitelist) {
            player.sendMessage(ChatColor.GREEN + current);
        }

        player.sendMessage(ChatColor.GREEN + "You still have " + (this.getAllowedWhitelistSlots() - this.nameWhitelist.size()) + " slots remaining");
    }

    public boolean isWhiteListed() {
        for (MPGPlayer player : MPGPlayerManager.getAllMPGPlayers()) {
            if (player.getNameWhitelist().contains(player.getUsername())) {
                return true;
            }
        }
        return false;
    }

	public PlayerState getState() {
		return this.playerState;
	}

	public final void setState(PlayerState state) throws IllegalPlayerStateTransition {
		MPGPlayerStateChangeEvent event = new MPGPlayerStateChangeEvent(this, state);
		MPG.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		// In case it was modified
		state = event.getNewState();

		System.out.println("CHANGING " + this.getUsername() + " TO " + state + " FROM " + this.getState());

		switch (state) {
			case DC:
				// Don't start the task if the game hasn't started yet, want to give people time to connect
				if (MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.GAME_COUNTDOWN) {
					// Start a task to kill the player if they've been disconnected for too long
					this.disconnectTimerTask = BukkitUtil.runTaskLater(new DisconnectTimerTask(this),
							MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.MAX_DISCONNECT_LENGTH) * 20L);
				}

				Player player = this.getPlayer();

				// Figure out if we need a prefix for the combat logger's name
				String prefix = "";
				if (MPG.GAME_TYPE == MPG.GameType.PARTY && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NAME_TAGS)) {
					if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.TEAM_NUMBERS)) {
						// Show "&4[Team #]" for games that use team numbers
						prefix = this.getTeam().getPrefix();
					} else {
						// Show "&4" for games that don't use team numbers
						prefix = this.getTeam().getColor().toString();
					}
				}

				// Player will be null when DC state is set once a match starts.
				// All players are set to DC state at match start so their death
				// gets handled correctly if they don't connect to the match ever.
				if (player != null) {
					// Create a combat logger NPC
					new MPGLoggerNPC(player, this, prefix);
					System.out.println("CREATED LOGGER NPC FOR " + this.username);
				} else {
					// Create a combat logger NPC
					new MPGLoggerNPC(this.uuid, this, PreGameCountdownTask.getInstance().getPlayerSpawnLocation(this.uuid), prefix);
					System.out.println("CREATED INITIAL LOGGER NPC FOR " + this.username);
				}

				// Remove spectator skull for the player
				if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
					SkullPlayerInventory.removeSkullForPlayer(this);
				}

				break;
			case DEAD:
				this.handlePlayerDeath();

				// Figure out if we need to remove this player's team
				boolean alivePlayers = false;
				for (UUID uuid2 : this.team.getUUIDs()) {
					MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid2);

					if (mpgPlayer.getState().ordinal() <= PlayerState.DC.ordinal()) {
						alivePlayers = true;
						break;
					}
				}

				if (!alivePlayers) {
					// Remove this team
					MPGTeamManager.removeTeam(this.team);
				}

				// Track data false after we handle the player death
				this.trackData = false;

				// Remove spectator skull for the player
				if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
					SkullPlayerInventory.removeSkullForPlayer(this);
				}

				break;
			case SPECTATOR:
				this.trackData = false;
				if (!MPG.ALLOW_SPECTATING) {
					throw new RuntimeException("Spectating is disabled. Cannot put into spectator state for " + this.username);
				}

				player = MPG.getInstance().getServer().getPlayer(this.uuid);
				if (player != null) {
					this.handleSpectator(player);
				}

				// Remove spectator skull for the player
				if (MPG.ALLOW_SPECTATING && MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
					SkullPlayerInventory.removeSkullForPlayer(this);
				}

				break;
			case PLAYER:
				this.trackData = true;

				player = this.getPlayer();

				// Are they transitioning from SPECTATOR state?
				if (this.playerState == PlayerState.SPECTATOR) {
					player.setGameMode(GameMode.SURVIVAL);
					player.spigot().setCollidesWithEntities(true);

					for (PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}

					// Clear inventory
					player.getInventory().setArmorContents(null);
					player.getInventory().clear();
					player.setItemOnCursor(null);
					player.getInventory().setHeldItemSlot(0);
					player.updateInventory();
				}

				this.handlePlayerStateTransition(player);

				// Hide spectators
				this.hideSpectators();

				// Add skull item in skull spectator inventory
				if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.USE_SKULL_SPECTATOR_INVENTORY)) {
					// Add their skull to the spectator inventory
					SkullPlayerInventory.addSkullForPlayer(player);
				}

				break;
		}

		MPGPlayerManager.updateMPGPlayerState(this.uuid, state);

		this.playerState = state;
	}

	public void addHealthObjective(Player player) {
		String hearts = ChatColor.DARK_RED + "\u2764";

		double playerHealth = player.getHealth();

		// Does this player have a combat logger?
		LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(player.getUniqueId());

		if (loggerNPC != null) {
			// Use the combat logger's health because if this player relogged, their health
			// isn't automatically adjusted to the combat logger's health
			playerHealth = loggerNPC.getEntity().getHealth();
		}

		try {
			Object createObjective = TinyProtocolReferences.scoreboardObjectivePacket.newInstance();
			TinyProtocolReferences.objectiveScoreboardPacketName.set(createObjective, "showhealth");
			TinyProtocolReferences.objectiveScoreboardPacketTitle.set(createObjective, hearts);
			TinyProtocolReferences.objectiveScoreboardPacketAction.set(createObjective, 0);

			Object displayObjective = TinyProtocolReferences.scoreboardDisplayObjectivePacket.newInstance();
			TinyProtocolReferences.displayObjectiveScoreboardPacketName.set(displayObjective, "showhealth");
			TinyProtocolReferences.displayObjectiveScoreboardPacketPosition.set(displayObjective, 2);

			Gberry.protocol.sendPacket(player, createObjective);
			Gberry.protocol.sendPacket(player, displayObjective);

			// TODO: MIGHT LAG FOR UHC 6.0 ON 500 PLAYER GAMES?

			// Send this everybody else's health to this player
			for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(PlayerState.PLAYER)) {
				// Don't send update to themselves
				if (mpgPlayer == this) continue;

				Player otherPlayer = mpgPlayer.getPlayer();

				// Send other player's health to player
				Object scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
				TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, otherPlayer.getDisguisedName());
				TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "showhealth");
				TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, (int) Math.ceil(otherPlayer.getHealth()));
				TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);

				Gberry.protocol.sendPacket(player, scorePacket);

				// Send player's health to other player
				scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
				TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, player.getDisguisedName());
				TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "showhealth");
				TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, (int) Math.ceil(playerHealth));
				TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);

				Gberry.protocol.sendPacket(otherPlayer, scorePacket);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateHealthObjective(Player player, double health) {
		// Send update packets to all the other players
		try {
			for (MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
				// Don't send update to themselves
				if (mpgPlayer == this) continue;

				Player otherPlayer = mpgPlayer.getPlayer();

				// Is the player offline?
				if (otherPlayer == null || !Gberry.isPlayerOnline(otherPlayer)) continue;

				Object scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
				TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, player.getDisguisedName());
				TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "showhealth");
				TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, (int) Math.ceil(health));
				TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);
				Gberry.protocol.sendPacket(mpgPlayer.getPlayer(), scorePacket);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// In case we need this in the future?
	/*public void removeHealthObjective(LivingEntity livingEntity) {
		// Do nothing if this was a combat logger
		if (!(livingEntity instanceof Player)) return;

		// IN HANDLE SPEC:
		// TODO: THIS DOES CRASH CLIENTS IF ITS SENT MULTIPLE TIMES

		// TODO: TEST, DOES THIS CRASH CLIENTS IF OBJECTIVE IS NONEXISTENT? TEST RELOG
		// Remove health objective when we set player to spectator state.
		// Some games like FFA shuffle players back and forth between PLAYER
		// and DC state so we can't remove the objective there without numerous
		// checks. If a player logs off, their health objective will still be there,
		// but obviously will not be seen. We also remove the health objective when
		// a spectator logs on.
		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.HEALTH_UNDER_NAME)) {
			System.out.println("REMOVE OBJECTIVE ON HANDLE SPECTATOR");
			this.removeHealthObjective(player);
		}

		Player player = (Player) livingEntity;

		try {
			Object removeObjective = TinyProtocolReferences.scoreboardObjectivePacket.newInstance();
			TinyProtocolReferences.objectiveScoreboardPacketName.set(removeObjective, "showhealth");
			TinyProtocolReferences.objectiveScoreboardPacketTitle.set(removeObjective, "");
			TinyProtocolReferences.objectiveScoreboardPacketAction.set(removeObjective, 1);

			Gberry.protocol.sendPacket(player, removeObjective);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/

    public abstract void update();

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public UUID getUniqueId() {
        return uuid;
    }

	public void setUsername(String username) {
		this.username = username;
	}

    public MPGTeam getTeam() {
        return team;
    }

    public void setTeam(MPGTeam team) {
        this.team = team;
    }

    public MPGTeam getInvitedTeam() {
        return invitedTeam;
    }

    public void setInvitedTeam(MPGTeam invitedTeam) {
        this.invitedTeam = invitedTeam;
    }

    public boolean isKitsLoaded() {
        return kitsLoaded;
    }

    public void setKitsLoaded(boolean kitsLoaded) {
        this.kitsLoaded = kitsLoaded;
    }

    public MPGKit getKit() {
        return kit;
    }

    public void setKit(MPGKit kit) {
        this.kit = kit;
    }

	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Gets a player's original disguised name before they were
	 * re-disguised for the game countdown.
	 */
	public String getOldDisguisedName() {
		return this.oldDisguisedName;
	}

	/**
	 * Sets a player's original disguised name before they get
	 * re-disguised for the game countdown.
	 */
	public void setOldDisguisedName(String oldDisguisedName) {
		this.oldDisguisedName = oldDisguisedName;
	}

	/**
	 * Whether or not they are currently manually disguised
	 * for the game countdown.
	 */
	public boolean isManuallyDisguised() {
		return this.manuallyDisguised;
	}

	/**
	 * Sets whether or not they are currently manually disguised
	 * for the game countdown.
	 */
	public void setManuallyDisguised(boolean manuallyDisguised) {
		this.manuallyDisguised = manuallyDisguised;
	}

	public int getNumOfTimesDisconnected() {
		return this.numOfTimesDisconnected;
	}

	public void incrementNumOfTimesDisconnected() {
		// Don't increment this if the player disconnects during the countdown
		if (MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.GAME_COUNTDOWN) {
			this.numOfTimesDisconnected++;
		}
	}

	public void setDisconnectTimerTask(BukkitTask disconnectTimerTask) {
		this.disconnectTimerTask = disconnectTimerTask;
	}

	public void cancelDisconnectTimerTask() {
		if (this.disconnectTimerTask != null) {
			this.disconnectTimerTask.cancel();
			this.disconnectTimerTask = null;
		}
	}

}
