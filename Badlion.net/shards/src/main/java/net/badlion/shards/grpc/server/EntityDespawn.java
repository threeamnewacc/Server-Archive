package net.badlion.shards.grpc.server;

import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.EntityDespawnGrpc;
import net.badlion.shards.grpc.EntityDespawnReply;
import net.badlion.shards.grpc.EntityDespawnRequest;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EntityDespawn  extends EntityDespawnGrpc.EntityDespawnImplBase {

    private final ShardPlugin plugin;

    public EntityDespawn(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void entityDespawn(EntityDespawnRequest request, StreamObserver<EntityDespawnReply> replyStreamObserver) {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(request.getEntityid());
        for (Map.Entry<UUID, Set<Integer>> entry : this.plugin.getEntitySyncManager().getPlayersTrackingEntities().entrySet()) {
            if (entry.getValue().contains(request.getEntityid())) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    this.plugin.getProtocol().sendPacket(player, packetPlayOutEntityDestroy);
                }
            }
        }
        this.plugin.getEntitySyncManager().removeEntity(request.getEntityid());
        replyStreamObserver.onNext(EntityDespawnReply.newBuilder().setDespawned(true).build());
        replyStreamObserver.onCompleted();
    }

}
