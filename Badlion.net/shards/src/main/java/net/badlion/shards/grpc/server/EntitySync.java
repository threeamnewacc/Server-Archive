package net.badlion.shards.grpc.server;

import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.EntitySyncGrpc;
import net.badlion.shards.grpc.EntitySyncReply;
import net.badlion.shards.grpc.EntitySyncRequest;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityItem;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntitySync extends EntitySyncGrpc.EntitySyncImplBase {

    private final ShardPlugin plugin;

    private Map<EntityType, Integer> entityIds = new HashMap<>();

    public EntitySync(ShardPlugin plugin) {
        this.plugin = plugin;
        this.entityIds.put(EntityType.BOAT, 1);
        this.entityIds.put(EntityType.DROPPED_ITEM, 2);
        this.entityIds.put(EntityType.MINECART, 10);
        this.entityIds.put(EntityType.PRIMED_TNT, 50);
        this.entityIds.put(EntityType.ENDER_CRYSTAL, 51);
        this.entityIds.put(EntityType.ARROW, 60);
        this.entityIds.put(EntityType.SNOWBALL, 61);
        this.entityIds.put(EntityType.EGG, 62);
        this.entityIds.put(EntityType.FIREBALL, 63);
        this.entityIds.put(EntityType.SMALL_FIREBALL, 64);
        this.entityIds.put(EntityType.ENDER_PEARL, 65);
        this.entityIds.put(EntityType.WITHER_SKULL, 66);
        this.entityIds.put(EntityType.FALLING_BLOCK, 70);
        this.entityIds.put(EntityType.ITEM_FRAME, 71);
        this.entityIds.put(EntityType.ENDER_SIGNAL, 72);
        this.entityIds.put(EntityType.SPLASH_POTION, 73);
        this.entityIds.put(EntityType.THROWN_EXP_BOTTLE, 75);
        this.entityIds.put(EntityType.FIREWORK, 76);
        this.entityIds.put(EntityType.LEASH_HITCH, 77);
        this.entityIds.put(EntityType.FISHING_HOOK, 90);
    }



    @Override
    public void entitySync(EntitySyncRequest request, StreamObserver<EntitySyncReply> replyStreamObserver) {

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
            if (this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(player.getUniqueId())) continue;


            if (!plugin.getEntitySyncManager().isTrackingEntity(player.getUniqueId(), request.getEntityid())) {
                Bukkit.getLogger().info("SEnding spawn packet to " + player.getUniqueId() + " for id " + request.getEntityid() + " Entitytype: " + EntityType.valueOf(request.getEntitytype()).getTypeId());
                if (this.entityIds.containsKey(EntityType.valueOf(request.getEntitytype()))) {
                    plugin.getProtocol().sendPacket(player, this.getEntityPacket(request));

                    if (EntityType.valueOf(request.getEntitytype()).equals(EntityType.DROPPED_ITEM)) {
                        // Parse extra data for the entity (Horses and a few other entities need this)
                        JSONObject extraData = null;
                        try {
                            extraData = (JSONObject) new JSONParser().parse(request.getExtradata());
                            plugin.getLogger().info("EXTRADATA ITEMSTACK: " + extraData);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (extraData != null) {
                            // TODO: NMS Just for testing, replace later
                            PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata();
                            entityMetadata.a = request.getEntityid();
                            entityMetadata.clss = EntityItem.class;
                            entityMetadata.metadata = new Object[32];
                            entityMetadata.metadata[0] = (byte) 0;
                            entityMetadata.metadata[1] = 300;
                            ItemStack itemStack = plugin.getGsonSmall().fromJson((String) extraData.get("item"), ItemStack.class);
                            net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
                            entityMetadata.metadata[10] = item;
                            plugin.getProtocol().sendPacket(player, entityMetadata);
                            plugin.getLogger().info("SENDING ITEMSTACK META...");
                        }
                    }
                } else {
                    plugin.getProtocol().sendPacket(player, this.getEntityLivingPacket(request));
                }
                plugin.getEntitySyncManager().setTrackingEntity(player.getUniqueId(), request.getEntityid());
            } else {
                Bukkit.getLogger().info("SEnding move packet to " + player.getUniqueId() + " for id " + request.getEntityid());
                plugin.getProtocol().sendPacket(player, entityTeleport);
            }
        }
        replyStreamObserver.onNext(EntitySyncReply.newBuilder().setReceived(true).build());
        replyStreamObserver.onCompleted();
    }

    // TODO: NMS Just for testing, replace later
    private Object getEntityLivingPacket(EntitySyncRequest request) {
        PacketPlayOutSpawnEntityLiving spawnEntityLiving = new PacketPlayOutSpawnEntityLiving();
        spawnEntityLiving.a = request.getEntityid();
        spawnEntityLiving.b = (byte) EntityType.valueOf(request.getEntitytype()).getTypeId();
        spawnEntityLiving.c = MathHelper.floor(request.getLocx() * 32.0D);
        spawnEntityLiving.d = MathHelper.floor(request.getLocy() * 32.0D);
        spawnEntityLiving.e = MathHelper.floor(request.getLocz() * 32.0D);
        spawnEntityLiving.i = (byte) ((int) (request.getYaw() * 256.0F / 360.0F));
        spawnEntityLiving.j = (byte) ((int) (request.getPitch() * 256.0F / 360.0F));
        spawnEntityLiving.k = (byte) ((int) (request.getYaw() * 256.0F / 360.0F));
        double d0 = 3.9D;
        double d1 = request.getVelx();
        double d2 = request.getVely();
        double d3 = request.getVelz();

        if (d1 < -d0) {
            d1 = -d0;
        }

        if (d2 < -d0) {
            d2 = -d0;
        }

        if (d3 < -d0) {
            d3 = -d0;
        }

        if (d1 > d0) {
            d1 = d0;
        }

        if (d2 > d0) {
            d2 = d0;
        }

        if (d3 > d0) {
            d3 = d0;
        }

        spawnEntityLiving.f = (int) (d1 * 8000.0D);
        spawnEntityLiving.g = (int) (d2 * 8000.0D);
        spawnEntityLiving.h = (int) (d3 * 8000.0D);

        // GStart - Protocol hack
        spawnEntityLiving.clss = EntityInsentient.class;
        spawnEntityLiving.metadata = new Object[32];
        spawnEntityLiving.metadata[10] = "TESTING"; // custom name
        spawnEntityLiving.metadata[11] = (byte) 1; // custom name visible
        spawnEntityLiving.uuid = UUID.fromString(request.getEntityuuid());
        return spawnEntityLiving;
    }

    // TODO: NMS Just for testing, replace later
    private Object getEntityPacket(EntitySyncRequest request) {
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity();
        packet.a = request.getEntityid();
        packet.b = MathHelper.floor(request.getLocx() * 32.0D);
        packet.c = MathHelper.floor(request.getLocy() * 32.0D);
        packet.d = MathHelper.floor(request.getLocz() * 32.0D);
        packet.h = MathHelper.d(request.getPitch() * 256.0F / 360.0F);
        packet.i = MathHelper.d(request.getYaw() * 256.0F / 360.0F);
        int i = this.getEntityTypeId(request.getEntitytype());

        packet.j = i;
        packet.k = request.getData(); //TODO: This value changes for a few entities, need to look into changing for those if it breaks something
        if (request.getData() > 0) {
            double d0 = request.getVelx();
            double d1 = request.getVely();
            double d2 = request.getLocz();
            double d3 = 3.9D;

            if (d0 < -d3) {
                d0 = -d3;
            }

            if (d1 < -d3) {
                d1 = -d3;
            }

            if (d2 < -d3) {
                d2 = -d3;
            }

            if (d0 > d3) {
                d0 = d3;
            }

            if (d1 > d3) {
                d1 = d3;
            }

            if (d2 > d3) {
                d2 = d3;
            }

            packet.e = (int) (d0 * 8000.0D);
            packet.f = (int) (d1 * 8000.0D);
            packet.g = (int) (d2 * 8000.0D);
        }
        packet.uuid = UUID.fromString(request.getEntityuuid()); // GNote - Protocol hack
        packet.doBlockHeightCorrection = i == 50 || i == 70; // GNote - Protocol hack: height offset fix for fall
        return packet;
    }

    public int getEntityTypeId(String entityType) {
        EntityType type = EntityType.valueOf(entityType);
        int i = type.getTypeId();
        if (this.entityIds.containsKey(type)) {
            i = entityIds.get(type);
        }
        return i;
    }
}
