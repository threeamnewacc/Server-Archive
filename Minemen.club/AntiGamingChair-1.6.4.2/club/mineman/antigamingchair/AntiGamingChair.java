// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair;

import club.mineman.antigamingchair.runnable.ExportLogsRunnable;
import club.mineman.core.CorePlugin;
import java.util.Arrays;
import club.mineman.antigamingchair.commands.PingCommand;
import club.mineman.antigamingchair.commands.AGCCommand;
import org.bukkit.command.Command;
import club.mineman.antigamingchair.listener.BanWaveListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import club.mineman.antigamingchair.listener.PlayerListener;
import club.mineman.paper.handler.PacketHandler;
import club.mineman.antigamingchair.packet.CustomPacketHandler;
import club.mineman.paper.PaperServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import club.mineman.antigamingchair.manager.LogManager;
import club.mineman.antigamingchair.manager.AlertsManager;
import club.mineman.antigamingchair.manager.BanWaveManager;
import club.mineman.antigamingchair.manager.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiGamingChair extends JavaPlugin
{
    private static AntiGamingChair instance;
    private PlayerDataManager playerDataManager;
    private BanWaveManager banWaveManager;
    private AlertsManager alertsManager;
    private LogManager logManager;
    private double rangeVl;
    private double rangeVlLow;
    
    public AntiGamingChair() {
        this.rangeVl = 25.0;
        this.rangeVlLow = 25.0;
    }
    
    public void onEnable() {
        (AntiGamingChair.instance = this).registerPacketHandler();
        this.registerManagers();
        this.registerListeners();
        this.registerCommands();
        this.registerRunnables();
    }
    
    public void onDisable() {
    }
    
    public boolean isAntiCheatEnabled() {
        return MinecraftServer.getServer().tps1.getAverage() > 19.0 && MinecraftServer.LAST_TICK_TIME + 100L > System.currentTimeMillis();
    }
    
    private void registerPacketHandler() {
        PaperServer.INSTANCE.addPacketHandler((PacketHandler)new CustomPacketHandler(this));
    }
    
    private void registerManagers() {
        this.alertsManager = new AlertsManager(this);
        this.banWaveManager = new BanWaveManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.logManager = new LogManager();
    }
    
    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new BanWaveListener(this), (Plugin)this);
    }
    
    private void registerCommands() {
        Arrays.asList(new AGCCommand(this), new PingCommand(this)).forEach(command -> CorePlugin.getInstance().registerCommand(command, this.getName()));
    }
    
    private void registerRunnables() {
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ExportLogsRunnable(this), 1200L, 200L);
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }
    
    public BanWaveManager getBanWaveManager() {
        return this.banWaveManager;
    }
    
    public AlertsManager getAlertsManager() {
        return this.alertsManager;
    }
    
    public LogManager getLogManager() {
        return this.logManager;
    }
    
    public static AntiGamingChair getInstance() {
        return AntiGamingChair.instance;
    }
    
    public double getRangeVl() {
        return this.rangeVl;
    }
    
    public double getRangeVlLow() {
        return this.rangeVlLow;
    }
    
    public void setRangeVl(final double rangeVl) {
        this.rangeVl = rangeVl;
    }
    
    public void setRangeVlLow(final double rangeVlLow) {
        this.rangeVlLow = rangeVlLow;
    }
}
