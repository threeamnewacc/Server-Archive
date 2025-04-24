package me.devkevin.landcore;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.devkevin.landcore.commands.impl.*;
import me.devkevin.landcore.commands.impl.staff.*;
import me.devkevin.landcore.commands.impl.staff.punish.*;
import me.devkevin.landcore.commands.impl.toggle.ToggleGlobalChat;
import me.devkevin.landcore.commands.impl.toggle.ToggleMessagesCommand;
import me.devkevin.landcore.commands.impl.toggle.ToggleSoundsCommand;
import me.devkevin.landcore.disguise.commands.DisguiseCommand;
import me.devkevin.landcore.disguise.manager.DisguiseManager;
import me.devkevin.landcore.disguise.commands.UndisguiseCommand;
import me.devkevin.landcore.disguise.menu.DisguiseMenu;
import me.devkevin.landcore.faction.commands.FactionHelpCommand;
import me.devkevin.landcore.faction.commands.captain.*;
import me.devkevin.landcore.faction.commands.leader.FactionDescriptionCommand;
import me.devkevin.landcore.faction.commands.leader.FactionDisbandCommand;
import me.devkevin.landcore.faction.commands.player.*;
import me.devkevin.landcore.faction.listener.FactionListener;
import me.devkevin.landcore.faction.manager.FactionManager;
import me.devkevin.landcore.gson.CustomLocationTypeAdapterFactory;
import me.devkevin.landcore.gson.ItemStackTypeAdapterFactory;
import me.devkevin.landcore.listeners.*;
import me.devkevin.landcore.listeners.redis.*;
import me.devkevin.landcore.managers.MenuManager;
import me.devkevin.landcore.managers.PlayerManager;
import me.devkevin.landcore.managers.ProfileManager;
import me.devkevin.landcore.managers.StaffManager;
import me.devkevin.landcore.nametag.NameTagAdapter;
import me.devkevin.landcore.nametag.impl.InternalNametag;
import me.devkevin.landcore.player.color.ColorCommand;
import me.devkevin.landcore.player.color.SetColorCommand;
import me.devkevin.landcore.player.color.menu.ColorMenu;
import me.devkevin.landcore.player.grant.procedure.GrantProcedureListener;
import me.devkevin.landcore.player.info.UserCommand;
import me.devkevin.landcore.player.notes.commands.NoteAddCommand;
import me.devkevin.landcore.player.notes.commands.NoteRemoveCommand;
import me.devkevin.landcore.player.notes.commands.NotesCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.player.rank.commands.*;
import me.devkevin.landcore.player.tags.PrefixCommand;
import me.devkevin.landcore.player.tags.menu.PrefixMenu;
import me.devkevin.landcore.punishment.listener.PunishmentListener;
import me.devkevin.landcore.redis.RedisMessenger;
import me.devkevin.landcore.server.ServerSettings;
import me.devkevin.landcore.server.filter.Filter;
import me.devkevin.landcore.storage.database.MongoStorage;
import me.devkevin.landcore.store.StorePCoinMenu;
import me.devkevin.landcore.task.BroadcastTask;
import me.devkevin.landcore.task.GrantDisguiseCheckTask;
import me.devkevin.landcore.utils.inventory.UIListener;
import me.devkevin.landcore.utils.menu.ButtonListener;
import me.devkevin.landcore.utils.menu.MenuUpdateTask;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.packet.PacketAdapter;
import me.devkevin.landcore.utils.packet.PacketListener;
import me.devkevin.landcore.utils.structure.Cuboid;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class LandCore extends JavaPlugin {
    @Getter
    private static LandCore instance;

    public static GsonBuilder GSONBUILDER;
    public static Gson GSON;
    public static final Random RANDOM = new Random();

    private ServerSettings serverSettings;
    private Filter filter;
    private MongoStorage mongoStorage;

    private ProfileManager profileManager;
    private StaffManager staffManager;
    private PlayerManager playerManager;
    private MenuManager menuManager;
    private RedisMessenger redisMessenger;
    private static Field bukkitCommandMap;
    private PrefixMenu prefixMenu;
    private ColorMenu colorMenu;
    private DisguiseMenu disguiseMenu;
    private DisguiseManager disguiseManager;
    private PacketListener packetListener;
    private FactionManager factionManager;
    private StorePCoinMenu storePCoinMenu;

    private static void registerSerializableClass(Class<?> clazz) {
        if (ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
            ConfigurationSerialization.registerClass(serializable);
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        LandCore.GSONBUILDER = new GsonBuilder()
                .registerTypeAdapterFactory(new CustomLocationTypeAdapterFactory())
                .registerTypeAdapterFactory(new ItemStackTypeAdapterFactory());
        LandCore.GSON = LandCore.GSONBUILDER.create();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        saveDefaultConfig();

        registerSerializableClass(Cuboid.class);

        serverSettings = new ServerSettings(this);
        filter = new Filter();

        redisMessenger = new RedisMessenger(
                this,
                getConfig().getString("redis.host"),
                getConfig().getInt("redis.port"),
                getConfig().getInt("redis.timeout"),
                getConfig().getString("redis.password")
        );

        redisMessenger.registerListeners(
                new FreezeListener(this),
                new FrozenDisconnectListener(this),
                new HelpopListener(this),
                new ReportListener(this),
                new StaffChatListener(this),
                new StaffStreamListener(this),
                new RedisServerMonitorListener(this),
                new RedisFactionListener(this)
        );

        redisMessenger.initialize();
        mongoStorage = new MongoStorage(this);

        this.disableLoggers();
        this.addServerMonitor();

        Bukkit.getConsoleSender().sendMessage(CC.SEPARATOR);
        Bukkit.getConsoleSender().sendMessage(CC.translate("&eLandCore &8- &b" + getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b • &eDeveloper: &eDevKevin"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b • &eMongo: &f" + (mongoStorage.isConnected() ? "&aenabled" : "&cdisabled")));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b • &eRedis: &f" + (redisMessenger.isActive() ? "&aenabled" : "&cdisabled")));
        Bukkit.getConsoleSender().sendMessage(CC.SEPARATOR);

        profileManager = new ProfileManager();
        staffManager = new StaffManager(this);
        playerManager = new PlayerManager();
        menuManager = new MenuManager(this);
        prefixMenu = new PrefixMenu();
        colorMenu = new ColorMenu();
        disguiseMenu = new DisguiseMenu();
        disguiseManager = new DisguiseManager();
        factionManager = new FactionManager();
        storePCoinMenu = new StorePCoinMenu();

        new InternalNametag(this, new NameTagAdapter());
        //LandSpigot.getInstance().registerPacketHandler(new EntityPacketHandler());

        Arrays.asList(
                new BroadcastCommand(this),
                new ClearChatCommand(this),
                new IgnoreCommand(this),
                new ListCommand(),
                new MessageCommand(this),
                new RankCommand(this),
                new ReplyCommand(this),
                new StaffChatCommand(this),
                new TeleportCommand(this),
                new ToggleMessagesCommand(this),
                new ToggleGlobalChat(this),
                new ToggleSoundsCommand(this),
                new VanishCommand(this),
                new ReportCommand(this),
                new CheckCommand(this),
                new HelpOpCommand(this),
                new PingCommand(),
                new BanCommand(this),
                new MuteCommand(this),
                new UnbanCommand(this),
                new UnmuteCommand(this),
                new WarnCommand(this),
                new KickCommand(this),
                new MuteChatCommand(this),
                new SlowChatCommand(this),
                new GameModeCommand(),
                new ShutdownCommand(this),
                new FreezeCommand(this),
                new WhitelistCommand(this),
                new GrantCommand(this),
                new GrantsCommand(this),
                new QuickGrantCommand(this),
                new UserCommand(this),
                new PlayTimeCommand(this),
                new RankListCommand(),
                new NoteAddCommand(this),
                new NoteRemoveCommand(this),
                new NotesCommand(this),
                new MisplaceCommand(this),
                new PrefixCommand(this),
                new ColorCommand(this),
                new SetColorCommand(this),
                new BuildServerCommand(this),
                new TeleportPositionCommand(this),
                new FeedCommand(this),
                new HealCommand(this),
                new AltsCommand(this),
                new UndisguiseCommand(this),
                new DisguiseCommand(this),
                new EnchantCommand(this),

                // factions
                new FactionHelpCommand(this),
                new FactionAcceptCommand(this),
                new FactionCreateCommand(this),
                new FactionLeaveCommand(this),
                new FactionInfoCommand(this),
                new FactionDescriptionCommand(this),
                new FactionDisbandCommand(this),
                new FactionDemoteCommand(this),
                new FactionInviteCommand(this),
                new FactionKickCommand(this),
                new FactionPasswordCommand(this),
                new FactionPromoteCommand(this),
                new FactionChatCommand(this),

                new PCoinsCommand(this),
                new StoreCommand(this)
        ).forEach(command -> registerCommand(command, getName()));

        Arrays.asList(
                new PlayerListener(this),
                new ButtonListener(),
                new MessageListener(this),
                new InventoryListener(this),
                new HelpCommandListener(this),
                new PlayerInteractListener(this),
                new UIListener(),
                new GrantProcedureListener(this),
                new PunishmentListener(),
                new FactionListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        getServer().getScheduler().runTaskTimerAsynchronously(this, new BroadcastTask(this), 20 * 120L, 20 * 120L);
        new MenuUpdateTask();
        new GrantDisguiseCheckTask();

        //WindSpigot.getInstance().registerPacketListener(new PacketListener());

        packetListener = new PacketListener();
        new PlayerPacketListener(this, packetListener);

        Rank.importRanks();
        CC.logConsole(CC.PRIMARY + "[LandCore] " + CC.SECONDARY + "Core has started successfully.");
        CC.logConsole(CC.PRIMARY + "[LandCore] " + CC.GREEN + " All databases connected successfully.");

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            factionManager.save();
        }, 0L, 6000L);
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);

        Map<String, Object> message = Maps.newHashMap();
        message.put("server", this.getServerName());

        this.getRedisMessenger().sendOff("server-monitor-remove", message);

        factionManager.save();
        getServer().getOnlinePlayers().parallelStream().forEach(player -> factionManager.savePlayerFaction(player));

        try {
            Thread.sleep(1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Player player : getServer().getOnlinePlayers()) {
            player.kickPlayer(CC.RED + "The server is restarting.");

            // remove fake players
            LandCore.getInstance().getPlayerManager().getDummyPlayers().remove(player.getUniqueId());
        }

        profileManager.saveProfiles();
        serverSettings.saveConfig();

        Bukkit.getScheduler().cancelTasks(this);
    }

    public String getServerName() {
        return getConfig().getString("server_name");
    }

    public String getNetworkName() {
        return getConfig().getString("network_name");
    }

    public void registerCommand(Command cmd, String fallbackPrefix) {
        try {
            if (bukkitCommandMap == null) {
                bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);
            }
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(cmd.getName(), fallbackPrefix, cmd);
        } catch (Exception e) {
            Bukkit.getLogger().info("[LandCore] CommandMap failed to register.");
            e.printStackTrace();
        }
    }

    private void disableLoggers() {
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
    }

    private void addServerMonitor() {
        Map<String, Object> message = Maps.newHashMap();
        message.put("server", this.getServerName());

        this.getRedisMessenger().send("server-monitor-add", message);
    }

    public <E extends Packet<?>> void registerAdapter(Class<? extends E> classType, PacketAdapter<E> handler) {
        packetListener.registerAdapter(classType, handler);
    }

}
