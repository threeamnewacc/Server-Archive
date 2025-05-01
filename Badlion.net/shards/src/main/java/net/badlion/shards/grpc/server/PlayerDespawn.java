package net.badlion.shards.grpc.server;

import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.PlayerDespawnGrpc;
import net.badlion.shards.grpc.PlayerDespawnReply;
import net.badlion.shards.grpc.PlayerDespawnRequest;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDespawn extends PlayerDespawnGrpc.PlayerDespawnImplBase {

    private final ShardPlugin plugin;

    public PlayerDespawn(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void playerDespawn(PlayerDespawnRequest request, StreamObserver<PlayerDespawnReply> streamObserver) {
        UUID playerUUID = UUID.fromString(request.getUuid());
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(request.getEntityid());
        for (Map.Entry<UUID, Set<UUID>> entry : this.plugin.getPlayerSyncManager().getPlayersTrackingPlayers().entrySet()) {
            if (entry.getValue().contains(playerUUID)) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    this.plugin.getProtocol().sendPacket(player, packetPlayOutEntityDestroy);
                }
            }
        }
        this.plugin.getPlayerSyncManager().removeTrackingPlayer(playerUUID);
        streamObserver.onNext(PlayerDespawnReply.newBuilder().setDespawned(true).build());
        streamObserver.onCompleted();
    }

}
