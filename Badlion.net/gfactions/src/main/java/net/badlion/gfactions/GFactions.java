package net.badlion.gfactions;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import net.badlion.archmoney.ArchMoney;
import net.badlion.cmdsigns.CmdSigns;
import net.badlion.common.libraries.DateCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.commands.*;
import net.badlion.gfactions.commands.admin.ReloadConfigCommand;
import net.badlion.gfactions.commands.admin.WrenchCommand;
import net.badlion.gfactions.commands.events.*;
import net.badlion.gfactions.events.DragonEvent;
import net.badlion.gfactions.events.Tower;
import net.badlion.gfactions.events.koth.EndKOTHTask;
import net.badlion.gfactions.events.koth.KOTH;
import net.badlion.gfactions.events.koth.KOTHListener;
import net.badlion.gfactions.events.koth.KOTHRemoveProtTask;
import net.badlion.gfactions.events.scavenge.Scavenge;
import net.badlion.gfactions.events.scavenge.ScavengeConfig;
import net.badlion.gfactions.events.stronghold.Stronghold;
import net.badlion.gfactions.events.stronghold.StrongholdConfig;
import net.badlion.gfactions.events.stronghold.StrongholdListener;
import net.badlion.gfactions.events.supermine.SuperMine;
import net.badlion.gfactions.events.supermine.SuperMineConfig;
import net.badlion.gfactions.listeners.*;
import net.badlion.gfactions.managers.*;
import net.badlion.gfactions.tasks.*;
import net.badlion.gfactions.tasks.dragon.KillDragonTask;
import net.badlion.gguard.GGuard;
import net.badlion.gpermissions.GPermissions;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Referrence for more advanced program: http://forums.bukkit.org/threads/serializing-itemmeta-and-all-your-wildest-dreams.137325/

public class GFactions extends JavaPlugin {

    public static int PVP_PROTECTION_TIME;
    public static GFactions plugin;
    public static String PREFIX;

	private Gberry gberry;
    private ArchMoney archMoney;
	private GGuard gGuardPlugin;
	private CmdSigns cmdSigns;
    private GPermissions gperms;
	private int warZoneMinX;
	private int warZoneMaxX;
	private int warZoneMinZ;
	private int warZoneMaxZ;
	
	private RandomItemGenerator itemGenerator;

	private int godAppleTimer;
    private HashMap<String, Long> godAppleBlacklist;

	private Map<String, Integer> mapNameToPvPTimeRemaining;
	private Map<String, Long> mapNameToJoinTime;

	// Spawn Locations
	private Location spawnLocation;
	private List<Location> endEnterLocations = new ArrayList<>();
	private Location endExitLocation;

	// Man Hunt
	private Block manHuntPP;
    private Player manHuntTagged;
    private ArrayList<DateTime> manhuntTimes;
	
	// Tower
	private Tower tower;
	private WorldEditPlugin worldEditPlugin;
	private WorldEdit worldEdit;
    private LocalSession localSession;
    private EditSession editSession;
    private LocalPlayer localPlayer;
    
    // Server restart stuff
 	private DateTime dateToRestart;
 	
 	// Dragon stuff
    private DragonEvent dragonEvent;
 	private KillDragonTask killDragonTask;
    private ArrayList<Location> endCrystalLocations = new ArrayList<Location>();
 	
 	// Kick stuff
 	private HashSet<String> playersToBeKicked;

    // Lottery stuff
    private int lottoTicketPrice;
    private ArrayList<DateTime> lotteryTimes;
    private LottoDrawingTask lottoDrawingTask;

    // Parkour stuff
    private Chest parkourChest;
    private ArrayList<DateTime> parkourTimes;

    // KOTH stuff
    private KOTH koth;
    private EndKOTHTask endKOTHTask;
    private ArrayList<DateTime> kothTimes;

	// New events
	private Scavenge scavenge;
	private Stronghold stronghold;
    private SuperMine superMine;

 	// Killstreak stuff
	private KDAManager kdaManager;
 	private KillStreakManager killStreakManager;
 	
 	// Dungeon stuff
 	private DungeonManager dungeonManager;
 	
 	// BloodBowl
 	private BloodBowlManager bloodBowlManager;
 	
 	// Donator stuff
 	private DonatorManager donatorManager;

    // People who have voted today
    private HashSet<String> peopleWhoVotedToday;

	// Battle Manager
	private BattleManager battleManager;

	// Faction Manager
	private FactionManager factionManager;

	// Check Time Task
	private CheckTimeTask checkTimeTask;

	// Event Configurations
	private StrongholdConfig strongholdConfig;
    private SuperMineConfig superMineConfig;
    private ScavengeConfig scavengeConfig;

 	public GFactions() {
        GFactions.plugin = this;
        this.godAppleBlacklist = new HashMap<>();
 		this.mapNameToPvPTimeRemaining = new ConcurrentHashMap<>();
		this.mapNameToJoinTime = new ConcurrentHashMap<>();
		this.playersToBeKicked = new HashSet<>();
        this.lotteryTimes = new ArrayList<>();
        this.manhuntTimes = new ArrayList<>();
        this.parkourTimes = new ArrayList<>();
        this.kothTimes = new ArrayList<>();
	    this.kdaManager = new KDAManager();
	    this.killStreakManager = new KillStreakManager(this);
		this.dungeonManager = new DungeonManager(this);
		this.bloodBowlManager = new BloodBowlManager(this);
        //this.mcMMOManager = new McMMOManager(this);
        this.peopleWhoVotedToday = new HashSet<>();
		this.battleManager = new BattleManager(this);
		this.factionManager = new FactionManager(this);
 	}

	@Override
	public void onEnable() {
		// Save default config if no config file exists
		this.saveDefaultConfig();

        GFactions.PREFIX = this.getConfig().getString("gfactions.prefix");

        if (GFactions.PREFIX.equals("default")) {
            GFactions.plugin.getServer().getLogger().info("NO DATABASE TABLE SPECIFIED FOR GFACTIONS");
            GFactions.plugin.getServer().dispatchCommand(GFactions.plugin.getServer().getConsoleSender(), "stop");
            return;
        }

		// We do DB stuff here, gotta intialize it later
        this.donatorManager = new DonatorManager(this);
		
		// Load general config values
		this.spawnLocation = this.parseLocation(this.getConfig().getString("gfactions.spawn_location"));
		for (String locationString : this.getConfig().getStringList("gfactions.end_enter_spawn_locations")) {
			this.endEnterLocations.add(this.parseLocation(locationString));
		}
		this.endExitLocation = this.parseLocation(this.getConfig().getString("gfactions.end_exit_spawn_location"));
		this.godAppleTimer = this.getConfig().getInt("gfactions.god_apple_timer");
		GFactions.PVP_PROTECTION_TIME = this.getConfig().getInt("gfactions.pvp_protection_time");

		// Grab required plugins
        this.archMoney = (ArchMoney) this.getServer().getPluginManager().getPlugin("ArchMoney");
        this.gGuardPlugin = (GGuard) this.getServer().getPluginManager().getPlugin("GGuard");
        this.gperms = (GPermissions) this.getServer().getPluginManager().getPlugin("GPermissions");
		this.cmdSigns = (CmdSigns) this.getServer().getPluginManager().getPlugin("CmdSigns");
		//this.worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");

		// World Edit stuff
		/*this.localPlayer = null;
		this.worldEdit = this.worldEditPlugin.getWorldEdit();
        this.localSession = new LocalSession(this.worldEdit.getConfiguration());
        this.editSession = new EditSession(new BukkitWorld(this.getServer().getWorld("world")), this.worldEdit.getConfiguration().maxChangeLimit);*/

        // Load shop world and it's protected region
        /*WorldCreator wc = new WorldCreator("shop");
        wc.generator(new CleanroomChunkGenerator("."));
        World world = this.getServer().createWorld(wc);
        this.getServer().getWorlds().add(world);
        this.getgGuardPlugin().getProtectedRegionFromConfig("shop");*/
		
		this.itemGenerator = new RandomItemGenerator(this);

		// Load warzone area
		this.warZoneMinX = this.getConfig().getInt("gfactions.war_zone.min_x");
		this.warZoneMaxX = this.getConfig().getInt("gfactions.war_zone.max_x");
		this.warZoneMinZ = this.getConfig().getInt("gfactions.war_zone.min_z");
		this.warZoneMaxZ = this.getConfig().getInt("gfactions.war_zone.max_z");

		// Load custom configs
		this.scavengeConfig = new ScavengeConfig("scavenge");
		this.strongholdConfig = new StrongholdConfig("stronghold");
        this.superMineConfig = new SuperMineConfig("supermine");

        DeathBanManager.initialize();

		// Load Manhunt stuff
		List<String> manhuntTimes = this.getConfig().getStringList("gfactions.man_hunt.man_hunt_times");
		for (String time : manhuntTimes) {
			DateTime dateTime = DateCommon.parseDateTime(time);
			if (dateTime != null) {
				this.manhuntTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
			} else {
				dateTime = DateCommon.parseDayTime(time);
				if (dateTime != null) {
					this.manhuntTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
				}
			}
		}

		// Load KOTH times
		List<String> kothTimes = this.getConfig().getStringList("gfactions.koth.koth_times");
		for (String time : kothTimes) {
			DateTime dateTime = DateCommon.parseDateTime(time);
			if (dateTime != null) {
				this.kothTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
			} else {
				dateTime = DateCommon.parseDayTime(time);
				if (dateTime != null) {
					this.kothTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
				}
			}
		}

		// Clear entities in the end
		for (Entity entity : this.getServer().getWorld("world_the_end").getEntities()) {
			entity.remove();
		}

		// Register BungeeCord for reboot stuff
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Load lottery stuff
        /*this.lottoTicketPrice = this.getConfig().getInt("gfactions.lotto.ticket_price");
        List<String> lotteryTimes = this.getConfig().getStringList("gfactions.lotto.draw_times");
        for (String time : lotteryTimes) {
            DateTime dateTime = Gberry.parseDateTime(time);
            if (dateTime != null) {
                this.lotteryTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
            } else {
                dateTime = Gberry.parseDayTime(time);
                if (dateTime != null) {
                    this.lotteryTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
                }
            }
        }*/

        // Load Dragon stuff
        /*World w = this.getServer().getWorld("world_the_end");
        List<String> endCrystalLocations = this.getConfig().getStringList("gfactions.dragon.ender_crystal_locations");
        for (String location : endCrystalLocations) {
            String[] coords = location.split(" ");
            this.endCrystalLocations.add(new Location(w, Double.valueOf(coords[0]), Double.valueOf(coords[1]), Double.valueOf(coords[2])));
        }*/

        // Load Parkour times & chests
        /*List<String> parkourTimes = this.getConfig().getStringList("gfactions.parkour.parkour_times");
        for (String time : parkourTimes) {
            DateTime dateTime = Gberry.parseDateTime(time);
            if (dateTime != null) {
                this.parkourTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
            } else {
                dateTime = Gberry.parseDayTime(time);
                if (dateTime != null) {
                    this.parkourTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
                }
            }
        }*/

        // Setup Dungeon config stuff
		//this.dungeonManager.setDungeonsToChooseFrom((ArrayList<String>) this.getConfig().getList("gfactions.dungeon_keys"));
		//this.dungeonManager.loadConfig();
		
		// Listeners
		this.getServer().getPluginManager().registerEvents(new AbuseListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ArcherKitListener(), this);
        this.getServer().getPluginManager().registerEvents(new BanAndReserveSlotListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BeaconListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BloodBowlListener(this), this);
		//this.getServer().getPluginManager().registerEvents(new BountyListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CombatTagListener(), this);
        this.getServer().getPluginManager().registerEvents(new CraftListener(), this);
		//this.getServer().getPluginManager().registerEvents(new DragonListener(this), this);
		//this.getServer().getPluginManager().registerEvents(new DungeonListener(this), this);
		this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
		//this.getServer().getPluginManager().registerEvents(new DuplicateItemListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EnderPearlCDListener(), this);
		this.getServer().getPluginManager().registerEvents(new EndListener(), this);
		this.getServer().getPluginManager().registerEvents(new EXPListener(this), this);
		this.getServer().getPluginManager().registerEvents(new FactionListener(this), this);
		this.getServer().getPluginManager().registerEvents(new GCheatListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GodAppleListener(), this);
		this.getServer().getPluginManager().registerEvents(new GodItemListener(this), this);
		this.getServer().getPluginManager().registerEvents(new KDAListener(this), this);
		this.getServer().getPluginManager().registerEvents(new KickListener(this), this);
        this.getServer().getPluginManager().registerEvents(new KitListener(), this);
		//this.getServer().getPluginManager().registerEvents(new KillStreakListener(this), this);
		this.getServer().getPluginManager().registerEvents(new KOTHListener(this), this);
        this.getServer().getPluginManager().registerEvents(new LimitSpawnerListener(), this);
		this.getServer().getPluginManager().registerEvents(new LootStealListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ManHuntListener(this), this);
		this.getServer().getPluginManager().registerEvents(new MobSpawnerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new NetherListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PotionReverterListener(this), this);
		//this.getServer().getPluginManager().registerEvents(new PropertyListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PunchListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PVPProtectionListener(this), this);
        //this.getServer().getPluginManager().registerEvents(new RegisteredListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RenameSwordListener(), this);
		this.getServer().getPluginManager().registerEvents(new SignListener(this), this);
		this.getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        //this.getServer().getPluginManager().registerEvents(new SkullListener(this), this);
		// EOTW this.getServer().getPluginManager().registerEvents(new SpawnTagListener(this), this);
        //this.getServer().getPluginManager().registerEvents(new SpongeListener(this), this);
		this.getServer().getPluginManager().registerEvents(new StrongholdListener(), this);
        this.getServer().getPluginManager().registerEvents(new TagListener(), this);
        //this.getServer().getPluginManager().registerEvents(new TowerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PreCommandListener(this), this);
		this.getServer().getPluginManager().registerEvents(new TrappedPortalListener(this), this);
		this.getServer().getPluginManager().registerEvents(new VillagerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new VanishListener(), this);
        //this.getServer().getPluginManager().registerEvents(new VoteListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WrenchListener(), this);

		// Commands
		FunModCommand funModCommand = new FunModCommand(this);
        this.getCommand("appletimer").setExecutor(new AppleTimerCommand(this));
		this.getCommand("bb").setExecutor(new BloodBowlCommand(this));
		this.getCommand("boot").setExecutor(funModCommand);
        this.getCommand("doubleore").setExecutor(new DoubleOresCommand());
		this.getCommand("pvptimer").setExecutor(new PVPTimerCommand(this));
		this.getCommand("deenchant").setExecutor(new DeEnchantCommand(this));
        this.getCommand("donator").setExecutor(new DonatorCommand(this));
		this.getCommand("endkoth").setExecutor(new EndKOTHCommand(this));
		this.getCommand("events").setExecutor(new EventsCommand(this));
        this.getCommand("giftlives").setExecutor(new GiftLivesCommand());
        this.getCommand("heartshard").setExecutor(new HeartShardCommand());
		this.getCommand("kothtimes").setExecutor(new KOTHTimesCommand(this));
        this.getCommand("lives").setExecutor(new LivesCommand());
		this.getCommand("manhunt").setExecutor(new ManHuntCommand(this));
		this.getCommand("mod").setExecutor(new ModCommand(this));
		this.getCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
        this.getCommand("scavenge").setExecutor(new ScavengeCommand());
		this.getCommand("seen").setExecutor(new SeenCommand(this));
		this.getCommand("showinvis").setExecutor(new ShowInvisCommand(this));
		this.getCommand("slap").setExecutor(funModCommand);
		this.getCommand("slot").setExecutor(new SlotCommand(this));
		this.getCommand("spanish").setExecutor(new SpanishCommand(this));
		this.getCommand("startkoth").setExecutor(new StartKOTHCommand(this));
		this.getCommand("strike").setExecutor(funModCommand);
		this.getCommand("stronghold").setExecutor(new StrongholdCommand());
		this.getCommand("stuck").setExecutor(new StuckCommand());
        this.getCommand("supermine").setExecutor(new SuperMineCommand());
		this.getCommand("vanish").setExecutor(new VanishCommand());
        this.getCommand("wild").setExecutor(new WildCommand());
        this.getCommand("wrench").setExecutor(new WrenchCommand());
		//this.getCommand("bounty").setExecutor(new BountyCommand(this));
		//this.getCommand("claim").setExecutor(new ClaimCommand(this));
		//this.getCommand("craft").setExecutor(new CraftCommand(this));
		//this.getCommand("dragonevent").setExecutor(new DragonEventCommand(this));
		//this.getCommand("lotto").setExecutor(new LottoTicketsCommand(this));
		//this.getCommand("parkour").setExecutor(new ParkourCommand(this));
		//this.getCommand("pvp").setExecutor(new PVPCommand(this));
		//this.getCommand("ranks").setExecutor(new RanksCommand(this));
		//this.getCommand("topvoters").setExecutor(new TopVotersCommand(this));
		//this.getCommand("vote").setExecutor(new VoteCommand(this));

		// Auto-restart tower tasks
		//new TowerBuilderTask(this).runTaskTimer(this, 600, 20); // every tick

		// Are we recovering from a crash?  Restore the world
		/*if (this.getConfig().getBoolean("gfactions.tower.reload_on_crash")) {
			// Get necessary info
			File towerCrashFile = new File(this.getDataFolder(), "towerBackup");
			int x = this.getConfig().getInt("gfactions.tower.x");
			int y = this.getConfig().getInt("gfactions.tower.y");
			int z = this.getConfig().getInt("gfactions.tower.z");
			
			try {
				this.loadSchematic(towerCrashFile, new Location(this.getServer().getWorld("world"), x, y, z));
			} catch (FilenameException e) {
				e.printStackTrace();
			} catch (DataException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MaxChangedBlocksException e) {
				e.printStackTrace();
			} catch (EmptyClipboardException e) {
				e.printStackTrace();
			}
			
			// Done
			this.getConfig().set("gfactions.tower.reload_on_crash", false);
			this.saveConfig();
			
			this.getLogger().info("Recoverring terrain from GFactions.Tower event.");
		}*/

        // Remove entities in the end in case server crashed with an event

		// Remove any man hunt pressure plate
		if (this.manHuntPP != null) {
			Block block = this.manHuntPP;
			if (block.getType() == Material.STONE_PLATE) {
				block.setType(Material.AIR);
			}
		}

        // Regenerate the area around spawn
        //regenerateAreaNearSpawn();
		
		// Entire system goes off this time setup
		this.checkTimeTask = new CheckTimeTask(this);
		this.checkTimeTask.runTaskTimer(this, 20, 20);
		new RemoveProtectionTask(this).runTaskTimer(this, 20, 20);
		// EOTW new SpawnTagTask(this).runTaskTimer(this, 5, 5); // every other fucking tick
		new EndTagTask(this).runTaskTimer(this, 2, 2); // every other fucking tick
        new KOTHRemoveProtTask(this).runTaskTimer(this, 100, 100); // every 5s
        //new VoteCheckerTask(this).runTask(this);
        new IllegalNetherLocationTask(this).runTaskTimer(this, 100, 200 * 20); // every 200 seconds
        new RoadSpeedTask().runTaskTimer(this, 80, 80);
		new LimitEXPTask().runTaskTimer(this, 40, 40);
		new DoubleOreAnnouncementTask().runTaskTimer(this, 3600L, 3600L);

        // Determine all the time related crud (last thing to do)
		// EOTW this.determineNextRestartTime();
		//this.determineDragonTimes();

        FactionPlayerManager.initialize();
        this.getServer().getPluginManager().registerEvents(new FactionPlayerManager(), this);
	}
	
	@Override
	public void onDisable() {
		// Despawn on disable
		/*if (this.tower != null) {
			this.getLogger().info("Despawning tower onDisable().");
			tower.despawn();
		}*/
		
		// Unload and don't save the dungeon world
		/*if (this.dungeonManager.getCurrentDungeon() != null) {
			// TP people out first
			List<Player> players = this.dungeonManager.getWorld().getPlayers();
			Location location = new Location(Bukkit.getWorld("world"), -8.5, 74, 19.5, 270, 0);
			for (Player player : players) {
				player.teleport(location);
			}
			
			this.getServer().unloadWorld(this.dungeonManager.getCurrentDungeon().getDungeonName(), false);
		}*/

		// Unload Scavenge
		if (this.scavenge != null) {
			for (Location location : this.scavenge.getChests()) {
				Chest chest = (Chest) location.getBlock().getState();
				chest.getInventory().clear();

				chest.getBlock().setType(Material.AIR);
			}
		}

		for (Player player : this.getServer().getOnlinePlayers()) {
			if (player.getLocation().getWorld().getName().equals("bloodbowl")) {
				player.teleport(this.spawnLocation);
			}
		}

        // Unload BloodBowl
		if (this.bloodBowlManager.isRunning()) {
			this.getServer().unloadWorld("bloodbowl", false);
		}

		// Remove any parkour chest
		/*if (this.getParkourChest() != null) {
			Block block = this.getParkourChest().getBlock();
			if (block.getType() == Material.CHEST) {
				block.setType(Material.AIR);
			}
		}*/
	}

	public Location parseLocation(String locationString) {
		String[] components = locationString.split(",");
		if (components.length == 4) {
			String worldName = components[3];
			World world = GFactions.plugin.getServer().getWorld(worldName);
			return new Location(world, Double.parseDouble(components[0]),
					Double.parseDouble(components[1]), Double.parseDouble(components[2]));
		} else if (components.length == 6) {
			String worldName = components[5];
			World world = GFactions.plugin.getServer().getWorld(worldName);
			return new Location(world, Double.parseDouble(components[0]),
					Double.parseDouble(components[1]), Double.parseDouble(components[2]),
					Float.parseFloat(components[3]), Float.parseFloat(components[4]));
		} else {
			throw new RuntimeException("Invalid location string");
		}
	}

    public void regenerateAreaNearSpawn() {
        // Generate the chunks
        World world = this.getServer().getWorld("world");
        for (int x = -10; x < 10; x++) {
            for (int z = -10; z < 10; z++) {
                if (x >= -5 && x < 5 && z >= -5 && z < 5) {
                    continue;
                }

                this.getLogger().info("Regenerating chunk " + x + " " + z);
                world.loadChunk(x, z);
                world.regenerateChunk(x, z);
                world.refreshChunk(x, z);
                world.unloadChunk(x, z);
                world.loadChunk(x, z);
            }
        }

        HashSet<Material> disallowedTypes = new HashSet<Material>();
        disallowedTypes.add(Material.COAL);
        disallowedTypes.add(Material.IRON_ORE);
        disallowedTypes.add(Material.GOLD_ORE);
        disallowedTypes.add(Material.DIAMOND_ORE);
        disallowedTypes.add(Material.REDSTONE_ORE);
        disallowedTypes.add(Material.CHEST);
        disallowedTypes.add(Material.LAPIS_ORE);
        disallowedTypes.add(Material.MOB_SPAWNER);

        // Remove all the ores and valuables
        for (int x = -10; x < 10; x++) {
            for (int z = -10; z < 10; z++) {
                if (x >= -5 && x < 5 && z >= -5 && z < 5) {
                    continue;
                }

                Chunk chunk = world.getChunkAt(x, z);
                for (int i = 0; i < 16; i ++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 256; k++) {
                            Block block = chunk.getBlock(i, k, j);
                            if (disallowedTypes.contains(block.getType())) {
                                this.getLogger().info("Replacing block at " + i + " " + k + " " + j);
                                block.setType(Material.STONE);
                            }
                        }
                    }
                }

                world.refreshChunk(x, z);
                world.unloadChunk(x, z);
                world.loadChunk(x, z);
            }
        }
    }

	
	public void determineNextRestartTime() {
		DateTime date = new DateTime();
		if (date.getHourOfDay() >= 17) {
			// 5 PM
			DateTime restart = new DateTime();
			restart = restart.withHourOfDay(17);
			restart = restart.plusHours(8);
			restart = restart.withMinuteOfHour(0);
			restart = restart.withSecondOfMinute(0);
			this.dateToRestart = restart;

		} else if (date.getHourOfDay() >= 9) {
			// 9 AM
			DateTime restart = new DateTime();
			restart = restart.withHourOfDay(9);
			restart = restart.plusHours(8);
			restart = restart.withMinuteOfHour(0);
			restart = restart.withSecondOfMinute(0);
			this.dateToRestart = restart;
		} else {
			// 1 AM
			DateTime restart = new DateTime();
			restart = restart.withHourOfDay(1);
			restart = restart.plusHours(8);
			restart = restart.withMinuteOfHour(0);
			restart = restart.withSecondOfMinute(0);
			this.dateToRestart = restart;
		}
	}
	
	/*public void addProtectedRegion(World world, Location location1, Location location2, Map flags) {
		ProtectedCuboidRegion pr = new ProtectedCuboidRegion("tower", this.convertToSk89qBV(location1), this.convertToSk89qBV(location2));
		if (flags == null) {
			flags = new HashMap();
			flags.put(DefaultFlag.BUILD, State.DENY);
			flags.put(DefaultFlag.CREEPER_EXPLOSION, State.DENY);
		}
		pr.setFlags(flags);
		try
		{
			rm.save();
		}
	}*/
	
	private BlockVector convertToSk89qBV(Location location) {
		return new BlockVector(location.getX(), location.getY(), location.getZ());
	}
	
	public void loadSchematic(File saveFile, Location loc) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException {
        saveFile = this.worldEdit.getSafeSaveFile(this.localPlayer,
                                      saveFile.getParentFile(), saveFile.getName(),
                                      "schematic");//, new String[] { "schematic" });

        this.editSession.enableQueue();
        this.localSession.setClipboard(SchematicFormat.MCEDIT.load(saveFile));
        this.localSession.getClipboard().place(editSession, new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), false);
        this.editSession.flushQueue();
        this.worldEdit.flushBlockBag(this.localPlayer, this.editSession);
	}
	
	public void saveTerrain(File saveFile, Location l1, Location l2) throws FilenameException, DataException, IOException {
        Vector min = getMin(l1, l2);
        Vector max = getMax(l1, l2);

        saveFile = this.worldEdit.getSafeSaveFile(this.localPlayer,
                                      saveFile.getParentFile(), saveFile.getName(),
                                      "schematic");//, new String[] { "schematic" });

        this.editSession.enableQueue();
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
        clipboard.copy(this.editSession);
        SchematicFormat.MCEDIT.save(clipboard, saveFile);
        this.editSession.flushQueue();
	}
	
	private Vector getMin(Location l1, Location l2) {
        return new Vector(
                          Math.min(l1.getBlockX(), l2.getBlockX()),
                          Math.min(l1.getBlockY(), l2.getBlockY()),
                          Math.min(l1.getBlockZ(), l2.getBlockZ())
                        );
	}
	
	private Vector getMax(Location l1, Location l2) {
        return new Vector(
                          Math.max(l1.getBlockX(), l2.getBlockX()),
                          Math.max(l1.getBlockY(), l2.getBlockY()),
                          Math.max(l1.getBlockZ(), l2.getBlockZ())
                        );
	}
	
	// Hard-coded to allow 0 to 10k
	public String niceLottoTicketNumber(int ticketNumber) {
		StringBuilder niceNumber = new StringBuilder();
		
		niceNumber.append(ticketNumber / 1000);
		niceNumber.append(" ");
		
		while (ticketNumber >= 1000) {
			ticketNumber -= 1000;
		}
		
		niceNumber.append(ticketNumber / 100);
		niceNumber.append(" ");
		
		while (ticketNumber >= 100) {
			ticketNumber -= 100;
		}
		
		niceNumber.append(ticketNumber / 10);
		niceNumber.append(" ");
		
		while (ticketNumber >= 10) {
			ticketNumber -= 10;
		}
		
		niceNumber.append(ticketNumber);
		
		return niceNumber.toString();
	}

	public int generateRandomInt(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}

	@Deprecated // Needs to be updated w/ location checks
	public Location spawnCannon(Player player) {
		int x = this.generateRandomInt(-2000, 2000);
		while (x >= -500 && x <= 500) {
			x = this.generateRandomInt(-2000, 2000);
		}
		
		int z = this.generateRandomInt(-2000, 2000);
		while (z >= -500 && x <= 500) {
			z = this.generateRandomInt(-2000, 2000);
		}
		
		return new Location(this.spawnLocation.getWorld(), x, this.spawnLocation.getWorld().getHighestBlockYAt(x, z), z);
	}
	
	
	public void checkIfPlayerHasProtection(Player player) {
		String query = "SELECT * FROM " + GFactions.PREFIX + "_pvp_protection WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			rs = Gberry.executeQuery(connection, ps);
			
			if (rs.next()) {
				this.mapNameToJoinTime.put(player.getUniqueId().toString(), System.currentTimeMillis());
				this.mapNameToPvPTimeRemaining.put(player.getUniqueId().toString(), rs.getInt("num_of_milliseconds_remaining"));
			} else {
				// We gotta add them man!  PROTECT THE NOOBS
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
	
	public void addProtection(Player player, long secRemaining) {
		String query = "INSERT INTO " + GFactions.PREFIX + "_pvp_protection (uuid, num_of_milliseconds_remaining) VALUES(?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			ps.setInt(2, (int) secRemaining);
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
	
	public void updateProtection(Player player, long secRemaining) {
		//String query = "INSERT INTO faction_pvp_protection (uuid, num_of_milliseconds_remaining) VALUES(?, ?) " +
		//		"ON DUPLICATE KEY UPDATE num_of_milliseconds_remaining = ?;";
		String query = "UPDATE " + GFactions.PREFIX + "_pvp_protection SET num_of_milliseconds_remaining = ? WHERE uuid = ?;\n";
		query += "INSERT INTO " + GFactions.PREFIX + "_pvp_protection (uuid, num_of_milliseconds_remaining) SELECT ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM " + GFactions.PREFIX + "_pvp_protection WHERE uuid = ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, (int) secRemaining);
			ps.setString(2, player.getUniqueId().toString());
			ps.setString(3, player.getUniqueId().toString());
			ps.setInt(4, (int) secRemaining);
			ps.setString(5, player.getUniqueId().toString());
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
	
	public void removeProtection(Player player) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_pvp_protection WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, player.getUniqueId().toString());
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public Map<String, Integer> getMapNameToPvPTimeRemaining() {
		return mapNameToPvPTimeRemaining;
	}

	public Map<String, Long> getMapNameToJoinTime() {
		return mapNameToJoinTime;
	}

	public KOTH getKoth() {
		return koth;
	}

	public void setKoth(KOTH koth) {
		this.koth = koth;
	}

	public int getWarZoneMinX() {
		return warZoneMinX;
	}

	public int getWarZoneMaxX() {
		return warZoneMaxX;
	}

	public int getWarZoneMinZ() {
		return warZoneMinZ;
	}

	public int getWarZoneMaxZ() {
		return warZoneMaxZ;
	}

	public RandomItemGenerator getItemGenerator() {
		return itemGenerator;
	}

	public Tower getTower() {
		return tower;
	}

	public void setTower(Tower tower) {
		this.tower = tower;
	}

	public Gberry getGberry() {
		return gberry;
	}

	public void setGberry(Gberry gberry) {
		this.gberry = gberry;
	}

	public CmdSigns getCmdSigns() {
		return cmdSigns;
	}

	public GGuard getgGuardPlugin() {
		return gGuardPlugin;
	}

	public DateTime getDateToRestart() {
		return dateToRestart;
	}

	public void setDateToRestart(DateTime dateToRestart) {
		this.dateToRestart = dateToRestart;
	}

    public DragonEvent getDragonEvent() {
        return dragonEvent;
    }

    public void setDragonEvent(DragonEvent dragonEvent) {
        this.dragonEvent = dragonEvent;
    }

    public KillDragonTask getKillDragonTask() {
        return killDragonTask;
    }

    public void setKillDragonTask(KillDragonTask killDragonTask) {
        this.killDragonTask = killDragonTask;
    }

    public ArrayList<Location> getEndCrystalLocations() {
        return endCrystalLocations;
    }

	public HashSet<String> getPlayersToBeKicked() {
		return playersToBeKicked;
	}

	public KDAManager getKdaManager() {
		return kdaManager;
	}

	public KillStreakManager getKillStreakManager() {
		return killStreakManager;
	}

	public DungeonManager getDungeonManager() {
		return dungeonManager;
	}

	public BloodBowlManager getBloodBowlManager() {
		return bloodBowlManager;
	}

    public Location getSpawnLocation() {
		return spawnLocation;
	}

	public List<Location> getEndEnterLocations() { return endEnterLocations; }

	public Location getEndExitLocation() {
		return endExitLocation;
	}

	public DonatorManager getDonatorManager() {
		return donatorManager;
	}

	public void setDonatorManager(DonatorManager donatorManager) {
		this.donatorManager = donatorManager;
	}

    public HashSet<String> getPeopleWhoVotedToday() {
        return peopleWhoVotedToday;
    }

    public ArrayList<DateTime> getKothTimes() {
        return kothTimes;
    }

    public ArchMoney getArchMoney() {
        return archMoney;
    }

    public int getLottoTicketPrice() {
        return lottoTicketPrice;
    }

    public ArrayList<DateTime> getLotteryTimes() {
        return lotteryTimes;
    }

    public LottoDrawingTask getLottoDrawingTask() {
        return lottoDrawingTask;
    }

    public void setLottoDrawingTask(LottoDrawingTask lottoDrawingTask) {
        this.lottoDrawingTask = lottoDrawingTask;
    }

	public BattleManager getBattleManager() {
		return battleManager;
	}

	public FactionManager getFactionManager() {
		return factionManager;
	}

    public Chest getParkourChest() {
        return parkourChest;
    }

    public void setParkourChest(Chest parkourChest) {
        this.parkourChest = parkourChest;
    }

    public ArrayList<DateTime> getParkourTimes() {
        return parkourTimes;
    }

	public int getGodAppleTimer() {
		return godAppleTimer;
	}

	public HashMap<String, Long> getGodAppleBlacklist() {
        return godAppleBlacklist;
    }

	public CheckTimeTask getCheckTimeTask() {
		return checkTimeTask;
	}

    public EndKOTHTask getEndKOTHTask() {
        return endKOTHTask;
    }

    public void setEndKOTHTask(EndKOTHTask endKOTHTask) {
        this.endKOTHTask = endKOTHTask;
    }

    public Block getManHuntPP() {
        return manHuntPP;
    }

    public void setManHuntPP(Block manHuntPP) {
        this.manHuntPP = manHuntPP;
    }

    public ArrayList<DateTime> getManhuntTimes() {
        return manhuntTimes;
    }

    public Player getManHuntTagged() {
        return manHuntTagged;
    }

    public void setManHuntTagged(Player manHuntTagged) {
        this.manHuntTagged = manHuntTagged;
    }

    public GPermissions getGperms() {
        return gperms;
    }

	public Stronghold getStronghold() {
		return stronghold;
	}

	public void setStronghold(Stronghold stronghold) {
		this.stronghold = stronghold;
	}

	public StrongholdConfig getStrongholdConfig() {
		return strongholdConfig;
	}

    public SuperMine getSuperMine() {
        return superMine;
    }

    public void setSuperMine(SuperMine superMine) {
        this.superMine = superMine;
    }

    public SuperMineConfig getSuperMineConfig() {
        return superMineConfig;
    }

    public Scavenge getScavenge() {
        return scavenge;
    }

    public void setScavenge(Scavenge scavenge) {
        this.scavenge = scavenge;
    }

    public ScavengeConfig getScavengeConfig() {
        return scavengeConfig;
    }

}
