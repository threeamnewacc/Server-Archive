package me.devkevin.landcore.listeners;

import me.devkevin.landcore.LandCoreAPI;
import me.devkevin.landcore.utils.packet.PacketInterceptor;
import me.devkevin.landcore.utils.packet.PacketListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/03/2023 @ 12:26
 * PlayerPacketListener / me.devkevin.landcore.listeners / LandCore
 */
public class PlayerPacketListener extends LandCoreAPI.LandCoreListener {

    private final Executor INJECT_EXECUTOR = Executors.newSingleThreadExecutor();
    private final Executor EJECT_EXECUTOR = Executors.newSingleThreadExecutor();
    private final HashMap<UUID, PacketInterceptor> packetInterceptorHashMap;

    private final PacketListener packetListener;

    public PlayerPacketListener(JavaPlugin javaPlugin, PacketListener packetListener) {
        super(javaPlugin);
        this.packetListener = packetListener;

        packetInterceptorHashMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {

        Player player = playerJoinEvent.getPlayer();

        INJECT_EXECUTOR.execute(() -> {
            PacketInterceptor interceptor = new PacketInterceptor(player, packetListener);
            interceptor.attach();
            packetInterceptorHashMap.put(player.getUniqueId(), interceptor);
        });

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {

        Player player = playerQuitEvent.getPlayer();

        EJECT_EXECUTOR.execute(() -> {
            if (packetInterceptorHashMap.containsKey(player.getUniqueId())) {
                PacketInterceptor interceptor = packetInterceptorHashMap.get(player.getUniqueId());
                if (interceptor.isAttached()) {
                    interceptor.detach();
                }
                packetInterceptorHashMap.remove(player.getUniqueId());
            }
        });

    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent playerKickEvent) {

        Player player = playerKickEvent.getPlayer();

        EJECT_EXECUTOR.execute(() -> {
            if (packetInterceptorHashMap.containsKey(player.getUniqueId())) {
                PacketInterceptor interceptor = packetInterceptorHashMap.get(player.getUniqueId());
                if (interceptor.isAttached()) {
                    interceptor.detach();
                }
                packetInterceptorHashMap.remove(player.getUniqueId());
            }
        });

    }
}
