// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.check;

import java.beans.ConstructorProperties;
import club.mineman.antigamingchair.event.player.PlayerBanEvent;
import club.mineman.antigamingchair.event.player.PlayerBanWaveEvent;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.AntiGamingChair;

public abstract class AbstractCheck<T> implements ICheck<T>
{
    protected final AntiGamingChair plugin;
    protected final PlayerData playerData;
    private final Class<T> clazz;
    
    @Override
    public Class<? extends T> getType() {
        return (Class<? extends T>)this.clazz;
    }
    
    protected boolean alert(final PlayerAlertEvent.AlertType alertType, final Player player, final String message) {
        final PlayerAlertEvent event = new PlayerAlertEvent(alertType, player, message);
        this.plugin.getServer().getPluginManager().callEvent((Event)event);
        if (!event.isCancelled()) {
            this.playerData.addViolation(this);
            return true;
        }
        return false;
    }
    
    protected boolean banWave(final Player player, final String message) {
        this.playerData.setBanWave(true);
        final PlayerBanWaveEvent event = new PlayerBanWaveEvent(player, message);
        this.plugin.getServer().getPluginManager().callEvent((Event)event);
        return !event.isCancelled();
    }
    
    protected boolean ban(final Player player, final String message) {
        this.playerData.setBanning(true);
        final PlayerBanEvent event = new PlayerBanEvent(player, message);
        this.plugin.getServer().getPluginManager().callEvent((Event)event);
        return !event.isCancelled();
    }
    
    public AntiGamingChair getPlugin() {
        return this.plugin;
    }
    
    public PlayerData getPlayerData() {
        return this.playerData;
    }
    
    public Class<T> getClazz() {
        return this.clazz;
    }
    
    @ConstructorProperties({ "plugin", "playerData", "clazz" })
    public AbstractCheck(final AntiGamingChair plugin, final PlayerData playerData, final Class<T> clazz) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.clazz = clazz;
    }
}
