package net.badlion.shards.listener;

import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.ShardInstanceClient;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class BlockListener implements Listener {

    private final ShardPlugin plugin;

    public BlockListener(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockChange(BlockChangeEvent event) {
        Block block = event.getBlock();
        // IGNORE LAVA/WATER its broken
        if(event.getBlock().getType().equals(Material.WATER)
                || event.getBlock().getType().equals(Material.LAVA)
                || event.getBlock().getType().equals(Material.STATIONARY_WATER)
                || event.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
            return;
        }

        if (!this.plugin.getPlayerSyncManager().getRecivedBlockLocations().remove(block.getLocation())) {
            for (ShardInstanceClient client : this.plugin.getNearbyShardClients(block.getLocation(), 64)) {
                this.plugin.getLogger().info("Syncing 1 block... " + block.getLocation().toString());
                client.syncBlock(block, block.getType(), block.getData());
            }
        }
    }

    @EventHandler
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();

        this.plugin.getLogger().info("BlockClicked: " + block.getType().toString());
        Block placeTarget = event.getBlockClicked().getRelative(event.getBlockFace());
        this.plugin.getLogger().info("BlockRelative: " + placeTarget.getType().toString());

        Material type = null;
        if(event.getPlayer().getItemInHand().getType().equals(Material.LAVA_BUCKET)) {
            type = Material.LAVA;
        } else if (event.getPlayer().getItemInHand().getType().equals(Material.WATER_BUCKET)) {
            type = Material.WATER;
        }

        for (ShardInstanceClient client : this.plugin.getNearbyShardClients(placeTarget.getLocation(), 64)) {
            this.plugin.getLogger().info("Syncing 1 LIQUID block... " + placeTarget.getLocation().toString());
            client.syncBlock(placeTarget, type, (byte) 0);
        }
    }

    /*
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LAST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        ShardInstanceClient client = this.plugin.getPlayerSyncManager().getClient();
        if (client != null) {
            plugin.getLogger().info("Syncing 1 block... " + block.getLocation().toString());
            client.syncBlock(block, event.getBlockPlaced().getType(), event.getBlockPlaced().getData());
            plugin.getLogger().info("Block sync done...");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LAST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        ShardInstanceClient client = this.plugin.getPlayerSyncManager().getClient();
        if (client != null) {
            client.syncBlock(block, Material.AIR, (byte) 0);
        }
    }

*/

    /*
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LAST)
    public void onBlockFlow(BlockFromToEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                ShardInstanceClient client = BlockListener.this.plugin.getPlayerSyncManager().getClient();
                if (client != null) {
                    client.syncBlock(event.getBlock(), event.getBlock().getType(), event.getBlock().getData());
                    client.syncBlock(event.getToBlock(), event.getToBlock().getType(), event.getToBlock().getData());
                }
            }
        }.runTaskAsynchronously(this.plugin);
    }*/

}
