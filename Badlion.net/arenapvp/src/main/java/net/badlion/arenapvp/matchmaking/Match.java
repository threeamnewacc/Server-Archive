package net.badlion.arenapvp.matchmaking;

import io.kohi.kpearl.PearlPlugin;
import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.BuildUHCRuleSet;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.HorseRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Game;
import net.badlion.arenapvp.PotPvPPlayer;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.arenas.Arena;
import net.badlion.arenapvp.event.MatchEndEvent;
import net.badlion.arenapvp.event.MatchStartEvent;
import net.badlion.arenapvp.helper.KitSelectorHelper;
import net.badlion.arenapvp.helper.PlayerHelper;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.arenapvp.inventory.CustomKitCreationInventories;
import net.badlion.arenapvp.listener.MCPListener;
import net.badlion.arenapvp.listener.rulesets.HorseListener;
import net.badlion.arenapvp.manager.ArenaSettingsManager;
import net.badlion.arenapvp.manager.EnderPearlManager;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.arenapvp.manager.PotPvPPlayerManager;
import net.badlion.arenapvp.manager.RatingManager;
import net.badlion.arenapvp.manager.SidebarManager;
import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.common.libraries.IPCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gcheat.bukkitevents.GCheatGameEndEvent;
import net.badlion.gcheat.bukkitevents.GCheatGameStartEvent;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class Match implements Game {

	public static String CURRENT_SEASON = "1.7";


	protected List<Team> teams;

	private JSONObject team1Info;
	private JSONObject team2Info;

	private Team winningGroup;
	protected int matchLengthTime = 15; // In minutes
	private String timeLeftString = "15:00";

	private SidebarItem matchTime = new SidebarItem(9) {
		@Override
		public String getText() {
			return timeLeftString;
		}

		@Override
		public boolean remove() {
			return isOver();
		}
	};

	private UUID matchUuid;
	private int matchId = -1;

	private boolean inProgress;
	private String endResult;
	protected BukkitTask tieGameTask;
	private BukkitTask checkInBroadcastTask;

	protected Arena arena;
	private boolean isRanked;
	protected KitRuleSet kitRuleSet;
	protected Map<Player, Integer> customKitSelections;

	private BukkitTask startGameTask;
	protected BukkitTask waitingForPlayersTask;

	private RatingUtil.Rank team1Rank;
	private boolean team1Demo = false;
	private boolean team1Promo = false;

	private RatingUtil.Rank team2Rank;
	private boolean team2Demo = false;
	private boolean team2Promo = false;

	// Match detail stuff
	private boolean _1v1;
	private JSONObject extraData;
	private boolean started = false;
	private DateTime startTime;
	private DateTime endTime;
	private String serverVersion;
	private String bukkitVersion;

	private ArenaCommon.LadderType ladderType;

	// Team match stuff
	protected Map<String, Collection<PotionEffect>> groupPotionEffects = new HashMap<>();
	protected Map<String, ItemStack[]> groupArmor = new HashMap<>();
	protected Map<String, ItemStack[]> groupItems = new HashMap<>();
	protected Map<String, Double> groupHealth = new HashMap<>();
	protected Map<String, Integer> groupFood = new HashMap<>();
	protected Map<UUID, UUID> lastDamage = new HashMap<>();
	private Map<String, Object> ipMap = new HashMap<>();

	private Set<Integer> playerEntityIds = new HashSet<>();

	private Map<UUID, Horse> playerToHorse;

	// MineagePvP cooldown
	private Map<String, Long> goldenAppleCooldowns = new HashMap<>();

	protected boolean isOver = false;
	private boolean addSidebar = true;
	protected Map<UUID, Integer> killCounts = new HashMap<>();

	private Set<UUID> sentHealthPackets = new HashSet<>();

	private boolean friendlyFireEnabled = false;

	private int matchPreStartCountdownTime = 5;

	public Match(Arena arena, boolean isRanked, KitRuleSet kitRuleSet, UUID matchUuid) {
		this.arena = arena;
		this.inProgress = true;
		this.isRanked = isRanked;
		this.kitRuleSet = kitRuleSet;
		this.startTime = new DateTime(DateTimeZone.UTC);
		this.matchUuid = matchUuid;

		if (kitRuleSet instanceof CustomRuleSet) {
			this.matchPreStartCountdownTime = 10;
		}

		// Tie game after 20 seconds if the players do not join
		this.waitingForPlayersTask = new MatchTieTask(this).runTaskLater(ArenaPvP.getInstance(), 20 * 20);

		this.serverVersion = ArenaPvP.getInstance().getServer().getVersion();
		this.bukkitVersion = ArenaPvP.getInstance().getServer().getBukkitVersion();
		this.endTime = new DateTime(DateTimeZone.UTC);
	}

	public Match(Arena arena, boolean isRanked, KitRuleSet kitRuleSet, Map<Player, Integer> customKitSelections, UUID matchUuid) {
		this.arena = arena;
		this.inProgress = true;
		this.isRanked = isRanked;
		this.kitRuleSet = kitRuleSet;
		this.customKitSelections = customKitSelections;
		this.startTime = new DateTime(DateTimeZone.UTC);
		this.matchUuid = matchUuid;

		if (kitRuleSet instanceof CustomRuleSet) {
			this.matchPreStartCountdownTime = 10;
		}
		// Tie game after 20 seconds if the players do not join
		this.waitingForPlayersTask = new MatchTieTask(this).runTaskLater(ArenaPvP.getInstance(), 20 * 20);
	}

	public void finalCleanup() {
		if (this.waitingForPlayersTask != null) {
			this.waitingForPlayersTask.cancel();
		}
		if (this.checkInBroadcastTask != null) {
			this.checkInBroadcastTask.cancel();
		}
		if (this.tieGameTask != null) {
			this.tieGameTask.cancel();
		}
	}

	// Check the player into the match, once all are checked in then we can start the game (We need this to make sure all the players show up to the arena server)
	public void checkIn(Player player) {
		for (Team team : teams) {
			if (team.contains(player) && !team.isCheckedIn(player)) {
				team.checkIn(player);
			}
		}

		if (!allTeamsCheckedIn()) {
			if (checkInBroadcastTask == null) {
				this.checkInBroadcastTask = new BukkitRunnable() {
					@Override
					public void run() {
						if (allTeamsCheckedIn()) {
							return;
						}
						broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Waiting for all players to join, match will tie in 15 seconds if they do not login.");
					}
				}.runTaskLater(ArenaPvP.getInstance(), 20 * 3);
			}
			return;
		}

		//Prep and start game
		startGameDelay();
	}

	public boolean allTeamsCheckedIn() {
		for (Team team : teams) {
			if (!team.isTeamCheckedIn()) {
				return false;
			}
		}
		return true;
	}

	public void prepGame(Team team1, Team team2) {
		List<Team> teamsToAdd = new ArrayList<>();
		teamsToAdd.add(team1);
		teamsToAdd.add(team2);
		this.prepGame(teamsToAdd);
	}

	public void prepGame(List<Team> teamList) {
		this.teams = teamList;

		_1v1 = true;
		if (teams.size() > 2) {
			_1v1 = false;
		}
		for (Team team : teams) {
			if (team.membersIds().size() != 1) {
				_1v1 = false;
			}
		}
	}

	public void startGameDelay() {
		if (waitingForPlayersTask != null) {
			waitingForPlayersTask.cancel();
			waitingForPlayersTask = null;
		}
		if (startGameTask != null) {
			return;
		}
		try {
			if (teams.size() == 2) {
				MatchStartEvent matchStartEvent = new MatchStartEvent(teams.get(0), teams.get(1), getTeam1Rank(), getTeam2Rank());
				Bukkit.getServer().getPluginManager().callEvent(matchStartEvent);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		for (Team team : teams) {
			this.storePlayerIps(team.members());
			PlayerHelper.healAndPrepGroupForBattle(team);
			for (Player player : team.members()) {
				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
				potPvPPlayer.setSelectingKit(true);
				MatchManager.getMatchesAwaitingPlayers().remove(player.getUniqueId());

				if (this.kitRuleSet instanceof CustomRuleSet) {
					CustomKitCreationInventories.openCustomKitLoadInventory(player);
					ItemStack openCustomKitSelector = ItemStackUtil.createItem(Material.BOOK, 1, ChatColor.GREEN + "Select Custom Kit");
					player.getInventory().setItem(0, openCustomKitSelector);
				} else {
					KitSelectorHelper.giveSelectorItems(player, kitRuleSet);
				}

				if (_1v1) {
					if (this.getTeams().get(0).contains(player)) {
						if (this.team1Demo) {
							player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
						} else if (this.team1Promo) {
							for (int i = 0; i < 10; i++) {
								player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
							}
						}
					} else if (this.getTeams().get(1).contains(player)) {
						if (this.team2Demo) {
							player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
						} else if (this.team2Promo) {
							for (int i = 0; i < 10; i++) {
								player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
							}
						}
					}
				}
				//TODO: Once prplz fixes bug player.freeze();
			}
			MatchManager.getActiveMatches().put(team, this);
		}


		startGameTask = new BukkitRunnable() {

			int timeLeft = matchPreStartCountdownTime;

			@Override
			public void run() {
				if (timeLeft == 0) {
					startGame();
					this.cancel();
					return;
				}
				for (Team team : teams) {
					Location warp = getWarpForTeam(team);
					for (Player member : team.members()) {
						try {
							if (ArenaSettingsManager.getSettings(member).showsTitles()) {
								member.sendTitle(new ComponentBuilder("Starting in...").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).create());
								member.sendSubTitle(new ComponentBuilder(timeLeft + "").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true).create());
								member.setTitleTimes(0, 20, 0);
							}
						} catch (Exception ex) {
							// Just incase spigot is missing the api for whatever reason, we dont want to break the whole match
							Bukkit.getLogger().log(Level.WARNING, "Match Countdown could not send title. " + ex.getMessage());
						}
						if (member.getLocation().distance(warp) > 5) {
							member.teleport(new Location(warp.getWorld(), warp.getX(), warp.getY(), warp.getZ(), member.getLocation().getYaw(), member.getLocation().getPitch()));
						}
					}
				}
				broadcastMessage(ChatColor.GOLD + "Match starting in " + ChatColor.GREEN + timeLeft + ChatColor.GOLD + " seconds.");
				broadcastSound(EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1.0F, 1.0F);
				timeLeft--;
			}
		}.runTaskTimer(ArenaPvP.getInstance(), 10, 20);
	}

	public void showPlayers(Team team1, Team team2) {
		for (Player p1 : team1.members()) {
			for (Player p2 : team2.members()) {
				if (p1 == p2) {
					continue;
				}
				p1.showPlayer(p2);
				p2.showPlayer(p1);
				Gberry.log("VISIBILITY", "Match: " + p1.getName() + " shows " + p2.getName());
				Gberry.log("VISIBILITY", "Match: " + p2.getName() + " shows " + p1.getName());
			}
		}
	}

	public void startGame() {
		started = true;
		if (startGameTask != null) {
			startGameTask.cancel();
		}

		broadcastMessage(ChatColor.GREEN + "Starting Match, Kit: " + kitRuleSet.getName());

		if (isFriendlyFireEnabled()) {
			broadcastMessage(ChatColor.GOLD + "Friendly Fire is ENABLED!");
		}

		// Show all the teams to eachother
		for (Team team : teams) {
			for (Team team2 : teams) {
				this.showPlayers(team, team2);
			}
		}

		// Initialize kill counts

		playerToHorse = new HashMap<>();


		List<ChatColor> teamColors = Arrays.asList(ChatColor.GREEN, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.BLUE, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE);

		int index = 0;
		for (Team team : teams) {
			if (index >= teamColors.size()) {
				index = 0;
			}
			List<Object> teamPackets = new ArrayList<>();
			for (Team otherTeam : teams) {
				teamPackets.add(otherTeam.getCreatePacket(team == otherTeam ? ChatColor.GREEN : ChatColor.RED, false));
			}
			for (Player player : team.members()) {
				//TODO: Once prplz fixes bug player.unfreeze();
				ArenaPvP.getInstance().getCustomArmorPlayers().put(player.getEntityId(), teamColors.get(index));
				this.playerEntityIds.add(player.getEntityId());

				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());

				// If they have not picked a kit yet pick it for them when the match starts
				if (potPvPPlayer.isSelectingKit()) {
					KitType kitType = new KitType(player.getUniqueId().toString(), this.getKitRuleSet().getName());
					Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());

					if (kitTypeListMap != null) {
						List<Kit> kits = kitTypeListMap.get(kitType);
						if (kits != null) {
							// Load the first custom kit we can find for the player, if they don't have a custom kit load the default kit
							for (Kit kit : kits) {
								KitCommon.loadKit(player, this.getKitRuleSet(), kit.getId());
								break;
							}
						} else {
							if (this.getKitRuleSet() instanceof CustomRuleSet) {
								player.sendFormattedMessage("{0}You failed to pick a custom kit, and you have not saved any custom kits yet. Here is a cookie.", ChatColor.RED);
								player.getInventory().clear();
								player.getInventory().addItem(new ItemStack(Material.COOKIE));
							} else {
								KitCommon.loadDefaultKit(player, this.getKitRuleSet(), true);
							}
						}
					} else {
						if (this.getKitRuleSet() instanceof CustomRuleSet) {
							player.sendFormattedMessage("{0}You failed to pick a custom kit, and you have not saved any custom kits yet. Here is a cookie.", ChatColor.RED);
							player.getInventory().clear();
							player.getInventory().addItem(new ItemStack(Material.COOKIE));
						} else {
							KitCommon.loadDefaultKit(player, this.getKitRuleSet(), true);
						}
					}
					potPvPPlayer.setSelectingKit(false);
				}

				// This is only false if they were already online and have a sidebar already (Bo3 or Bo5)
				if (addSidebar) {
					SidebarManager.addSidebarItems(player, this);
				} else {
					SidebarManager.updatePlayerSidebar(player, this);
				}
				this.killCounts.put(player.getUniqueId(), 0);
				if (teamPackets != null) {
					for (Object packet : teamPackets) {
						Gberry.protocol.sendPacket(player, packet);
					}
				}
			}

			index++;

			for (final Player pl : team.members()) {

				if (this.kitRuleSet instanceof BuildUHCRuleSet) {
					this.addHealthObjective(pl);
				}

				// Reset enderpearl cooldown
				PearlPlugin.getInstance().getCooldownManager().removeCooldown(pl);
				if (this.kitRuleSet instanceof HorseRuleSet) {
					pl.setFallDistance(0);
					// Spawn the horse
					Horse horse = HorseListener.createHorse(pl, getWarpForTeam(team), this.arena);
					playerToHorse.put(pl.getUniqueId(), horse);
					pl.setFallDistance(0);

					new BukkitRunnable() {
						public void run() {
							if (Gberry.isPlayerOnline(pl)) {
								horse.setPassenger(pl);
							}
						}
					}.runTaskLater(ArenaPvP.getInstance(), 1L);
				} else {
					Gberry.safeTeleport(pl, getWarpForTeam(team));
				}
				// Play sound
				pl.playSound(pl.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "FIREWORK_BLAST", "ENTITY_FIREWORK_BLAST"), 1f, 1f);
				this.kitRuleSet.sendMessages(pl);
				// Call game start event for GCheat
				ArenaPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameStartEvent(pl));
			}
		}

		if (this.kitRuleSet instanceof HorseRuleSet) {
			new BukkitRunnable() {
				public void run() {
					for (Map.Entry<UUID, Horse> entry : playerToHorse.entrySet()) {
						Player member = Bukkit.getPlayer(entry.getKey());
						if (member != null && member.isOnline()) {
							member.teleport(entry.getValue().getLocation());
							entry.getValue().setPassenger(member);
						}
					}
				}
			}.runTaskLater(ArenaPvP.getInstance(), 1L);
		}

		this.sendStartingMessage();

		this.tieGameTask = new MatchTieTask(this).runTaskLater(ArenaPvP.getInstance(), 20 * 60 * this.matchLengthTime);

		this.arena.startArenaUse(this);

		// Prevent people from glitching out of arenas
		new BukkitRunnable() {
			public void run() {
				if (Match.this.isOver()) {
					this.cancel();
					return;
				}

				for (Team team : teams) {
					for (Player pl : team.members()) {
						if (pl.getLocation().getY() < 10) {
							Gberry.safeTeleport(pl, getWarpForTeam(team));
						}
					}
				}
			}
		}.runTaskTimer(ArenaPvP.getInstance(), 5L, 5L);
	}


	// Sends packets to this player to remove the colored helmets
	public void removeLeatherColoredHelmets(Player player) {
		for (Team team : this.teams) {
			for (Player member : team.members()) {
				try {
					// Clone packet because MC sends literally the same packet to literally everyone like literally
					Object newPacket = TinyProtocolReferences.packetEntityEquipmentClass.newInstance();

					TinyProtocolReferences.packetEntityEquipmentEntityID.set(newPacket, member.getEntityId());

					// 1.9 slot is offset by 1
					TinyProtocolReferences.setPacketEntityEquipmentSlot(newPacket, 4);

					TinyProtocolReferences.packetEntityEquipmentItem.set(newPacket, TinyProtocolReferences.getItemStackNMSCopy.invoke(null, member.getInventory().getHelmet()));

					Gberry.protocol.sendPacket(player, newPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Sends packets to this player to remove the colored helmets
	public void addLeatherColoredHelmets(Player player) {
		ItemStack item;
		for (Team team : this.teams) {
			for (Player member : team.members()) {
				if (!ArenaPvP.getInstance().getCustomArmorPlayers().containsKey(member.getEntityId())) {
					continue;
				}
				item = new ItemStack(Material.LEATHER_HELMET);
				// Color item
				LeatherArmorMeta itemMeta = ((LeatherArmorMeta) item.getItemMeta());
				itemMeta.setColor(Gberry.getColorFromChatColor(ArenaPvP.getInstance().getCustomArmorPlayers().get(member.getEntityId())));
				item.setItemMeta(itemMeta);
				try {
					// Clone packet because MC sends literally the same packet to literally everyone like literally
					Object newPacket = TinyProtocolReferences.packetEntityEquipmentClass.newInstance();

					TinyProtocolReferences.packetEntityEquipmentEntityID.set(newPacket, member.getEntityId());

					// 1.9 slot is offset by 1
					TinyProtocolReferences.setPacketEntityEquipmentSlot(newPacket, 4);

					TinyProtocolReferences.packetEntityEquipmentItem.set(newPacket, TinyProtocolReferences.getItemStackNMSCopy.invoke(null, item));

					Gberry.protocol.sendPacket(player, newPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void addHealthObjective(Player player) {
		// Double check this is only builduhc
		if (!(this.kitRuleSet instanceof BuildUHCRuleSet)) {
			return;
		}

		String hearts = ChatColor.DARK_RED + "\u2764";

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
			this.sentHealthPackets.add(player.getUniqueId());

			for (Team team : teams) {
				for (Player member : team.members()) {
					if (member == player) {
						continue;
					}

					// Send other player's health to player
					Object scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
					TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, member.getDisguisedName());
					TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "showhealth");
					TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, (int) Math.ceil(member.getHealth()));
					TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);

					Gberry.protocol.sendPacket(player, scorePacket);

					// Send player's health to other player
					scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
					TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, player.getDisguisedName());
					TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "showhealth");
					TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, (int) Math.ceil(player.getHealth()));
					TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);

					Gberry.protocol.sendPacket(member, scorePacket);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void removeHealthObjective(Player player) {
		try {
			Object removeObjective = TinyProtocolReferences.scoreboardObjectivePacket.newInstance();
			TinyProtocolReferences.objectiveScoreboardPacketName.set(removeObjective, "showhealth");
			TinyProtocolReferences.objectiveScoreboardPacketTitle.set(removeObjective, "");
			TinyProtocolReferences.objectiveScoreboardPacketAction.set(removeObjective, 1);

			Gberry.protocol.sendPacket(player, removeObjective);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateHealthObjective(Player player, double health) {
		// Double check this is only builduhc
		if (!(this.kitRuleSet instanceof BuildUHCRuleSet)) {
			return;
		}

		// Send update packets to all the other players
		try {
			for (Team team : teams) {
				for (Player member : team.members()) {
					if (member == player) {
						continue;
					}
					if (!this.sentHealthPackets.contains(member.getUniqueId())) {
						continue;
					}
					Object scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
					TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, player.getDisguisedName());
					TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "showhealth");
					TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, (int) Math.ceil(health));
					TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);
					Gberry.protocol.sendPacket(member, scorePacket);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateScoreboards(Player player) {
		Team team = getPlayersTeam(player);
		if (team == null) {
			return;
		}
		List<Object> teamPackets = new ArrayList<>();
		for (Team otherTeam : teams) {
			teamPackets.add(otherTeam.getCreatePacket(team == otherTeam ? ChatColor.GREEN : ChatColor.RED, false));
		}

		if (teamPackets != null) {
			for (Object packet : teamPackets) {
				Gberry.protocol.sendPacket(player, packet);
			}
		}
	}


	public void updateSpectatorScoreboards(Player player) {
		// Only chat colors that will look decent for names
		List<ChatColor> teamColors = Arrays.asList(ChatColor.GREEN, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.BLUE, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE);
		List<Object> teamPackets = new ArrayList<>();
		int index = 0;
		for (Team otherTeam : teams) {
			if (index >= teamColors.size()) {
				index = 0;
			}
			teamPackets.add(otherTeam.getCreatePacket(teamColors.get(index), false));
			index++;
		}

		if (teamPackets != null) {
			for (Object packet : teamPackets) {
				Gberry.protocol.sendPacket(player, packet);
			}
		}
	}

	public void removeSpectatorScoreboards(Player player) {
		List<Object> teamPackets = new ArrayList<>();
		try {
			for (Team otherTeam : teams) {
				if (!otherTeam.members().isEmpty()) {
					teamPackets.add(otherTeam.getRemovePacket());
				}
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		if (teamPackets != null) {
			for (Object packet : teamPackets) {
				Gberry.protocol.sendPacket(player, packet);
			}
		}
	}

	public void handleLogout(Player player) {
		if (this.sentHealthPackets.contains(player.getUniqueId())) {
			this.removeHealthObjective(player);
			this.sentHealthPackets.remove(player.getUniqueId());
		}
	}

	public Location getWarpForPlayer(Player player) {
		if (teams.isEmpty()) {
			return arena.getWarp1Origin();
		}
		for (Team team : teams) {
			if (team.contains(player)) {
				return getWarpForTeam(team);
			}
		}
		return arena.getWarp1Origin();
	}

	public Location getWarpForTeam(Team team) {
		int index = teams.indexOf(team);
		if (index % 2 == 0) {
			return arena.getWarp1Origin();
		}
		return arena.getWarp2Origin();
	}

	public void broadcastMessage(String message) {
		for (Team team : teams) {
			team.sendMessage(message);
		}
	}

	public void broadcastSound(Sound sound, float pitch, float volume) {
		for (Team team : teams) {
			for (Player player : team.members()) {
				player.playSound(player.getLocation(), sound, pitch, volume);
			}
		}
	}

	public void sendStartingMessage() {
		// Send the messages
		if (ladderType.equals(ArenaCommon.LadderType.PARTY_FFA)) {
			for (Team team1 : teams) {
				StringBuilder sb = new StringBuilder(ChatColor.BLUE + "Now in match against ");
				for (Team team2 : teams) {
					if (team1.equals(team2)) {
						continue;
					}
					if(team2.members().size() > 0) {
						sb.append(team2.members().get(0).getDisguisedName());
						sb.append(", ");
					}
				}
				sb.append(" with kit " + this.kitRuleSet.getName());
				team1.sendMessage(sb.toString());
			}
		} else {
			this.teams.get(0).sendMessage(ChatColor.BLUE + "Now in match against " + teams.get(1).toString() + " with kit " + this.kitRuleSet.getName());
			this.teams.get(1).sendMessage(ChatColor.BLUE + "Now in match against " + teams.get(0).toString() + " with kit " + this.kitRuleSet.getName());
		}
	}

	/**
	 * Get a KitRuleSet
	 */
	public KitRuleSet getKitRuleSet() {
		return this.kitRuleSet;
	}

	public Map<String, ItemStack[]> getGroupArmor() {
		return groupArmor;
	}

	public Map<String, ItemStack[]> getGroupItems() {
		return groupItems;
	}

	/**
	 * Get unmodifiable list of players involved
	 */
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (Team team : teams) {
			players.addAll(team.members());
		}

		return Collections.unmodifiableList(players);
	}

	/**
	 * Check if a player is contained in this game mode
	 */
	public boolean contains(Player player) {
		for (Team team : teams) {
			if (team.contains(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Some game modes have god apple cooldowns (this is nasty, idgaf)
	 */
	public Map<String, Long> getGodAppleCooldowns() {
		return this.goldenAppleCooldowns;
	}

	public void declareWinner(Team group) {
		this.winningGroup = group;
	}

	public Team getWinner() {
		return this.winningGroup;
	}


	public Team getOtherGroup(Team group) {
		if (ladderType.equals(ArenaCommon.LadderType.PARTY_FFA)) {
			return null;
		} else {
			for (Team team : teams) {
				if (!team.equals(group)) {
					return team;
				}
			}
		}
		return null;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void handleWinnerChat() {
		String winnerMsg = null;
		String loserMsg = null;

		if (this.endResult.equals("kill")) {
			// Only send if unranked, ranked messages takes care of the other part
			if (!this.isRanked) {
				if (_1v1) {
					loserMsg = ChatColor.GREEN + "Winner: " + ChatColor.BLUE + this.getWinner().toString();
					winnerMsg = ChatColor.GREEN + "Winner: " + ChatColor.BLUE + this.getWinner().toString();
				} else {
					loserMsg = ChatColor.GREEN + "Winner: " + ChatColor.BLUE + this.getWinner().toString() + ChatColor.GREEN + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, this.getWinner().members().get(0).getHealth());
					winnerMsg = ChatColor.GREEN + "Winner: " + ChatColor.BLUE + this.getWinner().toString() + ChatColor.GREEN + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, this.getWinner().members().get(0).getHealth());
				}
			} else {
				return;
			}
		} else if (this.endResult.equals("quit")) {
			winnerMsg = ChatColor.GREEN + "Opponent quit. You win by default.";
		} else if (this.endResult.equals("spawn")) {
			loserMsg = ChatColor.RED + "Quit during match. You lose.";
			winnerMsg = ChatColor.GREEN + "Opponent TP'd to spawn. You win by default.";
		} else if (this.endResult.equals("time")) {
			winnerMsg = loserMsg = ChatColor.YELLOW + "Time limit reached. Tie match.";
		} else {
			// Something went wrong
			throw new RuntimeException("Invalid win reason for match");
		}

		Team winningGroup = this.getWinner();

		// Tie
		if (winningGroup == null) {
			broadcastMessage(ChatColor.BLUE + "Tie Game!");
			return;
		}
		String spectatorBroadcast = ChatColor.GREEN + "Winner: " + ChatColor.BLUE + winningGroup.toString();
		TeamStateMachine.spectatorState.broadcastToSpectators(this, spectatorBroadcast);

		winningGroup.sendMessage(winnerMsg);
		for (Team team : teams) {
			if (team.equals(winningGroup)) {
				continue;
			}
			team.sendMessage(loserMsg);
		}
	}

	public void checkWin() {
		Team _winner = null;
		for (Team team : teams) {
			if (team.hasActivePlayers()) {
				if (_winner != null) {
					return;
				}
				_winner = team;
			}
		}
		if (_winner == null) {
			// idk
			return;
		}
		final Team winner = _winner;
		storePlayerStats(winner.getActivePlayers());
		this.declareWinner(winner);

		// if 1v1, submit stats to api
		if (_1v1) {
			// find loser - the only team in here who isn't the winner
			if (winner.members().isEmpty()) {
				this.handle1v1End(null, "kill");
			} else {
				Player winnerPlayer = winner.members().get(0);
				this.handle1v1End(winnerPlayer, "kill");
			}
			return;
		}

		this.handleCommonEnd("kill");

	}


	public boolean checkTie(Player player) {
		for (Team team : teams) {
			if (team.contains(player)) {
				if (team.hasActivePlayers() && team.getActivePlayers().size() > 1) {
					// The player that is logging out has a team with more than 1 active player online
					return false;
				}
				continue;
			}
			if (team.hasActivePlayers() && !team.getActivePlayers().isEmpty()) {
				return false;
			}
		}
		// No one is online in the teams and the last player is logging out now
		this.declareWinner(null);
		this.handleTie();
		return true;
	}

	public void end() {
		List<Player> allPlayers = new ArrayList<>();
		for (Team team : teams) {
			allPlayers.addAll(team.members());
		}
		if (this.kitRuleSet instanceof BuildUHCRuleSet) {
			for (Player member : allPlayers) {
				if (this.sentHealthPackets.contains(member.getUniqueId())) {
					this.sentHealthPackets.remove(member.getUniqueId());
					this.removeHealthObjective(member);
				}
			}
		}

		MatchEndEvent matchEndEvent = new MatchEndEvent(allPlayers);
		Bukkit.getServer().getPluginManager().callEvent(matchEndEvent);

		for (Integer entityId : this.playerEntityIds) {
			ArenaPvP.getInstance().getCustomArmorPlayers().remove(entityId);
		}

		this.inProgress = false;

		for (Team team : teams) {
			for (Player member : team.members()) {
				SidebarAPI.removeSidebarItem(member, SidebarManager.ruleSetSidebars.get(this.getKitRuleSet()));
			}

			for (UUID memberId : team.membersIds()) {
				if (MatchManager.getCombatLoggedPlayers().containsKey(memberId)) {
					if (MatchManager.getCombatLoggedPlayers().get(memberId).equals(team)) {
						// Remove the logger
						try {
							CombatTagPlugin.getInstance().getLogger(memberId).remove(LoggerNPC.REMOVE_REASON.REJOIN);
						} catch (NullPointerException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}

		if (playerToHorse != null) {
			for (Team team : teams) {
				for (UUID playerId : team.membersIds()) {
					if (playerToHorse.containsKey(playerId)) {
						if (playerToHorse.get(playerId) != null && !playerToHorse.get(playerId).isDead()) {
							playerToHorse.get(playerId).remove();
							// Kill horses since players might logout with them?
						}
					}
				}
				EnderPearlManager.remove(team.members());
			}
		}

		// Cancel tieGameTask if other reason for end of match
		try {
			if (this.tieGameTask != null) {
				this.tieGameTask.cancel();
				if (Bukkit.getScheduler().isQueued(this.tieGameTask.getTaskId())) {
					Bukkit.getScheduler().cancelTask(this.tieGameTask.getTaskId());
				}
			}
		} catch (NullPointerException e) {
			Bukkit.getLogger().info("TieGame Task Null because the match tied");
		}

		// Fancy smancy messages
		this.handleWinnerChat();

		TeamStateMachine.spectatorState.cleanupMatchSpectators(this);

		// Ranked stuff
		if (this.isRanked && this.getWinner() != null && !ArenaPvP.restarting) {
			BukkitUtil.runTaskAsync(new Runnable() {
				public void run() {
					Connection connection = null;
					try {
						connection = Gberry.getConnection();
						for (Team team : teams) {
							for (UUID playerId : team.membersIds()) {
								// Add 1 match played for this region to each player
								RatingManager.addMatchPlayedForRegion(playerId, connection);
							}
						}
						Match.this.storeMatchDetails(connection);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(connection);
					}
				}
			});
		} else {
			this.mcpEndMatch(this.teams, winningGroup, -1);
		}
	}

	/**
	 * Handle a death
	 */
	public void handleDeath(Player player) {
		EnderPearlManager.remove(player);
		UUID attackerUUID = this.getLastDamage(player.getUniqueId());


		// They might have killed themselves
		if (attackerUUID == null) {
			try {
				if (teams.get(0).contains(player)) {
					attackerUUID = teams.get(1).members().get(0).getUniqueId();
				} else {
					attackerUUID = teams.get(0).members().get(0).getUniqueId();
				}
			} catch (Exception ex) {
				// One of the teams might of all logged out?
			}
		}


		Team group = ArenaPvP.getInstance().getPlayerTeam(player);
		group.handlePlayerDeath(player.getUniqueId());

        /*
        try {
            // Give credit for death
            if (attackerUUID != null) {
                this.killCounts.put(attackerUUID, this.killCounts.get(attackerUUID) + 1);
            }
        } catch (NullPointerException e) {
            Bukkit.getLogger().info("UUID: " + attackerUUID);
            if (killer != null) {
                Bukkit.getLogger().info("Player: " + killer.getName());
            }
            e.printStackTrace();
        }*/

		this.handleCommon(player);
		Player killer = null;

		if (attackerUUID != null) {
			killer = ArenaPvP.getInstance().getServer().getPlayer(attackerUUID);
		}


		if (this._1v1) {
			try {
				// Play some sounds to make them feel good if promo game
				if (this.getTeams().get(0).contains(player)) {
					if (this.team1Demo) {
						player.playSound(player.getLocation(), Sound.VILLAGER_DEATH, 1.0F, 1.0F);
					}

					if (this.team2Promo) {
						Player other = this.getTeams().get(1).members().get(0);
						if (other != null) {
							other.playSound(other.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
						}
					}

				} else if (this.getTeams().get(1).contains(player)) {
					if (this.team2Demo) {
						player.playSound(player.getLocation(), Sound.VILLAGER_DEATH, 1.0F, 1.0F);
					}

					if (this.team1Promo) {
						Player other = this.getTeams().get(0).members().get(0);
						if (other != null) {
							other.playSound(other.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Send death message
		if (killer != null) {
			Gberry.log("MATCH2", player.getDisguisedName() + " killed by " + killer.getDisguisedName());
			this.sendRedGreenMessage(group, player.getDisguisedName() + " killed by " + killer.getDisguisedName(), killer.getHealth());
			TeamStateMachine.spectatorState.broadcastToSpectators(this, ChatColor.RED + player.getDisguisedName() + " killed by " + killer.getDisguisedName() + PlayerHelper.getHeartsLeftString(ChatColor.RED, killer.getHealth()));
		} else {
			Gberry.log("MATCH2", player.getDisguisedName() + " died");
			this.broadcastMessage(ChatColor.RED + player.getDisguisedName() + " died");
			TeamStateMachine.spectatorState.broadcastToSpectators(this, ChatColor.RED + player.getDisguisedName() + " died");
		}


		// getHealth() returns health without the damage, so just use 0D
		this.groupHealth.put(player.getUniqueId().toString(), 0D);

		checkWin();
	}

	// Helper method
	protected void sendRedGreenMessage(Team redGroup, Team greenGroup, String msg, double health) {
		String redMsg = ChatColor.RED + msg + PlayerHelper.getHeartsLeftString(ChatColor.RED, health);
		for (Player pl : redGroup.members()) {
			pl.sendMessage(redMsg);
		}

		String greenMsg = ChatColor.GREEN + msg + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, health);
		for (Player pl : greenGroup.members()) {
			pl.sendMessage(greenMsg);
		}
	}

	// Helper method
	protected void sendRedGreenMessage(Team redGroup, String msg, double health) {
		String greenMsg = ChatColor.GREEN + msg + PlayerHelper.getHeartsLeftString(ChatColor.GREEN, health);
		for (Team team : teams) {
			if (team.equals(redGroup)) {
				continue;
			}
			team.sendMessage(greenMsg);
		}

		String redMsg = ChatColor.RED + msg + PlayerHelper.getHeartsLeftString(ChatColor.RED, health);
		for (Player pl : redGroup.members()) {
			pl.sendMessage(redMsg);
		}
	}

	public void handleTie() {

		// Create match data for GCheat
		Map<String, Object> data = new HashMap<>();
		data.put("match_id", this.matchId);
		data.put("season", Match.CURRENT_SEASON);

		// Cleanup their potpvp player objects just incase
		for (Team team : teams) {
			MatchManager.getActiveMatches().remove(team);

			List<Object> teamPackets = new ArrayList<>();
			if (this.isStarted()) {
				for (Team otherTeam : teams) {
					teamPackets.add(otherTeam.getRemovePacket());
				}
			}

			for (UUID memberId : team.membersIds()) {
				try {
					if (MatchManager.getMatchesAwaitingPlayers().get(memberId) != null) {
						if (MatchManager.getMatchesAwaitingPlayers().get(memberId) == this) {
							MatchManager.getMatchesAwaitingPlayers().remove(memberId);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				PotPvPPlayerManager.players.remove(memberId);
			}

			for (Player player : team.members()) {
				//Remove match from players match state
				TeamStateMachine.matchState.removePlayerMatch(player);

				// Give all players still in match state spectator incase mcp doesn't send them back to the lobby
				if (TeamStateMachine.matchState.contains(player)) {
					SpectatorHelper.activateSpectateGameMode(player);
				}

				// Call game end event for GCheat
				ArenaPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));
				if (teamPackets != null) {
					for (Object object : teamPackets) {
						Gberry.protocol.sendPacket(player, object);
					}
				}
			}
		}


		this.serverVersion = ArenaPvP.getInstance().getServer().getVersion();
		this.bukkitVersion = ArenaPvP.getInstance().getServer().getBukkitVersion();
		this.endTime = new DateTime(DateTimeZone.UTC);

		this.setEndResult("time");
		this.end();
	}


	/**
	 * Called by methods above first
	 */
	public void handleCommon(Player player) {
		// Stats
		this.storePlayerStats(player);

	}

	public void storePlayerStats(Collection<Player> players) {
		for (Player pl : players) {
			this.storePlayerStats(pl);
		}
	}

	public void storePlayerStats(Player... players) {
		for (Player pl : players) {
			this.groupPotionEffects.put(pl.getUniqueId().toString(), pl.getActivePotionEffects());
			this.groupArmor.put(pl.getUniqueId().toString(), pl.getInventory().getArmorContents());
			this.groupItems.put(pl.getUniqueId().toString(), pl.getInventory().getContents());
			this.groupHealth.put(pl.getUniqueId().toString(), pl.getHealth());
			this.groupFood.put(pl.getUniqueId().toString(), pl.getFoodLevel());
		}
	}

	public void storeLoggerStats(UUID playerId, ItemStack[] armor, ItemStack[] inventory) {
		this.groupPotionEffects.put(playerId.toString(), new ArrayList<>());
		this.groupArmor.put(playerId.toString(), armor);
		this.groupItems.put(playerId.toString(), inventory);
		this.groupHealth.put(playerId.toString(), 0.0);
		this.groupFood.put(playerId.toString(), 20);
	}

	public void handle1v1End(Player killer, String reason) {
		Bukkit.getLogger().log(Level.INFO, "1v1 has ended.");

		if (killer != null) {
			this.storePlayerStats(killer);
		}
		this.handleCommonEnd(reason);
	}

	/**
	 * Called by stuff above
	 */
	public void handleCommonEnd(String reason) {
		// Create match data for GCheat
		Map<String, Object> data = new HashMap<>();
		data.put("match_id", this.matchId);
		data.put("season", Match.CURRENT_SEASON);

		// Cleanup their potpvp player objects just incase
		for (Team team : teams) {
			MatchManager.getActiveMatches().remove(team);
			// Cleanup member ids
			for (UUID memberId : team.membersIds()) {
				if (CombatTagPlugin.getInstance().getLogger(memberId) != null) {
					CombatTagPlugin.getInstance().getLogger(memberId).remove(LoggerNPC.REMOVE_REASON.REJOIN);
				}

				MatchManager.getMatchesAwaitingPlayers().remove(memberId);
			}

			List<Object> teamPackets = new ArrayList<>();
			if (this.isStarted()) {
				try {
					for (Team otherTeam : teams) {
						if (!otherTeam.members().isEmpty()) {
							teamPackets.add(otherTeam.getRemovePacket());
						}
					}
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}
			}

			for (UUID memberId : team.membersIds()) {
				PotPvPPlayerManager.players.remove(memberId);
			}

			if (!team.members().isEmpty()) {
				for (Player player : team.members()) {
					//Remove match from players match state
					TeamStateMachine.matchState.removePlayerMatch(player);

					// Give all players still in match state spectator incase mcp doesn't send them back to the lobby
					if (TeamStateMachine.matchState.contains(player)) {
						SpectatorHelper.activateSpectateGameMode(player);
					}

					// Call game end event for GCheat
					ArenaPvP.getInstance().getServer().getPluginManager().callEvent(new GCheatGameEndEvent(player, data));
					if (teamPackets != null && !teamPackets.isEmpty()) {
						for (Object object : teamPackets) {
							Gberry.protocol.sendPacket(player, object);
						}
					}
				}
			}
		}


		this.serverVersion = ArenaPvP.getInstance().getServer().getVersion();
		this.bukkitVersion = ArenaPvP.getInstance().getServer().getBukkitVersion();
		this.endTime = new DateTime(DateTimeZone.UTC);

		this.setEndResult(reason);
		this.end();

		// Remove any cached player inventories
		//PartyPlayerInventoriesInventory.cleanUpCachedInventories(this.team1, this.team2);

		// THIS NEEDS TO BE BEFORE WE TOGGLE THE ARENA
	}

	/**
	 * Winner is null to tie the match
	 */
	public void mcpEndMatch(List<Team> teams, Team winner, int matchId) {
		this.isOver = true;

		// Stop any leftover tasks that may still be running
		this.finalCleanup();

		new BukkitRunnable() {
			@Override
			public void run() {
				// Remove team 1 from matchmaking
				List<String> playerIdStrings = new ArrayList<>();
				for (Team team : teams) {
					playerIdStrings.addAll(team.memberIdsToString());
				}

				JSONObject data = new JSONObject();
				data.put("uuids", playerIdStrings);
				data.put("match_id", matchUuid.toString());
				data.put("server_type", "arena");
				data.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				data.put("ladder", kitRuleSet.getName());
				data.put("type", ladderType.getTag());
				MCPManager.contactMCP(MCPManager.MCP_MESSAGE.MATCHMAKING_DEFAULT_REMOVE_PLAYERS, data);

				// Send them back to the lobby
				JSONObject finishedMatch = new JSONObject();
				finishedMatch.put("team_1", team1Info);
				finishedMatch.put("team_2", team2Info);
				if (matchId != -1) {
					finishedMatch.put("match_id", matchId);
				}
				finishedMatch.put("match_uuid", matchUuid.toString());
				if (ladderType.equals(ArenaCommon.LadderType.PARTY_FFA)) {
					finishedMatch.put("winner", "team_1");
				} else {
					if (winner != null) {
						finishedMatch.put("winner", winner.getName());
					} else {
						finishedMatch.put("winner", "tie");
					}
				}
				finishedMatch.put("server_region", Gberry.serverRegion.toString().toLowerCase());
				finishedMatch.put("type", ladderType.getTag());
				finishedMatch.put("ladder", kitRuleSet.getName());
				finishedMatch.put("arena_type", String.valueOf(kitRuleSet.getArenaType().ordinal()));
				finishedMatch.put("extra_data", extraData);
				List<JSONObject> currentFinishedMatches = MCPListener.data.get("finished_matches");
				if (currentFinishedMatches == null) {
					currentFinishedMatches = new ArrayList<>();
				}
				currentFinishedMatches.add(finishedMatch);
				MCPListener.data.put("finished_matches", currentFinishedMatches);
				ArenaPvP.getInstance().getLogger().log(Level.INFO, "[arena-match-finish request] " + finishedMatch.toString());

				// Open up the arena
				new BukkitRunnable() {
					@Override
					public void run() {
						arena.cleanArena();
					}
				}.runTask(ArenaPvP.getInstance());
			}
		}.runTaskAsynchronously(ArenaPvP.getInstance());
	}

	public void storePlayerIps(List<Player> players) {
		for (Player player : players) {
			this.ipMap.put(player.getUniqueId().toString(), IPCommon.toLongIP(player.getAddress().getAddress().getAddress()));
		}
	}

	@SuppressWarnings("unchecked")
	public void storeMatchDetails(Connection connection) {
		// Don't store party events
		if (ladderType.equals(ArenaCommon.LadderType.PARTY_FFA)
				|| ladderType.equals(ArenaCommon.LadderType.PARTY_RED_ROVER_BATTLE)
				|| ladderType.equals(ArenaCommon.LadderType.PARTY_RED_ROVER_DUEL)
				|| ladderType.equals(ArenaCommon.LadderType.PARTY_TEAM)) {
			return;
		}
		Gberry.log("MATCH", "Storing match details");
		String query = null;
		Team team1 = teams.get(0);
		Team team2 = teams.get(1);
		if (team1.isParty()) {
			if (team1.membersIds().size() == 2) {
				query = "INSERT INTO kit_pvp_matches_2_s14" + ArenaPvP.getInstance().getDBExtra() + " (season, region, winner1, winner2, loser1, loser2, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
			} else if (team1.membersIds().size() == 3) {
				query = "INSERT INTO kit_pvp_matches_3_s14" + ArenaPvP.getInstance().getDBExtra() + " (season, region, winner1, winner2, winner3, loser1, loser2, loser3, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			} else {
				query = "INSERT INTO kit_pvp_matches_5_s14" + ArenaPvP.getInstance().getDBExtra() + " (season, region, winner1, winner2, winner3, winner4, winner5, loser1, loser2, loser3, loser4, loser5, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			}
		} else {
			query = "INSERT INTO kit_pvp_matches_s14" + ArenaPvP.getInstance().getDBExtra() + " (season, region, winner, loser, ladder_id, data) VALUES(?, ?, ?, ?, ?, ?)";
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONObject json = new JSONObject();

		ArrayList<UUID> tmpList = new ArrayList<>();
		tmpList.addAll(team1.membersIds());
		tmpList.addAll(team2.membersIds());

		// Nice JSON format
		Gberry.addPlayerPotionEffects(json, tmpList, "totalPotionEffects", this.groupPotionEffects);
		Gberry.addPlayerItems(json, tmpList, "totalArmor", this.groupArmor);
		Gberry.addPlayerItems(json, tmpList, "totalInventory", this.groupItems);

		// Get the rest of the crap
		Map<String, Object> foodMap = new HashMap<>();
		for (UUID uuid : tmpList) {
			foodMap.put(uuid.toString(), this.groupFood.get(uuid.toString()));
		}
		json.put("foodMap", foodMap);

		Map<String, Object> healthMap = new HashMap<>();
		for (UUID uuid : tmpList) {
			healthMap.put(uuid.toString(), this.groupHealth.get(uuid.toString()));
		}
		json.put("healthMap", healthMap);

		json.put("startTime", this.getStartTime().toString());
		json.put("endTime", this.getEndTime().toString());
		json.put("matchVersion", 2); // Cuz i forgot food/health maps
		json.put("bukkitVersion", this.getBukkitVersion());
		json.put("serverVersion", this.getServerVersion());

		json.put("arena", this.getArena().getSchematicName());

		json.put("ipMap", this.ipMap);

		json.put("endResult", this.getEndResult());

		try {
			byte[] bytes = null;
			try {
				bytes = CompressionUtil.compress(json.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, CURRENT_SEASON);
			ps.setString(2, Gberry.serverRegion.toString().toLowerCase());

			List<UUID> winningPlayers = this.getWinner().sortedPlayers();
			List<UUID> losingPlayers = (team1.equals(getWinner()) ? team2 : team1).sortedPlayers();

			int index = 3;

			for (UUID pl : winningPlayers) {
				ps.setString(index++, pl.toString());
			}

			for (UUID pl : losingPlayers) {
				ps.setString(index++, pl.toString());
			}

			ps.setInt(index++, this.kitRuleSet.getId());
			ps.setBytes(index, bytes);

			Gberry.executeUpdate(connection, ps);
			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				// Save match link for GCheat logs
				this.matchId = rs.getInt(1);

				if (team1.isParty()) {
					if (this.ladderType == ArenaCommon.LadderType.RANKED_5V5_CLAN) {
						ArenaPvP.getInstance().sendMessageToAllTeams(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/5v5/" + rs.getInt(1), team1, team2);
					} else if (this.ladderType == ArenaCommon.LadderType.RANKED_3V3) {
						ArenaPvP.getInstance().sendMessageToAllTeams(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/3v3/" + rs.getInt(1), team1, team2);
					} else {
						ArenaPvP.getInstance().sendMessageToAllTeams(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/2v2/" + rs.getInt(1), team1, team2);
					}
				} else {
					ComponentBuilder builder = new ComponentBuilder("Inventories (click to view): ").color(net.md_5.bungee.api.ChatColor.GOLD);
					HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view inventory").color(net.md_5.bungee.api.ChatColor.GREEN).create());
					builder.append(teams.get(0).getMemberName() + ", ").event(hover).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/openoppinv " + rs.getInt(1) + " " + teams.get(0).getMemberId())).color(net.md_5.bungee.api.ChatColor.YELLOW);
					builder.append(teams.get(1).getMemberName() + ".").event(hover).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/openoppinv " + rs.getInt(1) + " " + teams.get(1).getMemberId())).color(net.md_5.bungee.api.ChatColor.YELLOW);
					BaseComponent[] invMessage = builder.create();
					if (!teams.get(0).members().isEmpty()) {
						teams.get(0).members().get(0).spigot().sendMessage(invMessage);
					}
					if (!teams.get(1).members().isEmpty()) {
						teams.get(1).members().get(0).spigot().sendMessage(invMessage);
					}
					ArenaPvP.getInstance().sendMessageToAllTeams(ChatColor.BLUE + "Match Link: http://www.badlion.net/match/arenapvp/1v1/" + rs.getInt(1), team1, team2);
				}
				this.mcpEndMatch(this.teams, winningGroup, rs.getInt(1));
			}
		} catch (SQLException e) {
			//Bukkit.getLogger().info(this.ladderType + ",||| " + this.team1.members().size() + ", " + this.team2.members().size()
			//		+ ",||| " + this.copyOfTeam1.members().size() + ", " + this.copyOfTeam2.members().size());
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps);
		}

	}

	/**
	 * Game is over
	 */
	public boolean isOver() {
		return this.isOver;
	}

	public boolean isStarted() {
		return this.started;
	}

	public boolean is1v1() {
		return _1v1;
	}

	public ArenaCommon.LadderType getLadderType() {
		return ladderType;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeam1Info(JSONObject team1Info) {
		this.team1Info = team1Info;
	}

	public void setTeam2Info(JSONObject team2Info) {
		this.team2Info = team2Info;
	}

	public void setLadderType(ArenaCommon.LadderType ladderType) {
		this.ladderType = ladderType;
	}

	public void setExtraData(JSONObject extraData) {
		this.extraData = extraData;
	}

	public UUID getLastDamage(UUID defender) {
		return this.lastDamage.get(defender);
	}

	public void putLastDamage(UUID attacker, UUID defender, double damage, double finalDamage) {
		this.lastDamage.put(defender, attacker);
	}

	public String getEndResult() {
		return endResult;
	}

	public void setEndResult(String endResult) {
		this.endResult = endResult;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		if (this.arena != null) {
			try {
				throw new Exception("Arena already set " + this.arena + " " + arena);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.arena = arena;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public String getBukkitVersion() {
		return bukkitVersion;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public Team getPlayersTeam(Player player) {
		for (Team team : teams) {
			if (team.contains(player)) {
				return team;
			}
		}
		return null;
	}

	public boolean isWinner(Team team) {
		if (getWinner() != null) {
			return team.equals(getWinner());
		}
		return false;
	}

	public boolean isRanked() {
		return isRanked;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public void setAddSidebar(boolean addSidebar) {
		this.addSidebar = addSidebar;
	}

	public Map<UUID, Integer> getKillCounts() {
		return killCounts;
	}

	public UUID getMatchUuid() {
		return matchUuid;
	}

	public JSONObject getExtraData() {
		return extraData;
	}

	public JSONObject getTeam1Info() {
		return team1Info;
	}

	public JSONObject getTeam2Info() {
		return team2Info;
	}

	public void setTeam1Rank(RatingUtil.Rank team1Rank, boolean demoGame, boolean promoGame) {
		this.team1Rank = team1Rank;
		this.team1Demo = demoGame;
		this.team1Promo = promoGame;
	}

	public void setTeam2Rank(RatingUtil.Rank team2Rank, boolean demoGame, boolean promoGame) {
		this.team2Rank = team2Rank;
		this.team2Demo = demoGame;
		this.team2Promo = promoGame;
	}

	public RatingUtil.Rank getTeam1Rank() {
		return team1Rank;
	}

	public RatingUtil.Rank getTeam2Rank() {
		return team2Rank;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public boolean isFriendlyFireEnabled() {
		return friendlyFireEnabled;
	}

	public void setFriendlyFireEnabled(boolean friendlyFireEnabled) {
		this.friendlyFireEnabled = friendlyFireEnabled;
	}

	public void updateMatchTimeLeftString() {
		long endTime = this.startTime.getMillis() + (this.matchLengthTime * 60 * 1000);
		//Seconds left
		long timeLeft = (endTime - System.currentTimeMillis()) / 1000;
		int min = (int) (timeLeft / 60);
		int sec = (int) (timeLeft % 60);
		this.timeLeftString = ChatColor.GOLD + "   " + min + ":" + (sec < 10 ? "0" + sec : sec);
	}

	public SidebarItem getMatchTime() {
		return this.matchTime;
	}

	public class MatchTieTask extends BukkitRunnable {

		private Match match;

		public MatchTieTask(Match match) {
			this.match = match;
		}

		@Override
		public void run() {
			this.match.declareWinner(null); // tie
			this.match.handleTie();
			//this.match.handleStasis(this.match.getTeam1(), this.match.getTeam2());
		}
	}

}