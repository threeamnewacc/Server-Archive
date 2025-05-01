package net.badlion.shards.listener;

import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.ShardInstanceClient;
import net.badlion.shards.type.Border;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityMoveEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EntityListener implements Listener {

    private final ShardPlugin plugin;

    public EntityListener(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDespawn(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }

        plugin.getLogger().info("SEnding despawn for entity id " + entity.getEntityId());
        for (ShardInstanceClient client : this.plugin.getNearbyShardClients(event.getEntity().getLocation(), 64)) {
            client.despawnEntity(entity.getEntityId());
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        plugin.getLogger().info("SEnding despawn for item entity id " + event.getItem().getEntityId());
        for (ShardInstanceClient client : this.plugin.getNearbyShardClients(event.getItem().getLocation(), 64)) {
            client.despawnEntity(event.getItem().getEntityId());
        }
    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        plugin.getLogger().info("SEnding despawn for inv item entity id " + event.getItem().getEntityId());
        for (ShardInstanceClient client : this.plugin.getNearbyShardClients(event.getItem().getLocation(), 64)) {
            client.despawnEntity(event.getItem().getEntityId());
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        // Don't transfer enderpearls, otherwise we will have to add something to teleport players cross server, this way the origin server can just teleport the player and they can switch servers if needed
        if (event.getEntity() instanceof EnderPearl) {
            return;
        }
        // Skip non movements
        if (event.getFrom().equals(event.getTo())) {
            return;
        }

        String shardTo = this.plugin.getShardAt(event.getTo());

        if (shardTo != null && !this.plugin.getConf().getServerName().equals(shardTo)) {
            ShardInstanceClient client = this.plugin.getShardClient(shardTo);

            event.getEntity().removeWithoutDestroyPackets();

            this.plugin.getEntitySyncManager().removeEntity(event.getEntity().getEntityId());

            client.transferEntity(event.getEntity(), event.getTo(), shardTo);
            return;
        }

        Border shardBorder = this.plugin.getMasterConf().getShardBorderMap().get(this.plugin.getConf().getServerName());

        for (ShardInstanceClient client : this.plugin.getNearbyShardClients(event.getTo(), 64)) {
            if (!shardBorder.isInsideNonRenderZone(event.getTo())) {
                client.syncEntity(event.getEntity(), event.getTo());
            } else if (!shardBorder.isInsideNonRenderZone(event.getFrom()) && shardBorder.isInsideNonRenderZone(event.getTo())) {
                client.despawnEntity(event.getEntity().getEntityId());
            }
        }
    }

}
