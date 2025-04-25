package cc.patrone.practice;

import cc.patrone.practice.commands.InventoryCommand;
import cc.patrone.practice.commands.LeaderboardCommand;
import cc.patrone.practice.commands.PartyCommand;
import cc.patrone.practice.commands.SettingsCommand;
import cc.patrone.practice.commands.StatsCommand;
import cc.patrone.practice.commands.management.ArenaCommand;
import cc.patrone.practice.commands.management.KitCommand;
import cc.patrone.practice.commands.management.LocationCommand;
import cc.patrone.practice.commands.management.SetEloCommand;
import cc.patrone.practice.commands.match.AcceptCommand;
import cc.patrone.practice.commands.match.DuelCommand;
import cc.patrone.practice.commands.match.SpectateCommand;
import cc.patrone.practice.commands.time.DayCommand;
import cc.patrone.practice.commands.time.NightCommand;
import cc.patrone.practice.commands.time.SunsetCommand;
import cc.patrone.practice.commands.toggle.ToggleDuelRequestsCommand;
import cc.patrone.practice.commands.toggle.TogglePlayerVisibilityCommand;
import cc.patrone.practice.commands.toggle.ToggleRankedCommand;
import cc.patrone.practice.commands.toggle.ToggleScoreboardCommand;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.listeners.EntityListener;
import cc.patrone.practice.listeners.InventoryListener;
import cc.patrone.practice.listeners.PlayerListener;
import cc.patrone.practice.listeners.ServerListener;
import cc.patrone.practice.listeners.WorldListener;
import cc.patrone.practice.managers.ArenaManager;
import cc.patrone.practice.managers.EditorManager;
import cc.patrone.practice.managers.LeaderboardManager;
import cc.patrone.practice.managers.LocationManager;
import cc.patrone.practice.managers.MatchManager;
import cc.patrone.practice.managers.MenuManager;
import cc.patrone.practice.managers.PartyManager;
import cc.patrone.practice.managers.PlayerManager;
import cc.patrone.practice.managers.QueueManager;
import cc.patrone.practice.managers.SnapshotManager;
import cc.patrone.practice.managers.SpectatorManager;
import cc.patrone.scoreboardapi.api.CustomScoreboard;
import java.util.Arrays;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PracticePlugin extends JavaPlugin {

	@Getter
	private static PracticePlugin instance;

	private LocationManager locationManager;
	private ArenaManager arenaManager;
	private EditorManager editorManager;
	private MatchManager matchManager;
	private PartyManager partyManager;
	private PlayerManager playerManager;
	private QueueManager queueManager;
	private SnapshotManager snapshotManager;
	private SpectatorManager spectatorManager;
	private MenuManager menuManager;
	private LeaderboardManager leaderboardManager;

	private CustomScoreboard customScoreboard;

	@Override
	public void onEnable() {
		instance = this;

		locationManager = new LocationManager(this);
		arenaManager = new ArenaManager(this);
		editorManager = new EditorManager(this);
		matchManager = new MatchManager(this);
		partyManager = new PartyManager(this);
		playerManager = new PlayerManager(this);
		queueManager = new QueueManager(this);
		snapshotManager = new SnapshotManager(this);
		spectatorManager = new SpectatorManager(this);
		menuManager = new MenuManager(this);
		leaderboardManager = new LeaderboardManager(this);

		registerCommands(
				new DayCommand(this),
				new NightCommand(this),
				new SunsetCommand(this),
				new StatsCommand(this),
				new KitCommand(this),
				new ArenaCommand(this),
				new LocationCommand(this),
				new PartyCommand(this),
				new InventoryCommand(snapshotManager),
				new DuelCommand(this),
				new AcceptCommand(this),
				new SpectateCommand(this),
				new ToggleDuelRequestsCommand(this),
				new ToggleScoreboardCommand(this),
				new TogglePlayerVisibilityCommand(this),
				new ToggleRankedCommand(this),
				new LeaderboardCommand(this),
				new SettingsCommand(this),
				new SetEloCommand(this)
		);

		registerListeners(
				new EntityListener(this),
				new InventoryListener(this),
				new PlayerListener(this),
				new WorldListener(),
				new ServerListener(this)
		);

		World world = getServer().getWorlds().get(0);

		world.setTime(6000L);

		disableGameRules(world,
				"doDaylightCycle",
				"doEntityDrops",
				"doMobSpawning",
				"doFireTick",
				"showDeathMessages"
		);

		customScoreboard = new CustomScoreboard(this, 10);

		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> leaderboardManager.updateALl(), 20 * 5L, 20 * 60L);
	}

	private void disableGameRules(World world, String... gameRules) {
		for (String gameRule : gameRules) {
			world.setGameRuleValue(gameRule, "false");
		}
	}

	@Override
	public void onDisable() {
		playerManager.saveProfiles();
		locationManager.saveLocations();
		arenaManager.saveArenas();

		Arrays.stream(Kit.values()).forEach(Kit::save);

		World world = getServer().getWorlds().get(0);

		for (Entity entity : world.getEntities()) {
			if (entity instanceof Player || entity instanceof ItemFrame) {
				continue;
			}

			entity.remove();
		}
	}
}
