package net.badlion.shards.grpc;

import com.google.gson.reflect.TypeToken;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.event.MasterSyncEvent;
import net.badlion.shards.grpc.server.EntityDespawn;
import net.badlion.shards.grpc.server.EntitySync;
import net.badlion.shards.grpc.server.MasterServerCheck;
import net.badlion.shards.grpc.server.PlayerDespawn;
import net.badlion.shards.grpc.server.PlayerSync;
import net.badlion.shards.grpc.server.SlaveShutdown;
import net.badlion.shards.type.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class ShardInstanceServer {

    private final ShardPlugin plugin;
    private Server server;

    public ShardInstanceServer(ShardPlugin plugin) {
        this.plugin = plugin;

        try {
            this.server = NettyServerBuilder.forPort(plugin.getConf().getPort())
                    .addService(new PlayerTransferImpl())
                    .addService(new BlockSyncImpl())
                    .addService(new EntityTransferImpl())
                    .addService(new EntitySync(this.plugin))
                    .addService(new MasterPluginSyncImpl())
                    .addService(new MasterServerCheck(this.plugin))
                    .addService(new SlaveShutdown(this.plugin))
                    .addService(new EntityDespawn(this.plugin))
                    .addService(new PlayerSync(this.plugin))
                    .addService(new PlayerDespawn(this.plugin))
                    .build()
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getServer().shutdown();
        }
        plugin.getLogger().info("Shard Instance Server started, listening on " + server.getPort());
    }

    public void stop() {
        if (this.server != null) {
            this.server.shutdown();
        }
    }

    private class PlayerTransferImpl extends PlayerTransferGrpc.PlayerTransferImplBase {

        @Override
        public void playerTransfer(PlayerTransferRequest req, StreamObserver<PlayerTransferReply> responseObserver) {
            PlayerData playerData = new PlayerData(req);
            plugin.getLogger().info("GOT PLAYER transfer: " + playerData.getUuid());
            ShardInstanceServer.this.plugin.getPlayerSyncManager().putPlayerData(playerData.getUuid(), playerData);
            PlayerTransferReply reply = PlayerTransferReply.newBuilder().setReceived(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    private class BlockSyncImpl extends BlockSyncGrpc.BlockSyncImplBase {

        @Override
        public void blockSync(BlockSyncRequest request, StreamObserver<BlockSyncReply> replyStreamObserver) {
            plugin.getLogger().info("GOT BLOCK SYNC: " + request.getMaterial());

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getPlayerSyncManager().getRecivedBlockLocations().add(new Location(Bukkit.getWorld(request.getWorld()), request.getLocx(), request.getLocy(), request.getLocz()));
                    Block block = Bukkit.getWorld(request.getWorld()).getBlockAt(request.getLocx(), request.getLocy(), request.getLocz());
                    block.setType(Material.valueOf(request.getMaterial()));
                    block.setData((byte) request.getDurability());
                }
            }.runTask(plugin);
            replyStreamObserver.onNext(BlockSyncReply.newBuilder().setPlaced(true).build());
            replyStreamObserver.onCompleted();
        }
    }

    private class EntityTransferImpl extends EntityTransferGrpc.EntityTransferImplBase {

        @Override
        public void entityTransfer(EntityTransferRequest request, StreamObserver<EntityTransferReply> replyStreamObserver) {
            plugin.getLogger().info("GOT Entity transfer: " + request.getEntitytype() + " id=" + request.getEntityid());

            plugin.getEntitySyncManager().getTransferedEntityIds().add(request.getEntityid());

            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = Bukkit.getWorld(request.getWorld())
                            .spawnEntity(new Location(Bukkit.getWorld(request.getWorld()), request.getLocx(), request.getLocy(), request.getLocz(), request.getYaw(), request.getPitch()),
                                    EntityType.valueOf(request.getEntitytype()),
                                    request.getEntityid());
                    entity.setFireTicks(request.getFireticks());
                    entity.setTicksLived(request.getTickslived());
                    entity.setVelocity(new Vector(request.getVelx(), request.getVely(), request.getVelz()));

                    if (plugin.getEntitySyncManager().getEntitiesWaitingForPassengerEntity().containsKey(request.getEntityid())) {
                        Vehicle vehicle = (Vehicle) plugin.getEntitySyncManager().getEntitiesWaitingForPassengerEntity().remove(request.getEntityid());
                        vehicle.setPassenger(entity);
                    }

                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (request.getHealth() != -1.0) {
                            livingEntity.setMaxHealth(request.getMaxhealth());
                            livingEntity.setHealth(request.getHealth());
                        }
                    }
                    if (entity instanceof Ageable) {
                        Ageable ageable = (Ageable) entity;
                        if (request.getIsbaby()) {
                            ageable.setBaby();
                        } else {
                            ageable.setAdult();
                        }
                        ageable.setAge(request.getAge());
                    }

                    // Parse extra data for the entity (Horses and a few other entities need this)
                    JSONObject extraData = null;
                    try {
                        plugin.getLogger().info("EXTRADATA: " + extraData);
                        extraData = (JSONObject) new JSONParser().parse(request.getExtradata());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (extraData != null) {
                        if (entity instanceof Vehicle) {
                            Vehicle vehicle = (Vehicle) entity;

                            if (extraData.containsKey("passenger_player")) {
                                UUID passengerUUID = UUID.fromString((String) extraData.get("passenger_player"));
                                Player passenger = Bukkit.getPlayer(passengerUUID);
                                if (passenger != null) {
                                    vehicle.setPassenger(passenger);
                                } else {
                                    plugin.getEntitySyncManager().getEntitiesWaitingForPassengerPlayer().put(passengerUUID, vehicle);
                                }
                            } else if (extraData.containsKey("passenger_entity")) {
                                int entityId = ((Long) extraData.get("passenger_entity")).intValue();
                                plugin.getEntitySyncManager().getEntitiesWaitingForPassengerEntity().put(entityId, vehicle);
                            }
                        }
                        if (entity instanceof Tameable) {
                            Tameable tameable = (Tameable) entity;

                            tameable.setTamed((Boolean) extraData.get("tamed"));
                            if (tameable.isTamed()) {
                                tameable.setOwner(Bukkit.getOfflinePlayer(UUID.fromString((String) extraData.get("owner"))));
                            }
                        }

                        if (entity instanceof Horse) {
                            Horse horse = (Horse) entity;

                            horse.setVariant(Horse.Variant.valueOf((String) extraData.get("variant")));
                            horse.setColor(Horse.Color.valueOf((String) extraData.get("color")));
                            horse.setDomestication(((Long) extraData.get("domestication")).intValue());
                            horse.setJumpStrength((Double) extraData.get("jump"));
                            horse.setStyle(Horse.Style.valueOf((String) extraData.get("style")));
                            horse.getInventory().setContents(plugin.getGsonSmall().fromJson((String) extraData.get("inventory"), ItemStack[].class));
                            horse.getInventory().setArmor(plugin.getGsonSmall().fromJson((String) extraData.get("armor"), ItemStack.class));
                            horse.getInventory().setSaddle(plugin.getGsonSmall().fromJson((String) extraData.get("saddle"), ItemStack.class));
                        }

                        if (entity instanceof ThrownPotion) {
                            ThrownPotion thrownPotion = (ThrownPotion) entity;
                            Type potionEffects = new TypeToken<List<PotionEffect>>() {
                            }.getType();
                            thrownPotion.getEffects().addAll(plugin.getGsonSmall().fromJson((String) extraData.get("effects"), potionEffects));
                            thrownPotion.setItem(plugin.getGsonSmall().fromJson((String) extraData.get("item"), ItemStack.class));
                        }

                        if (entity instanceof Item) {
                            Item item = (Item) entity;
                            item.setItemStack(plugin.getGsonSmall().fromJson((String) extraData.get("item"), ItemStack.class));
                        }
                    }

                    for (ShardInstanceClient client : ShardInstanceServer.this.plugin.getNearbyShardClients(entity.getLocation(), 64)) {
                        client.syncEntity(entity, entity.getLocation());
                    }
                }
            }.runTask(plugin);
            replyStreamObserver.onNext(EntityTransferReply.newBuilder().setReceived(true).build());
            replyStreamObserver.onCompleted();
        }
    }

    public class MasterPluginSyncImpl extends MasterPluginSyncGrpc.MasterPluginSyncImplBase {
        @Override
        public void masterPluginSync(MasterPluginSyncRequest request, StreamObserver<MasterPluginSyncReply> replyStreamObserver) {
            MasterSyncEvent event = new MasterSyncEvent(request.getFrom(), request.getData());
            Bukkit.getPluginManager().callEvent(event);
            replyStreamObserver.onNext(MasterPluginSyncReply.newBuilder().setResponse(event.getResponse()).build());
            replyStreamObserver.onCompleted();
        }
    }

}
