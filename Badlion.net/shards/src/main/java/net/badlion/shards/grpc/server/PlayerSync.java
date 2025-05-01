package net.badlion.shards.grpc.server;

import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.EntitySyncReply;
import net.badlion.shards.grpc.PlayerSyncGrpc;
import net.badlion.shards.grpc.PlayerSyncReply;
import net.badlion.shards.grpc.PlayerSyncRequest;
import net.minecraft.server.v1_7_R4.EntityItem;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.UUID;

public class PlayerSync extends PlayerSyncGrpc.PlayerSyncImplBase {

    private final ShardPlugin plugin;

    public PlayerSync(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void playerSync(PlayerSyncRequest request, StreamObserver<PlayerSyncReply> streamObserver) {

        if (this.plugin.getEntitySyncManager().getTransferedEntityIds().contains(request.getEntityid())) return;


        PacketPlayOutEntityTeleport entityTeleport = new PacketPlayOutEntityTeleport();
        entityTeleport.a = request.getEntityid();
        entityTeleport.b = MathHelper.floor(request.getLocx() * 32.0D);
        entityTeleport.c = MathHelper.floor(request.getLocy() * 32.0D);
        entityTeleport.d = MathHelper.floor(request.getLocz() * 32.0D);
        entityTeleport.e = (byte) ((int) (request.getYaw() * 256.0F / 360.0F));
        entityTeleport.f = (byte) ((int) (request.getPitch() * 256.0F / 360.0F));
        entityTeleport.doBlockHeightCorrection = false;

        for (Player player : Bukkit.getOnlinePlayers()) {

            // Don't send to players transfering servers
            if (this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(player.getUniqueId())) continue;

            // They are not tracking this player yet
            if (!plugin.getPlayerSyncManager().isTrackingPlayer(player.getUniqueId(), UUID.fromString(request.getUuid()))) {
                Bukkit.getLogger().info("SEnding spawn packet to " + player.getUniqueId() + " for player " + request.getUsername());

                // 1.8 and up
                if (player.getVersion() >= 47) {
                    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
                    packet.action = 0;
                    packet.username = request.getUsername();
                    packet.player = new GameProfile(UUID.fromString(request.getUuid()), request.getUsername());
                    packet.ping = 500;
                    packet.gamemode = 0; // TODO: TEMP VALUES FOR TEST
                    this.plugin.getProtocol().sendPacket(player, packet);
                }

                // TODO: Replace with tiny protocol or an api
                PacketPlayOutNamedEntitySpawn spawnNamed = new PacketPlayOutNamedEntitySpawn();
                spawnNamed.a = request.getEntityid();
                spawnNamed.b = new GameProfile(UUID.fromString(request.getUuid()), request.getUsername());
                spawnNamed.c = MathHelper.floor(request.getLocx() * 32.0D);
                spawnNamed.d = MathHelper.floor(request.getLocy() * 32.0D);
                spawnNamed.e = MathHelper.floor(request.getLocz() * 32.0D);
                spawnNamed.f = (byte)((int)(request.getYaw() * 256.0F / 360.0F));
                spawnNamed.g = (byte)((int)(request.getPitch() * 256.0F / 360.0F));

                ItemStack itemInHand = this.plugin.getGsonSmall().fromJson(request.getIteminhand(), ItemStack.class);

                net.minecraft.server.v1_7_R4.ItemStack itemstack = CraftItemStack.asNMSCopy(itemInHand);
                spawnNamed.h = itemstack == null?0: Item.getId(itemstack.getItem());
                spawnNamed.metadata = new Object[32];
                spawnNamed.metadata[0] = (byte) 0;
                spawnNamed.metadata[1] = (short) 1;
                this.plugin.getProtocol().sendPacket(player, spawnNamed);

                plugin.getPlayerSyncManager().setTrackingPlayer(player.getUniqueId(), UUID.fromString(request.getUuid()));
            } else {
                // TODO: Replace with tiny protocol or an api
                Bukkit.getLogger().info("SEnding move packet to " + player.getUniqueId() + " for player " + request.getUsername());
                plugin.getProtocol().sendPacket(player, entityTeleport);
                int yaw = this.d(request.getYaw() * 256.0F / 360.0F);
                PacketPlayOutEntityHeadRotation rotationPacket = new PacketPlayOutEntityHeadRotation();
                rotationPacket.a = request.getEntityid();
                rotationPacket.b = (byte) yaw;
                plugin.getProtocol().sendPacket(player, rotationPacket);
            }
        }
        streamObserver.onNext(PlayerSyncReply.newBuilder().build());
        streamObserver.onCompleted();
    }

    // TODO: Temp from nms for rotation
    public int d(float var0) {
        int var1 = (int)var0;
        return var0 < (float)var1?var1 - 1:var1;
    }

}
