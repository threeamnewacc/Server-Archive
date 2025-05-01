package net.badlion.ministats;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

	public class PlayerKill {

		private UUID killerUUID;
		private String killerUsername;

		private UUID killedUUID;
		private String killedUsername;

		private String cause;
		private long timestamp = new DateTime(DateTimeZone.UTC).getMillis();

		private int newPlayerELO;
		private int oldPlayerELO;

		private int newKilledPlayerELO;
		private int oldKilledPlayerELO;

		private Material killerWeaponType;

		private Map<String, Double> healthOnDeath = new HashMap<>();
		private Map<String, Integer> foodOnDeath = new HashMap<>();
		private Map<String, ItemStack[]> armorOnDeath = new HashMap<>();
		private Map<String, ItemStack[]> itemsOnDeath = new HashMap<>();
		private Map<String, Collection<PotionEffect>> potionEffectsOnDeath = new HashMap<>();

		public PlayerKill(Player killedPlayer) {
			this.killerUUID = PlayerData.this.uuid;
			this.killerUsername = PlayerData.this.username;

			this.killedUUID = killedPlayer.getUniqueId();
			this.killedUsername = killedPlayer.getName();

			if (killedPlayer.getLastDamageCause() != null) {
				this.cause = killedPlayer.getLastDamageCause().getCause().name();
			}

			this.storePlayerStats(PlayerData.this.uuid);
			this.storePlayerStats(killedPlayer.getUniqueId());
		}

		public PlayerKill(PlayerData killedPlayerData, LivingEntity combatLogger) {
			this.killerUUID = PlayerData.this.uuid;
			this.killerUsername = PlayerData.this.username;

			this.killedUUID = killedPlayerData.getUniqueId();
			this.killedUsername = killedPlayerData.getUsername();

			if (combatLogger.getLastDamageCause() != null) {
				this.cause = combatLogger.getLastDamageCause().getCause().name();
			}

			this.storePlayerStats(PlayerData.this.uuid);
			this.storePlayerStats(killedPlayerData.getUniqueId());
		}

        public void storePlayerStats(UUID uuid) {
	        Player player = Bukkit.getPlayer(uuid);

	        // Store player's data if player is online
			if (player != null) {
				this.potionEffectsOnDeath.put(player.getUniqueId().toString(), player.getActivePotionEffects());
				this.armorOnDeath.put(player.getUniqueId().toString(), player.getInventory().getArmorContents());
				this.itemsOnDeath.put(player.getUniqueId().toString(), player.getInventory().getContents());
				this.healthOnDeath.put(player.getUniqueId().toString(), player.getHealth());
				this.foodOnDeath.put(player.getUniqueId().toString(), player.getFoodLevel());
			} else {
				this.potionEffectsOnDeath.put(uuid.toString(), new ArrayList<PotionEffect>());
				this.armorOnDeath.put(uuid.toString(), new ItemStack[4]);
				this.itemsOnDeath.put(uuid.toString(), new ItemStack[36]);
				this.healthOnDeath.put(uuid.toString(), 0D);
				this.foodOnDeath.put(uuid.toString(), 0);
			}
        }

		public UUID getKillerUUID() {
			return this.killerUUID;
		}

		public String getKillerUsername() {
			return this.killerUsername;
		}

		public UUID getKilledUUID() {
			return this.killedUUID;
		}

		public String getKilledUsername() {
			return this.killedUsername;
		}

		public String getCause() {
			return this.cause;
		}

		public long getTimestamp() {
			return this.timestamp;
		}

		public int getNewPlayerELO() {
			return this.newPlayerELO;
		}

		public void setNewPlayerELO(int newPlayerELO) {
			this.newPlayerELO = newPlayerELO;
		}

		public int getOldPlayerELO() {
			return this.oldPlayerELO;
		}

		public void setOldPlayerELO(int oldPlayerELO) {
			this.oldPlayerELO = oldPlayerELO;
		}

		public int getNewKilledPlayerELO() {
			return this.newKilledPlayerELO;
		}

		public void setNewKilledPlayerELO(int newKilledPlayerELO) {
			this.newKilledPlayerELO = newKilledPlayerELO;
		}

		public int getOldKilledPlayerELO() {
			return this.oldKilledPlayerELO;
		}

		public void setOldKilledPlayerELO(int oldKilledPlayerELO) {
			this.oldKilledPlayerELO = oldKilledPlayerELO;
		}

		public Material getKillerWeaponType() {
			return this.killerWeaponType;
		}

		public void setKillerWeaponType(Material killerWeaponType) {
			this.killerWeaponType = killerWeaponType;
		}

		public Map<String, Double> getHealthOnDeath() {
			return this.healthOnDeath;
		}

		public Map<String, Integer> getFoodOnDeath() {
			return this.foodOnDeath;
		}

		public Map<String, ItemStack[]> getArmorOnDeath() {
			return this.armorOnDeath;
		}

		public Map<String, ItemStack[]> getItemsOnDeath() {
			return this.itemsOnDeath;
		}

		public Map<String, Collection<PotionEffect>> getPotionEffectsOnDeath() {
			return this.potionEffectsOnDeath;
		}

	}

	protected Suicide suicide;

	public class Suicide {

		protected int food;
		protected double health;

		protected String cause;
		protected long timestamp = new DateTime(DateTimeZone.UTC).getMillis();

		protected ItemStack[] armor;
		protected ItemStack[] items;
		protected Collection<PotionEffect> potionEffects;

		public Suicide(Player player) {
		 	this.potionEffects = player.getActivePotionEffects();

			this.items = player.getInventory().getContents();
			this.armor = player.getInventory().getArmorContents();

			this.health = player.getHealth();
			this.food = player.getFoodLevel();

			// Get damage cause
			EntityDamageEvent.DamageCause damageCause;
			if (player.getLastDamageCause() != null) {
				damageCause = player.getLastDamageCause().getCause();
			} else {
				damageCause = EntityDamageEvent.DamageCause.CUSTOM;
			}

			this.cause = damageCause.name();
		}

		public Suicide(LivingEntity combatLogger) {
			this.potionEffects = combatLogger.getActivePotionEffects();

			this.items = (ItemStack[]) combatLogger.getMetadata("CombatLoggerInventory").get(0).value();
			this.armor = (ItemStack[]) combatLogger.getMetadata("CombatLoggerArmorInventory").get(0).value();

			this.health = combatLogger.getHealth();
			this.food = 20; // Just set this to 20, we don't track it in the combat logger

			this.timestamp = new DateTime(DateTimeZone.UTC).getMillis();

			// Get damage cause
			EntityDamageEvent.DamageCause damageCause;
			if (combatLogger.getLastDamageCause() != null) {
				damageCause = combatLogger.getLastDamageCause().getCause();
			} else {
				damageCause = EntityDamageEvent.DamageCause.CUSTOM;
			}

			this.cause = damageCause.name();
		}

		public int getFood() {
			return this.food;
		}

		public double getHealth() {
			return this.health;
		}

		public String getCause() {
			return this.cause;
		}

		public long getTimestamp() {
			return this.timestamp;
		}

		public ItemStack[] getArmor() {
			return this.armor;
		}

		public ItemStack[] getItems() {
			return this.items;
		}

		public Collection<PotionEffect> getPotionEffects() {
			return this.potionEffects;
		}

	}

	protected UUID uuid;
	protected String username;

	private String mapName;

	private int points = 0;
	private int bowPunches = 0;
	private int swordSwings = 0;
	private int swordHits = 0;
	private int swordBlocks = 0;
	private int arrowsFired = 0;
	private int arrowsHitTarget = 0;
	private int currentKillStreak = 0;
	private int highestKillStreak = 0;

	private HashSet<PlayerKill> playerKills = new HashSet<>();
	private HashSet<PlayerKill> killedBy = new HashSet<>(); // Backwards mapping

	private int kills = 0;
	private int deaths = 0;
	private int assists = 0;
	private double damageDealt = 0L;
	private double damageTaken = 0L;
	private long totalTimePlayed = 0L;

	private int xpEarned;

	private boolean wonGame = false;

	public int playerWon() {
		return this.wonGame ? 1 : 0;
	}
	public int playerLost() {
		return this.wonGame ? 0 : 1;
	}

    public String lastTeamOn;
	protected boolean trackData = true;

	// Stat Track info for if they log out and DC
	private Collection<PotionEffect> potionEffects;
	private ItemStack[] armor;
	private ItemStack[] items;
	private double health;
	private int food;
	private long lastTimeOnline = 0;

    public PlayerData(UUID uuid, String username, String mapName) {
		this.uuid = uuid;
        this.username = username;

		this.mapName = mapName;
	}

	public PlayerKill addPlayerKill(Player killedPlayer) {
		this.addKill();

		PlayerKill playerKill = new PlayerKill(killedPlayer);
		this.playerKills.add(playerKill);

		// Track reverse mapping
		PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerData(killedPlayer);
		if (playerData != null) {
			playerData.addPlayerDeath(playerKill);
		}

		return playerKill;
	}

	public PlayerKill addPlayerKill(PlayerData killedPlayerData, LivingEntity combatLogger) {
		this.addKill();

		PlayerKill playerKill = new PlayerKill(killedPlayerData, combatLogger);
		this.playerKills.add(playerKill);

		// Track reverse mapping
		PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerData(killedPlayerData.getUniqueId());
		if (playerData != null) {
			playerData.addPlayerDeath(playerKill);
		}

		return playerKill;
	}

	public void addSuicide(Player player) {
		this.suicide = new Suicide(player);
	}

	/**
	 * NOTE: This method is only for a combat logger NPC
	 */
	public void addSuicide(LivingEntity combatLogger) {
		this.suicide = new Suicide(combatLogger);
	}

	public Suicide getSuicide() {
		return this.suicide;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * Returns the player's real username
	 */
    public String getUsername() {
        return username;
    }

    public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getBowPunches() {
		return bowPunches;
	}

	public void setBowPunches(int bowPunches) {
		this.bowPunches = bowPunches;
	}

	public int getSwordHits() {
		return swordHits;
	}

	public void setSwordHits(int swordHits) {
		this.swordHits = swordHits;
	}

	public int getSwordSwings() {
		return swordSwings;
	}

	public void setSwordSwings(int swordSwings) {
		this.swordSwings = swordSwings;
	}

	public int getSwordBlocks() {
		return swordBlocks;
	}

	public void setSwordBlocks(int swordBlocks) {
		this.swordBlocks = swordBlocks;
	}

	public int getArrowsFired() {
		return arrowsFired;
	}

	public void setArrowsFired(int arrowsFired) {
		this.arrowsFired = arrowsFired;
	}


	public int getArrowsHitTarget() {
		return arrowsHitTarget;
	}

	public void setArrowsHitTarget(int arrowsHitTarget) {
		this.arrowsHitTarget = arrowsHitTarget;
	}

	public int getCurrentKillStreak() {
		return currentKillStreak;
	}

	public void setCurrentKillStreak(int currentKillStreak) {
		this.currentKillStreak = currentKillStreak;
	}

	public int getHighestKillStreak() {
		return highestKillStreak;
	}

	public void setHighestKillStreak(int highestKillStreak) {
		this.highestKillStreak = highestKillStreak;
	}

	public HashSet<PlayerKill> getPlayerKills() {
		return playerKills;
	}

	public int addKill() {
		return this.kills++;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int addDeath() {
		return this.deaths++;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public double getDamageDealt() {
		return damageDealt;
	}

	public void setDamageDealt(double damageDealt) {
		this.damageDealt = damageDealt;
	}

	public double getDamageTaken() {
		return damageTaken;
	}

	public void setDamageTaken(double damageTaken) {
		this.damageTaken = damageTaken;
	}

	public long getTotalTimePlayed() {
		return totalTimePlayed;
	}

	public void addTotalTimePlayed(long timePlayed) {
		this.totalTimePlayed += timePlayed;
	}

	public int getXpEarned() {
		return xpEarned;
	}

	public void setXpEarned(int xpEarned) {
		this.xpEarned = xpEarned;
	}

	public boolean isWonGame() {
		return wonGame;
	}

	public void setWonGame(boolean wonGame) {
		this.wonGame = wonGame;
	}

	public int getAssists() {
		return assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public boolean isTrackData() {
		return trackData;
	}

	public void setTrackData(boolean trackData) {
		this.trackData = trackData;
	}

	public Collection<PotionEffect> getPotionEffects() {
		return potionEffects;
	}

	public void setPotionEffects(Collection<PotionEffect> potionEffects) {
		this.potionEffects = potionEffects;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void setItems(ItemStack[] items) {
		this.items = items;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public void addPlayerDeath(PlayerKill playerKill) {
		this.killedBy.add(playerKill);
	}

	public HashSet<PlayerKill> getKilledBy() {
		return this.killedBy;
	}

	public long getLastTimeOnline() {
		return lastTimeOnline;
	}

	public void setLastTimeOnline(long lastTimeOnline) {
		this.lastTimeOnline = lastTimeOnline;
	}

}
