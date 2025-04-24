// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.manager;

import java.beans.ConstructorProperties;
import club.mineman.core.util.finalutil.CC;
import org.bukkit.ChatColor;
import java.text.DecimalFormat;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.function.Function;
import java.util.Collection;
import java.util.HashSet;
import club.mineman.antigamingchair.data.PlayerData;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;
import java.util.UUID;
import java.util.Set;

public class AlertsManager
{
    private final Set<UUID> alertsToggled;
    private final AntiGamingChair plugin;
    
    public boolean hasAlertsToggled(final Player player) {
        return this.alertsToggled.contains(player.getUniqueId());
    }
    
    public void toggleAlerts(final Player player) {
        if (!this.alertsToggled.remove(player.getUniqueId())) {
            this.alertsToggled.add(player.getUniqueId());
        }
    }
    
    public void forceAlert(final String message) {
        this.forceAlertWithData(message, null);
    }
    
    private void forceAlertWithData(final String message, final PlayerData playerData) {
        final Set<UUID> playerUUIDs = new HashSet<UUID>(this.plugin.getAlertsManager().getAlertsToggled());
        if (playerData != null) {
            playerUUIDs.addAll(playerData.getPlayersWatching());
        }
        playerUUIDs.stream().map((Function<? super Object, ?>)this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(p -> p.sendMessage(message));
    }
    
    public void forceAlert(final String message, final Player player) {
        final double tps = MinecraftServer.getServer().tps1.getAverage();
        String fixedTPS = new DecimalFormat(".##").format(tps);
        if (tps > 20.0) {
            fixedTPS = "20.0";
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        final String alert = message + ChatColor.LIGHT_PURPLE + " Ping " + playerData.getPing() + " ms. TPS " + fixedTPS + ".";
        this.forceAlertWithData(ChatColor.LIGHT_PURPLE + player.getName() + CC.D_PURPLE + " " + alert, playerData);
    }
    
    @ConstructorProperties({ "plugin" })
    public AlertsManager(final AntiGamingChair plugin) {
        this.alertsToggled = new HashSet<UUID>();
        this.plugin = plugin;
    }
    
    public Set<UUID> getAlertsToggled() {
        return this.alertsToggled;
    }
}
