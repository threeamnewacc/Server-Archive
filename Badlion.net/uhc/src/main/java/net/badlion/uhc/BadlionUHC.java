package net.badlion.uhc;

import com.google.common.base.Joiner;
import io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.badlion.banmanager.BanManager;
import net.badlion.common.Configurator;
import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.MessageUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gguard.GGuard;
import net.badlion.gpermissions.GPermissions;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.uhc.commands.BorderShrinkCommand;
import net.badlion.uhc.commands.ClearInventoryCommand;
import net.badlion.uhc.commands.ConfigsCommand;
import net.badlion.uhc.commands.FeedCommand;
import net.badlion.uhc.commands.FoodCommand;
import net.badlion.uhc.commands.HealCommand;
import net.badlion.uhc.commands.HealthCommand;
import net.badlion.uhc.commands.HelpOpCommand;
import net.badlion.uhc.commands.KillCountCommand;
import net.badlion.uhc.commands.KillTopCommand;
import net.badlion.uhc.commands.MLGCommand;
import net.badlion.uhc.commands.MiningNotificationCommand;
import net.badlion.uhc.commands.ObjectivesCommand;
import net.badlion.uhc.commands.QChatCommand;
import net.badlion.uhc.commands.QTeamCommand;
import net.badlion.uhc.commands.ResetStatsCommand;
import net.badlion.uhc.commands.RulesCommand;
import net.badlion.uhc.commands.ScenariosCommand;
import net.badlion.uhc.commands.SendCoordsCommand;
import net.badlion.uhc.commands.SpectatorCommand;
import net.badlion.uhc.commands.StatsCommand;
import net.badlion.uhc.commands.TeamCommand;
import net.badlion.uhc.commands.TeamListCommand;
import net.badlion.uhc.commands.TeleCommand;
import net.badlion.uhc.commands.TransferHealthCommand;
import net.badlion.uhc.commands.UHCCommand;
import net.badlion.uhc.commands.UHCGlobalMuteCommand;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.commands.WhitelistCommand;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.commands.handlers.GenerateSpawnsCommandHandler;
import net.badlion.uhc.commands.handlers.StartCommandHandler;
import net.badlion.uhc.events.GameTimeElapsedEvent;
import net.badlion.uhc.events.GiveLobbyItemsEvent;
import net.badlion.uhc.events.GiveStarterItemsEvent;
import net.badlion.uhc.events.GoldenHeadRecipeEvent;
import net.badlion.uhc.events.ServerStateChangeEvent;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.inventories.SpectatorInventory;
import net.badlion.uhc.listeners.AbuseListener;
import net.badlion.uhc.listeners.CombatTagListener;
import net.badlion.uhc.listeners.DisguiseListener;
import net.badlion.uhc.listeners.HostListener;
import net.badlion.uhc.listeners.MainListener;
import net.badlion.uhc.listeners.MaxPlayerListener;
import net.badlion.uhc.listeners.MiniStatsListener;
import net.badlion.uhc.listeners.MiniUHCListener;
import net.badlion.uhc.listeners.ModeratorListener;
import net.badlion.uhc.listeners.PlayerListener;
import net.badlion.uhc.listeners.SpectatorListener;
import net.badlion.uhc.listeners.TrappedPortalListener;
import net.badlion.uhc.listeners.VanishedPlayerListener;
import net.badlion.uhc.listeners.WorldGenerationListener;
import net.badlion.uhc.listeners.WorldListener;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.practice.PracticeCommand;
import net.badlion.uhc.practice.PracticeListener;
import net.badlion.uhc.practice.PracticeManager;
import net.badlion.uhc.tasks.BorderShrinkTask;
import net.badlion.uhc.tasks.GameTimeTask;
import net.badlion.uhc.tasks.PermanentDayTask;
import net.badlion.worldborder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BadlionUHC extends JavaPlugin {

    public static String PREFIX = ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionUHC" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.RESET;

    public enum BadlionUHCState {
        WORLD_GENERATION, CONFIG, SPAWN_GENERATION, PRE_START, COUNTDOWN, STARTED
    }

    private static Map<UUID, String> usernames = new HashMap<>();

    public void putUsername(UUID uuid, String username) {
        BadlionUHC.usernames.put(uuid, username);
    }

    public String getUsername(UUID uuid) {
        return BadlionUHC.usernames.get(uuid);
    }

    private static Map<UUID, String> displayNames = new HashMap<>();

    public void putDisplayName(UUID uuid, String displayname) {
        BadlionUHC.displayNames.put(uuid, displayname);
    }

    public String getDisplayName(UUID uuid) {
        return BadlionUHC.displayNames.get(uuid);
    }

    private static Map<String, UUID> uuids = new HashMap<>();

    public void putUUID(UUID uuid, String username) {
        BadlionUHC.uuids.put(username.toLowerCase(), uuid);
    }

    public UUID getUUID(String username) {
        return BadlionUHC.uuids.get(username.toLowerCase());
    }

    public UUID removeUUID(String username) {
        return BadlionUHC.uuids.remove(username.toLowerCase());
    }

    private static BadlionUHC plugin;
    private static UHCGame uhcGame;
    public static int HEAD_HALF_HEARTS_TO_HEAL = 8;
    private BadlionUHCState state = BadlionUHCState.WORLD_GENERATION;

    public Gberry gberry;
    public GPermissions gPermissions;
	public BanManager banManager;

    private long startTime;
    private long endTime = -1;
	private int minutesElapsed = 5;
    private int id;

	private boolean pvp = false;
	private boolean practice = true;
    private int gameplayTimerTask;
    private int afkCheckerTask;

	private Scoreboard scoreboard;

    private UHCTeam.GameType gameType = UHCTeam.GameType.SOLO;

    // Spectator item
    private ItemStack spectatorItem;

	private Location winnerLocation;

	private List<Location> spawnLocations = new ArrayList<>();

    private HashSet<String> whitelist = new HashSet<>();  // getName().toLowerCase()

	private ArrayList<String> teamChatColors = new ArrayList<>();
	private ArrayList<String> usedTeamChatColors = new ArrayList<>();

    private ArrayList<Material> materials = new ArrayList<>();
    private ArrayList<EntityType> entityTypes = new ArrayList<>();

    private boolean canRestart = true;
    private boolean allowDonators = true;

	// Parkour! :D
	private ArrayList<String> completedParkour = new ArrayList<>();

    private Configurator configurator;

    private boolean whitelistBoolean = true;
    public static boolean lockdown = false;
    private boolean miniUHC = false;

    private Boolean borderShrink = false;
	private PermanentDayTask permanentDayTask;
    private BorderShrinkTask borderShrinkTask;

    private WorldBorder worldBorder;

    public static String UHCWORLD_NAME = "uhcworld";
    public static String UHCWORLD_NETHER_NAME = UHCWORLD_NAME + "_nether";
    public static String UHCWORLD_END_NAME = UHCWORLD_NAME + "_the_end";

    private UHCPlayer mostKillsUHCPlayer = null;

    public static BadlionUHC getInstance() {
        return BadlionUHC.plugin;
    }

    public UHCGame getGame() {
        return BadlionUHC.uhcGame;
    }

    public BadlionUHC() {
        Gberry.enableProtocol = true;
    }

	@Override
    public void onEnable() {
        this.saveDefaultConfig();

        BadlionUHC.plugin = this;
        BadlionUHC.uhcGame = new UHCGame();

        this.allowDonators = this.getConfig().getBoolean("allow-donators", true);
        this.miniUHC = this.getConfig().getBoolean("mini-uhc", false);

        MiniStats.TAG = "uhc";
        MiniStats.TABLE_NAME = this.miniUHC ? "uhcmini_ministats" : "uhc_s4_ministats";
        MiniStats.SEASON = 4;

        this.gberry = (Gberry) this.getServer().getPluginManager().getPlugin("Gberry");
        this.gPermissions = (GPermissions) this.getServer().getPluginManager().getPlugin("GPermissions");
	    this.banManager = (BanManager) this.getServer().getPluginManager().getPlugin("BanManager");
        this.worldBorder = (WorldBorder) this.getServer().getPluginManager().getPlugin("WorldBorder");

        // Initialize smellyinventory
        SmellyInventory.initialize(this, false);

		// Set UHC list command handler
		Gberry.plugin.setListCommandHandler(new UHCListCommandHandler());

		// Set ministats player creator
		MiniStats.getInstance().setMiniStatsPlayerCreator(new UHCMiniStatsPlayer.UHCMiniStatsPlayerCreator());

        ProtocolScheduler.addHook(new ProtocolOutHook() {

            @Override
            public Object handlePacket(Player receiver, Object packet) {
                if (TinyProtocolReferences.tabPacketClass.isInstance(packet)) {
                    String name = TinyProtocolReferences.tabPacketName.get(packet).toLowerCase();

                    for (UHCPlayer mod : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD)) {
                        if (mod.getUsername().contains(name)) {
                            return null;
                        }
                    }
                }

                return packet;
            }

            @Override
            public ProtocolPriority getPriority() {
                return ProtocolPriority.LAST;
            }
        });

        // Create spectator item
        this.spectatorItem = ItemStackUtil.createItem(Material.WATCH, ChatColor.AQUA + "Alive Players");

        // Initialize managers and inventories
        SpectatorInventory.initialize();
        UHCPlayerManager.initialize();

        /* SETUP WORLDS */
        Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME).setSpawnLocation(0, Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME).getHighestBlockYAt(0, 0) + 2, 0);

        // Default stuff
        if (BadlionUHC.getInstance().isMiniUHC()) {
            BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld set " + (500) + " 0 0");
            BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld_nether set " + ((500 / 8) + 25) + " 0 0");
        } else {
            BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld set " + (3000) + " 0 0");
            BadlionUHC.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb uhcworld_nether set " + ((3000 / 8) + 25) + " 0 0");
        }


		WorldCreator wc = new WorldCreator("uhcpractice");
		wc.generator(new CleanroomChunkGenerator("."));
		World world = this.getServer().createWorld(wc);
		this.getServer().getWorlds().add(world);

        wc = new WorldCreator("uhclobby");
        wc.generator(new CleanroomChunkGenerator("."));
        world = this.getServer().createWorld(wc);
        this.getServer().getWorlds().add(world);

		this.winnerLocation = new Location(world, 0.5, 95, 0.5, 0, 0);

		this.spawnLocations.add(new Location(world, 0.5, 95, 0.5, 0, 0));
		this.spawnLocations.add(new Location(world, 51.5, 95, 72.5, 135, 0));
		this.spawnLocations.add(new Location(world, 49.5, 91, -71.5, 45, 0));
		this.spawnLocations.add(new Location(world, -43.5, 93, -69.5, -50, 0));
		this.spawnLocations.add(new Location(world, -51.5, 95, 71.5, -135, 0));

		// Initialize after spawn loaded
		PracticeManager.initialize();

        // Reload the GGuard stuff now
        GGuard gGuard = (GGuard) this.getServer().getPluginManager().getPlugin("GGuard");
        gGuard.getProtectedRegionsFromConfig();

        for (World w : Bukkit.getWorlds()) { // Set difficulty to hard
            w.setDifficulty(Difficulty.HARD);
        }
        /* SETUP WORLDS */

        for (ChatColor ch : ChatColor.values()) { // Load chat colors into colors list
            if (ch != ChatColor.BLACK && ch != ChatColor.BOLD && ch != ChatColor.ITALIC && ch != ChatColor.MAGIC
                    && ch != ChatColor.RESET && ch != ChatColor.STRIKETHROUGH && ch != ChatColor.UNDERLINE) {
                this.teamChatColors.add(ch.toString());
            }
        }

        // Statistics stuff
        this.materials.add(Material.DIAMOND_ORE);
        this.materials.add(Material.EMERALD_ORE);
        this.materials.add(Material.REDSTONE_ORE);
        this.materials.add(Material.LAPIS_ORE);
        this.materials.add(Material.GOLD_ORE);
        this.materials.add(Material.IRON_ORE);
        this.materials.add(Material.COAL_ORE);

        this.entityTypes.add(EntityType.CAVE_SPIDER);
        this.entityTypes.add(EntityType.CHICKEN);
        this.entityTypes.add(EntityType.COW);
        this.entityTypes.add(EntityType.CREEPER);
        this.entityTypes.add(EntityType.ENDERMAN);
        this.entityTypes.add(EntityType.PIG);
        this.entityTypes.add(EntityType.SILVERFISH);
        this.entityTypes.add(EntityType.SKELETON);
        this.entityTypes.add(EntityType.SLIME);
        this.entityTypes.add(EntityType.SPIDER);
        this.entityTypes.add(EntityType.ZOMBIE);

	    //RulesCommandHandler.initialize();

	    this.getServer().getPluginManager().registerEvents(new AbuseListener(), this);

        // Enable CombatTag
        if (this.getConfig().getBoolean("ct", true)) {
            this.getServer().getPluginManager().registerEvents(new CombatTagListener(), this);
        }

        this.getServer().getPluginManager().registerEvents(new DisguiseListener(), this);
        this.getServer().getPluginManager().registerEvents(new HostListener(), this);
        this.getServer().getPluginManager().registerEvents(new MainListener(), this);
        this.getServer().getPluginManager().registerEvents(new MaxPlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new MiniStatsListener(), this);
        this.getServer().getPluginManager().registerEvents(new ModeratorListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
        this.getServer().getPluginManager().registerEvents(new TrappedPortalListener(), this);
        this.getServer().getPluginManager().registerEvents(new VanishedPlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
	    this.getServer().getPluginManager().registerEvents(new WorldGenerationListener(), this);
	    this.getServer().getPluginManager().registerEvents(new PracticeListener(), this);

        // If MiniUHC
        if (this.miniUHC) {
            this.getServer().getPluginManager().registerEvents(new MiniUHCListener(), this);
        }

	    this.getCommand("transferhealth").setExecutor(new TransferHealthCommand());
	    this.getCommand("killcount").setExecutor(new KillCountCommand());
	    this.getCommand("killtop").setExecutor(new KillTopCommand());
	    this.getCommand("sendcoords").setExecutor(new SendCoordsCommand());
	    this.getCommand("uhcgm").setExecutor(new UHCGlobalMuteCommand());
	    this.getCommand("health").setExecutor(new HealthCommand());
        //this.getCommand("stick").setExecutor(new StickCommand());
        this.getCommand("vanish").setExecutor(new VanishCommand());
	    this.getCommand("tele").setExecutor(new TeleCommand());
	    this.getCommand("configs").setExecutor(new ConfigsCommand());
	    this.getCommand("bordershrink").setExecutor(new BorderShrinkCommand());
	    this.getCommand("spectator").setExecutor(new SpectatorCommand());
	    this.getCommand("wl").setExecutor(new WhitelistCommand());
	    this.getCommand("team").setExecutor(new TeamCommand());
	    this.getCommand("tl").setExecutor(new TeamListCommand());
        this.getCommand("uhc").setExecutor(new UHCCommand());
	    this.getCommand("feed").setExecutor(new FeedCommand());
	    this.getCommand("food").setExecutor(new FoodCommand());
	    this.getCommand("heal").setExecutor(new HealCommand());
	    this.getCommand("ci").setExecutor(new ClearInventoryCommand());
	    this.getCommand("helpop").setExecutor(new HelpOpCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("miningnotification").setExecutor(new MiningNotificationCommand());
        this.getCommand("scenarios").setExecutor(new ScenariosCommand());
        this.getCommand("resetstats").setExecutor(new ResetStatsCommand());
	    this.getCommand("rules").setExecutor(new RulesCommand());
	    this.getCommand("mlg").setExecutor(new MLGCommand());
	    this.getCommand("practice").setExecutor(new PracticeCommand());
	    this.getCommand("qc").setExecutor(new QChatCommand());
	    this.getCommand("objectives").setExecutor(new ObjectivesCommand());
	    this.getCommand("qteam").setExecutor(new QTeamCommand());

	    // Help book
	    new UHCCommand();

        // Scoreboard
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objectiveList = this.scoreboard.registerNewObjective("UHCHealth", "dummy");
        objectiveList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        Objective objectiveName = this.scoreboard.registerNewObjective("UHCHealthName", "dummy");
        objectiveName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objectiveName.setDisplayName(MessageUtil.HEART_WITH_COLOR);
    }

    public void onDisable() {
        System.out.println("BadlionUHC disabled!");
    }

	public Location getTeleportLocation(Player player) {
		Location location = null;

		// Try to TP to teammate first
		if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
			UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

			for (UUID uuid : uhcp.getTeam().getUuids()) {
				Player p = BadlionUHC.getInstance().getServer().getPlayer(uuid);
				if (p != null && p.getLocation().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME)) {
					location = p.getLocation();
					break;
				}
			}
		}

		// Ok couldn't find team-mate, get them a new spawn
		if (location == null) {
            // Let custom game modes do something first
            UHCTeleportPlayerLocationEvent event = new UHCTeleportPlayerLocationEvent(player);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

            if (event.getLocation() != null) {
                return event.getLocation();
            }

            if (GenerateSpawnsCommandHandler.scatterPoints.size() > 0) {
                location = GenerateSpawnsCommandHandler.scatterPoints.remove(0);
            }

            // Might have ran out of locations
            if (location == null) {
                location = GenerateSpawnsCommandHandler.getNewLocation();
            }
		} else {
            UHCTeleportPlayerLocationEvent event = new UHCTeleportPlayerLocationEvent(player, location);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

            if (event.getLocation() != null) {
                return event.getLocation();
            }
        }

		return location;
	}

    public void handlePlayerTeleportAndStart(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) {
            StartCommandHandler.prepPlayerForStart(player);

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

	        Location location = getTeleportLocation(player);
	        if (location != null) {
		        Gberry.safeTeleport(player, location);
	        } else {
		        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "mc " + player.getName() + " needs to be teleported to the main world.");
	        }

            GiveStarterItemsEvent event = new GiveStarterItemsEvent(player);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

            // Starter food
            if (FoodCommand.lastFoodAmountGiven != 0) {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
                uhcPlayer.setWasFed(true);
                player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, FoodCommand.lastFoodAmountGiven));
            }
        }
    }

    public void handlePlayerJoinLobby(UHCPlayer uhcPlayer, Player player) {
	    // Respawn them first if they're dead in Practice
	    if (player.isDead()) {
		    player.spigot().respawn();
	    }

        if (uhcPlayer.getState().ordinal() < UHCPlayer.State.SPEC.ordinal()) {
            player.setGameMode(GameMode.SURVIVAL);
        }

	    // Heal player
	    player.setHealth(20.0);
	    player.setFoodLevel(20);
	    player.setSaturation(20);
	    player.setExhaustion(0);

	    player.setLevel(0);
	    player.setTotalExperience(0);
	    player.setArrowsStuck(0);

	    for (PotionEffect effect : player.getActivePotionEffects()) {
		    player.removePotionEffect(effect.getType());
	    }

	    // Clear inventory
	    player.getInventory().setArmorContents(null);
	    player.getInventory().clear();
	    player.setItemOnCursor(null);
	    player.getInventory().setHeldItemSlot(0);
	    player.updateInventory();

	    player.teleport(BadlionUHC.getInstance().getSpawnLocation());
	    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 50000, 127, true));

        GiveLobbyItemsEvent event = new GiveLobbyItemsEvent(player);
        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

	    // Hotbar item for cosmetics
	    player.getInventory().setItem(8, CosmeticsInventory.getOpenCosmeticInventoryItem());
    }

    public boolean checkForWinners() {
        // Stop this from being executed multiple times
        if (this.endTime != -1) {
            return false;
        }

        ConcurrentLinkedQueue<UHCPlayer> players = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER);
        UHCTeam remainingTeam = null;

        if (players.size() == 0) {
            return true;
        }

        for (UHCPlayer p : players) {
            if (remainingTeam == null) {
                remainingTeam = p.getTeam();
            } else if (remainingTeam != p.getTeam()) {
                return false;
            }
        }

        // Make Intellij happy
        if (remainingTeam == null) {
            return false;
        }

        this.declareWinningTeam(remainingTeam);

        return true;
    }

    public void declareWinningTeam(UHCTeam uhcTeam) {
	    List<UUID> uuids = uhcTeam.getUuids();
	    StringBuilder builder = new StringBuilder();
	    for (UUID uuid : uuids) {
		    // Stat Tracking
		    BadlionUHC.getInstance().getGame().addWinner(uuid);

		    Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
		    if (player != null) {
			    player.sendMessage(ChatColor.AQUA + "Congratulations on winning the UHC!");
			    builder.append(player.getDisguisedName());
			    builder.append(", ");
		    }
	    }

	    if (builder.length() > 2) {
		    Gberry.broadcastMessage(ChatColor.AQUA + "Congratulations to " + builder.toString().substring(0, builder.length() - 2) + " for winning!");
	    }

	    this.endGame();

	    if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
		    BadlionUHC.getInstance().setCanRestart(false);
		    MiniStats.getInstance().getPlayerDataListener().setTrackStats(false);
	    }


	    BadlionUHC.getInstance().getGame().setEndTime(new DateTime(DateTimeZone.UTC).getMillis());
	    GameTimeTask.secondsInGame = GameTimeTask.getNumOfSeconds();

	    if (BadlionUHC.getInstance().isMiniUHC()) {
		    for (UUID uuid : uuids) {
			    // Stat Tracking
			    BadlionUHC.getInstance().getGame().addWinner(uuid);

			    UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);

			    // Only let them do MLG if they're alive
			    if (uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
				    Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
				    if (player != null) {
					    MLGCommand.allowedMLGPlayers.add(player.getUniqueId());
					    player.sendMessage(ChatColor.BOLD.toString() + ChatColor.YELLOW + "You have 10 seconds to type /mlg to try the MLG water bucket challenge!");
				    }
			    }
		    }
	    }

	    // Start off stat storage
	    if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
		    BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
			    @Override
			    public void run() {
				    int res = DatabaseManager.saveMatchData(BadlionUHC.getInstance().getGame());
				    String msg = ChatColor.AQUA + "The stats are now stored in the database.";
				    if (res == -1) { // error
					    msg = ChatColor.RED + "Failed to insert stats. Report to Admin. Do not delete world.";
				    }

				    final String finalMsg = msg;
				    BadlionUHC.getInstance().getServer().getScheduler().runTask(BadlionUHC.getInstance(), new Runnable() {
					    @Override
					    public void run() {
						    Gberry.broadcastMessage(finalMsg);
						    BadlionUHC.getInstance().setCanRestart(true);

						    if (BadlionUHC.getInstance().isMiniUHC()) {
							    // Do MLG after 10 seconds
							    new BukkitRunnable() {
								    @Override
								    public void run() {
									    // MLG
									    MLGCommand.doMLG();
								    }
							    }.runTaskLater(BadlionUHC.this, 20L * 10);

							    // Stop server after 60 seconds
							    new BukkitRunnable() {
								    public void run() {
									    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc deleteworld");
								    }
							    }.runTaskLater(BadlionUHC.getInstance(), 20 * 60);
						    }
					    }
				    });
			    }
		    });
	    } else {
		    // It's statless
		    if (BadlionUHC.getInstance().isMiniUHC()) {
			    Gberry.broadcastMessage(BadlionUHC.PREFIX + ChatColor.AQUA + "Server reboots in 1 minute.");
			    // Do MLG after 10 seconds
			    BadlionUHC.getInstance().getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
				    @Override
				    public void run() {
					    // MLG
					    MLGCommand.doMLG();
				    }
			    }, 20L * 10);

			    // Restart server after 60 seconds
			    new BukkitRunnable() {
				    public void run() {
					    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "uhc deleteworld");
				    }
			    }.runTaskLater(BadlionUHC.getInstance(), 20 * 60);
		    }
	    }
    }

    public void endGame() {
        this.getServer().getScheduler().cancelTask(this.getGameplayTimerTask());
        this.getServer().getScheduler().cancelTask(this.getAfkCheckerTask());
        this.setEndTime(System.currentTimeMillis());
    }

    public World getUHCWorld() {
        return Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
    }

    public void teleportToMainWorldAndVanish(Player player) {
        World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
        int y = w.getHighestBlockYAt(0, 0) + 10;
        player.teleport(new Location(w, 0, y, 0));
        VanishCommand.vanishPlayer(player);
    }

    public void createRecipes() {
        ShapedRecipe appleRecipe = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE));
        appleRecipe.shape("AAA", "ABA", "AAA");
        appleRecipe.setIngredient('A', Material.GOLD_INGOT);
        appleRecipe.setIngredient('B', Material.APPLE);
        Bukkit.addRecipe(appleRecipe);

        ShapedRecipe carrotRecipe = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT));
        carrotRecipe.shape("AAA", "ABA", "AAA");
	    carrotRecipe.setIngredient('A', Material.GOLD_INGOT);
        carrotRecipe.setIngredient('B', Material.CARROT_ITEM);
        Bukkit.addRecipe(carrotRecipe);

        ShapelessRecipe melonRecipe = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
        melonRecipe.addIngredient(Material.GOLD_BLOCK);
        melonRecipe.addIngredient(Material.MELON);
        Bukkit.addRecipe(melonRecipe);

        GoldenHeadRecipeEvent event = new GoldenHeadRecipeEvent();
        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ShapedRecipe headRecipe = new ShapedRecipe(ItemStackUtil.createGoldenHead());
            headRecipe.shape("AAA", "ABA", "AAA");
            headRecipe.setIngredient('A', Material.GOLD_INGOT);
            headRecipe.setIngredient('B', Material.SKULL_ITEM, 3);
            Bukkit.addRecipe(headRecipe);
        }
    }

	public void announceGameplayTime() {
		Gberry.broadcastMessage(ChatColor.AQUA.toString() + this.minutesElapsed + " minutes have elapsed!");
		this.minutesElapsed += 5;

        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(new GameTimeElapsedEvent(this.minutesElapsed));
	}

    private static int teamColorNumber = 0;
    public static ChatColor[] validTeamColors = new ChatColor[] {
            ChatColor.DARK_GREEN, /*ChatColor.DARK_AQUA, ChatColor.DARK_RED,*/ ChatColor.DARK_PURPLE, ChatColor.GOLD,
            ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, /*ChatColor.AQUA,*/ ChatColor.RED, ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW, ChatColor.WHITE
    };

	public ChatColor getRandomTeamChatColor() {
        return BadlionUHC.validTeamColors[teamColorNumber++ % BadlionUHC.validTeamColors.length];
	}

    public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
	        for (File file : files) {
		        if (file.isDirectory()) {
			        deleteDirectory(file);
		        } else {
			        file.delete();
		        }
	        }
        }
        return (path.delete());
    }

    public void clearTeams() {
	    BadlionUHC.getInstance().getUsedTeamChatColors().clear();

        for (UHCPlayer uhcp : UHCPlayerManager.getAllUHCPlayers()) {
            uhcp.setTeam(null);
        }

	    for (Team sbTeam : this.scoreboard.getTeams()) {
		    sbTeam.unregister();
	    }
    }

    public boolean containsMaterial(ItemStack[] matrix, Material mat) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() == mat) return true;
        }
        return false;
    }

    public enum CONFIG_OPTIONS {
        RADIUS, NETHER, STR, STR2, IPVP, INVISIBILITY, ABSORPTION, GOLDENHEADS, GOLDENHEADSSTACK, GODAPPLES, PVPTIMER, MAXPLAYERS,
        PEARLS, STATS, SCOREBOARDHEALTHSCALE, TEAMSIZE, TEAMHEALTHTRANSFER, TEAMHEALTHSHARE, HEALTIME, FOOD, HORSE_REGEN
    }

    public void createSoloConfigurator() {
        this.configurator = new Configurator();
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.RADIUS.name(), ChatColor.GOLD + "World Radius", null, true, 100, 5000);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.NETHER.name(), ChatColor.GOLD + "Nether", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.STR.name(), ChatColor.GOLD + "Strength 1", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.STR2.name(), ChatColor.GOLD + "Strength 2", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.IPVP.name(), ChatColor.GOLD + "IPVP", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.INVISIBILITY.name(), ChatColor.GOLD + "Invisibility", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.ABSORPTION.name(), ChatColor.GOLD + "Absorption", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.GOLDENHEADS.name(), ChatColor.GOLD + "Golden Heads", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.GOLDENHEADSSTACK.name(), ChatColor.GOLD + "Golden Heads Stackable", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.GODAPPLES.name(), ChatColor.GOLD + "God Apples", null, true);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.PVPTIMER.name(), ChatColor.GOLD + "PVP Timer", null, true, 0, 60);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.MAXPLAYERS.name(), ChatColor.GOLD + "Max Players", null, true, 0, 1000);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.PEARLS.name(), ChatColor.GOLD + "Enderpearl Damage", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.STATS.name(), ChatColor.GOLD + "Stats", null, true);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.SCOREBOARDHEALTHSCALE.name(), ChatColor.GOLD + "Scoreboard Health Scale", 1, false, 1, 1);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.HEALTIME.name(), ChatColor.GOLD + "Heal Time", 10, false, 0, 10);
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.FOOD.name(), ChatColor.GOLD + "Food", 10, true, 0, 20);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.HORSE_REGEN.name(), ChatColor.GOLD + "Horse Regen", null, true);
    }

    public void createTeamConfigurator() {
        this.createSoloConfigurator();
        this.configurator.addNewIntegerOption(CONFIG_OPTIONS.TEAMSIZE.name(), ChatColor.GOLD + "Team Size", null, true, 2, 10);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.TEAMHEALTHTRANSFER.name(), ChatColor.GOLD + "Team Health Transfer", null, true);
        this.configurator.addNewBooleanOption(CONFIG_OPTIONS.TEAMHEALTHSHARE.name(), ChatColor.GOLD + "Team Health Share", null, true);
    }

    public UHCPlayer getHost() {
        Iterator<UHCPlayer> it = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.HOST).iterator();
        if (it.hasNext()) {
            return it.next();
        }

        return null;
    }

	public boolean isPVP() {
		return pvp;
	}

	public void setPVP(boolean pvp) {
		this.pvp = pvp;
	}

	public boolean isPractice() {
		return practice;
	}

	public void setPractice(boolean practice) {
		this.practice = practice;
	}

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public void setWorldGenerated(boolean worldGenerated, String worldName, BorderData borderData) {
        this.setState(BadlionUHCState.CONFIG);

        // Setup Badlion UHC borders
        EditSession es = new EditSession(new BukkitWorld(Bukkit.getWorld(worldName)), 2147483647);

        try {
            switch (this.borderShape) { // Make borders 3 thick to prevent vclipping
                case "square":
                    com.sk89q.worldedit.Vector v1 = new com.sk89q.worldedit.Vector(-1 * borderData.getRadiusX(), 0, -1 * borderData.getRadiusX());
                    com.sk89q.worldedit.Vector v2 = new com.sk89q.worldedit.Vector(borderData.getRadiusX(), 256, borderData.getRadiusX());
                    es.makeCuboidWalls(new CuboidRegion(v1, v2), new SingleBlockPattern(new BaseBlock(22)));
                    es.makeCuboidWalls(new CuboidRegion(v1.add(-1, 0, -1), v2.add(1, 0, 1)), new SingleBlockPattern(new BaseBlock(22)));
                    es.makeCuboidWalls(new CuboidRegion(v1.add(-2, 0, -2), v2.add(2, 0, 2)), new SingleBlockPattern(new BaseBlock(22)));
                    break;
                case "circle":
                    com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(0, 0, 0);
                    es.makeCylinder(v, new SingleBlockPattern(new BaseBlock(22)), borderData.getRadiusX(), 256, false);
                    es.makeCylinder(v, new SingleBlockPattern(new BaseBlock(22)), borderData.getRadiusX() + 1, 256, false);
                    es.makeCylinder(v, new SingleBlockPattern(new BaseBlock(22)), borderData.getRadiusX() + 2, 256, false);
                    break;
            }
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }*/

    public void addMuteBanPerms(Player player) {
	    if (player.hasPermission("badlion.uhchost")) {
		    GPermissions.giveModPermissions(player);
	    } else if (player.hasPermission("badlion.uhctrial")) {
            GPermissions.giveTrialPermissions(player);
        }
    }

    public void sendWelcomeMessages(Player player) {
        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
        player.sendMessage(ChatColor.AQUA + "Welcome to Badlion UHC 5.0");

        // If Game is setup
        if (this.state.ordinal() >= BadlionUHCState.PRE_START.ordinal()) {
            player.sendMessage("");

            if (this.getConfigurator().getIntegerOption(CONFIG_OPTIONS.TEAMSIZE.name()) != null) {
                player.sendMessage(ChatColor.AQUA + "Team size: " + ChatColor.GOLD + this.getConfigurator().getIntegerOption(CONFIG_OPTIONS.TEAMSIZE.name()).getValue());
            } else {
                player.sendMessage(ChatColor.AQUA + "FFA Match");
            }

            if (GameModeHandler.GAME_MODES.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "Scenarios: " + ChatColor.GOLD + Joiner.on(", ").skipNulls().join(GameModeHandler.GAME_MODES).replace("_", " "));
            } else {
                player.sendMessage(ChatColor.AQUA + "This is a Vanilla UHC match");
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "Do " + ChatColor.DARK_RED + "/practice" + ChatColor.AQUA + " to join/leave the practice arena");
            player.sendMessage(ChatColor.AQUA + "Do " + ChatColor.DARK_RED + "/config" + ChatColor.AQUA + " and " + ChatColor.DARK_RED + "/scenarios" + ChatColor.AQUA + " for more information");
        }

        player.sendMessage(Gberry.getLineSeparator(ChatColor.GOLD));
    }

    public void removeBeginningPotionEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public void hideAllVanishedPlayers(final Player player) {
        Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
	        public void run() {
		        ConcurrentLinkedQueue<UHCPlayer> moderators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD);
		        for (UHCPlayer mod : moderators) {
			        Player p = BadlionUHC.getInstance().getServer().getPlayer(mod.getUUID());
			        if (p != null) {
				        player.hidePlayer(p);
			        }
		        }

		        ConcurrentLinkedQueue<UHCPlayer> spectators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.SPEC);
		        for (UHCPlayer spec : spectators) {
			        Player p = BadlionUHC.getInstance().getServer().getPlayer(spec.getUUID());
			        if (p != null) {
				        player.hidePlayer(p);
			        }
		        }

		        ConcurrentLinkedQueue<UHCPlayer> hosts = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.HOST);
		        for (UHCPlayer host : hosts) {
			        Player p = BadlionUHC.getInstance().getServer().getPlayer(host.getUUID());
			        if (p != null) {
				        player.hidePlayer(p);
			        }
		        }
	        }
        }, 1L);
    }

    public void updateDeathState(Player player) {
        this.updateDeathState(player.getUniqueId(), player.hasPermission("badlion.donatorplus"));
    }

    public void updateDeathState(UUID uuid, boolean hasDonatorPlus) {
        // Update their state
        UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(uuid);
        UHCPlayer.State state = UHCPlayer.State.DEAD;
        if (uhcp.getState() == UHCPlayer.State.SPEC_IN_GAME || hasDonatorPlus) {
            state = UHCPlayer.State.SPEC;
        }

        UHCPlayerManager.updateUHCPlayerState(uuid, state);

        // Stop tracking data if we were
        PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerData(uuid);
        if (playerData != null) {
            playerData.setTrackData(false);
        }
    }

    public void createExpOrb(Location location, int amount) {
        ExperienceOrb experienceOrb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(amount);
    }

    public boolean hasHostPermissions(CommandSender sender) {
        return sender.isOp() || (sender instanceof Player && ((Player) sender).getUniqueId().equals(BadlionUHC.getInstance().getHost().getUUID()));
    }

    public UHCPlayer getMostKillsUHCPlayer() {
        return mostKillsUHCPlayer;
    }

    public void setMostKillsUHCPlayer(UHCPlayer mostKillsUHCPlayer) {
        this.mostKillsUHCPlayer = mostKillsUHCPlayer;
    }

    public Configurator getConfigurator() {
        return configurator;
    }

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getGameplayTimerTask() {
		return gameplayTimerTask;
	}

	public void setGameplayTimerTask(int gameplayTimerTask) {
		this.gameplayTimerTask = gameplayTimerTask;
	}

	public int getAfkCheckerTask() {
		return afkCheckerTask;
	}

    public ItemStack getSpectatorItem() {
        return spectatorItem;
    }

    public Location getSpawnLocation() {
		return this.spawnLocations.get(Gberry.generateRandomInt(0, this.spawnLocations.size() - 1));
	}

	public Location getWinnerLocation() {
		return winnerLocation;
	}

	public HashSet<String> getWhitelist() {
		return whitelist;
	}

	public ArrayList<Material> getMaterials() {
		return materials;
	}

	public ArrayList<EntityType> getEntityTypes() {
		return entityTypes;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public ArrayList<String> getCompletedParkour() {
		return completedParkour;
	}

	public boolean isWhitelistBoolean() {
		return whitelistBoolean;
	}

	public void setWhitelistBoolean(boolean whitelistBoolean) {
		this.whitelistBoolean = whitelistBoolean;
	}

	public Boolean getBorderShrink() {
		return borderShrink;
	}

	public void setBorderShrink(Boolean borderShrink) {
		this.borderShrink = borderShrink;
	}

	public BorderShrinkTask getBorderShrinkTask() {
		return borderShrinkTask;
	}

	public void setBorderShrinkTask(BorderShrinkTask borderShrinkTask) {
		this.borderShrinkTask = borderShrinkTask;
	}

	public Gberry getGberry() {
		return gberry;
	}

    public GPermissions getgPermissions() {
        return gPermissions;
    }

    public BanManager getBanManager() {
		return banManager;
	}

	public PermanentDayTask getPermanentDayTask() {
		return permanentDayTask;
	}

	public void setPermanentDayTask(PermanentDayTask permanentDayTask) {
		this.permanentDayTask = permanentDayTask;
	}

	public UHCTeam.GameType getGameType() {
		return gameType;
	}

	public void setGameType(UHCTeam.GameType gameType) {
		this.gameType = gameType;
	}

	public ArrayList<String> getTeamChatColors() {
		return teamChatColors;
	}

	public ArrayList<String> getUsedTeamChatColors() {
		return usedTeamChatColors;
	}

    public BadlionUHCState getState() {
        return state;
    }

    public void setState(BadlionUHCState state) {
        if (state.compareTo(this.state) < 0) {
            throw new RuntimeException("Invalid server state being transitioned from " + this.state + " to " + state);
        }

        ServerStateChangeEvent event = new ServerStateChangeEvent(this.state, state);
        this.getServer().getPluginManager().callEvent(event);

        Bukkit.getLogger().info("Server state is now " + state.name());

        this.state = state;
    }

    public WorldBorder getWorldBorder() {
        return worldBorder;
    }

    public boolean isCanRestart() {
        return canRestart;
    }

    public void setCanRestart(boolean canRestart) {
        this.canRestart = canRestart;
    }

    public boolean isAllowDonators() {
        return allowDonators;
    }

    public void setAllowDonators(boolean allowDonators) {
        this.allowDonators = allowDonators;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isMiniUHC() {
        return miniUHC;
    }

}
