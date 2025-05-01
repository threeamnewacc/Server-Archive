package net.badlion.survivalgames;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.survivalgames.managers.SGSidebarManager;
import net.badlion.survivalgames.tasks.ChestRefillTask;
import net.badlion.survivalgames.tasks.SupplyDropTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchDamageTask;
import net.badlion.survivalgames.tasks.deathmatch.DeathMatchStartCountdownTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SGPlayer extends MPGPlayer {

	private static final int TIER_1_CHEST_SCORE_MULTIPLIER = 4;
	private static final int TIER_2_CHEST_SCORE_MULTIPLIER = 10;
	private static final int SUPPLY_DROP_SCORE_MULTIPLIER = 50;
	private static final int NO_KILLS_BEFORE_DM_SCORE_MULTIPLIER = 150;
	private static final int NOT_ENOUGH_DAMAGE_BEFORE_DM_SCORE_MULTIPLIER = 260;
	private static final int KILLS_SCORE_MULTIPLIER = 50;

	// Rating stuff
	private int totalKills = 0;
	private int gamesWon = 0;
	private int gamesLost = 0;
	private int gamesPlayed = 0;

	private int nonDeathMatchKills = 0;
	protected boolean reachedPreDMDamageThreshold = false;

	private Map<Integer, Integer> tierChestsOpened = new HashMap<>();
	private Set<Location> chestLocationsOpened = new HashSet<>();

	private Set<Location> supplyDropLocationsOpened = new HashSet<>();

    public SGPlayer(UUID uuid, String username) {
        super(uuid, username);
    }

	@Override
    public void update() {
        final Player player = SurvivalGames.getInstance().getServer().getPlayer(this.uuid);

		// Are they offline?
        if (player == null) {
            return;
        }

		SGGame sgGame = SurvivalGames.getInstance().getSGGame();

		// Boss health bar
		// The time calculations for deathmatch are CANCER, don't touch it unless you want to do math for an hour
		int totalSeconds = GameTimeTask.getInstance().getTotalSeconds();
		int chestRefillTime = sgGame.areChestsRefilled() ? ChestRefillTask.REFILL_TIME : 0;
		if (sgGame.getGameState() == MPGGame.GameState.GAME && (!sgGame.areChestsRefilled() || !SupplyDropTask.getInstance().haveAllSupplyDropsDropped())) {
			// Set to 0 by default
			int lastEventTime = 0; // An event is a supply drop or a chest refill

			int nextSupplyDropTime = 0;

			for (Map.Entry<Integer, Boolean> dropTime : SupplyDropTask.getInstance().getSupplyDropTimes().entrySet()) {
				// Get the first undropped supply drop
				if (!dropTime.getValue()) {
					nextSupplyDropTime = dropTime.getKey();
					break;
				} else {
					// Set this as the last supply drop time
					lastEventTime = dropTime.getKey();
				}
			}

			if (sgGame.areChestsRefilled() && ChestRefillTask.REFILL_TIME > lastEventTime) {
				lastEventTime = ChestRefillTask.REFILL_TIME;
			}

			// Which event will happen next?
			if (!sgGame.areChestsRefilled() && ChestRefillTask.REFILL_TIME < nextSupplyDropTime) {
				// Chest refill
				player.setBossBar(ChatColor.AQUA + "Time Until Chests Refill " + StringCommon.niceTime(ChestRefillTask.REFILL_TIME - totalSeconds, false), 1F - (((float) (totalSeconds - lastEventTime)) / (ChestRefillTask.REFILL_TIME - lastEventTime)));
			} else {
				// Supply drop
				player.setBossBar(ChatColor.AQUA + "Next Supply Drop In " + StringCommon.niceTime(nextSupplyDropTime - totalSeconds, false), 1F - (((float) (totalSeconds - lastEventTime)) / (nextSupplyDropTime - lastEventTime)));
			}
		} else if (SupplyDropTask.getInstance().haveAllSupplyDropsDropped() && sgGame.getGameState().ordinal() < MPGGame.GameState.DEATH_MATCH_COUNTDOWN.ordinal()) {
			// This is before deathmatch
			int realDeathmatchStartTime = MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_START_TIME) + MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME);

			if (sgGame.getDeathmatchStartTime() == realDeathmatchStartTime) {
				// Get the last supply drop time
				int lastSupplyDropTime = chestRefillTime;
				Iterator<Integer> iterator = SupplyDropTask.getInstance().getSupplyDropTimes().keySet().iterator();
				while (iterator.hasNext()) lastSupplyDropTime = iterator.next();

				player.setBossBar(ChatColor.AQUA + "Time Until Deathmatch " + StringCommon.niceTime(sgGame.getDeathmatchStartTime() - totalSeconds, false), 1F - (((float) (totalSeconds - lastSupplyDropTime)) / (sgGame.getDeathmatchStartTime() - lastSupplyDropTime)));
			} else {
				player.setBossBar(ChatColor.AQUA + "Time Until Deathmatch " + StringCommon.niceTime(sgGame.getDeathmatchStartTime() - totalSeconds, false), (sgGame.getDeathmatchStartTime() - totalSeconds) / MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_TELEPORT_COUNTDOWN_TIME));
			}
		} else if (sgGame.getGameState() == MPGGame.GameState.DEATH_MATCH_COUNTDOWN) {
			player.setBossBar(ChatColor.AQUA + "Time Until Random Damage " + StringCommon.niceTime(DeathMatchDamageTask.TIME_TO_START_DAMAGING, false), 1F);
		} else if (sgGame.getGameState() == MPGGame.GameState.DEATH_MATCH && totalSeconds <= DeathMatchDamageTask.TIME_TO_START_DAMAGING) {
			int deathmatchStartTimeWithCountdown = sgGame.getDeathmatchStartTime() + DeathMatchStartCountdownTask.COUNTDOWN_TIME;

			player.setBossBar(ChatColor.AQUA + "Time Until Random Damage " + StringCommon.niceTime(DeathMatchDamageTask.TIME_TO_START_DAMAGING - totalSeconds, false), 1F - (((float) (totalSeconds - deathmatchStartTimeWithCountdown)) / (DeathMatchDamageTask.TIME_TO_START_DAMAGING - deathmatchStartTimeWithCountdown)));
		} else {
			player.removeBossBar();
		}
    }

	@Override
	public void handlePlayerDeathInternal(final LivingEntity livingEntity, Map<String, Object> extraPayload) {
		// Does the player have a killer?
		if (livingEntity.getKiller() != null) {
			String killerRank = ChatColor.DARK_PURPLE + "hidden";
			SGPlayer killerSGPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(livingEntity.getKiller());

			// Increment internal sg cache of kill count
			killerSGPlayer.incrementTotalKills();

			// Has the player finished their placement matches to show their rating?
			if (killerSGPlayer.getGamesPlayed() >= RatingUtil.SG_PLACEMENT_MATCHES) {
				// Is killer's rank visible?
				if ((boolean) UserDataManager.getUserData(livingEntity.getKiller()).getSGSettings().get("rating_visibility")) {
					killerRank = RatingUtil.getDivisionFromRating(SurvivalGames.getInstance().getSGGame().getPlayerRating(livingEntity.getKiller().getUniqueId()));
				}
			} else {
				killerRank = ChatColor.WHITE + "[Unranked]";
			}

			if (livingEntity instanceof Player) {
				((Player) livingEntity).sendMessage(ChatColor.AQUA + livingEntity.getKiller().getDisguisedName() + ChatColor.YELLOW + "'s rank is " + killerRank);
			}
		}

		// Does the player have a killer?
		if (livingEntity.getKiller() != null) {
			// Is the player online?
			if (livingEntity instanceof Player) {
				String playerRank = ChatColor.DARK_PURPLE + "hidden";

				// Has the player finished their placement matches to show their rating?
				if (this.gamesPlayed >= RatingUtil.SG_PLACEMENT_MATCHES) {
					// Is this player's rank visible?
					if ((boolean) UserDataManager.getUserData(this.uuid).getSGSettings().get("rating_visibility")) {
						playerRank = RatingUtil.getDivisionFromRating(SurvivalGames.getInstance().getSGGame().getPlayerRating(this.uuid));
					}
				} else {
					playerRank = ChatColor.WHITE + "[Unranked]";
				}

				String disguisedName = ((Player) livingEntity).getDisguisedName();

				// Send player's rank to the killer
				livingEntity.getKiller().sendMessage(ChatColor.AQUA + disguisedName + ChatColor.YELLOW + "'s rank is " + playerRank);
			} else {
				// Load their user data manually
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						String playerRank = ChatColor.DARK_PURPLE + "hidden";

						// Has the player finished their placement matches to show their rating?
						if (SGPlayer.this.gamesPlayed >= RatingUtil.SG_PLACEMENT_MATCHES) {
							// Is this player's rank visible?
							if ((boolean) UserDataManager.getUserDataFromDB(SGPlayer.this.uuid).getSGSettings().get("rating_visibility")) {
								playerRank = RatingUtil.getDivisionFromRating(SurvivalGames.getInstance().getSGGame().getPlayerRating(SGPlayer.this.uuid));
							}
						} else {
							playerRank = ChatColor.WHITE + "[Unranked]";
						}

						String disguisedName = livingEntity.getCustomName();

						// Send player's rank to the killer
						livingEntity.getKiller().sendMessage(ChatColor.AQUA + disguisedName + ChatColor.YELLOW + "'s rank is " + playerRank);
					}
				});
			}
		}

		Map<String, Object> ratingData = new HashMap<>();
		Map<String, Object> map = new HashMap<>();

		// Calculate this player's score
		int score = this.calculateScore();

		int position = SurvivalGames.getInstance().getSGGame().decrementPosition();
		this.setPosition(position);

		// Insert extra data into payload
		map.put("score", score);
		map.put("position", position);

		ratingData.put(this.uuid.toString(), map);

		extraPayload.put("rating_data", ratingData);

		super.handlePlayerDeathInternal(livingEntity, extraPayload);

		// Lightning bitch
		livingEntity.getWorld().strikeLightningEffect(livingEntity.getLocation());

		// Is this not a clan game?
		if (!MPG.getInstance().getMPGGame().isClanGame()) {
			// Figure out if we need to add a loss for this team in the ratings table

			// Get list of team UUIDs
			final List<UUID> teamUUIDs = team.getUUIDs();

			for (UUID uuid : teamUUIDs) {
				MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

				// Don't add a loss if not everyone in this team is dead yet
				if (this.getUniqueId() != uuid && (mpgPlayer.getState() == PlayerState.PLAYER || mpgPlayer.getState() == PlayerState.DC))
					return;
			}

			// Add a loss for all team members
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					String query = "UPDATE sg_ladder_ratings_s2 SET losses = losses + 1 WHERE uuid = ?";

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
	}

	public int calculateScore() {
		int score = this.getNumberTierChestsOpened(1) * SGPlayer.TIER_1_CHEST_SCORE_MULTIPLIER;

		score += this.getNumberTierChestsOpened(2) * SGPlayer.TIER_2_CHEST_SCORE_MULTIPLIER;

		score += this.getNumberSupplyDropsOpened() * SGPlayer.SUPPLY_DROP_SCORE_MULTIPLIER;

		// Penalize people who didn't contribute damage prior to deathmatch
		if (!this.reachedPreDMDamageThreshold && MPG.getInstance().getMPGGame().getGameState().ordinal() >= MPGGame.GameState.DEATH_MATCH.ordinal()) {
			score -= SGPlayer.NOT_ENOUGH_DAMAGE_BEFORE_DM_SCORE_MULTIPLIER;
		} else if (!this.gotKillsBeforeDM()) {
			score -= SGPlayer.NO_KILLS_BEFORE_DM_SCORE_MULTIPLIER;
		}

		score += this.getKills() * SGPlayer.KILLS_SCORE_MULTIPLIER;

		return score;
	}

	@Override
	public int addKill() {
		// Add to separate stat if it wasn't during deathmatch
		if (MPG.getInstance().getMPGGame().getGameState() != MPGGame.GameState.DEATH_MATCH) {
			this.nonDeathMatchKills += 1;
		}

		// Increment number of kills for player in sidebar manager cache
		Integer kills = SGSidebarManager.playerKills.get(this.getUniqueId());

		if (kills == null) kills = 0;

		SGSidebarManager.playerKills.put(this.getUniqueId(), ++kills);

		if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
			// Increment number of kills for team in sidebar manager cache
			kills = SGSidebarManager.teamKills.get(this.getTeam());

			if (kills == null) kills = 0;

			SGSidebarManager.teamKills.put(this.getTeam(), ++kills);
		}

		return super.addKill();
	}

	public boolean gotKillsBeforeDM() {
		return this.getKills() > this.nonDeathMatchKills;
	}

	public void incrementNumberTierChestsOpened(int tier) {
		Integer opened = this.tierChestsOpened.get(tier);
		if (opened == null) {
			opened = 0;
			this.tierChestsOpened.put(tier, 0);
		}

		this.tierChestsOpened.put(tier, opened + 1);
	}

	public int getNumberTierChestsOpened(int tier) {
		Integer opened = this.tierChestsOpened.get(tier);
		if (opened == null) {
			opened = 0;
			this.tierChestsOpened.put(tier, 0);
		}

		return opened;
	}

	public int getNumberSupplyDropsOpened() {
		return this.supplyDropLocationsOpened.size();
	}

	public Set<Location> getChestLocationsOpened() {
		return this.chestLocationsOpened;
	}

	public Set<Location> getSupplyDropLocationsOpened() {
		return this.supplyDropLocationsOpened;
	}

	public void setReachedPreDMDamageThreshold(boolean reachedPreDMDamageThreshold) {
		this.reachedPreDMDamageThreshold = reachedPreDMDamageThreshold;
	}

	public void incrementTotalKills() {
		this.totalKills++;
	}

	public int getTotalKills() {
		return this.totalKills;
	}

	public void setTotalKills(int totalKills) {
		this.totalKills = totalKills;
	}

	public void incrementGamesWon() {
		this.gamesWon++;

		this.gamesPlayed = this.gamesWon + this.gamesLost;
	}

	public int getGamesWon() {
		return this.gamesWon;
	}

	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;

		this.gamesPlayed = gamesWon + this.gamesLost;
	}

	public int getGamesLost() {
		return this.gamesLost;
	}

	public void setGamesLost(int gamesLost) {
		this.gamesLost = gamesLost;

		this.gamesPlayed = gamesLost + this.gamesWon;
	}

	public int getGamesPlayed() {
		return this.gamesPlayed;
	}

}