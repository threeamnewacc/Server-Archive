package club.minemen.practice;

import club.minemen.core.CorePlugin;
import club.minemen.core.manager.BoardManager;
import club.minemen.core.redis.JedisPublisher;
import club.minemen.core.redis.JedisSubscriber;
import club.minemen.core.server.ServerType;
import club.minemen.core.timer.impl.EnderpearlTimer;
import club.minemen.core.util.Config;
import club.minemen.practice.board.PracticeBoard;
import club.minemen.practice.commands.EloCommand;
import club.minemen.practice.commands.FlyCommand;
import club.minemen.practice.commands.InvCommand;
import club.minemen.practice.commands.PartyCommand;
import club.minemen.practice.commands.PremiumCommand;
import club.minemen.practice.commands.ResetStatsCommand;
import club.minemen.practice.commands.TournamentCommand;
import club.minemen.practice.commands.duel.AcceptCommand;
import club.minemen.practice.commands.duel.DuelCommand;
import club.minemen.practice.commands.duel.SpecCommand;
import club.minemen.practice.commands.management.ArenaCommand;
import club.minemen.practice.commands.management.KitCommand;
import club.minemen.practice.commands.management.RankedCommand;
import club.minemen.practice.commands.management.SpawnsCommand;
import club.minemen.practice.commands.time.DayCommand;
import club.minemen.practice.commands.time.NightCommand;
import club.minemen.practice.commands.time.SunsetCommand;
import club.minemen.practice.commands.toggle.ToggleDuelCommand;
import club.minemen.practice.commands.toggle.ToggleScoreboardCommand;
import club.minemen.practice.commands.toggle.ToggleSpectatorsCommand;
import club.minemen.practice.commands.warp.WarpCommand;
import club.minemen.practice.ffa.FFAManager;
import club.minemen.practice.handler.CustomMovementHandler;
import club.minemen.practice.jedis.JedisHandler;
import club.minemen.practice.listeners.EntityListener;
import club.minemen.practice.listeners.InventoryListener;
import club.minemen.practice.listeners.MatchListener;
import club.minemen.practice.listeners.PlayerListener;
import club.minemen.practice.listeners.ShutdownListener;
import club.minemen.practice.listeners.WorldListener;
import club.minemen.practice.managers.ArenaManager;
import club.minemen.practice.managers.ChunkManager;
import club.minemen.practice.managers.EditorManager;
import club.minemen.practice.managers.EventManager;
import club.minemen.practice.managers.InventoryManager;
import club.minemen.practice.managers.ItemManager;
import club.minemen.practice.managers.KitManager;
import club.minemen.practice.managers.MatchManager;
import club.minemen.practice.managers.PartyManager;
import club.minemen.practice.managers.PlayerManager;
import club.minemen.practice.managers.QueueManager;
import club.minemen.practice.managers.SpawnManager;
import club.minemen.practice.managers.TournamentManager;
import club.minemen.practice.runnable.ExpBarRunnable;
import club.minemen.practice.runnable.SaveDataRunnable;
import club.minemen.practice.settings.PracticeSettingsHandler;
import club.minemen.practice.task.PremiumResetTask;
import club.minemen.spigot.ClubSpigot;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Practice extends JavaPlugin {

	@Getter
	private static Practice instance;

	private Config mainConfig;

	private InventoryManager inventoryManager;
	private EditorManager editorManager;
	private PlayerManager playerManager;
	private ArenaManager arenaManager;
	private MatchManager matchManager;
	private PartyManager partyManager;
	private QueueManager queueManager;
	private EventManager eventManager;
	private ItemManager itemManager;
	private KitManager kitManager;
	private FFAManager ffaManager;
	private SpawnManager spawnManager;
	private TournamentManager tournamentManager;
	private ChunkManager chunkManager;

	private JedisSubscriber<? extends JsonObject> practiceSubscriber;
	private JedisPublisher practicePublisher;

	@Override
	public void onDisable() {
		this.arenaManager.saveArenas();
		this.kitManager.saveKits();
		this.spawnManager.saveConfig();
	}

	@Override
	public void onEnable() {
		Practice.instance = this;

		this.mainConfig = new Config("config", this);

		if (CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class) == null) {
			CorePlugin.getInstance().getTimerManager().registerTimer(new EnderpearlTimer());
		}
		CorePlugin.getInstance().getServerManager().setServerType(ServerType.PRACTICE);
		CorePlugin.getInstance().setBoardManager(new BoardManager(new PracticeBoard()));
		CorePlugin.getInstance().getCommandManager().registerAllClasses(Collections.singletonList(
				new PremiumCommand()
		));
		CorePlugin.getInstance().getSettingsManager().addSettingsHandler(new PracticeSettingsHandler());

		ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

		this.practiceSubscriber = new JedisSubscriber<>(CorePlugin.getInstance().getJedisConfig().toJedisSettings(),
				"practice", JsonObject.class, new JedisHandler());
		this.practicePublisher = new JedisPublisher(CorePlugin.getInstance().getJedisConfig().toJedisSettings(),
				"practice");

		this.registerCommands();
		this.registerListeners();
		this.registerManagers();
		this.registerPremiumTimer();

		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(),
				20L * 60L * 5L, 20L * 60L * 5L);

		this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);

		//this.getServer().getScheduler().runTaskTimer(this, new ItemDespawnRunnable(this)
		//		, 20L * 5L, 20L);
	}

	private void registerCommands() {
		CorePlugin.getInstance().getCommandManager().registerAllClasses(Arrays.asList(
				new FlyCommand()
				//new HostCommand(),
				//new JoinEventCommand()
		));

		Arrays.asList(
				new ToggleDuelCommand(),
				new ToggleSpectatorsCommand(),
				new ToggleScoreboardCommand(),
				new ResetStatsCommand(),
				new AcceptCommand(),
				new RankedCommand(),
				new SunsetCommand(),
				new ArenaCommand(),
				new NightCommand(),
				new PartyCommand(),
				new DuelCommand(),
				new SpecCommand(),
				new DayCommand(),
				new KitCommand(),
				new EloCommand(),
				new InvCommand(),
				new SpawnsCommand(),
				new WarpCommand(),
				new TournamentCommand()
		).forEach(command -> CorePlugin.getInstance().registerCommand(command, getName()));
	}

	private void registerListeners() {
		Arrays.asList(
				new EntityListener(),
				new PlayerListener(),
				new MatchListener(),
				new WorldListener(),
				new ShutdownListener(),
				new InventoryListener()
		).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
	}

	private void registerManagers() {
		this.spawnManager = new SpawnManager();
		this.arenaManager = new ArenaManager();
		this.chunkManager = new ChunkManager();
		this.editorManager = new EditorManager();
		this.itemManager = new ItemManager();
		this.kitManager = new KitManager();
		this.matchManager = new MatchManager();
		this.partyManager = new PartyManager();
		this.playerManager = new PlayerManager();
		this.queueManager = new QueueManager();
		this.inventoryManager = new InventoryManager();
		this.eventManager = new EventManager();
		this.tournamentManager = new TournamentManager();
		/*
		this.ffaManager = new FFAManager(this, CustomLocation.fromBukkitLocation(LocationUtil.SPAWN),
				this.kitManager.getKit("SoupRefill"));
				*/
	}

	private void registerPremiumTimer() {
		if (this.getConfig().getBoolean("parent")) {
			CorePlugin.getInstance().runRedisCommand(redis -> {
				String lastUpdateTime = redis.get("practice:premium:match_reset");

				if (!lastUpdateTime.isEmpty()) {
					long lastTime = Long.parseLong(lastUpdateTime);
				}

			});
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("EST"));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date date = calendar.getTime();

		Timer timer = new Timer();
		timer.schedule(new PremiumResetTask(), date.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
	}
}
