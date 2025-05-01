package net.badlion.potpvp.tdm;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.arenas.TDMArena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.inventories.lobby.TDMInventory;
import net.badlion.potpvp.inventories.spectator.SpectateTDMInventory;
import net.badlion.potpvp.inventories.tdm.TDMVoteInventory;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.managers.RespawnManager;
import net.badlion.potpvp.managers.TDMManager;
import net.badlion.potpvp.rulesets.HorseRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.TDMState;
import net.badlion.potpvp.tasks.TDMTimeLimitTask;
import org.bukkit.*;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TDMGame implements Game {

	public static final int COMBAT_TAG_TIME = 15000;
	public static final int RESPAWN_TIME = 3; // In seconds
	public static final int RESISTANCE_TIME = 10; // In seconds
	public static final int ASSIST_TIME = 15 * 1000; // in milliseconds
	public static final double ASSIST_DAMAGE = 5.0;

	public static final String PREFIX = ChatColor.GOLD + ChatColor.BOLD.toString() + "[TDM] " + ChatColor.RESET;

	public static final ChatColor[] TEAM_COLORS = new ChatColor[] { ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.GOLD };

	private static List<KitRuleSet> kitRuleSets = new ArrayList<>();

	private static List<TDMGame> tdmGames = new ArrayList<>();
	private static Map<String, TDMGame> tdmItemGames = new HashMap<>();

	protected TDMArena arena;

	protected int teamSize;
	private final int totalPlayers;
	protected ItemStack tdmItem;
	protected KitRuleSet kitRuleSet;

	protected TDMTimeLimitTask tdmTimeLimitTask;

	protected List<Player> players = new ArrayList<>();

	protected Map<UUID, TDMTeam> playersToTeams = new HashMap<>();
	protected Map<TDMTeam, List<UUID>> teamsToPlayers = new HashMap<>();

	// Kit voting
	private boolean voting = false;
	private Map<UUID, KitRuleSet> playersVoted = new HashMap<>();
	private Map<KitRuleSet, Integer> votes = new HashMap<>();

	public TDMGame(int numOfTeams, int teamSize, KitRuleSet kitRuleSet) {
		this.arena = (TDMArena) ArenaManager.getArena(ArenaManager.ArenaType.TDM);

		this.teamSize = teamSize;
		this.totalPlayers = numOfTeams * teamSize;
		this.kitRuleSet = kitRuleSet;

		// Create all the teams
		for (int i = 0; i < numOfTeams; i++) {
			new TDMTeam(TDMGame.TEAM_COLORS[i]);
		}

		// Create TDM item
		int tdmNumber = TDMGame.tdmGames.size() + 1;
		ItemStack kitItem = kitRuleSet.getKitItem();
		this.tdmItem = ItemStackUtil.createItem(kitItem.getType(), kitItem.getDurability(), ChatColor.GREEN + "Join TDM #" + tdmNumber,
				ChatColor.YELLOW + "Players: 0/" + this.totalPlayers);

		TDMGame.tdmGames.add(this);
		TDMGame.tdmItemGames.put(this.tdmItem.getItemMeta().getDisplayName(), this);

		// Start time limit task
		this.tdmTimeLimitTask = new TDMTimeLimitTask(this);
		this.tdmTimeLimitTask.runTaskTimer(PotPvP.getInstance(), 20L, 20L);
	}

	/**
	 * Start a game
	 */
	@Override
	public void startGame() {
		// Always started
	}

	/**
	 * Get a KitRuleSet
	 */
	@Override
	public KitRuleSet getKitRuleSet() {
		return this.kitRuleSet;
	}

	/**
	 * Add player
	 */
	public boolean addPlayer(Player player) {
		player.setFallDistance(0F);

		if (this.voting) {
			player.setGameMode(GameMode.CREATIVE);
			player.spigot().setCollidesWithEntities(false);

			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		} else {
			PlayerHelper.healAndPrepPlayerForBattle(player);
		}

		TDMTeam tdmTeam = GroupStateMachine.tdmState.getAssignedTeam(player);

		// Add player to team
		tdmTeam.addPlayer(player);

		// Update item player count
		TDMInventory.updateTDMInventory(false);
		TDMInventory.updateTeamPlayerCountItems(this);
		SpectateTDMInventory.updateSpectateTDMInventory();

		// Don't give them kits if they're in the voting stage
		if (!this.voting) {
			KitHelper.loadKit(player, this.kitRuleSet);

			// Add unbreaking to weapons/armor (TDMState doesn't handle because this is called in before())
			ItemStackUtil.addUnbreakingToArmor(player);
			ItemStackUtil.addUnbreakingToWeapons(player);

			// Give them resistance
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * TDMGame.RESISTANCE_TIME, 128));

			ChatColor color = tdmTeam.getColor();

			// Do they have leather armor?
			for (ItemStack item : player.getInventory().getArmorContents()) {
				if (item == null || item.getType() == Material.AIR) continue;

				if (item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_CHESTPLATE
						|| item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_BOOTS) {
					// Color leather
					LeatherArmorMeta itemMeta = ((LeatherArmorMeta) item.getItemMeta());
					itemMeta.setColor(Gberry.getColorFromChatColor(color));
					item.setItemMeta(itemMeta);
				}
			}
		} else {
			player.getInventory().setItem(0, TDMVoteInventory.getVoteItem());

			player.sendMessage(TDMGame.PREFIX + ChatColor.GOLD + "Right click with the book to vote for the next kit!");
		}


		// Teleport to team spawn point
		player.teleport(this.arena.getSpawnLocation(tdmTeam));

		// Call follow event
		PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

		player.sendMessage(ChatColor.GREEN + "Added to the " + tdmTeam.getColor() + tdmTeam.getName()
				+ ChatColor.GREEN + " team! Score points for your team by killing other players. 1 kill = 1 point");

		return this.players.add(player);
	}

	/**
	 * Remove player
	 */
	public boolean removePlayer(Player player) {
		if (this.players.remove(player)) {
			TDMTeam tdmTeam = this.getTeam(player);
			tdmTeam.removePlayer(player);

			// Update item player count
			TDMInventory.updateTDMInventory(false);
			TDMInventory.updateTeamPlayerCountItems(this);
			SpectateTDMInventory.updateSpectateTDMInventory();

			return true;
		}

		return false;
	}

	/**
	 * Get unmodifiable list of players involved
	 */
	@Override
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(this.players);
	}

	/**
	 * Check if a player is contained in this game mode
	 */
	@Override
	public boolean contains(Player player) {
		return this.players.contains(player);
	}

	/**
	 * Some game modes have god apple cooldowns (this is nasty, idgaf)
	 */
	@Override
	public Map<String, Long> getGodAppleCooldowns() {
		return null; // Not supported
	}

	/**
	 * Handle a death
	 */
	@Override
	public void handleDeath(Player player) {
		GroupStateMachine.tdmState.handleScoreboardDeath(player, this);
	}

	@Override
	public Location handleRespawn(Player player) {
		if (!this.voting) {
			Location location = this.arena.getSpawnLocation(this.playersToTeams.get(player.getUniqueId()));

			RespawnManager.addPlayerRespawning(this, player, location, TDMGame.RESPAWN_TIME, TDMGame.RESISTANCE_TIME);
		}

		return player.getLocation();
	}

	/**
	 * Handle when someone quits or /spawn's
	 */
	@Override
	public boolean handleQuit(Player player, String reason) {
		// Let them leave if game is over
		if (this.voting) return true;

		if (reason.equals("spawn")) {
			if (TDMState.lastDamageTime.containsKey(player.getName())
					&& TDMState.lastDamageTime.get(player.getName()) + TDMGame.COMBAT_TAG_TIME >= System.currentTimeMillis()) {

				long timeRemaining = TDMState.lastDamageTime.get(player.getName()) + TDMGame.COMBAT_TAG_TIME - System.currentTimeMillis();

				player.sendMessage(ChatColor.RED + "Cannot use /spawn when in combat in TDM. You have "
						+ ((double) Math.round(((double) timeRemaining / 1000) * 10) / 10) + " seconds remaining.");

				return false; // Don't change states
			}

			return true; // Let the state machine do it's thing
		}

		// They logged off
		if (TDMState.lastDamageTime.containsKey(player.getName())
				&& TDMState.lastDamageTime.get(player.getName()) + TDMGame.COMBAT_TAG_TIME >= System.currentTimeMillis()) {

			// They were combat tagged, give them a death
			GroupStateMachine.tdmState.handleScoreboardDeath(player, this);
		}

		return true;
	}

	public void sendMessage(String msg, Player... force) {
		MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.TDM_MESSAGES, msg, this.players, force);
		PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
	}

	/**
	 * Assist tracking
	 */
	public void putLastDamage(UUID attacker, UUID defender, double damage, double finalDamage) {
		TDMManager.TDMStats tdmStats = TDMManager.getTDMStats(defender);
		tdmStats.addDamage(attacker, finalDamage);
	}

	public void addKillForTeam(Player player) {
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		// Is player still in the TDM?
		if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.tdmState) {
			// Give kill reward items
			for (ItemStack itemStack : this.kitRuleSet.getTDMKillRewardItems()) {
				player.getInventory().addItem(itemStack);
			}

			// Add 1 to team score
			this.getTeam(player).addScore();
		}
	}

	public void refreshKit(Player player) {
		PlayerHelper.healAndPrepPlayerForBattle(player);
		KitHelper.loadKit(player, this.kitRuleSet);

		if (this.kitRuleSet instanceof HorseRuleSet) {
			if (player.getVehicle() != null && player.getVehicle() instanceof Horse) {
				// Heal the horse
				Horse horse = ((Horse) player.getVehicle());
				horse.setHealth(40D);
			} else {
				// Spawn the horse
				HorseRuleSet.createHorseAndAttach(player, player.getLocation(), this.arena);
			}
		}

		// Handle counter checks when they respawn
		TDMManager.getTDMStats(player.getUniqueId()).addCounter(0);

		Gberry.log("LMS", "Refreshing kit for " + player.getName());
	}

	public boolean canHurt(Player attacker, Player defender) {
		return this.getTeam(attacker) != this.getTeam(defender);
	}

	public int getTeamSize() {
		return teamSize;
	}

	public Collection<TDMTeam> getTeams() {
		return this.teamsToPlayers.keySet();
	}

	public TDMTeam getTeam(Player player) {
		return this.getTeam(player.getUniqueId());
	}

	public TDMTeam getTeam(UUID uuid) {
		return this.playersToTeams.get(uuid);
	}

	public List<UUID> getPlayers(TDMTeam tdmTeam) {
		return this.teamsToPlayers.get(tdmTeam);
	}

	public Map<TDMTeam, List<UUID>> getTeamsToPlayers() {
		return this.teamsToPlayers;
	}

	public ItemStack getTDMItem() {
		// Update player count
		ItemStack item = this.tdmItem;
		ItemMeta itemMeta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Players: " + this.players.size() + "/" + this.totalPlayers);
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		return item;
	}

	/**
	 * Restarts TDM with the kit with the most votes
	 */
	public void restart() {
		// Get kitruleset with most votes
		KitRuleSet kitRuleSet = null;
		for (KitRuleSet kitRuleSet2 : this.votes.keySet()) {
			if (kitRuleSet == null) {
				kitRuleSet = kitRuleSet2;
				continue;
			}

			if (this.votes.get(kitRuleSet2) > this.votes.get(kitRuleSet)) {
				kitRuleSet = kitRuleSet2;
			}
		}

		this.kitRuleSet = kitRuleSet;

		this.votes.clear();
		this.playersVoted.clear();

		// Reset team scores
		Collection<TDMTeam> tdmTeams = this.getTeams();
		for (TDMTeam tdmTeam : tdmTeams) {
			tdmTeam.resetScore();
		}

		// Broadcast message that TDM is starting soon, try to get more people to join
		Gberry.broadcastMessage(TDMGame.PREFIX + ChatColor.AQUA + "Vote ended! Restarting in 15 seconds with kit " + kitRuleSet.getName() + ". Join now!");

		// Reset scoreboards and send messages
		for (Player player : TDMGame.this.players) {
			for (TDMTeam tdmTeam : tdmTeams) {
				Team team = ScoreboardUtil.getTeam(player.getScoreboard(), tdmTeam.getName() + "pts", "", tdmTeam.getColor() + "Points: " + ChatColor.WHITE);
				team.setSuffix("0");
			}

			TDMManager.resetCurrentStats(player.getUniqueId());

			// Reset killstreak/kills/deaths on scoreboard
			ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setSuffix("0");
			ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix("0");
			ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).setSuffix("0");
			ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "asst", ChatColor.GOLD + "Ass", "ists: " + ChatColor.WHITE).setSuffix("0");
			ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "cntr", ChatColor.GOLD + "Cou", "nter: " + ChatColor.WHITE).setSuffix("0%");
		}

		// GET READY FOR CANCER!!!

		// 3
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for (Player player : TDMGame.this.players) {
					player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);
				}
			}
		}, 240L);

		// 2
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for (Player player : TDMGame.this.players) {
					player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);
				}
			}
		}, 260L);

		// 1
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for (Player player : TDMGame.this.players) {
					player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 1f);
				}
			}
		}, 280L);

		// Start actual game in 15 seconds
		BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for (Player player : TDMGame.this.players) {
					player.sendMessage(TDMGame.PREFIX + ChatColor.AQUA + "The game has started!");

					TDMTeam tdmTeam = TDMGame.this.getTeam(player);

					PlayerHelper.healAndPrepPlayerForBattle(player);
					KitHelper.loadKit(player, TDMGame.this.kitRuleSet);

					// Give them resistance
					player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * TDMGame.RESISTANCE_TIME, 128));

					// Teleport to team spawn point
					player.teleport(TDMGame.this.arena.getSpawnLocation(tdmTeam));

					// Call follow event
					PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

				}

				TDMGame.this.setVoting(false);

				TDMGame.this.tdmTimeLimitTask = new TDMTimeLimitTask(TDMGame.this);
				TDMGame.this.tdmTimeLimitTask.runTaskTimer(PotPvP.getInstance(), 20L, 20L);
			}
		}, 300L);
	}

	/**
	 * @return - Spawn location
	 */
	public Location getSpawn() { // Unused
		return this.arena.getWarp1();
	}

	/**
	 * Get arena
	 */
	public Arena getArena() {
		return null;
	}

	/**
	 * Game is over
	 */
	public boolean isOver() {
		return false;
	}

	public KitRuleSet addVote(Player player, KitRuleSet kitRuleSet) {
		KitRuleSet oldKit = this.playersVoted.get(player.getUniqueId());

		this.playersVoted.put(player.getUniqueId(), kitRuleSet);
		this.votes.put(kitRuleSet, this.votes.get(kitRuleSet) + 1);

		return oldKit;
	}

	public boolean isCountingDown() {
		return this.isVoting() && this.votes.isEmpty();
	}

	public boolean isVoting() {
		return voting;
	}

	public void setVoting(boolean voting) {
		this.voting = voting;

		if (voting) {
			for (KitRuleSet kitRuleSet : TDMGame.kitRuleSets) {
				if (kitRuleSet != this.kitRuleSet) {
					this.votes.put(kitRuleSet, 0);
				}
			}

			// Create voting inventory
			TDMVoteInventory.createTDMVoteInventory(this);
		}
	}

	public TDMTimeLimitTask getTDMTimeLimitTask() {
		return tdmTimeLimitTask;
	}

	public static List<KitRuleSet> getKitRuleSets() {
		return kitRuleSets;
	}

	public static List<TDMGame> getTDMGames() {
		return TDMGame.tdmGames;
	}

	public static TDMGame getTDMGame(ItemStack itemStack) {
		return TDMGame.tdmItemGames.get(itemStack.getItemMeta().getDisplayName());
	}

	public class TDMTeam {

		private String name = "";
		private ChatColor color;

		private int size = 0;
		private int score = 0;

		public TDMTeam(ChatColor color) {
			String[] s = color.name().split("_");
			for (String s2 : s) {
				this.name += s2.substring(0, 1).toUpperCase() + s2.substring(1).toLowerCase();
			}

			this.color = color;

			TDMGame.this.teamsToPlayers.put(this, new ArrayList<UUID>());
		}

		public String getName() {
			return name;
		}

		public ChatColor getColor() {
			return color;
		}

		public void addScore() {
			this.score += 1; // 1 point per kill

			// Update side scoreboards
			for (Player player : TDMGame.this.players) {
				Team team = ScoreboardUtil.getTeam(player.getScoreboard(), this.name + "pts", "", this.color + "Points: " + ChatColor.WHITE);
				team.setSuffix(this.score + "");
			}
		}

		public void resetScore() {
			this.score = 0;
		}

		public void addPlayer(Player player) {
			this.size++;

			TDMGame.this.playersToTeams.put(player.getUniqueId(), this);
			TDMGame.this.teamsToPlayers.get(this).add(player.getUniqueId());
		}

		public void removePlayer(Player player) {
			this.size--;

			TDMGame.this.teamsToPlayers.get(this).remove(player.getUniqueId());
			TDMGame.this.playersToTeams.remove(player.getUniqueId());
		}

		public int getSize() {
			return size;
		}

		public int getScore() {
			return score;
		}

	}

}
