// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice;

import redis.clients.jedis.Jedis;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import club.minemen.practice.task.PremiumResetTask;
import java.util.TimeZone;
import java.util.Calendar;
import club.minemen.practice.listeners.InventoryListener;
import club.minemen.practice.listeners.ShutdownListener;
import club.minemen.practice.listeners.WorldListener;
import club.minemen.practice.listeners.MatchListener;
import club.minemen.practice.listeners.PlayerListener;
import club.minemen.practice.listeners.EntityListener;
import org.bukkit.event.Listener;
import club.minemen.practice.commands.TournamentCommand;
import club.minemen.practice.commands.warp.WarpCommand;
import club.minemen.practice.commands.management.SpawnsCommand;
import club.minemen.practice.commands.InvCommand;
import club.minemen.practice.commands.EloCommand;
import club.minemen.practice.commands.management.KitCommand;
import club.minemen.practice.commands.time.DayCommand;
import club.minemen.practice.commands.duel.SpecCommand;
import club.minemen.practice.commands.duel.DuelCommand;
import club.minemen.practice.commands.PartyCommand;
import club.minemen.practice.commands.time.NightCommand;
import club.minemen.practice.commands.management.ArenaCommand;
import club.minemen.practice.commands.time.SunsetCommand;
import club.minemen.practice.commands.management.RankedCommand;
import club.minemen.practice.commands.duel.AcceptCommand;
import club.minemen.practice.commands.ResetStatsCommand;
import club.minemen.practice.commands.toggle.ToggleScoreboardCommand;
import club.minemen.practice.commands.toggle.ToggleSpectatorsCommand;
import club.minemen.practice.commands.toggle.ToggleDuelCommand;
import org.bukkit.command.Command;
import java.util.Arrays;
import club.minemen.practice.commands.FlyCommand;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.practice.runnable.ExpBarRunnable;
import org.bukkit.plugin.Plugin;
import club.minemen.practice.runnable.SaveDataRunnable;
import club.minemen.core.redis.subscription.JedisSubscriptionHandler;
import club.minemen.practice.jedis.JedisHandler;
import club.minemen.spigot.handler.MovementHandler;
import club.minemen.practice.handler.CustomMovementHandler;
import club.minemen.spigot.ClubSpigot;
import club.minemen.core.settings.SettingsHandler;
import club.minemen.practice.settings.PracticeSettingsHandler;
import java.util.Collection;
import java.util.Collections;
import club.minemen.practice.commands.PremiumCommand;
import club.minemen.core.board.BoardAdapter;
import club.minemen.core.manager.BoardManager;
import club.minemen.practice.board.PracticeBoard;
import club.minemen.core.server.ServerType;
import club.minemen.core.timer.Timer;
import club.minemen.core.timer.impl.EnderpearlTimer;
import club.minemen.core.CorePlugin;
import club.minemen.core.redis.JedisPublisher;
import com.google.gson.JsonObject;
import club.minemen.core.redis.JedisSubscriber;
import club.minemen.practice.managers.ChunkManager;
import club.minemen.practice.managers.TournamentManager;
import club.minemen.practice.managers.SpawnManager;
import club.minemen.practice.ffa.FFAManager;
import club.minemen.practice.managers.KitManager;
import club.minemen.practice.managers.ItemManager;
import club.minemen.practice.managers.EventManager;
import club.minemen.practice.managers.QueueManager;
import club.minemen.practice.managers.PartyManager;
import club.minemen.practice.managers.MatchManager;
import club.minemen.practice.managers.ArenaManager;
import club.minemen.practice.managers.PlayerManager;
import club.minemen.practice.managers.EditorManager;
import club.minemen.practice.managers.InventoryManager;
import club.minemen.core.util.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin
{
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
    
    public void onDisable() {
        this.arenaManager.saveArenas();
        this.kitManager.saveKits();
        this.spawnManager.saveConfig();
    }
    
    public void onEnable() {
        Practice.instance = this;
        this.mainConfig = new Config("config", (JavaPlugin)this);
        if (CorePlugin.getInstance().getTimerManager().getTimer((Class)EnderpearlTimer.class) == null) {
            CorePlugin.getInstance().getTimerManager().registerTimer((Timer)new EnderpearlTimer());
        }
        CorePlugin.getInstance().getServerManager().setServerType(ServerType.PRACTICE);
        CorePlugin.getInstance().setBoardManager(new BoardManager((BoardAdapter)new PracticeBoard()));
        CorePlugin.getInstance().getCommandManager().registerAllClasses((Collection)Collections.singletonList(new PremiumCommand()));
        CorePlugin.getInstance().getSettingsManager().addSettingsHandler((SettingsHandler)new PracticeSettingsHandler());
        ClubSpigot.INSTANCE.addMovementHandler((MovementHandler)new CustomMovementHandler());
        this.practiceSubscriber = (JedisSubscriber<? extends JsonObject>)new JedisSubscriber(CorePlugin.getInstance().getJedisConfig().toJedisSettings(), "practice", (Class)JsonObject.class, (JedisSubscriptionHandler)new JedisHandler());
        this.practicePublisher = new JedisPublisher(CorePlugin.getInstance().getJedisConfig().toJedisSettings(), "practice");
        this.registerCommands();
        this.registerListeners();
        this.registerManagers();
        this.registerPremiumTimer();
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new SaveDataRunnable(), 6000L, 6000L);
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ExpBarRunnable(), 2L, 2L);
    }
    
    private void registerCommands() {
        CorePlugin.getInstance().getCommandManager().registerAllClasses((Collection)Arrays.asList((CommandHandler)new FlyCommand()));
        Arrays.asList(new ToggleDuelCommand(), new ToggleSpectatorsCommand(), new ToggleScoreboardCommand(), new ResetStatsCommand(), new AcceptCommand(), new RankedCommand(), new SunsetCommand(), new ArenaCommand(), new NightCommand(), new PartyCommand(), new DuelCommand(), new SpecCommand(), new DayCommand(), new KitCommand(), new EloCommand(), new InvCommand(), new SpawnsCommand(), (Command)new WarpCommand(), new TournamentCommand()).forEach(command -> CorePlugin.getInstance().registerCommand(command, this.getName()));
    }
    
    private void registerListeners() {
        Arrays.asList((Listener)new EntityListener(), (Listener)new PlayerListener(), (Listener)new MatchListener(), (Listener)new WorldListener(), (Listener)new ShutdownListener(), (Listener)new InventoryListener()).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, (Plugin)this));
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
    }
    
    private void registerPremiumTimer() {
        if (this.getConfig().getBoolean("parent")) {
            CorePlugin.getInstance().runRedisCommand(redis -> {
                final String lastUpdateTime = redis.get("practice:premium:match_reset");
                if (!lastUpdateTime.isEmpty()) {
                    Long.parseLong(lastUpdateTime);
                }
            });
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("EST"));
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        final Date date = calendar.getTime();
        final java.util.Timer timer = new java.util.Timer();
        timer.schedule(new PremiumResetTask(), date.getTime(), TimeUnit.MILLISECONDS.convert(1L, TimeUnit.DAYS));
    }
    
    public Config getMainConfig() {
        return this.mainConfig;
    }
    
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }
    
    public EditorManager getEditorManager() {
        return this.editorManager;
    }
    
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }
    
    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }
    
    public MatchManager getMatchManager() {
        return this.matchManager;
    }
    
    public PartyManager getPartyManager() {
        return this.partyManager;
    }
    
    public QueueManager getQueueManager() {
        return this.queueManager;
    }
    
    public EventManager getEventManager() {
        return this.eventManager;
    }
    
    public ItemManager getItemManager() {
        return this.itemManager;
    }
    
    public KitManager getKitManager() {
        return this.kitManager;
    }
    
    public FFAManager getFfaManager() {
        return this.ffaManager;
    }
    
    public SpawnManager getSpawnManager() {
        return this.spawnManager;
    }
    
    public TournamentManager getTournamentManager() {
        return this.tournamentManager;
    }
    
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }
    
    public JedisSubscriber<? extends JsonObject> getPracticeSubscriber() {
        return this.practiceSubscriber;
    }
    
    public JedisPublisher getPracticePublisher() {
        return this.practicePublisher;
    }
    
    public static Practice getInstance() {
        return Practice.instance;
    }
}
