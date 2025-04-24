// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import club.mineman.core.task.BroadcastTask;
import java.util.function.Consumer;
import java.util.Arrays;
import club.mineman.core.command.SettingsCommand;
import club.mineman.core.command.ShutdownCommand;
import club.mineman.core.command.MCKickCommand;
import club.mineman.core.command.ColorCommand;
import club.mineman.core.command.ClearChatCommand;
import club.mineman.core.command.AdminChatCommand;
import club.mineman.core.command.WhoCommand;
import club.mineman.core.command.ToggleChatCommand;
import club.mineman.core.command.ToggleMessagesCommand;
import club.mineman.core.command.ReportCommand;
import club.mineman.core.command.SilenceChatCommand;
import club.mineman.core.command.VanishCommand;
import club.mineman.core.command.StaffChatCommand;
import club.mineman.core.command.IgnoreCommand;
import club.mineman.core.command.ReplyCommand;
import club.mineman.core.command.MessageCommand;
import club.mineman.core.command.punish.IPBanCommand;
import club.mineman.core.command.BanInfoCommand;
import club.mineman.core.command.PunishHistoryCommand;
import club.mineman.core.command.punish.UnblacklistCommand;
import club.mineman.core.command.punish.BlacklistCommand;
import club.mineman.core.command.punish.UnmuteCommand;
import club.mineman.core.command.punish.UnbanCommand;
import club.mineman.core.command.RankCommand;
import club.mineman.core.command.punish.KickCommand;
import club.mineman.core.command.punish.MuteCommand;
import club.mineman.core.command.punish.BanCommand;
import org.bukkit.command.Command;
import org.bukkit.plugin.messaging.PluginMessageListener;
import club.mineman.core.listener.BungeeListener;
import club.mineman.core.listener.RankListener;
import club.mineman.core.listener.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import club.mineman.core.listener.GuiListener;
import java.util.LinkedList;
import club.mineman.core.util.Config;
import club.mineman.core.util.PluginUpdater;
import club.mineman.core.manager.MinemanManager;
import club.mineman.core.api.RequestManager;
import club.mineman.core.settings.SettingsManager;
import club.mineman.core.manager.PunishmentManager;
import club.mineman.core.gui.GuiFolder;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class CorePlugin extends JavaPlugin
{
    public static boolean SETUP;
    private static CorePlugin instance;
    private List<GuiFolder> folders;
    private List<String> filterList;
    private PunishmentManager punishmentManager;
    private SettingsManager settingsManager;
    private RequestManager requestManager;
    private MinemanManager playerManager;
    private PluginUpdater pluginUpdater;
    
    public void onEnable() {
        CorePlugin.instance = this;
        this.pluginUpdater = new PluginUpdater(this);
        this.playerManager = new MinemanManager();
        this.settingsManager = new SettingsManager(this);
        this.requestManager = new RequestManager(this);
        this.punishmentManager = new PunishmentManager(this);
        final Config config = new Config("filter", this);
        this.filterList = config.getConfig().getStringList("filtered-phrases");
        this.folders = new LinkedList<GuiFolder>();
        this.getServer().getPluginManager().registerEvents((Listener)new GuiListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new RankListener(), (Plugin)this);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "BungeeCord", (PluginMessageListener)new BungeeListener(this));
        Arrays.asList(new BanCommand(), new MuteCommand(), new KickCommand(), new RankCommand(this), new UnbanCommand(), new UnmuteCommand(), new BlacklistCommand(), new UnblacklistCommand(), new PunishHistoryCommand(this), new BanInfoCommand(this), new IPBanCommand(), new MessageCommand(this), new ReplyCommand(this), new IgnoreCommand(this), new StaffChatCommand(this), new VanishCommand(this), new SilenceChatCommand(this), new ReportCommand(), new ToggleMessagesCommand(this), new ToggleChatCommand(this), new WhoCommand(this), new AdminChatCommand(this), new ClearChatCommand(this), new ColorCommand(this), new MCKickCommand(), new ShutdownCommand(this), new SettingsCommand(this)).forEach(this::registerCommand);
        this.getServer().getScheduler().runTaskLater((Plugin)this, () -> CorePlugin.SETUP = true, 100L);
        new BroadcastTask(this).runTaskTimer((Plugin)this, 1200L, 2400L);
    }
    
    public void onDisable() {
    }
    
    private void registerCommand(final Command cmd) {
        this.registerCommand(cmd, this.getName());
    }
    
    public void registerCommand(final Command cmd, final String fallbackPrefix) {
        MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
    }
    
    public List<GuiFolder> getFolders() {
        return this.folders;
    }
    
    public List<String> getFilterList() {
        return this.filterList;
    }
    
    public PunishmentManager getPunishmentManager() {
        return this.punishmentManager;
    }
    
    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }
    
    public RequestManager getRequestManager() {
        return this.requestManager;
    }
    
    public MinemanManager getPlayerManager() {
        return this.playerManager;
    }
    
    public PluginUpdater getPluginUpdater() {
        return this.pluginUpdater;
    }
    
    public static CorePlugin getInstance() {
        return CorePlugin.instance;
    }
    
    static {
        CorePlugin.SETUP = false;
    }
}
