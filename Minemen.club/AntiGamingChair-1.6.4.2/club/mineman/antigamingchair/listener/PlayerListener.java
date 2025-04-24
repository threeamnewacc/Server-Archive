// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.listener;

import java.beans.ConstructorProperties;
import org.bukkit.command.CommandSender;
import club.mineman.antigamingchair.event.player.PlayerBanEvent;
import java.util.Iterator;
import java.util.Set;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.rank.Rank;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.function.Function;
import java.util.Collection;
import java.util.UUID;
import java.util.HashSet;
import org.bukkit.ChatColor;
import java.text.DecimalFormat;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.antigamingchair.request.banwave.AGCAddBanWaveRequest;
import club.mineman.antigamingchair.log.Log;
import club.mineman.core.CorePlugin;
import club.mineman.antigamingchair.event.player.PlayerBanWaveEvent;
import club.mineman.paper.event.PlayerUpdateRotationEvent;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.commands.subcommands.ToggleCommand;
import club.mineman.antigamingchair.util.BlockUtil;
import club.mineman.paper.event.PlayerUpdatePositionEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import club.mineman.antigamingchair.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.plugin.Plugin;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerJoinEvent;
import club.mineman.antigamingchair.AntiGamingChair;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private final AntiGamingChair plugin;
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.plugin.getPlayerDataManager().addPlayerData(event.getPlayer());
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            final PlayerConnection playerConnection = ((CraftPlayer)event.getPlayer()).getHandle().playerConnection;
            new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CB-Client".getBytes())));
            final PacketPlayOutCustomPayload packetPlayOutCustomPayload;
            playerConnection.sendPacket((Packet)packetPlayOutCustomPayload);
            final PlayerConnection playerConnection2 = ((CraftPlayer)event.getPlayer()).getHandle().playerConnection;
            new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CC".getBytes())));
            final PacketPlayOutCustomPayload packetPlayOutCustomPayload2;
            playerConnection2.sendPacket((Packet)packetPlayOutCustomPayload2);
        }, 10L);
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (this.plugin.getAlertsManager().hasAlertsToggled(event.getPlayer())) {
            this.plugin.getAlertsManager().toggleAlerts(event.getPlayer());
        }
        this.plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setLastTeleportTime(System.currentTimeMillis());
            playerData.setSendingVape(true);
        }
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setInventoryOpen(false);
        }
    }
    
    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setInventoryOpen(false);
        }
    }
    
    @EventHandler
    public void onPlayerUpdatePosition(final PlayerUpdatePositionEvent event) {
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        playerData.setOnGround(BlockUtil.isOnGround(event.getTo(), 0) || BlockUtil.isOnGround(event.getTo(), 1));
        if (playerData.isOnGround()) {
            playerData.setLastGroundY(event.getTo().getY());
        }
        playerData.setInLiquid(BlockUtil.isOnLiquid(event.getTo(), 0) || BlockUtil.isOnLiquid(event.getTo(), 1));
        playerData.setInWeb(BlockUtil.isOnWeb(event.getTo(), 0));
        playerData.setOnIce(BlockUtil.isOnIce(event.getTo(), 1) || BlockUtil.isOnIce(event.getTo(), 2));
        playerData.setOnStairs(BlockUtil.isOnStairs(event.getTo(), 0) || BlockUtil.isOnStairs(event.getTo(), 1));
        playerData.setUnderBlock(BlockUtil.isOnGround(event.getTo(), -2));
        if (event.getTo().getY() != event.getFrom().getY() && playerData.getVelocityV() > 0) {
            playerData.setVelocityV(playerData.getVelocityV() - 1);
        }
        if (Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ()) > 0.0 && playerData.getVelocityH() > 0) {
            playerData.setVelocityH(playerData.getVelocityH() - 1);
        }
        for (final Class<? extends ICheck> checkClass : PlayerData.CHECKS) {
            if (!ToggleCommand.DISABLED_CHECKS.contains(checkClass.getSimpleName().toUpperCase())) {
                final ICheck check = (ICheck)playerData.getCheck(checkClass);
                if (check.getType() == PlayerUpdatePositionEvent.class) {
                    check.handleCheck(player, event);
                }
            }
        }
        if (playerData.getVelocityY() > 0.0 && event.getTo().getY() > event.getFrom().getY()) {
            playerData.setVelocityY(0.0);
        }
    }
    
    @EventHandler
    public void onPlayerUpdateRotation(final PlayerUpdateRotationEvent event) {
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        for (final Class<? extends ICheck> checkClass : PlayerData.CHECKS) {
            if (!ToggleCommand.DISABLED_CHECKS.contains(checkClass.getSimpleName().toUpperCase())) {
                final ICheck check = (ICheck)playerData.getCheck(checkClass);
                if (check.getType() == PlayerUpdateRotationEvent.class) {
                    check.handleCheck(player, event);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerBanWave(final PlayerBanWaveEvent event) {
        if (!this.plugin.isAntiCheatEnabled() && !event.getReason().equals("Manual")) {
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        final Log log = new Log(mineman.getId(), "was added to the next ban wave for " + event.getReason());
        this.plugin.getLogManager().addToLogQueue(log);
        CorePlugin.getInstance().getRequestManager().sendRequest((APIMessage)new AGCAddBanWaveRequest(mineman.getId(), event.getReason()), (RequestCallback)new AbstractCallback("Error adding " + player.getName() + " to the ban wave.") {
            public void callback(final JSONObject data) {
                final String response = (String)data.get((Object)"response");
                if (response.equals("success")) {
                    PlayerListener.this.plugin.getLogger().info("Added " + player.getName() + " to the ban wave.");
                }
            }
        });
    }
    
    @EventHandler
    public void onPlayerAlert(final PlayerAlertEvent event) {
        if (!this.plugin.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        final double tps = MinecraftServer.getServer().tps1.getAverage();
        String fixedTPS = new DecimalFormat(".##").format(tps);
        if (tps > 20.0) {
            fixedTPS = "20.0";
        }
        final String alert = event.getAlert() + ChatColor.LIGHT_PURPLE + " Ping " + playerData.getPing() + " ms. TPS " + fixedTPS + ".";
        final Set<UUID> playerUUIDs = new HashSet<UUID>(this.plugin.getAlertsManager().getAlertsToggled());
        playerUUIDs.addAll(playerData.getPlayersWatching());
        final PlayerAlertEvent.AlertType type = event.getAlertType();
        playerUUIDs.stream().map((Function<? super Object, ?>)this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(p -> {
            final Mineman mineman2 = CorePlugin.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
            if ((mineman2.getRank().hasRank(Rank.ADMIN) && type == PlayerAlertEvent.AlertType.RELEASE) || mineman2.getRank().hasRank(Rank.DEVELOPER)) {
                final PlayerData alertedData = this.plugin.getPlayerDataManager().getPlayerData(p);
                boolean sendAlert = true;
                alertedData.getFilteredPhrases().iterator();
                final Iterator iterator;
                while (iterator.hasNext()) {
                    final String phrase = iterator.next();
                    if (alert.toLowerCase().contains(phrase)) {
                        sendAlert = false;
                        break;
                    }
                }
                if (sendAlert) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + CC.D_PURPLE + " " + alert);
                }
            }
            return;
        });
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        final Log log = new Log(mineman.getId(), ChatColor.stripColor(alert));
        this.plugin.getLogManager().addToLogQueue(log);
    }
    
    @EventHandler
    public void onPlayerBan(final PlayerBanEvent event) {
        if (!this.plugin.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        this.plugin.getServer().broadcastMessage(CC.S + "--------------------------------------------------\n" + CC.R + "\u2718 " + CC.PINK + player.getName() + CC.D_PURPLE + " was banned by " + CC.PINK + "AntiGamingChair" + CC.D_PURPLE + " for cheating.\n" + CC.R + CC.S + "--------------------------------------------------\n");
        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getServer().dispatchCommand((CommandSender)this.plugin.getServer().getConsoleSender(), "ban " + player.getName() + " Unfair Advantage -s"));
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        final Log log = new Log(mineman.getId(), "was autobanned for " + event.getReason());
        this.plugin.getLogManager().addToLogQueue(log);
    }
    
    @ConstructorProperties({ "plugin" })
    public PlayerListener(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
