package me.devkevin.practice;

import club.inverted.chatcolor.CC;
import com.bizarrealex.aether.Aether;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.arena.chunk.listener.ArenaPlayerChunkLimiter;
import me.devkevin.practice.arena.chunk.manager.ChunkManager;
import me.devkevin.practice.arena.chunk.manager.ChunkRestorationManager;
import me.devkevin.practice.arena.command.ArenaChunkCommands;
import me.devkevin.practice.arena.command.ArenaCommand;
import me.devkevin.practice.arena.manager.ArenaManager;
import me.devkevin.practice.board.PracticeBoard;
import me.devkevin.practice.command.general.*;
import me.devkevin.practice.command.requeue.PlayAgainCommand;
import me.devkevin.practice.command.staff.CancelMatchCommand;
import me.devkevin.practice.command.staff.FollowCommand;
import me.devkevin.practice.command.staff.OngoingMatchesCommand;
import me.devkevin.practice.command.staff.StaffModeCommand;
import me.devkevin.practice.command.stats.ResetStatsCommand;
import me.devkevin.practice.command.stats.SetStatsCommand;
import me.devkevin.practice.command.time.DayCommand;
import me.devkevin.practice.command.time.NightCommand;
import me.devkevin.practice.command.time.SunsetCommand;
import me.devkevin.practice.data.PracticeDatabase;
import me.devkevin.practice.events.commands.*;
import me.devkevin.practice.events.manager.EventManager;
import me.devkevin.practice.events.menu.HostMenu;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.general.GeneralSettingMenu;
import me.devkevin.practice.hcf.classes.Archer;
import me.devkevin.practice.hcf.classes.Bard;
import me.devkevin.practice.hcf.effects.EffectRestorer;
import me.devkevin.practice.hcf.listener.HCFClassListener;
import me.devkevin.practice.hcf.listener.HCFMatchListener;
import me.devkevin.practice.hcf.manager.HCFManager;
import me.devkevin.practice.kit.command.KitManageCommand;
import me.devkevin.practice.kit.command.KitsCommand;
import me.devkevin.practice.kit.listener.KitEditorError32Listener;
import me.devkevin.practice.kit.manager.EditorManager;
import me.devkevin.practice.kit.manager.KitManager;
import me.devkevin.practice.kit.menu.KitEditorMenu;
import me.devkevin.practice.leaderboard.LeaderboardManager;
import me.devkevin.practice.leaderboard.hologram.HologramManager;
import me.devkevin.practice.leaderboard.npc.NPCRunnable;
import me.devkevin.practice.location.manager.CustomLocationManager;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.duel.command.AcceptCommand;
import me.devkevin.practice.match.duel.command.DuelCommand;
import me.devkevin.practice.match.duel.menu.DuelMenu;
import me.devkevin.practice.match.history.MatchLocatedData;
import me.devkevin.practice.match.history.command.MatchHistoryCommand;
import me.devkevin.practice.match.listener.entity.*;
import me.devkevin.practice.match.listener.game.MatchEndListener;
import me.devkevin.practice.match.listener.game.MatchStartListener;
import me.devkevin.practice.match.listener.time.MatchDurationLimitListener;
import me.devkevin.practice.match.manager.MatchManager;
import me.devkevin.practice.match.menu.MatchDetailSnapshot;
import me.devkevin.practice.match.menu.command.InvCommand;
import me.devkevin.practice.match.spec.SpectateCommand;
import me.devkevin.practice.match.timer.TimerManager;
import me.devkevin.practice.match.timer.impl.EnderpearlTimer;
import me.devkevin.practice.match.vote.VoteManager;
import me.devkevin.practice.match.vote.commands.ArenaStatsCommand;
import me.devkevin.practice.match.vote.commands.RateCommand;
import me.devkevin.practice.options.command.OptionsCommand;
import me.devkevin.practice.options.listener.ProfileOptionsListeners;
import me.devkevin.practice.panel.commands.impl.*;
import me.devkevin.practice.party.command.*;
import me.devkevin.practice.party.manager.PartyManager;
import me.devkevin.practice.party.menu.PartyMenu;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.hotbar.HotbarItem;
import me.devkevin.practice.profile.listener.*;
import me.devkevin.practice.profile.manager.ProfileManager;
import me.devkevin.practice.profile.menu.StatsMenu;
import me.devkevin.practice.profile.task.DataLoaderWorkerRunnable;
import me.devkevin.practice.profile.task.ExpBarTask;
import me.devkevin.practice.profile.task.ProfileSaveDataTask;
import me.devkevin.practice.queue.Queue;
import me.devkevin.practice.queue.menu.party.PartyQueueJoinMenu;
import me.devkevin.practice.queue.menu.party.PlayJoinMenu;
import me.devkevin.practice.queue.menu.play.QueueMenu;
import me.devkevin.practice.staff.StaffMode;
import me.devkevin.practice.tournament.command.TournamentCommand;
import me.devkevin.practice.tournament.host.TournamentHostMenu;
import me.devkevin.practice.tournament.manager.TournamentManager;
import me.devkevin.practice.util.GsonFactory;
import me.devkevin.practice.util.command.CommandFramework;
import me.devkevin.practice.util.inventory.UIListener;
import me.devkevin.practice.util.menu.ButtonListener;
import me.devkevin.practice.util.menu.MenuUpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getPluginManager;

/**
 * Copyright (c) Kevin Acaymo (DevKevin) 2020.
 * You may not copy, re-sell, distribute, modify,
 * or use any code contained in this document or file,
 * collection of documents or files, or project. Thank you.
 */
@Getter
public class Practice extends JavaPlugin {

    @Getter private static Practice instance;

    public static final int RECORDING_VERSION = 1;
    public static final int ENTITY_ID_OFFSET = 1000;

    public static Random RANDOM = new Random();
    private final JsonParser jsonParser = new JsonParser();
    public static Gson GSON;

    private PracticeDatabase practiceDatabase;

    private Config mainConfig;
    private CommandFramework framework;

    private ProfileManager profileManager;
    private KitManager kitManager;
    private ArenaManager arenaManager;
    private CustomLocationManager customLocationManager;
    private ChunkManager chunkManager;
    private HotbarItem hotbarItem;
    private Queue queue;
    private MatchManager matchManager;
    private MatchDetailSnapshot matchDetailSnapshot;
    private DuelMenu duelMenu;
    private TimerManager timerManager;
    private PartyMenu partyMenu;
    private PartyManager partyManager;
    private PartyQueueJoinMenu partyQueueJoinMenu;
    private EventManager eventManager;
    private HostMenu hostMenu;
    private StaffMode staffMode;
    private ChunkRestorationManager chunkRestorationManager;
    private MatchLocatedData matchLocatedData;
    private DataLoaderWorkerRunnable dataLoaderWorkerRunnable;
    private TournamentManager tournamentManager;
    private TournamentHostMenu tournamentHostMenu;
    private PlayJoinMenu playJoinMenu;
    private LeaderboardManager leaderboardManager;
    private GeneralSettingMenu generalSettingMenu;
    private QueueMenu queueMenu;
    private KitEditorMenu kitEditorMenu;
    private EditorManager editorManager;
    private VoteManager voteManager;
    private StatsMenu statsMenu;
    private HCFManager hcfManager;
    private EffectRestorer effectRestorer;
    private HologramManager hologramManager;

    @Setter private boolean restarting = false;

    private Aether board;

    private String serverName, networkWebsite;

    private long leaderboardUpdateTime;

    @Override
    public void onEnable() {
        instance = this;

        if (!Practice.getInstance().getDescription().getAuthors().contains("DevKevin") || !Practice.getInstance().getDescription().getName().equals("Practice")) {
            getPluginManager().disablePlugin(this);
        }

        Bukkit.getConsoleSender().sendMessage(CC.STRIKETHROUGH);
        Bukkit.getConsoleSender().sendMessage(CC.translate("&4Practice &8- &fv" + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7Made by &6DevKevin"));
        Bukkit.getConsoleSender().sendMessage(CC.STRIKETHROUGH);

        leaderboardUpdateTime = (15 * 60L) * 20L; // The time is calculated in minutes

        mainConfig = new Config("config", this);
        practiceDatabase = new PracticeDatabase(this);
        framework = new CommandFramework(this);
        GSON = GsonFactory.getPrettyGson();

        serverName = "Prac";
        networkWebsite = "prac.lol";

        this.matchLocatedData = new MatchLocatedData();

        // setup board, nametag,
        this.board = new Aether(this, new PracticeBoard());

        disableLoggers();

        // register managers
        customLocationManager = new CustomLocationManager();
        chunkRestorationManager = new ChunkRestorationManager();
        arenaManager = new ArenaManager();
        chunkManager = new ChunkManager();
        kitManager = new KitManager();
        matchManager = new MatchManager();
        hotbarItem = new HotbarItem();
        matchDetailSnapshot = new MatchDetailSnapshot();
        duelMenu = new DuelMenu();
        timerManager = new TimerManager(this);
        partyMenu = new PartyMenu();
        partyManager = new PartyManager();
        partyQueueJoinMenu = new PartyQueueJoinMenu();
        profileManager = new ProfileManager();
        queue = new Queue();
        eventManager = new EventManager();
        hostMenu = new HostMenu();
        staffMode = new StaffMode();
        tournamentManager = new TournamentManager();
        tournamentHostMenu =  new TournamentHostMenu();
        leaderboardManager = new LeaderboardManager(this);
        queueMenu = new QueueMenu();
        generalSettingMenu = new GeneralSettingMenu();
        kitEditorMenu = new KitEditorMenu();
        editorManager = new EditorManager();
        voteManager = new VoteManager(this);
        statsMenu = new StatsMenu();
        effectRestorer = new EffectRestorer();
        hcfManager = new HCFManager();
        hologramManager = new HologramManager(this);

        if (this.timerManager.getTimer(EnderpearlTimer.class) == null) this.timerManager.registerTimer(new EnderpearlTimer());

        List<Listener> listeners = Arrays.asList(
                new EntityListener(),
                new ProfileListener(),
                new WorldListener(),
                new UIListener(),
                new ButtonListener(),
                new MatchStartListener(),
                new MatchEndListener(),
                new PotionMatchListener(),
                new MatchDurationLimitListener(),
                new MatchRodListener(),
                new TabCompleteListener(),
                new ProfileOptionsListeners(),
                new InventoryListener(),
                //new FreezeListener(),
                new PlayerTabCompleteFix(),
                new KitEditorError32Listener(),
                new SwordBlockDetector(),
                new ArenaPlayerChunkLimiter(this),
                new MovementListener(this),
                new BowBoostingListener(),
                new HCFMatchListener(),
                new HCFClassListener(hcfManager),
                new Bard(), new Archer()
        );

        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        // register commands
        new SetSpawnCommand();
        new SetEditorSpawnCommand();

        // kit related
        new KitManageCommand();
        new KitsCommand();

        // arena related
        new ArenaCommand();
        new ArenaChunkCommands();

        new InvCommand();
        new FlyCommand();

        new DuelCommand();
        new AcceptCommand();
        new SpectateCommand();

        // party related
        new PartyInviteCommand();
        new PartyAcceptCommand();
        new PartyHelpCommand();
        new PartyCreateCommand();
        new PartyLeaveCommand();
        new PartyOpenCommand();
        new PartyInfoCommand();
        new PartyListCommand();
        new PartyJoinCommand();
        new PartyKickCommand();
        new PartyLimitCommand();

        new EventManagerCommand();
        new HostCommand();
        new HostEventCommand();
        new JoinEventCommand();
        new SetEventSpawnCommand();
        new LeaveEventCommand();

        new OptionsCommand();

        new DayCommand();
        new NightCommand();
        new SunsetCommand();

        new ResetLeaderboardsCommand();
        new ResetStatsCommand();
        new SetStatsCommand();

        // Staff Mode
        new StaffModeCommand();
        new FollowCommand();
        new CancelMatchCommand();
        new OngoingMatchesCommand();

        // Match history
        new MatchHistoryCommand();

        // Practice Panel stuff
        new DebugBlockedCommands();
        new ForceChunkSaveCommand();
        new ForceGlobalEloUpdateCommand();
        new ForceQueueCommand();
        new GlobalEloDebugCommand();
        new GlobalQueueDebugCommand();
        new GotoEventCommand();
        new GotoEventCommand();
        new PartyDebugCommand();
        new PlayerStateDebugCommand();
        new SafeStopCommand();
        new SetMatchesPlayedCommand();

        new TournamentCommand();

        new PlayAgainCommand();

        new RateCommand();
        new ArenaStatsCommand();

        new StatsCommand();

        // register schedulers
        dataLoaderWorkerRunnable = new DataLoaderWorkerRunnable();
        Thread thread = new Thread(dataLoaderWorkerRunnable);
        thread.setName("[Practice] Data Loader Worker");
        thread.start();

        getServer().getScheduler().runTaskTimerAsynchronously(this, new ProfileSaveDataTask(), 20L * 60L * 5L, 20L * 60L * 5L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new MenuUpdateTask(), 5L, 5L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarTask(), 2L, 2L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            getLeaderboardManager().updateLeaderboards();
        }, 20L, getLeaderboardUpdateTime());


        getServer().getScheduler().runTaskTimerAsynchronously(this, new NPCRunnable(this), 300L, 300L);

        new PracticeCache().start();
    }

    @Override
    public void onDisable() {
        for (Profile profile : profileManager.getAllData()) {
            profileManager.saveData(profile);
        }

        for (Map.Entry<UUID, Match> entry : matchManager.getMatches().entrySet()) {
            Match match = entry.getValue();
            if (match.getKit().isBuild() || match.getKit().isSpleef()) {
                ChunkRestorationManager.getIChunkRestoration().reset(match.getStandaloneArena());
            }
        }

        for (Entity entity : this.getServer().getWorld("world").getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }

        for (Chunk chunk : this.getServer().getWorld("world").getLoadedChunks()) {
            chunk.unload(true);
        }

        getServer().getConsoleSender().sendMessage(CC.translate("&8[&6Practice&8] &cUnloading chunks..."));

        this.arenaManager.saveArenas();
        this.kitManager.saveKits();
        this.customLocationManager.saveConfig();
        this.practiceDatabase.getClient().close();
    }

    private void removeCrafting(Material material) {
        Iterator<Recipe> iterator = getServer().recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe != null && recipe.getResult().getType() == material) {
                iterator.remove();
            }
        }
    }

    private void disableLoggers() {
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
    }

    /**
     * Runs the given runnable asynchronously
     * This method is usually used in mongo and
     * other non-bukkit related tasks
     *
     * @param runnable {@link Runnable} the runnable
     */
    public void submitToThread(Runnable runnable) {
        ForkJoinPool.commonPool().execute(runnable);
    }
}
