package net.badlion.uhc;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import net.badlion.uhc.tasks.GameTimeTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UHCPlayer {

	public enum State {
		PLAYER, SPEC_IN_GAME, DEAD, SPEC, MOD, HOST
	}

	// Stat tracking stuff
	public static Set<Material> BLOCKS = new HashSet<>();
	private static Set<EntityType> ANIMALS_MOBS = new HashSet<>();

	static {
		BLOCKS.add(Material.COAL_ORE);
		BLOCKS.add(Material.IRON_ORE);
		BLOCKS.add(Material.GOLD_ORE);
		BLOCKS.add(Material.DIAMOND_ORE);
		BLOCKS.add(Material.EMERALD_ORE);
		BLOCKS.add(Material.REDSTONE_ORE);
		BLOCKS.add(Material.GLOWING_REDSTONE_ORE);
		BLOCKS.add(Material.LAPIS_ORE);
		BLOCKS.add(Material.GLOWSTONE);

		ANIMALS_MOBS.add(EntityType.PIG);
		ANIMALS_MOBS.add(EntityType.COW);
		ANIMALS_MOBS.add(EntityType.CHICKEN);
		ANIMALS_MOBS.add(EntityType.SHEEP);
		ANIMALS_MOBS.add(EntityType.ZOMBIE);
		ANIMALS_MOBS.add(EntityType.CREEPER);
		ANIMALS_MOBS.add(EntityType.SKELETON);
		ANIMALS_MOBS.add(EntityType.PIG_ZOMBIE);
		ANIMALS_MOBS.add(EntityType.SPIDER);
		ANIMALS_MOBS.add(EntityType.CAVE_SPIDER);
		ANIMALS_MOBS.add(EntityType.HORSE);
		ANIMALS_MOBS.add(EntityType.SLIME);
		ANIMALS_MOBS.add(EntityType.ENDERMAN);
		ANIMALS_MOBS.add(EntityType.SILVERFISH);
		ANIMALS_MOBS.add(EntityType.BLAZE);
		ANIMALS_MOBS.add(EntityType.WITCH);
		ANIMALS_MOBS.add(EntityType.WOLF);
		ANIMALS_MOBS.add(EntityType.VILLAGER);
	}

	private JSONObject potions = new JSONObject();

	private UUID uuid;
	private String username;
	private String disguisedName;
	private UHCTeam team;
	private UHCTeam teamRequest;
	private State state;
	private boolean doStatTracking;

	private Long AFKTimeLeft;
	private Location lastAFKCheckLocation;
	private BukkitTask offlineTask;
	private Long deathTime;
	private int gameDeathTimeInSeconds;
	private int numOfPlayersAliveOnDeath;
	private int numOfTeamsAliveOnDeath;

	private int kills = 0;
	private boolean solo = false;
	private boolean wasFed = false;
	private boolean giveInventory = false;
	private ItemStack[] armor;
	private ItemStack[] inventory;
	private Location deathLocation;

	private JSONObject blocksBroken = new JSONObject();
	private JSONObject animalMobsKilled = new JSONObject();
	private int levels = 0;
	private int heartsHealed = 0;
	private int horses = 0;
	private double fallDamageTaken = 0;
	private int absorptionHearts = 0;
	private int gapplesEaten = 0;
	private int headsEaten = 0;
	private int netherPortalsEntered = 0;
	private int endPortalsEntered = 0;

	private boolean canSpectate = false;

	private boolean scoreboardInitialized = false;

	public UHCPlayer(UUID uuid, String username, State state) {
		this.uuid = uuid;
		this.username = username;
		this.state = state;

		// Only track stats if we were a player (used for the end flushing)
		this.doStatTracking = this.state == State.PLAYER;
	}

	public UUID getUUID() {
		return uuid;
	}

	public UHCTeam getTeam() {
		return team;
	}

	public boolean isAliveAndPlaying() {
		return this.state == State.PLAYER || this.state == State.SPEC_IN_GAME;
	}

	public void setTeam(UHCTeam team) {
		this.team = team;
	}

	public UHCTeam getTeamRequest() {
		return teamRequest;
	}

	public void setTeamRequest(UHCTeam teamRequest) {
		this.teamRequest = teamRequest;
	}

	public Long getAFKTimeLeft() {
		return AFKTimeLeft;
	}

	public void setAFKTimeLeft(Long AFKTimeLeft) {
		this.AFKTimeLeft = AFKTimeLeft;

		if (AFKTimeLeft == null) {
			if (this.getOfflineTask() != null) {
				this.getOfflineTask().cancel();
				this.setOfflineTask(null);
			}
		}
	}

	public Location getLastAFKCheckLocation() {
		return lastAFKCheckLocation;
	}

	public void setLastAFKCheckLocation(Location lastAFKCheckLocation) {
		this.lastAFKCheckLocation = lastAFKCheckLocation;
	}

	public String getUsername() {
		return username;
	}

	public State getState() {
		return state;
	}

	/**
	 * Mini State Machine
	 */
	public void setState(State state) {
		if (state == State.DEAD && this.state != State.PLAYER) {
			throw new RuntimeException("Player being moved to dead state from " + this.state.name());
		}

		this.state = state;

		if (state == State.DEAD) {
			BadlionUHC.getInstance().checkForWinners();
		}
	}

	public boolean isVanishedPlayer() {
		return this.state == State.HOST || this.state == State.SPEC || this.state == State.MOD;
	}

	public BukkitTask getOfflineTask() {
		return offlineTask;
	}

	public void setOfflineTask(BukkitTask offlineTask) {
		this.offlineTask = offlineTask;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void addKill() {
		this.kills += 1;

		this.team.addKill();
	}

	public int getKills() {
		return kills;
	}

	public boolean canSpectate() {
		return this.canSpectate;
	}

	public void checkIfCanSpectate(Player player) {
		this.canSpectate = player.hasPermission("badlion.donatorplus");
	}

	public static enum SCOREBOARD_ENTRIES {
		YOUR_KILLS, TOP_KILLS, GAME_TIME, PVP_TIME, CURRENT_BORDER, PLAYERS_LEFT, TEAMS_LEFT, TEAM_KILLS, WEBSITE, BLANK, BLANK_1, BLANK_2, SPECTATORS, BLANK_3, BLANK_4
	}

	public void updateScoreboard() {
		final Player pl = BadlionUHC.getInstance().getServer().getPlayer(this.uuid);
		if (pl == null) {
			return;
		}

		Scoreboard scoreboard = pl.getScoreboard();
		Objective objective = ScoreboardUtil.getObjective(scoreboard, ChatColor.AQUA + "Badlion UHC", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion UHC");

		Team team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.GAME_TIME.name(), ChatColor.GREEN + "Game ", "Time: " + ChatColor.WHITE);
		team.setSuffix(StringCommon.niceTime(GameTimeTask.hours, GameTimeTask.minutes, GameTimeTask.seconds));
		objective.getScore("Time: " + ChatColor.WHITE).setScore(11);

		ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.BLANK_2.name(), ChatColor.WHITE + "", "  ");
		objective.getScore("  ").setScore(10);

		team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.YOUR_KILLS.name(), ChatColor.GREEN + "Your ", "Kills: " + ChatColor.WHITE);
		team.setSuffix("" + this.kills);
		objective.getScore("Kills: " + ChatColor.WHITE).setScore(9);

		if (BadlionUHC.getInstance().getGameType().ordinal() >= UHCTeam.GameType.TEAM.ordinal()) {
			team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.TEAM_KILLS.name(), ChatColor.GREEN + "Team ", "Kills: " + ChatColor.RESET + ChatColor.WHITE);
			team.setSuffix("" + this.team.getKills());
			objective.getScore("Kills: " + ChatColor.RESET + ChatColor.WHITE).setScore(8);

			team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.TEAMS_LEFT.name(), ChatColor.GREEN + "Teams ", "Left: " + ChatColor.RESET + ChatColor.WHITE);
			team.setSuffix("" + UHCTeamManager.getAllAlivePlayingTeams().size());
			objective.getScore("Left: " + ChatColor.RESET + ChatColor.WHITE).setScore(6);
		}

		ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.BLANK_1.name(), ChatColor.WHITE + "", " ");
		objective.getScore(" ").setScore(7);

		team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.PLAYERS_LEFT.name(), ChatColor.GREEN + "Players ", "Left: " + ChatColor.WHITE);
		team.setSuffix("" + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size());
		objective.getScore("Left: " + ChatColor.WHITE).setScore(5);

		team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.SPECTATORS.name(), ChatColor.GREEN + "Spectators:", " " + ChatColor.WHITE);
		team.setSuffix("" + GameTimeTask.NUM_OF_SPEC_ONLINE);
		objective.getScore(" " + ChatColor.WHITE).setScore(4);

		ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.BLANK.name(), ChatColor.WHITE + "", "");
		objective.getScore("").setScore(3);

		team = ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.CURRENT_BORDER.name(), ChatColor.GOLD + "Current ", "Border: " + ChatColor.WHITE);
		team.setSuffix("" + BadlionUHC.getInstance().getWorldBorder().GetWorldBorder(BadlionUHC.UHCWORLD_NAME).getRadiusX());
		objective.getScore("Border: " + ChatColor.WHITE).setScore(2);

		ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.WEBSITE.name(), ChatColor.AQUA + "", "www.badlion.net");
		objective.getScore("www.badlion.net").setScore(1);

		// Mini UHC Team Colors
		if (!this.scoreboardInitialized && BadlionUHC.getInstance().isMiniUHC() && BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
			this.scoreboardInitialized = true;

			this.handleColorScoreboard(scoreboard);
		}
	}

	public void handleColorScoreboard(Scoreboard scoreboard) {
		for (int i = 0; i < BadlionUHC.validTeamColors.length; i++) {
			ScoreboardUtil.getTeam(scoreboard, BadlionUHC.validTeamColors[i].name(), BadlionUHC.validTeamColors[i] + "", BadlionUHC.validTeamColors[i].name());
		}

		for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(State.PLAYER)) {
			Team team = ScoreboardUtil.getTeam(scoreboard, uhcPlayer.getTeam().getChatColor().name(), uhcPlayer.getTeam().getChatColor() + "", uhcPlayer.getTeam().getChatColor().name());
			team.addEntry(uhcPlayer.getUsername());
			if (uhcPlayer.getUsername().length() > 12) {
				team.addEntry(uhcPlayer.getUsername().substring(0, 11));
			}
		}
	}

	public boolean isSolo() {
		return solo;
	}

	public void setSolo(boolean solo) {
		this.solo = solo;
	}

	public Long getDeathTime() {
		return deathTime;
	}

	public void setDeathTime(Long deathTime) {
		this.deathTime = deathTime;
		this.gameDeathTimeInSeconds = GameTimeTask.getNumOfSeconds();

		// Stat Tracking
		PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().get(this.uuid);
		if (playerData != null) {
			playerData.addTotalTimePlayed(this.gameDeathTimeInSeconds);
		}

		this.numOfTeamsAliveOnDeath = UHCTeamManager.getAllAlivePlayingTeams().size();
		this.numOfPlayersAliveOnDeath = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER).size();
	}

	public void addBlock(Material material) {
		if (UHCPlayer.BLOCKS.contains(material)) {
			if (material == Material.GLOWING_REDSTONE_ORE) {
				material = Material.REDSTONE_ORE;
			}

			if (!this.blocksBroken.containsKey(material.name())) {
				this.blocksBroken.put(material.name(), 0);
			}

			this.blocksBroken.put(material.name(), (int) this.blocksBroken.get(material.name()) + 1);
		}
	}

	public void addAnimalMob(EntityType entityType) {
		if (UHCPlayer.ANIMALS_MOBS.contains(entityType)) {
			if (!this.animalMobsKilled.containsKey(entityType.name())) {
				this.animalMobsKilled.put(entityType.name(), 0);
			}

			this.animalMobsKilled.put(entityType.name(), (int) this.animalMobsKilled.get(entityType.name()) + 1);
		}
	}

	public void storeDeathData(Player player) {
		this.armor = player.getInventory().getArmorContents();
		this.inventory = player.getInventory().getContents();
		this.deathLocation = player.getLocation();
	}

	public void addLevels(int num) {
		this.levels += num;
	}

	public void addHorsesTamed() {
		this.horses += 1;
	}

	public void addFallDamage(double amt) {
		this.fallDamageTaken += amt;
	}

	public void addAbsorptionHearts() {
		this.absorptionHearts += 2;
	}

	public int getGoldenHeads() {
		return this.headsEaten;
	}

	public int getGoldenApples() {
		return this.gapplesEaten;
	}

	public void addGoldenHead() {
		this.heartsHealed += 4;
		this.headsEaten += 1;
	}

	public void addGoldenApple() {
		this.heartsHealed += 2;
		this.gapplesEaten += 1;
	}

	public int getNetherPortalsEntered() {
		return this.netherPortalsEntered;
	}

	public int getEndPortalsEntered() {
		return this.endPortalsEntered;
	}

	// Only track 1 per game
	public void addNetherPortal() {
		this.netherPortalsEntered = 1;
	}

	// Only track 1 per game
	public void addEndPortal() {
		this.endPortalsEntered = 1;
	}

	public int getGameDeathTimeInSeconds() {
		return gameDeathTimeInSeconds;
	}

	public int getNumOfTeamsAliveOnDeath() {
		return numOfTeamsAliveOnDeath;
	}

	public int getNumOfPlayersAliveOnDeath() {
		return numOfPlayersAliveOnDeath;
	}

	public void addPotion(short potion) {
		String key = ((Short) potion).toString();
		if (!this.potions.containsKey(key)) {
			this.potions.put(key, 0);
		}

		this.potions.put(key, MiniStats.mergeNumbers(this.potions.get(key), 1));
	}

	public boolean trackStats() {
		return this.doStatTracking;
	}

	public JSONObject getBlocksBroken() {
		return blocksBroken;
	}

	public JSONObject getAnimalMobsKilled() {
		return animalMobsKilled;
	}

	public int getLevels() {
		return levels;
	}

	public int getHeartsHealed() {
		return heartsHealed;
	}

	public int getHorses() {
		return horses;
	}

	public double getFallDamageTaken() {
		return fallDamageTaken;
	}

	public int getAbsorptionHearts() {
		return absorptionHearts;
	}

	public JSONObject getPotions() {
		return potions;
	}

	public boolean isWasFed() {
		return wasFed;
	}

	public void setWasFed(boolean wasFed) {
		this.wasFed = wasFed;
	}

	public boolean isGiveInventory() {
		return giveInventory;
	}

	public void setGiveInventory(boolean giveInventory) {
		this.giveInventory = giveInventory;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

	public Location getDeathLocation() {
		return deathLocation;
	}

	public void setDeathLocation(Location deathLocation) {
		this.deathLocation = deathLocation;
	}

	public String getDisguisedName() {
		if (this.disguisedName != null) {
			return this.disguisedName;
		}

		return this.username;
	}

	public void setDisguisedName(String disguisedName) {
		this.disguisedName = disguisedName;
	}

	public Player getPlayer() {
		return BadlionUHC.getInstance().getServer().getPlayer(this.uuid);
	}

	@Override
	public String toString() {
		return this.getUsername();
	}

}