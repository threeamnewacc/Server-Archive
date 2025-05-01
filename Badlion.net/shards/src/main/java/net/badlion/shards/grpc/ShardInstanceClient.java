package net.badlion.shards.grpc;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import net.badlion.shards.MasterConf;
import net.badlion.shards.ShardPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ShardInstanceClient {

    private final ShardPlugin plugin;
    private final ManagedChannel channel;
    private final PlayerTransferGrpc.PlayerTransferBlockingStub playerTransferBlockingStub;
    private final BlockSyncGrpc.BlockSyncBlockingStub blockSyncBlockingStub;
    private final EntityTransferGrpc.EntityTransferBlockingStub entityTransferBlockingStub;
    private final EntitySyncGrpc.EntitySyncBlockingStub entitySyncBlockingStub;
    private final EntityDespawnGrpc.EntityDespawnBlockingStub entityDespawnBlockingStub;
    private final MasterPluginSyncGrpc.MasterPluginSyncBlockingStub masterPluginSyncBlockingStub;
    private final MasterServerCheckGrpc.MasterServerCheckBlockingStub masterServerCheckBlockingStub;
    private final SlaveShutdownGrpc.SlaveShutdownBlockingStub slaveShutdownBlockingStub;
    private final PlayerSyncGrpc.PlayerSyncBlockingStub playerSyncBlockingStub;
    private final PlayerDespawnGrpc.PlayerDespawnBlockingStub playerDespawnBlockingStub;

    public ShardInstanceClient(ShardPlugin plugin, String host, int port) {
        this.plugin = plugin;
        this.channel = NettyChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .nameResolverFactory(new DnsNameResolverProvider())
                .usePlaintext(true)
                .build();
        this.playerTransferBlockingStub = PlayerTransferGrpc.newBlockingStub(this.channel);
        this.blockSyncBlockingStub = BlockSyncGrpc.newBlockingStub(this.channel);
        this.entityTransferBlockingStub = EntityTransferGrpc.newBlockingStub(this.channel);
        this.entitySyncBlockingStub = EntitySyncGrpc.newBlockingStub(this.channel);
        this.entityDespawnBlockingStub = EntityDespawnGrpc.newBlockingStub(this.channel);
        this.masterPluginSyncBlockingStub = MasterPluginSyncGrpc.newBlockingStub(this.channel);
        this.masterServerCheckBlockingStub = MasterServerCheckGrpc.newBlockingStub(this.channel);
        this.slaveShutdownBlockingStub = SlaveShutdownGrpc.newBlockingStub(this.channel);
        this.playerSyncBlockingStub = PlayerSyncGrpc.newBlockingStub(this.channel);
        this.playerDespawnBlockingStub = PlayerDespawnGrpc.newBlockingStub(this.channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public boolean isMasterServer() {
        Empty request = Empty.newBuilder().build();
        MasterServerCheckReply reply;
        try {
            reply = this.masterServerCheckBlockingStub.masterServerCheck(request);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        if (reply.getIsmaster()) {
            // Sync world time and the masters config that contains the borders
            this.plugin.getServer().getWorlds().get(0).setFullTime(reply.getWorldtime());
            this.plugin.setMasterConf(this.plugin.getGsonSmall().fromJson(reply.getMasterconf(), MasterConf.class));
        }
        return reply.getIsmaster();
    }

    /**
     * Tells the server to shutdown
     */
    public void sendShutdownServer() {
        Empty request = Empty.newBuilder().build();
        try {
            this.slaveShutdownBlockingStub.slaveShutdown(request);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }
    }

    /**
     * Send a players data to another server, right before the player is moved to that server.
     */
    public boolean transferPlayer(Player player, Location location) {
        this.plugin.getLogger().info("Syncing player data for " + player.getName() + " ...");
        PlayerTransferRequest request = PlayerTransferRequest.newBuilder()
                .setName(player.getName())
                .setUuid(player.getUniqueId().toString())
                .setEntityid(player.getEntityId())
                .setWorld(location.getWorld().getName())
                .setLocx(location.getX())
                .setLocy(location.getY())
                .setLocz(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch())
                .setSprinting(player.isSprinting())
                .setFlying(player.isFlying())
                .setInventory(this.plugin.getGsonSmall().toJson(player.getInventory().getContents()))
                .setArmor(this.plugin.getGsonSmall().toJson(player.getInventory().getArmorContents()))
                .setHandslot(player.getInventory().getHeldItemSlot())
                .setGamemode(player.getGameMode().getValue())
                .setHealth(player.getHealth())
                .setFood(player.getFoodLevel())
                .setSaturation(player.getSaturation())
                .setExhaustion(player.getExhaustion())
                .setPotions(this.plugin.getGsonSmall().toJson(player.getActivePotionEffects()))
                .setExp(player.getExp())
                .setTotalexp(player.getTotalExperience())
                .setLevel(player.getLevel())
                .setFireticks(player.getFireTicks())
                .build();
        PlayerTransferReply response;
        try {
            response = playerTransferBlockingStub.playerTransfer(request);
            return response.getReceived();
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
    }

    public void syncBlock(Block block, Material material, byte data) {
        BlockSyncRequest blockSyncRequest = BlockSyncRequest.newBuilder()
                .setMaterial(material.name())
                .setDurability(data)
                .setLocx(block.getLocation().getBlockX())
                .setLocy(block.getLocation().getBlockY())
                .setLocz(block.getLocation().getBlockZ())
                .setWorld(block.getLocation().getWorld().getName())
                .build();
        BlockSyncReply reply;
        try {
            plugin.getLogger().info("Trying to send block sync...");
            reply = this.blockSyncBlockingStub.blockSync(blockSyncRequest);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        this.plugin.getLogger().info("Block Sync: " + reply.getPlaced());
    }

    public void syncEntity(Entity entity, Location location) {
        int entityData = 0;
        JSONObject extraData = new JSONObject();
        switch (entity.getType()) {
            case ARROW:
                Arrow arrow = (Arrow) entity;
                if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
                    entityData = ((Player) arrow.getShooter()).getEntityId();
                }
                break;
            case DROPPED_ITEM:
                Item item = (Item) entity;
                extraData.put("item", this.plugin.getGsonSmall().toJson(item.getItemStack()));
                break;

        }
        EntitySyncRequest entitySyncRequest = EntitySyncRequest.newBuilder()
                .setEntityid(entity.getEntityId())
                .setEntitytype(entity.getType().name())
                .setLocx(location.getX())
                .setLocy(location.getY())
                .setLocz(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch())
                .setVelx(entity.getVelocity().getX())
                .setVely(entity.getVelocity().getY())
                .setVelz(entity.getVelocity().getZ())
                .setEntityuuid(entity.getUniqueId().toString())
                .setData(entityData)
                .setExtradata(extraData.toJSONString())
                .build();
        EntitySyncReply reply;
        try {
            plugin.getLogger().info("Trying to send entity sync... " + entity.getType() + " id= " + entity.getEntityId());
            reply = this.entitySyncBlockingStub.entitySync(entitySyncRequest);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        this.plugin.getLogger().info("Entity sync: " + reply.getReceived());
    }

    public void syncPlayer(Player player, Location location) {
        PlayerSyncRequest playerSyncRequest = PlayerSyncRequest.newBuilder()
                .setEntityid(player.getEntityId())
                .setWorld(location.getWorld().getName())
                .setLocx(location.getX())
                .setLocy(location.getY())
                .setLocz(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch())
                .setVelx(player.getVelocity().getX())
                .setVely(player.getVelocity().getY())
                .setVelz(player.getVelocity().getZ())
                .setUuid(player.getUniqueId().toString())
                .setUsername(player.getDisguisedName())
                .setIteminhand(this.plugin.getGsonSmall().toJson(player.getItemInHand()))
                .setArmor(this.plugin.getGsonSmall().toJson(player.getInventory().getArmorContents()))
                .setFireticks(player.getFireTicks())
                .setHealth(player.getHealth())
                .build();
        PlayerSyncReply reply;
        try {
            plugin.getLogger().info("Trying to send player sync... " + player.getName() + " id= " + player.getEntityId());
            reply = this.playerSyncBlockingStub.playerSync(playerSyncRequest);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        this.plugin.getLogger().info("Player sync: Done");
    }

    public void despawnPlayer(int id, UUID playerUUID) {
        PlayerDespawnRequest playerDespawnRequest = PlayerDespawnRequest.newBuilder().setEntityid(id).setUuid(playerUUID.toString()).build();
        PlayerDespawnReply reply;
        try {
            plugin.getLogger().info("Trying to send player despawn...");
            reply = this.playerDespawnBlockingStub.playerDespawn(playerDespawnRequest);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        this.plugin.getLogger().info("Player despawn: " + reply.getDespawned());
    }


    public void despawnEntity(int id) {
        EntityDespawnRequest entityDespawnRequest = EntityDespawnRequest.newBuilder().setEntityid(id).build();
        EntityDespawnReply reply;
        plugin.getEntitySyncManager().getTransferedEntityIds().remove(id);

        try {
            plugin.getLogger().info("Trying to send entity despawn...");
            reply = this.entityDespawnBlockingStub.entityDespawn(entityDespawnRequest);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        this.plugin.getLogger().info("Entity despawn: " + reply.getDespawned());
    }

    public void damageEntity(int id) {

    }


    public void transferEntity(Entity entity, Location location, String server) {
        JSONObject extraData = new JSONObject();

        plugin.getEntitySyncManager().getTransferedEntityIds().remove(entity.getEntityId());

        if (entity instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) entity;
            if (vehicle.getPassenger() != null) {
                if (vehicle.getPassenger() instanceof Player) {
                    Player passenger = (Player) vehicle.getPassenger();
                    if (!this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(passenger.getUniqueId())) {
                        this.plugin.getPlayerSyncManager().getPlayersBeingSent().add(passenger.getUniqueId());
                        if (this.transferPlayer(passenger, passenger.getLocation())) {
                            this.plugin.getLogger().info("Sending " + passenger.getName() + " to " + server + " SYNC DONE: " + System.currentTimeMillis());
                            this.plugin.sendToServer(passenger, server);
                        } else {
                            this.plugin.getPlayerSyncManager().getPlayersBeingSent().remove(passenger.getUniqueId());
                        }
                    }
                    extraData.put("passenger_player", vehicle.getPassenger().getUniqueId().toString());
                } else {
                    extraData.put("passenger_entity", vehicle.getPassenger().getEntityId());
                }
            }
        }

        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            extraData.put("tamed", tameable.isTamed());
            if (tameable.isTamed()) {
                extraData.put("owner", tameable.getOwner().getUniqueId().toString());
            }
        }
        if (entity instanceof Horse) {
            Horse horse = (Horse) entity;
            extraData.put("variant", horse.getVariant().name());
            extraData.put("color", horse.getColor().name());
            extraData.put("domestication", horse.getDomestication());
            extraData.put("jump", horse.getJumpStrength());
            extraData.put("style", horse.getStyle().name());
            extraData.put("saddle", this.plugin.getGsonSmall().toJson(horse.getInventory().getSaddle(), ItemStack.class));
            extraData.put("armor", this.plugin.getGsonSmall().toJson(horse.getInventory().getArmor(), ItemStack.class));
            extraData.put("inventory", this.plugin.getGsonSmall().toJson(horse.getInventory().getContents(), ItemStack[].class));
        }
        if (entity instanceof ThrownPotion) {
            ThrownPotion thrownPotion = (ThrownPotion) entity;
            extraData.put("effects", this.plugin.getGsonSmall().toJson(thrownPotion.getEffects()));
            extraData.put("item", this.plugin.getGsonSmall().toJson(thrownPotion.getItem()));
        }
        if (entity instanceof Item) {
            Item item = (Item) entity;
            extraData.put("item", this.plugin.getGsonSmall().toJson(item.getItemStack()));
        }

        EntityTransferRequest entityTransferRequest = EntityTransferRequest.newBuilder()
                .setEntitytype(entity.getType().name())
                .setEntityid(entity.getEntityId())
                .setLocx(location.getX())
                .setLocy(location.getY())
                .setLocz(location.getZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch())
                .setWorld(location.getWorld().getName())
                .setTickslived(entity.getTicksLived())
                .setFireticks(entity.getFireTicks())
                .setVelx(entity.getVelocity().getX())
                .setVely(entity.getVelocity().getY())
                .setVelz(entity.getVelocity().getZ())
                .setMaxhealth(entity instanceof LivingEntity ? ((LivingEntity) entity).getMaxHealth() : -1.0)
                .setHealth(entity instanceof LivingEntity ? ((LivingEntity) entity).getHealth() : -1.0)
                .setIsbaby(entity instanceof Ageable ? !((Ageable) entity).isAdult() : false)
                .setAge(entity instanceof Ageable ? ((Ageable) entity).getAge() : 0)
                .setExtradata(extraData.toJSONString())
                .build();
        EntityTransferReply reply;
        try {
            plugin.getLogger().info("Trying to send entity transfer... " + extraData.toJSONString());
            reply = this.entityTransferBlockingStub.entityTransfer(entityTransferRequest);
        } catch (StatusRuntimeException e) {
            this.plugin.getLogger().log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        this.plugin.getLogger().info("Entity transfer: " + reply.getReceived());
    }

    public String syncMasterPlugin(String data) {
        MasterPluginSyncRequest masterPluginSyncRequest = MasterPluginSyncRequest.newBuilder()
                .setFrom(ShardPlugin.getPlugin().getConf().getServerName())
                .setData(data)
                .build();
        MasterPluginSyncReply reply;
        try {
            reply = this.masterPluginSyncBlockingStub.masterPluginSync(masterPluginSyncRequest);
        } catch (StatusRuntimeException e) {
            return "";
        }
        return reply.getResponse();
    }

}
